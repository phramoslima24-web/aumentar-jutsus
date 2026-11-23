
package dev.narutotoaddon.jutsuslots.event;

import dev.narutotoaddon.jutsuslots.NarutotoJutsuSlots;
import dev.narutotoaddon.jutsuslots.data.ExtraSlotsProvider;
import dev.narutotoaddon.jutsuslots.network.SlotsNetwork;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Anexa, copia (morte/End) e sincroniza os slots extras do jogador. */
@Mod.EventBusSubscriber(modid = NarutotoJutsuSlots.MODID)
public final class ServerEvents {
    private static final ResourceLocation CAPABILITY_ID =
            new ResourceLocation(NarutotoJutsuSlots.MODID, "extra_slots");

    private ServerEvents() {
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            ExtraSlotsProvider provider = new ExtraSlotsProvider();
            event.addCapability(CAPABILITY_ID, provider);
            event.addListener(provider::invalidate);
        }
    }

    /** Morte / volta do End: os slots extras sobrevivem. */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();

        event.getOriginal()
                .getCapability(ExtraSlotsProvider.CAPABILITY, null)
                .ifPresent(oldData ->
                        event.getEntity()
                                .getCapability(ExtraSlotsProvider.CAPABILITY, null)
                                .ifPresent(newData -> newData.copyFrom(oldData)));

        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        sync(event.getEntity());
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync(event.getEntity());
    }

    private static void sync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            SlotsNetwork.syncTo(serverPlayer);
        }
    }
}
