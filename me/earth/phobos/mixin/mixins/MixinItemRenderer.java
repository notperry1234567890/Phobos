//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.render.NoRender;
import me.earth.phobos.features.modules.render.SmallShield;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public abstract class MixinItemRenderer {
  private boolean injection = true;
  
  @Shadow
  public abstract void renderItemInFirstPerson(AbstractClientPlayer paramAbstractClientPlayer, float paramFloat1, float paramFloat2, EnumHand paramEnumHand, float paramFloat3, ItemStack paramItemStack, float paramFloat4);
  
  @Inject(method = {"renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V"}, at = {@At("HEAD")}, cancellable = true)
  public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
    if (this.injection) {
      info.cancel();
      SmallShield offset = SmallShield.getINSTANCE();
      float xOffset = 0.0F;
      float yOffset = 0.0F;
      this.injection = false;
      if (hand == EnumHand.MAIN_HAND) {
        if (offset.isOn() && player.getHeldItemMainhand() != ItemStack.EMPTY) {
          xOffset = ((Float)offset.mainX.getValue()).floatValue();
          yOffset = ((Float)offset.mainY.getValue()).floatValue();
        } 
      } else if (!((Boolean)offset.normalOffset.getValue()).booleanValue() && offset.isOn() && player.getHeldItemOffhand() != ItemStack.EMPTY) {
        xOffset = ((Float)offset.offX.getValue()).floatValue();
        yOffset = ((Float)offset.offY.getValue()).floatValue();
      } 
      renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_ + yOffset);
      this.injection = true;
    } 
  }
  
  @Redirect(method = {"renderArmFirstPerson"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 0))
  public void translateHook(float x, float y, float z) {
    SmallShield offset = SmallShield.getINSTANCE();
    boolean shiftPos = ((Minecraft.getMinecraft()).player != null && (Minecraft.getMinecraft()).player.getHeldItemMainhand() != ItemStack.EMPTY && offset.isOn());
    GlStateManager.translate(x + (shiftPos ? ((Float)offset.mainX.getValue()).floatValue() : 0.0F), y + (shiftPos ? ((Float)offset.mainY.getValue()).floatValue() : 0.0F), z);
  }
  
  @Inject(method = {"renderFireInFirstPerson"}, at = {@At("HEAD")}, cancellable = true)
  public void renderFireInFirstPersonHook(CallbackInfo info) {
    if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).fire.getValue()).booleanValue())
      info.cancel(); 
  }
  
  @Inject(method = {"renderSuffocationOverlay"}, at = {@At("HEAD")}, cancellable = true)
  public void renderSuffocationOverlay(CallbackInfo ci) {
    if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).blocks.getValue()).booleanValue())
      ci.cancel(); 
  }
}
