//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import com.mojang.authlib.GameProfile;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.misc.BetterPortals;
import me.earth.phobos.features.modules.movement.Phase;
import me.earth.phobos.features.modules.movement.TestPhase;
import me.earth.phobos.features.modules.player.TpsSync;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer extends EntityLivingBase {
  public MixinEntityPlayer(World worldIn, GameProfile gameProfileIn) {
    super(worldIn);
  }
  
  @Inject(method = {"getCooldownPeriod"}, at = {@At("HEAD")}, cancellable = true)
  private void getCooldownPeriodHook(CallbackInfoReturnable<Float> callbackInfoReturnable) {
    if (TpsSync.getInstance().isOn() && ((Boolean)(TpsSync.getInstance()).attack.getValue()).booleanValue())
      callbackInfoReturnable.setReturnValue(Float.valueOf((float)(1.0D / ((EntityPlayer)EntityPlayer.class.cast(this)).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * 20.0D * Phobos.serverManager.getTpsFactor()))); 
  }
  
  @ModifyConstant(method = {"getPortalCooldown"}, constant = {@Constant(intValue = 10)})
  private int getPortalCooldownHook(int cooldown) {
    int time = cooldown;
    if (BetterPortals.getInstance().isOn() && ((Boolean)(BetterPortals.getInstance()).fastPortal.getValue()).booleanValue())
      time = ((Integer)(BetterPortals.getInstance()).cooldown.getValue()).intValue(); 
    return time;
  }
  
  @Inject(method = {"isEntityInsideOpaqueBlock"}, at = {@At("HEAD")}, cancellable = true)
  private void isEntityInsideOpaqueBlockHook(CallbackInfoReturnable<Boolean> info) {
    if (Phase.getInstance().isOn() && (Phase.getInstance()).type.getValue() != Phase.PacketFlyMode.NONE) {
      info.setReturnValue(Boolean.valueOf(false));
    } else if (TestPhase.getInstance().isOn()) {
      info.setReturnValue(Boolean.valueOf(false));
    } 
  }
}
