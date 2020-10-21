//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.client.HUD;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FontRenderer.class})
public abstract class MixinFontRenderer {
  @Shadow
  public abstract int renderString(String paramString, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean);
  
  @Redirect(method = {"drawString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderString(Ljava/lang/String;FFIZ)I"))
  public int renderStringHook(FontRenderer fontrenderer, String text, float x, float y, int color, boolean dropShadow) {
    if (Phobos.moduleManager != null && ((Boolean)(HUD.getInstance()).shadow.getValue()).booleanValue() && dropShadow)
      return renderString(text, x - 0.5F, y - 0.5F, color, true); 
    return renderString(text, x, y, color, dropShadow);
  }
}
