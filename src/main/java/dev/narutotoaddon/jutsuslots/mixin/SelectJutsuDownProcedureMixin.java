package dev.narutotoaddon.jutsuslots.mixin;

import dev.narutotoaddon.jutsuslots.logic.JutsuSlotLogic;
import net.mcreator.narutoto.procedures.SelectJutsuDownProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Substitui a troca "jutsu anterior" (limitada a 15) pela versao com slots extras. */
@Mixin(SelectJutsuDownProcedure.class)
public abstract class SelectJutsuDownProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutotoSlots$selectPrevious(LevelAccessor world, Entity entity, CallbackInfo ci) {
        JutsuSlotLogic.selectPrevious(entity);
        ci.cancel();
    }
}
