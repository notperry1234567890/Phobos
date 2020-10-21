//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.misc.ToolTips;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class MixinGuiScreen extends Gui {
  @Inject(method = {"renderToolTip"}, at = {@At("HEAD")}, cancellable = true)
  public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
    if (ToolTips.getInstance().isOn() && ((Boolean)(ToolTips.getInstance()).shulkers.getValue()).booleanValue() && stack.getItem() instanceof net.minecraft.item.ItemShulkerBox) {
      ToolTips.getInstance().renderShulkerToolTip(stack, x, y, null);
      info.cancel();
    } 
  }
}
