package dev.narutotoaddon.jutsuslots;

import com.mojang.logging.LogUtils;
import dev.narutotoaddon.jutsuslots.config.SlotsConfig;
import dev.narutotoaddon.jutsuslots.data.ExtraSlots;
import dev.narutotoaddon.jutsuslots.network.SlotsNetwork;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Addon do Narutoto: aumenta a quantidade de slots da barra de jutsus (padrão: 15 -> 40).
 *
 * A mudança de comportamento é feita por Mixins nos procedures do Narutoto
 * (veja o pacote {@code mixin}); a lógica fica em {@code logic.JutsuSlotLogic}.
 */
@Mod(NarutotoJutsuSlots.MODID)
public class NarutotoJutsuSlots {
    public static final String MODID = "narutoto_jutsu_slots";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NarutotoJutsuSlots() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(NarutotoJutsuSlots::registerCapabilities);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SlotsConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SlotsConfig.CLIENT_SPEC);

        SlotsNetwork.register();
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(ExtraSlots.class);
    }
}
