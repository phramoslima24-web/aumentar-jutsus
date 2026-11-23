package dev.narutotoaddon.jutsuslots.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

/** Anexa {@link ExtraSlots} aos jogadores e salva/carrega junto com o NBT do jogador. */
public class ExtraSlotsProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<ExtraSlots> CAPABILITY = CapabilityManager.get(new CapabilityToken<ExtraSlots>() {
    });

    private final ExtraSlots backend = new ExtraSlots();
    private final LazyOptional<ExtraSlots> optional = LazyOptional.of(() -> backend);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == CAPABILITY ? optional.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        optional.invalidate();
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.deserializeNBT(nbt);
    }
}
