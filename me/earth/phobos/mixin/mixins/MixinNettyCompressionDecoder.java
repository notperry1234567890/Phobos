package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.misc.Bypass;
import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({NettyCompressionDecoder.class})
public abstract class MixinNettyCompressionDecoder {
  @ModifyConstant(method = {"decode"}, constant = {@Constant(intValue = 2097152)})
  private int decodeHook(int n) {
    if (Bypass.getInstance().isOn() && ((Boolean)(Bypass.getInstance()).packets.getValue()).booleanValue() && ((Boolean)(Bypass.getInstance()).noLimit.getValue()).booleanValue())
      return Integer.MAX_VALUE; 
    return n;
  }
}
