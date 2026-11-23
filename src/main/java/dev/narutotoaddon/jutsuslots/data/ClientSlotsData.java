package dev.narutotoaddon.jutsuslots.data;

import dev.narutotoaddon.jutsuslots.logic.SlotRules;
import java.util.Collections;
import java.util.Map;

/**
 * Copia, no cliente, do que o servidor enviou (quantidade de slots + nomes dos slots extras).
 * Classe 100% "pura" (sem classes de cliente do Minecraft) para poder ser carregada em qualquer lado.
 */
public final class ClientSlotsData {
    private static volatile int maxSlots = SlotRules.ORIGINAL_SLOTS;
    private static volatile Map<Integer, String> names = Collections.emptyMap();

    private ClientSlotsData() {
    }

    public static int getMaxSlots() {
        return maxSlots;
    }

    public static String get(int slot) {
        return names.getOrDefault(slot, "");
    }

    public static void apply(int newMaxSlots, Map<Integer, String> newNames) {
        maxSlots = Math.max(SlotRules.ORIGINAL_SLOTS, newMaxSlots);
        names = Map.copyOf(newNames);
    }

    public static void reset() {
        maxSlots = SlotRules.ORIGINAL_SLOTS;
        names = Collections.emptyMap();
    }
}
