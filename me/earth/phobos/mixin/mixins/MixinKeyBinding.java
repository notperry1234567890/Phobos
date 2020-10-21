//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.event.events.KeyEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({KeyBinding.class})
public class MixinKeyBinding {
  @Shadow
  private boolean pressed;
  
  @Inject(method = {"isKeyDown"}, at = {@At("RETURN")}, cancellable = true)
  private void isKeyDown(CallbackInfoReturnable<Boolean> info) {
    KeyEvent event = new KeyEvent(0, ((Boolean)info.getReturnValue()).booleanValue(), this.pressed);
    MinecraftForge.EVENT_BUS.post((Event)event);
    info.setReturnValue(Boolean.valueOf(event.info));
  }
}
