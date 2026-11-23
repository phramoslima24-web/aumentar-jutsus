package dev.narutotoaddon.jutsuslots.mixin;

import dev.narutotoaddon.jutsuslots.logic.JutsuSlotLogic;
import net.mcreator.narutoto.procedures.Bind2Procedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Depois de aprender/limpar um jutsu, copia o valor do slot extra selecionado para o espelho do addon. */
@Mixin(Bind2Procedure.class)
public abstract class Bind2ProcedureMixin {
    @Inject(method = "execute(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), remap = false)
    private static void narutotoSlots$bindExtraSlot(Entity entity, CallbackInfo ci) {
        JutsuSlotLogic.onBind(entity);
    }
}
