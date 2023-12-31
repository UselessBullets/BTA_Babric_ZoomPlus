package useless.zoomplus.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.camera.CameraUtil;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import useless.zoomplus.ZoomPlus;

@Mixin(value = WorldRenderer.class, remap = false)
public class WorldRendererMixin {
    @Unique
    private final Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
    @Unique
    private final GameSettings gameSettings = mc.gameSettings;
    @Shadow
    private float field_22222_x = 0.0f;
    @Shadow
    private float field_22221_y = 0.0f;
    @Inject(method = "getFOVModifier(FZ)F", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void capFOV(float renderPartialTicks, boolean isModifiedByFOV, CallbackInfoReturnable<Float> cir, EntityLiving entityliving, float f1){
        if (isModifiedByFOV && this.mc.gameSettings.keyZoom.isPressed() && this.mc.currentScreen == null){
            float finalFov = f1 + this.field_22221_y + (this.field_22222_x - this.field_22221_y) * renderPartialTicks;
            if (finalFov > ZoomPlus.getMaxFOV(gameSettings)){
                finalFov = (float) ZoomPlus.getMaxFOV(gameSettings);
            } else if (finalFov < ZoomPlus.getMinFOV(gameSettings)) {
                finalFov = (float) ZoomPlus.getMinFOV(gameSettings);
            }
            cir.setReturnValue(finalFov);
        }
    }
    @Redirect(method = "setupCameraTransform(F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isPressed()Z"))
    private boolean disableDefaultZoom(KeyBinding instance){
        return false;
    }
}
