package dev.narutotoaddon.jutsuslots.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.narutotoaddon.jutsuslots.NarutotoJutsuSlots;
import dev.narutotoaddon.jutsuslots.config.SlotsConfig;
import dev.narutotoaddon.jutsuslots.data.ClientSlotsData;
import dev.narutotoaddon.jutsuslots.logic.JutsuSlotLogic;
import dev.narutotoaddon.jutsuslots.logic.SlotRules;
import net.mcreator.narutoto.network.NarutotoModVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Desenha os slots extras (16..max) ao lado da lista expandida original do Narutoto
 * (a que aparece ao apertar a tecla "extra_choose", padrao: '.').
 *
 * Mesmo visual do original: nome em amarelo e triangulo vermelho no slot selecionado.
 */
@Mod.EventBusSubscriber(modid = NarutotoJutsuSlots.MODID, value = Dist.CLIENT)
public final class ExtraSlotsOverlay {
    private static final ResourceLocation RED_TRIANGLE = new ResourceLocation("narutoto", "textures/screens/red_triangle.png");
    private static final int TEXT_COLOR = 0xFFFFFF33; // mesmo amarelo (-205) usado pelo Narutoto
    private static final int START_X = 24;
    private static final int START_Y = 24;
    private static final int ROW_HEIGHT = 10;
    private static final int COLUMN_GAP = 18;

    private ExtraSlotsOverlay() {
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) {
            return;
        }
        NarutotoModVariables.PlayerVariables vars = JutsuSlotLogic.vars(player);
        if (vars == null || vars.ninja <= 0.0 || !vars.extra_choose) {
            return; // mesma condicao da lista original: so aparece com a lista expandida ligada
        }
        int max = ClientSlotsData.getMaxSlots();
        if (max <= SlotRules.ORIGINAL_SLOTS) {
            return;
        }

        Font font = mc.font;
        GuiGraphics graphics = event.getGuiGraphics();
        int rows = Math.max(5, SlotsConfig.hudRowsPerColumn());
        int selected = (int) Math.round(vars.jutsu_choosed);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 1a coluna extra comeca logo depois da coluna original (slots 1..15)
        int firstColumnWidth = 0;
        for (int slot = 1; slot <= SlotRules.ORIGINAL_SLOTS; slot++) {
            firstColumnWidth = Math.max(firstColumnWidth, font.width(JutsuSlotLogic.originalSlotName(vars, slot)));
        }
        int columnX = START_X + firstColumnWidth + COLUMN_GAP;

        int slot = SlotRules.FIRST_EXTRA_SLOT;
        while (slot <= max) {
            int last = Math.min(slot + rows - 1, max);
            int columnWidth = 0;
            for (int s = slot; s <= last; s++) {
                columnWidth = Math.max(columnWidth, font.width(ClientSlotsData.get(s)));
            }
            for (int s = slot; s <= last; s++) {
                int y = START_Y + (s - slot) * ROW_HEIGHT;
                if (s == selected) {
                    graphics.blit(RED_TRIANGLE, columnX - 7, y + 2, 0.0F, 0.0F, 5, 5, 5, 5);
                }
                graphics.drawString(font, ClientSlotsData.get(s), columnX, y, TEXT_COLOR, false);
            }
            columnX += columnWidth + COLUMN_GAP;
            slot = last + 1;
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSlotsData.reset();
    }
}
