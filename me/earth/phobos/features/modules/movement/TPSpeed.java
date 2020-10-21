//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import java.util.Objects;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TPSpeed extends Module {
  private Setting<Mode> mode = register(new Setting("Mode", Mode.NORMAL));
  
  private Setting<Double> speed = register(new Setting("Speed", Double.valueOf(0.25D), Double.valueOf(0.1D), Double.valueOf(10.0D)));
  
  private Setting<Double> fallSpeed = register(new Setting("FallSpeed", Double.valueOf(0.25D), Double.valueOf(0.1D), Double.valueOf(10.0D), v -> (this.mode.getValue() == Mode.STEP)));
  
  private Setting<Boolean> turnOff = register(new Setting("Off", Boolean.valueOf(false)));
  
  private Setting<Integer> tpLimit = register(new Setting("Limit", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(10), v -> ((Boolean)this.turnOff.getValue()).booleanValue(), "Turn it off."));
  
  private int tps = 0;
  
  private double[] selectedPositions = new double[] { 0.42D, 0.75D, 1.0D };
  
  public TPSpeed() {
    super("TpSpeed", "Teleports you.", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onEnable() {
    this.tps = 0;
  }
  
  @SubscribeEvent
  public void onUpdatePlayerWalking(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0)
      return; 
    if (this.mode.getValue() == Mode.NORMAL) {
      if (((Boolean)this.turnOff.getValue()).booleanValue() && this.tps >= ((Integer)this.tpLimit.getValue()).intValue()) {
        disable();
        return;
      } 
      if (mc.player.moveForward != 0.0F || (mc.player.moveStrafing != 0.0F && mc.player.onGround)) {
        for (double x = 0.0625D; x < ((Double)this.speed.getValue()).doubleValue(); x += 0.262D) {
          double[] dir = MathUtil.directionSpeed(x);
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY, mc.player.posZ + dir[1], mc.player.onGround));
        } 
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, 0.0D, mc.player.posZ + mc.player.motionZ, mc.player.onGround));
        this.tps++;
      } 
    } else if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
      double pawnY = 0.0D;
      double[] lastStep = MathUtil.directionSpeed(0.262D);
      double x;
      for (x = 0.0625D; x < ((Double)this.speed.getValue()).doubleValue(); x += 0.262D) {
        double[] dir = MathUtil.directionSpeed(x);
        AxisAlignedBB bb = ((AxisAlignedBB)Objects.<AxisAlignedBB>requireNonNull(mc.player.getEntityBoundingBox())).offset(dir[0], pawnY, dir[1]);
        while (collidesHorizontally(bb)) {
          for (double position : this.selectedPositions)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir[0] - lastStep[0], mc.player.posY + pawnY + position, mc.player.posZ + dir[1] - lastStep[1], true)); 
          pawnY++;
          bb = ((AxisAlignedBB)Objects.<AxisAlignedBB>requireNonNull(mc.player.getEntityBoundingBox())).offset(dir[0], pawnY, dir[1]);
        } 
        if (!mc.world.checkBlockCollision(bb.grow(0.0125D, 0.0D, 0.0125D).offset(0.0D, -1.0D, 0.0D))) {
          double i;
          for (i = 0.0D; i <= 1.0D; i += ((Double)this.fallSpeed.getValue()).doubleValue())
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY + pawnY - i, mc.player.posZ + dir[1], true)); 
          pawnY--;
        } 
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + dir[0], mc.player.posY + pawnY, mc.player.posZ + dir[1], mc.player.onGround));
      } 
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX + mc.player.motionX, 0.0D, mc.player.posZ + mc.player.motionZ, mc.player.onGround));
    } 
  }
  
  private static boolean collidesHorizontally(AxisAlignedBB bb) {
    if (mc.world.collidesWithAnyBlock(bb)) {
      Vec3d center = bb.getCenter();
      BlockPos blockpos = new BlockPos(center.x, bb.minY, center.z);
      return (mc.world.isBlockFullCube(blockpos.west()) || mc.world.isBlockFullCube(blockpos.east()) || mc.world.isBlockFullCube(blockpos.north()) || mc.world.isBlockFullCube(blockpos.south()) || mc.world.isBlockFullCube(blockpos));
    } 
    return false;
  }
  
  public enum Mode {
    NORMAL, STEP;
  }
}
