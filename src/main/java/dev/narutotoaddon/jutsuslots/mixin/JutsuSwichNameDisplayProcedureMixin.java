package dev.narutotoaddon.jutsuslots.mixin;

import dev.narutotoaddon.jutsuslots.logic.JutsuSlotLogic;
import net.mcreator.narutoto.procedures.JutsuSwichNameDisplayProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * O Narutoto lanca jutsus pelo NOME do slot selecionado (jutsu_choosed_name).
 * Para slots 16+ o nome vem do addon; para 1..15 o original continua igual.
 */
@Mixin(JutsuSwichNameDisplayProcedure.class)
public abstract class JutsuSwichNameDisplayProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void narutotoSlots$extraSlotName(Entity entity, CallbackInfo ci) {
        if (JutsuSlotLogic.handleNameDisplay(entity)) {
            ci.cancel();
        }
    }
}
