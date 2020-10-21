//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.event.events.JesusEvent;
import me.earth.phobos.features.modules.player.LiquidInteract;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockLiquid.class})
public class MixinBlockLiquid extends Block {
  protected MixinBlockLiquid(Material materialIn) {
    super(materialIn);
  }
  
  @Inject(method = {"getCollisionBoundingBox"}, at = {@At("HEAD")}, cancellable = true)
  public void getCollisionBoundingBoxHook(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> info) {
    JesusEvent event = new JesusEvent(0, pos);
    MinecraftForge.EVENT_BUS.post((Event)event);
    if (event.isCanceled())
      info.setReturnValue(event.getBoundingBox()); 
  }
  
  @Inject(method = {"canCollideCheck"}, at = {@At("HEAD")}, cancellable = true)
  public void canCollideCheckHook(IBlockState blockState, boolean hitIfLiquid, CallbackInfoReturnable<Boolean> info) {
    info.setReturnValue(Boolean.valueOf(((hitIfLiquid && ((Integer)blockState.getValue((IProperty)BlockLiquid.LEVEL)).intValue() == 0) || LiquidInteract.getInstance().isOn())));
  }
}
