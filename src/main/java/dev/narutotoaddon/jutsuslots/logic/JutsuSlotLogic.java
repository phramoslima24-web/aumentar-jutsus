package dev.narutotoaddon.jutsuslots.logic;

import dev.narutotoaddon.jutsuslots.config.SlotsConfig;
import dev.narutotoaddon.jutsuslots.data.ClientSlotsData;
import dev.narutotoaddon.jutsuslots.data.ExtraSlotsProvider;
import dev.narutotoaddon.jutsuslots.network.SlotsNetwork;
import net.mcreator.narutoto.NarutotoMod;
import net.mcreator.narutoto.network.NarutotoModVariables;
import net.mcreator.narutoto.procedures.JutsuSwichNameDisplayProcedure;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Ponte entre os Mixins e o Narutoto. Toda a logica "de verdade" dos slots extras mora aqui.
 *
 * Importante: no Narutoto as teclas de trocar jutsu rodam a procedure no CLIENTE (predicao)
 * e no SERVIDOR. Por isso {@link #selectNext}/{@link #selectPrevious} funcionam nos dois lados.
 */
public final class JutsuSlotLogic {
    private static final ResourceLocation SWITCH_SOUND = new ResourceLocation("narutoto", "jutsuswitch");
    private static final String PERSISTENT_KEY_PREFIX = "jutsu";

    private JutsuSlotLogic() {
    }

    // ------------------------------------------------------------------ navegacao

    public static void selectNext(Entity entity) {
        changeSlot(entity, true);
    }

    public static void selectPrevious(Entity entity) {
        changeSlot(entity, false);
    }

    private static void changeSlot(Entity entity, boolean up) {
        if (entity == null) {
            return;
        }
        NarutotoModVariables.PlayerVariables vars = vars(entity);
        if (vars != null && vars.ninja > 0.0) {
            int current = (int) Math.round(vars.jutsu_choosed);
            int max = maxSlots(entity);
            int target = up
                    ? SlotRules.next(current, vars.attribute_intelligence, max)
                    : SlotRules.previous(current, vars.attribute_intelligence, max);
            setChoosedSlot(entity, target);
            playSwitchSound(entity);
        }
        // igual ao original: 1 tick depois atualiza o nome do jutsu selecionado
        NarutotoMod.queueServerWork(1, () -> JutsuSwichNameDisplayProcedure.execute(entity));
    }

    private static void setChoosedSlot(Entity entity, int slot) {
        final double value = slot;
        entity.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
            capability.jutsu_choosed = value;
            capability.syncPlayerVariables(entity);
        });
    }

    private static void playSwitchSound(Entity entity) {
        Level level = entity.level();
        if (level.isClientSide()) {
            return;
        }
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(SWITCH_SOUND);
        if (sound != null) {
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    /** Quantidade de slots em vigor: servidor usa a config; cliente usa o valor que o servidor enviou. */
    public static int maxSlots(Entity entity) {
        int configured = entity.level().isClientSide() ? ClientSlotsData.getMaxSlots() : SlotsConfig.maxSlots();
        return Math.max(SlotRules.ORIGINAL_SLOTS, Math.min(SlotsConfig.HARD_MAX_SLOTS, configured));
    }

    // ------------------------------------------------------------------ nome do jutsu selecionado

    /**
     * Chamado no inicio de {@code JutsuSwichNameDisplayProcedure.execute}.
     *
     * @return true se o slot atual e extra (16+) e o nome ja foi aplicado (o original deve ser cancelado);
     *         false para deixar o Narutoto tratar os slots 1..15 normalmente.
     */
    public static boolean handleNameDisplay(Entity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }
        NarutotoModVariables.PlayerVariables vars = vars(entity);
        if (vars == null) {
            return false;
        }
        int slot = (int) Math.round(vars.jutsu_choosed);
        if (!SlotRules.isExtraSlot(slot)) {
            return false;
        }
        final String name = extraName(entity, slot);
        entity.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null).ifPresent(capability -> {
            capability.jutsu_choosed_name = name;
            capability.syncPlayerVariables(entity);
        });
        return true;
    }

    /** Nome gravado no slot extra (cliente: copia sincronizada; servidor: capability do jogador). */
    public static String extraName(Entity entity, int slot) {
        if (entity.level().isClientSide()) {
            return ClientSlotsData.get(slot);
        }
        return entity.getCapability(ExtraSlotsProvider.CAPABILITY, null).map(extra -> extra.get(slot)).orElse("");
    }

    // ------------------------------------------------------------------ gravar / limpar (Bind2 / ResetAll)

    /**
     * Chamado no inicio de {@code Bind2Procedure.execute}: o Narutoto grava o jutsu em
     * persistentData "jutsuN" e chama Bind2 para copiar para o espelho. Aqui fazemos a copia
     * para o espelho dos slots extras.
     */
    public static void onBind(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        NarutotoModVariables.PlayerVariables vars = vars(entity);
        if (vars == null) {
            return;
        }
        int slot = (int) Math.round(vars.jutsu_choosed);
        if (!SlotRules.isExtraSlot(slot)) {
            return;
        }
        final String name = player.getPersistentData().getString(PERSISTENT_KEY_PREFIX + slot);
        player.getCapability(ExtraSlotsProvider.CAPABILITY, null).ifPresent(extra -> extra.set(slot, name));
        SlotsNetwork.syncTo(player);
    }

    /** Chamado no inicio de {@code ResetAllProcedure.execute}: limpa todos os slots extras. */
    public static void onResetAll(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        player.getCapability(ExtraSlotsProvider.CAPABILITY, null).ifPresent(extra -> extra.clear());
        for (int slot = SlotRules.FIRST_EXTRA_SLOT; slot <= SlotsConfig.HARD_MAX_SLOTS; slot++) {
            player.getPersistentData().remove(PERSISTENT_KEY_PREFIX + slot);
        }
        SlotsNetwork.syncTo(player);
    }

    // ------------------------------------------------------------------ utilitarios (tambem usados pelo HUD)

    public static NarutotoModVariables.PlayerVariables vars(Entity entity) {
        return entity.getCapability(NarutotoModVariables.PLAYER_VARIABLES_CAPABILITY, null).resolve().orElse(null);
    }

    /** Nome gravado nos slots originais 1..15 (campos jutsu_key1..15 do Narutoto). */
    public static String originalSlotName(NarutotoModVariables.PlayerVariables v, int slot) {
        String name;
        switch (slot) {
            case 1: name = v.jutsu_key1; break;
            case 2: name = v.jutsu_key2; break;
            case 3: name = v.jutsu_key3; break;
            case 4: name = v.jutsu_key4; break;
            case 5: name = v.jutsu_key5; break;
            case 6: name = v.jutsu_key6; break;
            case 7: name = v.jutsu_key7; break;
            case 8: name = v.jutsu_key8; break;
            case 9: name = v.jutsu_key9; break;
            case 10: name = v.jutsu_key10; break;
            case 11: name = v.jutsu_key11; break;
            case 12: name = v.jutsu_key12; break;
            case 13: name = v.jutsu_key13; break;
            case 14: name = v.jutsu_key14; break;
            case 15: name = v.jutsu_key15; break;
            default: name = ""; break;
        }
        return name == null ? "" : name;
    }
}
