//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.NCP));
  
  private final Setting<Boolean> limiter = register(new Setting("SetGround", Boolean.valueOf(true)));
  
  private final Setting<Boolean> bhop2 = register(new Setting("Hop", Boolean.valueOf(true)));
  
  private final Setting<Boolean> limiter2 = register(new Setting("Bhop", Boolean.valueOf(false)));
  
  private final Setting<Boolean> noLag = register(new Setting("NoLag", Boolean.valueOf(false)));
  
  private final Setting<Integer> specialMoveSpeed = register(new Setting("Speed", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(150)));
  
  private final Setting<Integer> potionSpeed = register(new Setting("Speed1", Integer.valueOf(130), Integer.valueOf(0), Integer.valueOf(150)));
  
  private final Setting<Integer> potionSpeed2 = register(new Setting("Speed2", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(150)));
  
  private final Setting<Integer> dFactor = register(new Setting("DFactor", Integer.valueOf(159), Integer.valueOf(100), Integer.valueOf(200)));
  
  private final Setting<Integer> acceleration = register(new Setting("Accel", Integer.valueOf(2149), Integer.valueOf(1000), Integer.valueOf(2500)));
  
  private final Setting<Float> speedLimit = register(new Setting("SpeedLimit", Float.valueOf(35.0F), Float.valueOf(20.0F), Float.valueOf(60.0F)));
  
  private final Setting<Float> speedLimit2 = register(new Setting("SpeedLimit2", Float.valueOf(60.0F), Float.valueOf(20.0F), Float.valueOf(60.0F)));
  
  private final Setting<Integer> yOffset = register(new Setting("YOffset", Integer.valueOf(400), Integer.valueOf(350), Integer.valueOf(500)));
  
  private final Setting<Boolean> potion = register(new Setting("Potion", Boolean.valueOf(false)));
  
  private final Setting<Boolean> wait = register(new Setting("Wait", Boolean.valueOf(true)));
  
  private final Setting<Boolean> hopWait = register(new Setting("HopWait", Boolean.valueOf(true)));
  
  private final Setting<Integer> startStage = register(new Setting("Stage", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(4)));
  
  private final Setting<Boolean> setPos = register(new Setting("SetPos", Boolean.valueOf(true)));
  
  private final Setting<Boolean> setNull = register(new Setting("SetNull", Boolean.valueOf(false)));
  
  private final Setting<Integer> setGroundLimit = register(new Setting("GroundLimit", Integer.valueOf(138), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private final Setting<Integer> groundFactor = register(new Setting("GroundFactor", Integer.valueOf(13), Integer.valueOf(0), Integer.valueOf(50)));
  
  private final Setting<Integer> step = register(new Setting("SetStep", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(2), v -> (this.mode.getValue() == Mode.BHOP)));
  
  private int stage = 1;
  
  private double moveSpeed;
  
  private double lastDist;
  
  private int cooldownHops = 0;
  
  private boolean waitForGround = false;
  
  private Timer timer = new Timer();
  
  private int hops = 0;
  
  public Strafe() {
    super("Strafe", "AirControl etc.", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onEnable() {
    if (!mc.player.onGround)
      this.waitForGround = true; 
    this.hops = 0;
    this.timer.reset();
    this.moveSpeed = getBaseMoveSpeed();
  }
  
  public void onDisable() {
    this.hops = 0;
    this.moveSpeed = 0.0D;
    this.stage = ((Integer)this.startStage.getValue()).intValue();
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0)
      this.lastDist = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ)); 
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (event.getStage() != 0 || shouldReturn())
      return; 
    if (!mc.player.onGround) {
      if (((Boolean)this.wait.getValue()).booleanValue() && this.waitForGround)
        return; 
    } else {
      this.waitForGround = false;
    } 
    if (this.mode.getValue() == Mode.NCP) {
      doNCP(event);
    } else if (this.mode.getValue() == Mode.BHOP) {
      float moveForward = mc.player.movementInput.moveForward;
      float moveStrafe = mc.player.movementInput.moveStrafe;
      float rotationYaw = mc.player.rotationYaw;
      if (((Integer)this.step.getValue()).intValue() == 1)
        mc.player.stepHeight = 0.6F; 
      if (((Boolean)this.limiter2.getValue()).booleanValue() && mc.player.onGround && Phobos.speedManager.getSpeedKpH() < ((Float)this.speedLimit2.getValue()).floatValue())
        this.stage = 2; 
      if (((Boolean)this.limiter.getValue()).booleanValue() && round(mc.player.posY - (int)mc.player.posY, 3) == round(((Integer)this.setGroundLimit.getValue()).intValue() / 1000.0D, 3))
        if (((Boolean)this.setNull.getValue()).booleanValue()) {
          mc.player.motionY = 0.0D;
        } else {
          mc.player.motionY -= ((Integer)this.groundFactor.getValue()).intValue() / 100.0D;
          event.setY(event.getY() - ((Integer)this.groundFactor.getValue()).intValue() / 100.0D);
          if (((Boolean)this.setPos.getValue()).booleanValue())
            mc.player.posY -= ((Integer)this.groundFactor.getValue()).intValue() / 100.0D; 
        }  
      if (this.stage == 1 && EntityUtil.isMoving()) {
        this.stage = 2;
        this.moveSpeed = getMultiplier() * getBaseMoveSpeed() - 0.01D;
      } else if (this.stage == 2 && EntityUtil.isMoving()) {
        this.stage = 3;
        mc.player.motionY = ((Integer)this.yOffset.getValue()).intValue() / 1000.0D;
        event.setY(((Integer)this.yOffset.getValue()).intValue() / 1000.0D);
        if (this.cooldownHops > 0)
          this.cooldownHops--; 
        this.hops++;
        this.moveSpeed *= ((Integer)this.acceleration.getValue()).intValue() / 1000.0D;
      } else if (this.stage == 3) {
        this.stage = 4;
        double difference = 0.66D * (this.lastDist - getBaseMoveSpeed());
        this.moveSpeed = this.lastDist - difference;
      } else {
        if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || (mc.player.collidedVertically && this.stage > 0))
          if (((Boolean)this.bhop2.getValue()).booleanValue() && Phobos.speedManager.getSpeedKpH() >= ((Float)this.speedLimit.getValue()).floatValue()) {
            this.stage = 0;
          } else {
            this.stage = (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) ? 1 : 0;
          }  
        this.moveSpeed = this.lastDist - this.lastDist / ((Integer)this.dFactor.getValue()).intValue();
      } 
      this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
      if (((Boolean)this.hopWait.getValue()).booleanValue() && ((Boolean)this.limiter2.getValue()).booleanValue() && this.hops < 2)
        this.moveSpeed = EntityUtil.getMaxSpeed(); 
      if (moveForward == 0.0F && moveStrafe == 0.0F) {
        event.setX(0.0D);
        event.setZ(0.0D);
        this.moveSpeed = 0.0D;
      } else if (moveForward != 0.0F) {
        if (moveStrafe >= 1.0F) {
          rotationYaw += (moveForward > 0.0F) ? -45.0F : 45.0F;
          moveStrafe = 0.0F;
        } else if (moveStrafe <= -1.0F) {
          rotationYaw += (moveForward > 0.0F) ? 45.0F : -45.0F;
          moveStrafe = 0.0F;
        } 
        if (moveForward > 0.0F) {
          moveForward = 1.0F;
        } else if (moveForward < 0.0F) {
          moveForward = -1.0F;
        } 
      } 
      double motionX = Math.cos(Math.toRadians((rotationYaw + 90.0F)));
      double motionZ = Math.sin(Math.toRadians((rotationYaw + 90.0F)));
      if (this.cooldownHops == 0) {
        event.setX(moveForward * this.moveSpeed * motionX + moveStrafe * this.moveSpeed * motionZ);
        event.setZ(moveForward * this.moveSpeed * motionZ - moveStrafe * this.moveSpeed * motionX);
      } 
      if (((Integer)this.step.getValue()).intValue() == 2)
        mc.player.stepHeight = 0.6F; 
      if (moveForward == 0.0F && moveStrafe == 0.0F) {
        this.timer.reset();
        event.setX(0.0D);
        event.setZ(0.0D);
      } 
    } 
  }
  
  private void doNCP(MoveEvent event) {
    double motionY;
    if (!((Boolean)this.limiter.getValue()).booleanValue() && mc.player.onGround)
      this.stage = 2; 
    switch (this.stage) {
      case 0:
        this.stage++;
        this.lastDist = 0.0D;
        break;
      case 2:
        motionY = 0.40123128D;
        if ((mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
          if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
            motionY += ((mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F); 
          event.setY(mc.player.motionY = motionY);
          this.moveSpeed *= 2.149D;
        } 
        break;
      case 3:
        this;
        this.moveSpeed = this.lastDist - 0.76D * (this.lastDist - getBaseMoveSpeed());
        break;
      default:
        if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || (mc.player.collidedVertically && this.stage > 0))
          if (((Boolean)this.bhop2.getValue()).booleanValue() && Phobos.speedManager.getSpeedKpH() >= ((Float)this.speedLimit.getValue()).floatValue()) {
            this.stage = 0;
          } else {
            this.stage = (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) ? 1 : 0;
          }  
        this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
        break;
    } 
    this;
    this.moveSpeed = Math.max(this.moveSpeed, getBaseMoveSpeed());
    double forward = mc.player.movementInput.moveForward;
    double strafe = mc.player.movementInput.moveStrafe;
    double yaw = mc.player.rotationYaw;
    if (forward == 0.0D && strafe == 0.0D) {
      event.setX(0.0D);
      event.setZ(0.0D);
    } else if (forward != 0.0D && strafe != 0.0D) {
      forward *= Math.sin(0.7853981633974483D);
      strafe *= Math.cos(0.7853981633974483D);
    } 
    event.setX((forward * this.moveSpeed * -Math.sin(Math.toRadians(yaw)) + strafe * this.moveSpeed * Math.cos(Math.toRadians(yaw))) * 0.99D);
    event.setZ((forward * this.moveSpeed * Math.cos(Math.toRadians(yaw)) - strafe * this.moveSpeed * -Math.sin(Math.toRadians(yaw))) * 0.99D);
    this.stage++;
  }
  
  public static double getBaseMoveSpeed() {
    double baseSpeed = 0.272D;
    if (mc.player.isPotionActive(MobEffects.SPEED)) {
      int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
      baseSpeed *= 1.0D + 0.2D * amplifier;
    } 
    return baseSpeed;
  }
  
  private float getMultiplier() {
    float baseSpeed = ((Integer)this.specialMoveSpeed.getValue()).intValue();
    if (((Boolean)this.potion.getValue()).booleanValue() && mc.player.isPotionActive(MobEffects.SPEED)) {
      int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier() + 1;
      if (amplifier >= 2) {
        baseSpeed = ((Integer)this.potionSpeed2.getValue()).intValue();
      } else {
        baseSpeed = ((Integer)this.potionSpeed.getValue()).intValue();
      } 
    } 
    return baseSpeed / 100.0F;
  }
  
  private boolean shouldReturn() {
    return (Phobos.moduleManager.isModuleEnabled(Freecam.class) || Phobos.moduleManager.isModuleEnabled(Phase.class) || Phobos.moduleManager.isModuleEnabled(ElytraFlight.class) || Phobos.moduleManager.isModuleEnabled(Flight.class));
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerPosLook && ((Boolean)this.noLag.getValue()).booleanValue())
      if (this.mode.getValue() == Mode.BHOP && (((Boolean)this.limiter2.getValue()).booleanValue() || ((Boolean)this.bhop2.getValue()).booleanValue())) {
        this.stage = 1;
      } else {
        this.stage = 4;
      }  
  }
  
  public String getDisplayInfo() {
    if (this.mode.getValue() != Mode.NONE) {
      if (this.mode.getValue() == Mode.NCP)
        return this.mode.currentEnumName().toUpperCase(); 
      return this.mode.currentEnumName();
    } 
    return null;
  }
  
  public static double round(double value, int places) {
    if (places < 0)
      throw new IllegalArgumentException(); 
    BigDecimal bigDecimal = (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP);
    return bigDecimal.doubleValue();
  }
  
  public enum Mode {
    NONE, NCP, BHOP;
  }
}
