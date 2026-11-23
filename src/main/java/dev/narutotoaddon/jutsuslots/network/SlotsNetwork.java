package dev.narutotoaddon.jutsuslots.network;

import dev.narutotoaddon.jutsuslots.NarutotoJutsuSlots;
import dev.narutotoaddon.jutsuslots.config.SlotsConfig;
import dev.narutotoaddon.jutsuslots.data.ExtraSlotsProvider;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/** Canal de rede proprio do addon (nao mexe no canal do Narutoto). */
public final class SlotsNetwork {
    private static final String PROTOCOL = "1";
    private static SimpleChannel channel;

    private SlotsNetwork() {
    }

    public static void register() {
        channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(NarutotoJutsuSlots.MODID, "main"),
                () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
        channel.registerMessage(0, SyncExtraSlotsPacket.class,
                SyncExtraSlotsPacket::encode, SyncExtraSlotsPacket::decode, SyncExtraSlotsPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    /** Envia ao jogador a quantidade de slots e o conteudo dos slots extras. */
    public static void syncTo(ServerPlayer player) {
        player.getCapability(ExtraSlotsProvider.CAPABILITY, null).ifPresent(extra ->
                channel.send(PacketDistributor.PLAYER.with(() -> player),
                        new SyncExtraSlotsPacket(SlotsConfig.maxSlots(), extra.snapshot())));
    }
}
