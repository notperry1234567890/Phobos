//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import java.util.Random;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.INSTANT));
  
  public Setting<Boolean> strafeJump = register(new Setting("Jump", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.INSTANT)));
  
  public Setting<Boolean> noShake = register(new Setting("NoShake", Boolean.valueOf(true), v -> (this.mode.getValue() != Mode.INSTANT)));
  
  public Setting<Boolean> useTimer = register(new Setting("UseTimer", Boolean.valueOf(false), v -> (this.mode.getValue() != Mode.INSTANT)));
  
  private static Speed INSTANCE = new Speed();
  
  private double highChainVal = 0.0D;
  
  private double lowChainVal = 0.0D;
  
  private boolean oneTime = false;
  
  public double startY = 0.0D;
  
  public boolean antiShake = false;
  
  private double bounceHeight = 0.4D;
  
  private float move = 0.26F;
  
  public Speed() {
    super("Speed", "Makes you faster", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Speed getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Speed(); 
    return INSTANCE;
  }
  
  public enum Mode {
    INSTANT, ONGROUND, ACCEL, BOOST;
  }
  
  private boolean shouldReturn() {
    return (Phobos.moduleManager.isModuleEnabled("Freecam") || Phobos.moduleManager.isModuleEnabled("Phase") || Phobos.moduleManager.isModuleEnabled("ElytraFlight") || Phobos.moduleManager.isModuleEnabled("Strafe") || Phobos.moduleManager.isModuleEnabled("Flight"));
  }
  
  public void onUpdate() {
    if (shouldReturn() || mc.player.isSneaking() || mc.player.isInWater() || mc.player.isInLava())
      return; 
    switch ((Mode)this.mode.getValue()) {
      case BOOST:
        doBoost();
        break;
      case ACCEL:
        doAccel();
        break;
      case ONGROUND:
        doOnground();
        break;
    } 
  }
  
  private void doBoost() {
    this.bounceHeight = 0.4D;
    this.move = 0.26F;
    if (mc.player.onGround)
      this.startY = mc.player.posY; 
    if (EntityUtil.getEntitySpeed((Entity)mc.player) <= 1.0D) {
      this.lowChainVal = 1.0D;
      this.highChainVal = 1.0D;
    } 
    if (EntityUtil.isEntityMoving((Entity)mc.player) && !mc.player.collidedHorizontally && !BlockUtil.isBlockAboveEntitySolid((Entity)mc.player) && BlockUtil.isBlockBelowEntitySolid((Entity)mc.player)) {
      this.oneTime = true;
      this.antiShake = (((Boolean)this.noShake.getValue()).booleanValue() && mc.player.getRidingEntity() == null);
      Random random = new Random();
      boolean rnd = random.nextBoolean();
      if (mc.player.posY >= this.startY + this.bounceHeight) {
        mc.player.motionY = -this.bounceHeight;
        this.lowChainVal++;
        if (this.lowChainVal == 1.0D)
          this.move = 0.075F; 
        if (this.lowChainVal == 2.0D)
          this.move = 0.15F; 
        if (this.lowChainVal == 3.0D)
          this.move = 0.175F; 
        if (this.lowChainVal == 4.0D)
          this.move = 0.2F; 
        if (this.lowChainVal == 5.0D)
          this.move = 0.225F; 
        if (this.lowChainVal == 6.0D)
          this.move = 0.25F; 
        if (this.lowChainVal >= 7.0D)
          this.move = 0.27895F; 
        if (((Boolean)this.useTimer.getValue()).booleanValue())
          Phobos.timerManager.setTimer(1.0F); 
      } 
      if (mc.player.posY == this.startY) {
        mc.player.motionY = this.bounceHeight;
        this.highChainVal++;
        if (this.highChainVal == 1.0D)
          this.move = 0.075F; 
        if (this.highChainVal == 2.0D)
          this.move = 0.175F; 
        if (this.highChainVal == 3.0D)
          this.move = 0.325F; 
        if (this.highChainVal == 4.0D)
          this.move = 0.375F; 
        if (this.highChainVal == 5.0D)
          this.move = 0.4F; 
        if (this.highChainVal >= 6.0D)
          this.move = 0.43395F; 
        if (((Boolean)this.useTimer.getValue()).booleanValue())
          if (rnd) {
            Phobos.timerManager.setTimer(1.3F);
          } else {
            Phobos.timerManager.setTimer(1.0F);
          }  
      } 
      EntityUtil.moveEntityStrafe(this.move, (Entity)mc.player);
    } else {
      if (this.oneTime) {
        mc.player.motionY = -0.1D;
        this.oneTime = false;
      } 
      this.highChainVal = 0.0D;
      this.lowChainVal = 0.0D;
      this.antiShake = false;
      speedOff();
    } 
  }
  
  private void doAccel() {
    this.bounceHeight = 0.4D;
    this.move = 0.26F;
    if (mc.player.onGround)
      this.startY = mc.player.posY; 
    if (EntityUtil.getEntitySpeed((Entity)mc.player) <= 1.0D) {
      this.lowChainVal = 1.0D;
      this.highChainVal = 1.0D;
    } 
    if (EntityUtil.isEntityMoving((Entity)mc.player) && !mc.player.collidedHorizontally && !BlockUtil.isBlockAboveEntitySolid((Entity)mc.player) && BlockUtil.isBlockBelowEntitySolid((Entity)mc.player)) {
      this.oneTime = true;
      this.antiShake = (((Boolean)this.noShake.getValue()).booleanValue() && mc.player.getRidingEntity() == null);
      Random random = new Random();
      boolean rnd = random.nextBoolean();
      if (mc.player.posY >= this.startY + this.bounceHeight) {
        mc.player.motionY = -this.bounceHeight;
        this.lowChainVal++;
        if (this.lowChainVal == 1.0D)
          this.move = 0.075F; 
        if (this.lowChainVal == 2.0D)
          this.move = 0.175F; 
        if (this.lowChainVal == 3.0D)
          this.move = 0.275F; 
        if (this.lowChainVal == 4.0D)
          this.move = 0.35F; 
        if (this.lowChainVal == 5.0D)
          this.move = 0.375F; 
        if (this.lowChainVal == 6.0D)
          this.move = 0.4F; 
        if (this.lowChainVal == 7.0D)
          this.move = 0.425F; 
        if (this.lowChainVal == 8.0D)
          this.move = 0.45F; 
        if (this.lowChainVal == 9.0D)
          this.move = 0.475F; 
        if (this.lowChainVal == 10.0D)
          this.move = 0.5F; 
        if (this.lowChainVal == 11.0D)
          this.move = 0.5F; 
        if (this.lowChainVal == 12.0D)
          this.move = 0.525F; 
        if (this.lowChainVal == 13.0D)
          this.move = 0.525F; 
        if (this.lowChainVal == 14.0D)
          this.move = 0.535F; 
        if (this.lowChainVal == 15.0D)
          this.move = 0.535F; 
        if (this.lowChainVal == 16.0D)
          this.move = 0.545F; 
        if (this.lowChainVal >= 17.0D)
          this.move = 0.545F; 
        if (((Boolean)this.useTimer.getValue()).booleanValue())
          Phobos.timerManager.setTimer(1.0F); 
      } 
      if (mc.player.posY == this.startY) {
        mc.player.motionY = this.bounceHeight;
        this.highChainVal++;
        if (this.highChainVal == 1.0D)
          this.move = 0.075F; 
        if (this.highChainVal == 2.0D)
          this.move = 0.175F; 
        if (this.highChainVal == 3.0D)
          this.move = 0.375F; 
        if (this.highChainVal == 4.0D)
          this.move = 0.6F; 
        if (this.highChainVal == 5.0D)
          this.move = 0.775F; 
        if (this.highChainVal == 6.0D)
          this.move = 0.825F; 
        if (this.highChainVal == 7.0D)
          this.move = 0.875F; 
        if (this.highChainVal == 8.0D)
          this.move = 0.925F; 
        if (this.highChainVal == 9.0D)
          this.move = 0.975F; 
        if (this.highChainVal == 10.0D)
          this.move = 1.05F; 
        if (this.highChainVal == 11.0D)
          this.move = 1.1F; 
        if (this.highChainVal == 12.0D)
          this.move = 1.1F; 
        if (this.highChainVal == 13.0D)
          this.move = 1.15F; 
        if (this.highChainVal == 14.0D)
          this.move = 1.15F; 
        if (this.highChainVal == 15.0D)
          this.move = 1.175F; 
        if (this.highChainVal == 16.0D)
          this.move = 1.175F; 
        if (this.highChainVal >= 17.0D)
          this.move = 1.175F; 
        if (((Boolean)this.useTimer.getValue()).booleanValue())
          if (rnd) {
            Phobos.timerManager.setTimer(1.3F);
          } else {
            Phobos.timerManager.setTimer(1.0F);
          }  
      } 
      EntityUtil.moveEntityStrafe(this.move, (Entity)mc.player);
    } else {
      if (this.oneTime) {
        mc.player.motionY = -0.1D;
        this.oneTime = false;
      } 
      this.antiShake = false;
      this.highChainVal = 0.0D;
      this.lowChainVal = 0.0D;
      speedOff();
    } 
  }
  
  private void doOnground() {
    this.bounceHeight = 0.4D;
    this.move = 0.26F;
    if (mc.player.onGround)
      this.startY = mc.player.posY; 
    if (EntityUtil.getEntitySpeed((Entity)mc.player) <= 1.0D) {
      this.lowChainVal = 1.0D;
      this.highChainVal = 1.0D;
    } 
    if (EntityUtil.isEntityMoving((Entity)mc.player) && !mc.player.collidedHorizontally && !BlockUtil.isBlockAboveEntitySolid((Entity)mc.player) && BlockUtil.isBlockBelowEntitySolid((Entity)mc.player)) {
      this.oneTime = true;
      this.antiShake = (((Boolean)this.noShake.getValue()).booleanValue() && mc.player.getRidingEntity() == null);
      Random random = new Random();
      boolean rnd = random.nextBoolean();
      if (mc.player.posY >= this.startY + this.bounceHeight) {
        mc.player.motionY = -this.bounceHeight;
        this.lowChainVal++;
        if (this.lowChainVal == 1.0D)
          this.move = 0.075F; 
        if (this.lowChainVal == 2.0D)
          this.move = 0.175F; 
        if (this.lowChainVal == 3.0D)
          this.move = 0.275F; 
        if (this.lowChainVal == 4.0D)
          this.move = 0.35F; 
        if (this.lowChainVal == 5.0D)
          this.move = 0.375F; 
        if (this.lowChainVal == 6.0D)
          this.move = 0.4F; 
        if (this.lowChainVal == 7.0D)
          this.move = 0.425F; 
        if (this.lowChainVal == 8.0D)
          this.move = 0.45F; 
        if (this.lowChainVal == 9.0D)
          this.move = 0.475F; 
        if (this.lowChainVal == 10.0D)
          this.move = 0.5F; 
        if (this.lowChainVal == 11.0D)
          this.move = 0.5F; 
        if (this.lowChainVal == 12.0D)
          this.move = 0.525F; 
        if (this.lowChainVal == 13.0D)
          this.move = 0.525F; 
        if (this.lowChainVal == 14.0D)
          this.move = 0.535F; 
        if (this.lowChainVal == 15.0D)
          this.move = 0.535F; 
        if (this.lowChainVal == 16.0D)
          this.move = 0.545F; 
        if (this.lowChainVal >= 17.0D)
          this.move = 0.545F; 
        if (((Boolean)this.useTimer.getValue()).booleanValue())
          Phobos.timerManager.setTimer(1.0F); 
      } 
      if (mc.player.posY == this.startY) {
        mc.player.motionY = this.bounceHeight;
        this.highChainVal++;
        if (this.highChainVal == 1.0D)
          this.move = 0.075F; 
        if (this.highChainVal == 2.0D)
          this.move = 0.175F; 
        if (this.highChainVal == 3.0D)
          this.move = 0.375F; 
        if (this.highChainVal == 4.0D)
          this.move = 0.6F; 
        if (this.highChainVal == 5.0D)
          this.move = 0.775F; 
        if (this.highChainVal == 6.0D)
          this.move = 0.825F; 
        if (this.highChainVal == 7.0D)
          this.move = 0.875F; 
        if (this.highChainVal == 8.0D)
          this.move = 0.925F; 
        if (this.highChainVal == 9.0D)
          this.move = 0.975F; 
        if (this.highChainVal == 10.0D)
          this.move = 1.05F; 
        if (this.highChainVal == 11.0D)
          this.move = 1.1F; 
        if (this.highChainVal == 12.0D)
          this.move = 1.1F; 
        if (this.highChainVal == 13.0D)
          this.move = 1.15F; 
        if (this.highChainVal == 14.0D)
          this.move = 1.15F; 
        if (this.highChainVal == 15.0D)
          this.move = 1.175F; 
        if (this.highChainVal == 16.0D)
          this.move = 1.175F; 
        if (this.highChainVal >= 17.0D)
          this.move = 1.2F; 
        if (((Boolean)this.useTimer.getValue()).booleanValue())
          if (rnd) {
            Phobos.timerManager.setTimer(1.3F);
          } else {
            Phobos.timerManager.setTimer(1.0F);
          }  
      } 
      EntityUtil.moveEntityStrafe(this.move, (Entity)mc.player);
    } else {
      if (this.oneTime) {
        mc.player.motionY = -0.1D;
        this.oneTime = false;
      } 
      this.antiShake = false;
      this.highChainVal = 0.0D;
      this.lowChainVal = 0.0D;
      speedOff();
    } 
  }
  
  public void onDisable() {
    if (this.mode.getValue() == Mode.ONGROUND || this.mode.getValue() == Mode.BOOST)
      mc.player.motionY = -0.1D; 
    Phobos.timerManager.setTimer(1.0F);
    this.highChainVal = 0.0D;
    this.lowChainVal = 0.0D;
    this.antiShake = false;
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting().equals(this.mode) && 
      this.mode.getPlannedValue() == Mode.INSTANT)
      mc.player.motionY = -0.1D; 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  @SubscribeEvent
  public void onMode(MoveEvent event) {
    if (!shouldReturn() && event.getStage() == 0 && this.mode.getValue() == Mode.INSTANT && !nullCheck() && !mc.player.isSneaking() && !mc.player.isInWater() && !mc.player.isInLava() && (
      mc.player.movementInput.moveForward != 0.0F || mc.player.movementInput.moveStrafe != 0.0F)) {
      if (mc.player.onGround && ((Boolean)this.strafeJump.getValue()).booleanValue()) {
        mc.player.motionY = 0.4D;
        event.setY(0.4D);
      } 
      MovementInput movementInput = mc.player.movementInput;
      float moveForward = movementInput.moveForward;
      float moveStrafe = movementInput.moveStrafe;
      float rotationYaw = mc.player.rotationYaw;
      if (moveForward == 0.0D && moveStrafe == 0.0D) {
        event.setX(0.0D);
        event.setZ(0.0D);
      } else {
        if (moveForward != 0.0D) {
          if (moveStrafe > 0.0D) {
            rotationYaw += ((moveForward > 0.0D) ? -45 : 45);
          } else if (moveStrafe < 0.0D) {
            rotationYaw += ((moveForward > 0.0D) ? 45 : -45);
          } 
          moveStrafe = 0.0F;
          moveForward = (moveForward == 0.0F) ? moveForward : ((moveForward > 0.0D) ? 1.0F : -1.0F);
        } 
        moveStrafe = (moveStrafe == 0.0F) ? moveStrafe : ((moveStrafe > 0.0D) ? 1.0F : -1.0F);
        event.setX(moveForward * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians((rotationYaw + 90.0F))) + moveStrafe * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians((rotationYaw + 90.0F))));
        event.setZ(moveForward * EntityUtil.getMaxSpeed() * Math.sin(Math.toRadians((rotationYaw + 90.0F))) - moveStrafe * EntityUtil.getMaxSpeed() * Math.cos(Math.toRadians((rotationYaw + 90.0F))));
      } 
    } 
  }
  
  private void speedOff() {
    float yaw = (float)Math.toRadians(mc.player.rotationYaw);
    if (BlockUtil.isBlockAboveEntitySolid((Entity)mc.player)) {
      if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
        mc.player.motionX -= MathUtil.sin(yaw) * 0.15D;
        mc.player.motionZ += MathUtil.cos(yaw) * 0.15D;
      } 
    } else if (mc.player.collidedHorizontally) {
      if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
        mc.player.motionX -= MathUtil.sin(yaw) * 0.03D;
        mc.player.motionZ += MathUtil.cos(yaw) * 0.03D;
      } 
    } else if (!BlockUtil.isBlockBelowEntitySolid((Entity)mc.player)) {
      if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
        mc.player.motionX -= MathUtil.sin(yaw) * 0.03D;
        mc.player.motionZ += MathUtil.cos(yaw) * 0.03D;
      } 
    } else {
      mc.player.motionX = 0.0D;
      mc.player.motionZ = 0.0D;
    } 
  }
}
