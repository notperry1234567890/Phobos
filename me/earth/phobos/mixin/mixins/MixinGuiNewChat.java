//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import java.util.List;
import me.earth.phobos.features.modules.misc.ChatModifier;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({GuiNewChat.class})
public class MixinGuiNewChat extends Gui {
  @Redirect(method = {"drawChat"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
  private void drawRectHook(int left, int top, int right, int bottom, int color) {
    Gui.drawRect(left, top, right, bottom, (ChatModifier.getInstance().isOn() && ((Boolean)(ChatModifier.getInstance()).clean.getValue()).booleanValue()) ? 0 : color);
  }
  
  @Redirect(method = {"setChatLine"}, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 0, remap = false))
  public int drawnChatLinesSize(List<ChatLine> list) {
    return (ChatModifier.getInstance().isOn() && ((Boolean)(ChatModifier.getInstance()).infinite.getValue()).booleanValue()) ? -2147483647 : list.size();
  }
  
  @Redirect(method = {"setChatLine"}, at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 2, remap = false))
  public int chatLinesSize(List<ChatLine> list) {
    return (ChatModifier.getInstance().isOn() && ((Boolean)(ChatModifier.getInstance()).infinite.getValue()).booleanValue()) ? -2147483647 : list.size();
  }
}
