package dev.narutotoaddon.jutsuslots.data;

import dev.narutotoaddon.jutsuslots.logic.SlotRules;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Guarda o nome do jutsu dos slots extras (16..max) de um jogador.
 *
 * Faz o mesmo papel dos campos {@code jutsu_keyN} do Narutoto original (espelho
 * persistente + sincronizado do que foi gravado em persistentData "jutsuN").
 */
public class ExtraSlots implements INBTSerializable<CompoundTag> {
    private static final String PREFIX = "s";
    private final Map<Integer, String> names = new TreeMap<>();

    public String get(int slot) {
        return names.getOrDefault(slot, "");
    }

    public void set(int slot, String name) {
        if (slot < SlotRules.FIRST_EXTRA_SLOT) {
            return;
        }
        if (name == null || name.isBlank()) {
            names.remove(slot);
        } else {
            names.put(slot, name);
        }
    }

    public void clear() {
        names.clear();
    }

    public void copyFrom(ExtraSlots other) {
        names.clear();
        names.putAll(other.names);
    }

    public Map<Integer, String> snapshot() {
        return Collections.unmodifiableMap(new TreeMap<>(names));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        names.forEach((slot, name) -> tag.putString(PREFIX + slot, name));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        names.clear();
        for (String key : tag.getAllKeys()) {
            if (!key.startsWith(PREFIX)) {
                continue;
            }
            try {
                set(Integer.parseInt(key.substring(PREFIX.length())), tag.getString(key));
            } catch (NumberFormatException ignored) {
                // chave desconhecida: ignora
            }
        }
    }
}
