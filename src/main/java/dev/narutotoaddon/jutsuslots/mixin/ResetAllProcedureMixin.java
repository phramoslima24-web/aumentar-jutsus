package dev.narutotoaddon.jutsuslots.mixin;

import dev.narutotoaddon.jutsuslots.logic.JutsuSlotLogic;
import net.mcreator.narutoto.procedures.ResetAllProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** "Resetar tudo" do Narutoto tambem limpa os slots extras. */
@Mixin(ResetAllProcedure.class)
public abstract class ResetAllProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"), remap = false)
    private static void narutotoSlots$resetExtraSlots(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        JutsuSlotLogic.onResetAll(entity);
    }
}
