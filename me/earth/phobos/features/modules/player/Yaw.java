//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import java.util.Objects;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Yaw extends Module {
  public Setting<Boolean> lockYaw;
  
  public Setting<Boolean> byDirection;
  
  public Setting<Direction> direction;
  
  public Setting<Integer> yaw;
  
  public Setting<Boolean> lockPitch;
  
  public Setting<Integer> pitch;
  
  public Yaw() {
    super("Yaw", "Locks your yaw", Module.Category.PLAYER, true, false, false);
    this.lockYaw = register(new Setting("LockYaw", Boolean.valueOf(false)));
    this.byDirection = register(new Setting("ByDirection", Boolean.valueOf(false)));
    this.direction = register(new Setting("Direction", Direction.NORTH, v -> ((Boolean)this.byDirection.getValue()).booleanValue()));
    this.yaw = register(new Setting("Yaw", Integer.valueOf(0), Integer.valueOf(-180), Integer.valueOf(180), v -> !((Boolean)this.byDirection.getValue()).booleanValue()));
    this.lockPitch = register(new Setting("LockPitch", Boolean.valueOf(false)));
    this.pitch = register(new Setting("Pitch", Integer.valueOf(0), Integer.valueOf(-90), Integer.valueOf(90)));
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (((Boolean)this.lockYaw.getValue()).booleanValue())
      if (((Boolean)this.byDirection.getValue()).booleanValue()) {
        switch ((Direction)this.direction.getValue()) {
          case NORTH:
            setYaw(180);
            break;
          case NE:
            setYaw(225);
            break;
          case EAST:
            setYaw(270);
            break;
          case SE:
            setYaw(315);
            break;
          case SOUTH:
            setYaw(0);
            break;
          case SW:
            setYaw(45);
            break;
          case WEST:
            setYaw(90);
            break;
          case NW:
            setYaw(135);
            break;
        } 
      } else {
        setYaw(((Integer)this.yaw.getValue()).intValue());
      }  
    if (((Boolean)this.lockPitch.getValue()).booleanValue()) {
      if (mc.player.isRiding())
        ((Entity)Objects.requireNonNull((T)mc.player.getRidingEntity())).rotationPitch = ((Integer)this.pitch.getValue()).intValue(); 
      mc.player.rotationPitch = ((Integer)this.pitch.getValue()).intValue();
    } 
  }
  
  private void setYaw(int yaw) {
    if (mc.player.isRiding())
      ((Entity)Objects.requireNonNull((T)mc.player.getRidingEntity())).rotationYaw = yaw; 
    mc.player.rotationYaw = yaw;
  }
  
  public enum Direction {
    NORTH, NE, EAST, SE, SOUTH, SW, WEST, NW;
  }
}
