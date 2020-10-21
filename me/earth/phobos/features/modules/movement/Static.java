//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class Static extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.ROOF));
  
  private final Setting<Boolean> disabler = register(new Setting("Disable", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.ROOF)));
  
  private final Setting<Boolean> ySpeed = register(new Setting("YSpeed", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.STATIC)));
  
  private final Setting<Float> speed = register(new Setting("Speed", Float.valueOf(0.1F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (((Boolean)this.ySpeed.getValue()).booleanValue() && this.mode.getValue() == Mode.STATIC)));
  
  private final Setting<Float> height = register(new Setting("Height", Float.valueOf(3.0F), Float.valueOf(0.0F), Float.valueOf(256.0F), v -> (this.mode.getValue() == Mode.NOVOID)));
  
  public Static() {
    super("Static", "Stops any movement. Glitches you up.", Module.Category.MOVEMENT, false, false, false);
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    switch ((Mode)this.mode.getValue()) {
      case STATIC:
        mc.player.capabilities.isFlying = false;
        mc.player.motionX = 0.0D;
        mc.player.motionY = 0.0D;
        mc.player.motionZ = 0.0D;
        if (((Boolean)this.ySpeed.getValue()).booleanValue()) {
          mc.player.jumpMovementFactor = ((Float)this.speed.getValue()).floatValue();
          if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.player.motionY += ((Float)this.speed.getValue()).floatValue(); 
          if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.motionY -= ((Float)this.speed.getValue()).floatValue(); 
        } 
        break;
      case ROOF:
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, 10000.0D, mc.player.posZ, mc.player.onGround));
        if (((Boolean)this.disabler.getValue()).booleanValue())
          disable(); 
        break;
      case NOVOID:
        if (!mc.player.noClip && mc.player.posY <= ((Float)this.height.getValue()).floatValue()) {
          RayTraceResult trace = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(mc.player.posX, 0.0D, mc.player.posZ), false, false, false);
          if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
            return; 
          if (Phobos.moduleManager.isModuleEnabled(Phase.class) || Phobos.moduleManager.isModuleEnabled(Flight.class))
            return; 
          mc.player.setVelocity(0.0D, 0.0D, 0.0D);
          if (mc.player.getRidingEntity() != null)
            mc.player.getRidingEntity().setVelocity(0.0D, 0.0D, 0.0D); 
        } 
        break;
    } 
  }
  
  public String getDisplayInfo() {
    if (this.mode.getValue() == Mode.ROOF)
      return "Roof"; 
    if (this.mode.getValue() == Mode.NOVOID)
      return "NoVoid"; 
    return null;
  }
  
  public enum Mode {
    STATIC, ROOF, NOVOID;
  }
}
