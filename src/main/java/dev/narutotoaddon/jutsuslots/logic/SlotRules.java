package dev.narutotoaddon.jutsuslots.logic;

/**
 * Regras da barra de jutsus, SEM nenhuma dependência do Minecraft/Forge.
 *
 * Reproduz exatamente o comportamento original do Narutoto para os slots 1..15
 * e estende a mesma ideia para os slots extras (16..max):
 *
 *  - slots 1..10  : livres desde o início;
 *  - slots 11..15 : exigem inteligência 10, 20, 30, 40 e 50 respectivamente;
 *  - slots 16..max: abrem junto com o slot 15 (inteligência máxima = 50).
 *
 * Todos os números de slot são 1-based (igual ao mod original).
 */
public final class SlotRules {
    /** Quantidade de slots da barra original. */
    public static final int ORIGINAL_SLOTS = 15;
    /** Slots sempre liberados. */
    public static final int FREE_SLOTS = 10;
    /** Inteligência máxima do mod original (AddIntelligence limita em 50). */
    public static final int MAX_INTELLIGENCE = 50;
    /** Primeiro slot controlado por este addon. */
    public static final int FIRST_EXTRA_SLOT = ORIGINAL_SLOTS + 1;

    private SlotRules() {
    }

    /** Inteligência mínima necessária para usar o slot. */
    public static double requiredIntelligence(int slot) {
        if (slot <= FREE_SLOTS) {
            return 0.0;
        }
        if (slot <= ORIGINAL_SLOTS) {
            return (slot - FREE_SLOTS) * 10.0;
        }
        return MAX_INTELLIGENCE;
    }

    public static boolean canUse(int slot, double intelligence) {
        return slot >= 1 && intelligence >= requiredIntelligence(slot);
    }

    /** Maior slot alcançável em sequência a partir do 1 (equivale a floor(int/10)+10 do original, limitado a 15). */
    public static int highestUsable(double intelligence, int maxSlots) {
        int slot = 1;
        while (slot < maxSlots && canUse(slot + 1, intelligence)) {
            slot++;
        }
        return slot;
    }

    /** Próximo slot ao apertar a tecla "trocar jutsu para cima". */
    public static int next(int current, double intelligence, int maxSlots) {
        if (current < 1 || current >= maxSlots) {
            return 1;
        }
        int candidate = current + 1;
        return canUse(candidate, intelligence) ? candidate : 1;
    }

    /** Slot anterior ao apertar a tecla "trocar jutsu para baixo". */
    public static int previous(int current, double intelligence, int maxSlots) {
        int highest = highestUsable(intelligence, maxSlots);
        if (current > highest) {
            return highest;
        }
        if (current > 1) {
            return current - 1;
        }
        return highest;
    }

    public static boolean isExtraSlot(int slot) {
        return slot >= FIRST_EXTRA_SLOT;
    }
}
