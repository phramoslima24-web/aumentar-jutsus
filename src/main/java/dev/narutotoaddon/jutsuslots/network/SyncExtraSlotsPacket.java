package dev.narutotoaddon.jutsuslots.network;

import dev.narutotoaddon.jutsuslots.data.ClientSlotsData;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

/** Servidor -> cliente: quantidade de slots e nomes dos slots extras. */
public final class SyncExtraSlotsPacket {
    private static final int MAX_NAME_LENGTH = 256;

    private final int maxSlots;
    private final Map<Integer, String> names;

    public SyncExtraSlotsPacket(int maxSlots, Map<Integer, String> names) {
        this.maxSlots = maxSlots;
        this.names = names;
    }

    public static void encode(SyncExtraSlotsPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.maxSlots);
        buf.writeVarInt(msg.names.size());
        msg.names.forEach((slot, name) -> {
            buf.writeVarInt(slot);
            buf.writeUtf(name, MAX_NAME_LENGTH);
        });
    }

    public static SyncExtraSlotsPacket decode(FriendlyByteBuf buf) {
        int max = buf.readVarInt();
        int count = buf.readVarInt();
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < count; i++) {
            int slot = buf.readVarInt();
            String name = buf.readUtf(MAX_NAME_LENGTH);
            map.put(slot, name);
        }
        return new SyncExtraSlotsPacket(max, map);
    }

    public static void handle(SyncExtraSlotsPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientSlotsData.apply(msg.maxSlots, msg.names));
        context.setPacketHandled(true);
    }
}
