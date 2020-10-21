//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Flight extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET));
  
  public Setting<Boolean> better = register(new Setting("Better", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKET)));
  
  public Setting<Format> format = register(new Setting("Format", Format.DAMAGE, v -> (this.mode.getValue() == Mode.DAMAGE)));
  
  public Setting<PacketMode> type = register(new Setting("Type", PacketMode.Y, v -> (this.mode.getValue() == Mode.PACKET)));
  
  public Setting<Boolean> phase = register(new Setting("Phase", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKET && ((Boolean)this.better.getValue()).booleanValue())));
  
  public Setting<Float> speed = register(new Setting("Speed", Float.valueOf(0.1F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.mode.getValue() == Mode.PACKET || this.mode.getValue() == Mode.DESCEND || this.mode.getValue() == Mode.DAMAGE), "The speed."));
  
  public Setting<Boolean> noKick = register(new Setting("NoKick", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKET || this.mode.getValue() == Mode.DAMAGE)));
  
  public Setting<Boolean> noClip = register(new Setting("NoClip", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.DAMAGE)));
  
  public Setting<Boolean> groundSpoof = register(new Setting("GroundSpoof", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SPOOF)));
  
  public Setting<Boolean> antiGround = register(new Setting("AntiGround", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.SPOOF)));
  
  public Setting<Integer> cooldown = register(new Setting("Cooldown", Integer.valueOf(1), v -> (this.mode.getValue() == Mode.DESCEND)));
  
  public Setting<Boolean> ascend = register(new Setting("Ascend", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.DESCEND)));
  
  private List<CPacketPlayer> packets = new ArrayList<>();
  
  private int teleportId = 0;
  
  private static Flight INSTANCE = new Flight();
  
  private int counter = 0;
  
  private final Fly flySwitch = new Fly();
  
  private double moveSpeed;
  
  private double lastDist;
  
  private int level;
  
  private Timer delayTimer = new Timer();
  
  public Flight() {
    super("Flight", "Makes you fly.", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Flight getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Flight(); 
    return INSTANCE;
  }
  
  @SubscribeEvent
  public void onTickEvent(TickEvent.ClientTickEvent event) {
    if (fullNullCheck() || this.mode.getValue() != Mode.DESCEND)
      return; 
    if (event.phase == TickEvent.Phase.END) {
      if (!mc.player.isElytraFlying())
        if (this.counter < 1) {
          this.counter += ((Integer)this.cooldown.getValue()).intValue();
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.03D, mc.player.posZ, true));
        } else {
          this.counter--;
        }  
    } else if (((Boolean)this.ascend.getValue()).booleanValue()) {
      mc.player.motionY = ((Float)this.speed.getValue()).floatValue();
    } else {
      mc.player.motionY = -((Float)this.speed.getValue()).floatValue();
    } 
  }
  
  public void onEnable() {
    if (fullNullCheck())
      return; 
    if (this.mode.getValue() == Mode.PACKET) {
      this.teleportId = 0;
      this.packets.clear();
      CPacketPlayer.Position position = new CPacketPlayer.Position(mc.player.posX, 0.0D, mc.player.posZ, mc.player.onGround);
      this.packets.add(position);
      mc.player.connection.sendPacket((Packet)position);
    } 
    if (this.mode.getValue() == Mode.CREATIVE) {
      mc.player.capabilities.isFlying = true;
      if (mc.player.capabilities.isCreativeMode)
        return; 
      mc.player.capabilities.allowFlying = true;
    } 
    if (this.mode.getValue() == Mode.SPOOF)
      this.flySwitch.enable(); 
    if (this.mode.getValue() == Mode.DAMAGE) {
      this.level = 0;
      if (this.format.getValue() == Format.PACKET && 
        mc.world != null) {
        this.teleportId = 0;
        this.packets.clear();
        CPacketPlayer.Position position = new CPacketPlayer.Position(mc.player.posX, (mc.player.posY <= 10.0D) ? 255.0D : 1.0D, mc.player.posZ, mc.player.onGround);
        this.packets.add(position);
        mc.player.connection.sendPacket((Packet)position);
      } 
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (this.mode.getValue() == Mode.DAMAGE) {
      if (this.format.getValue() == Format.DAMAGE) {
        if (event.getStage() == 0) {
          mc.player.motionY = 0.0D;
          double motionY = 0.41999998688697815D;
          if (mc.player.onGround) {
            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
              motionY += ((mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F); 
            Phobos.positionManager.setPlayerPosition(mc.player.posX, mc.player.motionY = motionY, mc.player.posZ);
            this.moveSpeed *= 2.149D;
          } 
        } 
        if (mc.player.ticksExisted % 2 == 0)
          mc.player.setPosition(mc.player.posX, mc.player.posY + MathUtil.getRandom(1.2354235325235235E-14D, 1.2354235325235233E-13D), mc.player.posZ); 
        if (mc.gameSettings.keyBindJump.isKeyDown())
          mc.player.motionY += (((Float)this.speed.getValue()).floatValue() / 2.0F); 
        if (mc.gameSettings.keyBindSneak.isKeyDown())
          mc.player.motionY -= (((Float)this.speed.getValue()).floatValue() / 2.0F); 
      } 
      if (this.format.getValue() == Format.NORMAL) {
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
          mc.player.motionY = ((Float)this.speed.getValue()).floatValue();
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
          mc.player.motionY = -((Float)this.speed.getValue()).floatValue();
        } else {
          mc.player.motionY = 0.0D;
        } 
        if (((Boolean)this.noKick.getValue()).booleanValue() && 
          mc.player.ticksExisted % 5 == 0)
          Phobos.positionManager.setPlayerPosition(mc.player.posX, mc.player.posY - 0.03125D, mc.player.posZ, true); 
        double[] dir = EntityUtil.forward(((Float)this.speed.getValue()).floatValue());
        mc.player.motionX = dir[0];
        mc.player.motionZ = dir[1];
      } 
      if (this.format.getValue() == Format.PACKET) {
        if (this.teleportId <= 0) {
          CPacketPlayer.Position position = new CPacketPlayer.Position(mc.player.posX, (mc.player.posY <= 10.0D) ? 255.0D : 1.0D, mc.player.posZ, mc.player.onGround);
          this.packets.add(position);
          mc.player.connection.sendPacket((Packet)position);
          return;
        } 
        mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        double posY = -1.0E-8D;
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
          if (EntityUtil.isMoving()) {
            double x;
            for (x = 0.0625D; x < ((Float)this.speed.getValue()).floatValue(); x += 0.262D) {
              double[] dir = EntityUtil.forward(x);
              mc.player.setVelocity(dir[0], posY, dir[1]);
              move(dir[0], posY, dir[1]);
            } 
          } 
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
          for (int i = 0; i <= 3; i++) {
            mc.player.setVelocity(0.0D, (mc.player.ticksExisted % 20 == 0) ? -0.03999999910593033D : (0.062F * i), 0.0D);
            move(0.0D, (mc.player.ticksExisted % 20 == 0) ? -0.03999999910593033D : (0.062F * i), 0.0D);
          } 
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
          for (int i = 0; i <= 3; i++) {
            mc.player.setVelocity(0.0D, posY - 0.0625D * i, 0.0D);
            move(0.0D, posY - 0.0625D * i, 0.0D);
          } 
        } 
      } 
      if (this.format.getValue() == Format.SLOW) {
        double posX = mc.player.posX;
        double posY = mc.player.posY;
        double posZ = mc.player.posZ;
        boolean ground = mc.player.onGround;
        mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
          double[] dir = EntityUtil.forward(0.0625D);
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX + dir[0], posY, posZ + dir[1], ground));
          mc.player.setPositionAndUpdate(posX + dir[0], posY, posZ + dir[1]);
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, posY + 0.0625D, posZ, ground));
          mc.player.setPositionAndUpdate(posX, posY + 0.0625D, posZ);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, posY - 0.0625D, posZ, ground));
          mc.player.setPositionAndUpdate(posX, posY - 0.0625D, posZ);
        } 
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX + mc.player.motionX, (mc.player.posY <= 10.0D) ? 255.0D : 1.0D, posZ + mc.player.motionZ, ground));
      } 
      if (this.format.getValue() == Format.DELAY) {
        if (this.delayTimer.passedMs(1000L))
          this.delayTimer.reset(); 
        if (this.delayTimer.passedMs(600L)) {
          mc.player.setVelocity(0.0D, 0.0D, 0.0D);
          return;
        } 
        if (this.teleportId <= 0) {
          CPacketPlayer.Position position = new CPacketPlayer.Position(mc.player.posX, (mc.player.posY <= 10.0D) ? 255.0D : 1.0D, mc.player.posZ, mc.player.onGround);
          this.packets.add(position);
          mc.player.connection.sendPacket((Packet)position);
          return;
        } 
        mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        double posY = -1.0E-8D;
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
          if (EntityUtil.isMoving()) {
            double[] dir = EntityUtil.forward(0.2D);
            mc.player.setVelocity(dir[0], posY, dir[1]);
            move(dir[0], posY, dir[1]);
          } 
        } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
          mc.player.setVelocity(0.0D, 0.06199999898672104D, 0.0D);
          move(0.0D, 0.06199999898672104D, 0.0D);
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
          mc.player.setVelocity(0.0D, 0.0625D, 0.0D);
          move(0.0D, 0.0625D, 0.0D);
        } 
      } 
      if (((Boolean)this.noClip.getValue()).booleanValue())
        mc.player.noClip = true; 
    } 
    if (event.getStage() == 0) {
      if (this.mode.getValue() == Mode.CREATIVE) {
        mc.player.capabilities.setFlySpeed(((Float)this.speed.getValue()).floatValue());
        mc.player.capabilities.isFlying = true;
        if (mc.player.capabilities.isCreativeMode)
          return; 
        mc.player.capabilities.allowFlying = true;
      } 
      if (this.mode.getValue() == Mode.VANILLA) {
        mc.player.setVelocity(0.0D, 0.0D, 0.0D);
        mc.player.jumpMovementFactor = ((Float)this.speed.getValue()).floatValue();
        if (((Boolean)this.noKick.getValue()).booleanValue() && 
          mc.player.ticksExisted % 4 == 0)
          mc.player.motionY = -0.03999999910593033D; 
        double[] dir = MathUtil.directionSpeed(((Float)this.speed.getValue()).floatValue());
        if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
          mc.player.motionX = dir[0];
          mc.player.motionZ = dir[1];
        } else {
          mc.player.motionX = 0.0D;
          mc.player.motionZ = 0.0D;
        } 
        if (mc.gameSettings.keyBindJump.isKeyDown())
          if (((Boolean)this.noKick.getValue()).booleanValue()) {
            mc.player.motionY = (mc.player.ticksExisted % 20 == 0) ? -0.03999999910593033D : ((Float)this.speed.getValue()).floatValue();
          } else {
            mc.player.motionY += ((Float)this.speed.getValue()).floatValue();
          }  
        if (mc.gameSettings.keyBindSneak.isKeyDown())
          mc.player.motionY -= ((Float)this.speed.getValue()).floatValue(); 
      } 
      if (this.mode.getValue() == Mode.PACKET && !((Boolean)this.better.getValue()).booleanValue())
        doNormalPacketFly(); 
      if (this.mode.getValue() == Mode.PACKET && ((Boolean)this.better.getValue()).booleanValue())
        doBetterPacketFly(); 
    } 
  }
  
  private void doNormalPacketFly() {
    if (this.teleportId <= 0) {
      CPacketPlayer.Position position = new CPacketPlayer.Position(mc.player.posX, 0.0D, mc.player.posZ, mc.player.onGround);
      this.packets.add(position);
      mc.player.connection.sendPacket((Packet)position);
      return;
    } 
    mc.player.setVelocity(0.0D, 0.0D, 0.0D);
    if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, 0.0D, -0.0625D)).isEmpty()) {
      double ySpeed = 0.0D;
      if (mc.gameSettings.keyBindJump.isKeyDown()) {
        if (((Boolean)this.noKick.getValue()).booleanValue()) {
          ySpeed = (mc.player.ticksExisted % 20 == 0) ? -0.03999999910593033D : 0.06199999898672104D;
        } else {
          ySpeed = 0.06199999898672104D;
        } 
      } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
        ySpeed = -0.062D;
      } else {
        ySpeed = mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, -0.0625D, -0.0625D)).isEmpty() ? ((mc.player.ticksExisted % 4 == 0) ? (((Boolean)this.noKick.getValue()).booleanValue() ? -0.04F : 0.0F) : 0.0D) : 0.0D;
      } 
      double[] directionalSpeed = MathUtil.directionSpeed(((Float)this.speed.getValue()).floatValue());
      if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
        if (directionalSpeed[0] != 0.0D || directionalSpeed[1] != 0.0D)
          if (mc.player.movementInput.jump && (mc.player.moveStrafing != 0.0F || mc.player.moveForward != 0.0F)) {
            mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            move(0.0D, 0.0D, 0.0D);
            for (int i = 0; i <= 3; i++) {
              mc.player.setVelocity(0.0D, ySpeed * i, 0.0D);
              move(0.0D, ySpeed * i, 0.0D);
            } 
          } else if (mc.player.movementInput.jump) {
            mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            move(0.0D, 0.0D, 0.0D);
            for (int i = 0; i <= 3; i++) {
              mc.player.setVelocity(0.0D, ySpeed * i, 0.0D);
              move(0.0D, ySpeed * i, 0.0D);
            } 
          } else {
            for (int i = 0; i <= 2; i++) {
              mc.player.setVelocity(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
              move(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
            } 
          }  
      } else if (((Boolean)this.noKick.getValue()).booleanValue() && 
        mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, -0.0625D, -0.0625D)).isEmpty()) {
        mc.player.setVelocity(0.0D, (mc.player.ticksExisted % 2 == 0) ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
        move(0.0D, (mc.player.ticksExisted % 2 == 0) ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
      } 
    } 
  }
  
  private void doBetterPacketFly() {
    if (this.teleportId <= 0) {
      CPacketPlayer.Position position = new CPacketPlayer.Position(mc.player.posX, 10000.0D, mc.player.posZ, mc.player.onGround);
      this.packets.add(position);
      mc.player.connection.sendPacket((Packet)position);
      return;
    } 
    mc.player.setVelocity(0.0D, 0.0D, 0.0D);
    if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, 0.0D, -0.0625D)).isEmpty()) {
      double ySpeed = 0.0D;
      if (mc.gameSettings.keyBindJump.isKeyDown()) {
        if (((Boolean)this.noKick.getValue()).booleanValue()) {
          ySpeed = (mc.player.ticksExisted % 20 == 0) ? -0.03999999910593033D : 0.06199999898672104D;
        } else {
          ySpeed = 0.06199999898672104D;
        } 
      } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
        ySpeed = -0.062D;
      } else {
        ySpeed = mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, -0.0625D, -0.0625D)).isEmpty() ? ((mc.player.ticksExisted % 4 == 0) ? (((Boolean)this.noKick.getValue()).booleanValue() ? -0.04F : 0.0F) : 0.0D) : 0.0D;
      } 
      double[] directionalSpeed = MathUtil.directionSpeed(((Float)this.speed.getValue()).floatValue());
      if (mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown() || mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown()) {
        if (directionalSpeed[0] != 0.0D || directionalSpeed[1] != 0.0D)
          if (mc.player.movementInput.jump && (mc.player.moveStrafing != 0.0F || mc.player.moveForward != 0.0F)) {
            mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            move(0.0D, 0.0D, 0.0D);
            for (int i = 0; i <= 3; i++) {
              mc.player.setVelocity(0.0D, ySpeed * i, 0.0D);
              move(0.0D, ySpeed * i, 0.0D);
            } 
          } else if (mc.player.movementInput.jump) {
            mc.player.setVelocity(0.0D, 0.0D, 0.0D);
            move(0.0D, 0.0D, 0.0D);
            for (int i = 0; i <= 3; i++) {
              mc.player.setVelocity(0.0D, ySpeed * i, 0.0D);
              move(0.0D, ySpeed * i, 0.0D);
            } 
          } else {
            for (int i = 0; i <= 2; i++) {
              mc.player.setVelocity(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
              move(directionalSpeed[0] * i, ySpeed * i, directionalSpeed[1] * i);
            } 
          }  
      } else if (((Boolean)this.noKick.getValue()).booleanValue() && 
        mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, -0.0625D, -0.0625D)).isEmpty()) {
        mc.player.setVelocity(0.0D, (mc.player.ticksExisted % 2 == 0) ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
        move(0.0D, (mc.player.ticksExisted % 2 == 0) ? 0.03999999910593033D : -0.03999999910593033D, 0.0D);
      } 
    } 
  }
  
  public void onUpdate() {
    if (this.mode.getValue() == Mode.SPOOF) {
      if (fullNullCheck())
        return; 
      if (!mc.player.capabilities.allowFlying) {
        this.flySwitch.disable();
        this.flySwitch.enable();
        mc.player.capabilities.isFlying = false;
      } 
      mc.player.capabilities.setFlySpeed(0.05F * ((Float)this.speed.getValue()).floatValue());
    } 
  }
  
  public void onDisable() {
    if (this.mode.getValue() == Mode.CREATIVE && mc.player != null) {
      mc.player.capabilities.isFlying = false;
      mc.player.capabilities.setFlySpeed(0.05F);
      if (mc.player.capabilities.isCreativeMode)
        return; 
      mc.player.capabilities.allowFlying = false;
    } 
    if (this.mode.getValue() == Mode.SPOOF)
      this.flySwitch.disable(); 
    if (this.mode.getValue() == Mode.DAMAGE) {
      Phobos.timerManager.reset();
      mc.player.setVelocity(0.0D, 0.0D, 0.0D);
      this.moveSpeed = Strafe.getBaseMoveSpeed();
      this.lastDist = 0.0D;
      if (((Boolean)this.noClip.getValue()).booleanValue())
        mc.player.noClip = false; 
    } 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public void onLogout() {
    if (isOn())
      disable(); 
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (event.getStage() == 0 && this.mode.getValue() == Mode.DAMAGE && this.format.getValue() == Format.DAMAGE) {
      double forward = mc.player.movementInput.moveForward;
      double strafe = mc.player.movementInput.moveStrafe;
      float yaw = mc.player.rotationYaw;
      if (forward == 0.0D && strafe == 0.0D) {
        event.setX(0.0D);
        event.setZ(0.0D);
      } 
      if (forward != 0.0D && strafe != 0.0D) {
        forward *= Math.sin(0.7853981633974483D);
        strafe *= Math.cos(0.7853981633974483D);
      } 
      if (this.level != 1 || (mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F)) {
        if (this.level == 2) {
          this.level++;
        } else if (this.level == 3) {
          this.level++;
          double difference = ((mc.player.ticksExisted % 2 == 0) ? -0.05D : 0.1D) * (this.lastDist - Strafe.getBaseMoveSpeed());
          this.moveSpeed = this.lastDist - difference;
        } else {
          if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D)).size() > 0 || mc.player.collidedVertically)
            this.level = 1; 
          this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
        } 
      } else {
        this.level = 2;
        double boost = mc.player.isPotionActive(MobEffects.SPEED) ? 1.86D : 2.05D;
        this.moveSpeed = boost * Strafe.getBaseMoveSpeed() - 0.01D;
      } 
      this.moveSpeed = Math.max(this.moveSpeed, Strafe.getBaseMoveSpeed());
      double mx = -Math.sin(Math.toRadians(yaw));
      double mz = Math.cos(Math.toRadians(yaw));
      event.setX(forward * this.moveSpeed * mx + strafe * this.moveSpeed * mz);
      event.setZ(forward * this.moveSpeed * mz - strafe * this.moveSpeed * mx);
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0) {
      if (this.mode.getValue() == Mode.PACKET) {
        if (fullNullCheck())
          return; 
        if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position))
          event.setCanceled(true); 
        if (event.getPacket() instanceof CPacketPlayer) {
          CPacketPlayer packet = (CPacketPlayer)event.getPacket();
          if (this.packets.contains(packet)) {
            this.packets.remove(packet);
            return;
          } 
          event.setCanceled(true);
        } 
      } 
      if (this.mode.getValue() == Mode.SPOOF) {
        if (fullNullCheck())
          return; 
        if (!((Boolean)this.groundSpoof.getValue()).booleanValue() || !(event.getPacket() instanceof CPacketPlayer) || !mc.player.capabilities.isFlying)
          return; 
        CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        if (!packet.moving)
          return; 
        AxisAlignedBB range = mc.player.getEntityBoundingBox().expand(0.0D, -mc.player.posY, 0.0D).contract(0.0D, -mc.player.height, 0.0D);
        List<AxisAlignedBB> collisionBoxes = mc.player.world.getCollisionBoxes((Entity)mc.player, range);
        AtomicReference<Double> newHeight = new AtomicReference<>(Double.valueOf(0.0D));
        collisionBoxes.forEach(box -> newHeight.set(Double.valueOf(Math.max(((Double)newHeight.get()).doubleValue(), box.maxY))));
        packet.y = ((Double)newHeight.get()).doubleValue();
        packet.onGround = true;
      } 
      if (this.mode.getValue() == Mode.DAMAGE && (
        this.format.getValue() == Format.PACKET || this.format.getValue() == Format.DELAY)) {
        if (event.getPacket() instanceof CPacketPlayer && !(event.getPacket() instanceof CPacketPlayer.Position))
          event.setCanceled(true); 
        if (event.getPacket() instanceof CPacketPlayer) {
          CPacketPlayer packet = (CPacketPlayer)event.getPacket();
          if (this.packets.contains(packet)) {
            this.packets.remove(packet);
            return;
          } 
          event.setCanceled(true);
        } 
      } 
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() == 0) {
      if (this.mode.getValue() == Mode.PACKET) {
        if (fullNullCheck())
          return; 
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
          SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
          if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain))
            if (this.teleportId <= 0) {
              this.teleportId = packet.getTeleportId();
            } else {
              event.setCanceled(true);
            }  
        } 
      } 
      if (this.mode.getValue() == Mode.SPOOF) {
        if (fullNullCheck())
          return; 
        if (!((Boolean)this.antiGround.getValue()).booleanValue() || !(event.getPacket() instanceof SPacketPlayerPosLook) || !mc.player.capabilities.isFlying)
          return; 
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
        double oldY = mc.player.posY;
        mc.player.setPosition(packet.x, packet.y, packet.z);
        AxisAlignedBB range = mc.player.getEntityBoundingBox().expand(0.0D, (256.0F - mc.player.height) - mc.player.posY, 0.0D).contract(0.0D, mc.player.height, 0.0D);
        List<AxisAlignedBB> collisionBoxes = mc.player.world.getCollisionBoxes((Entity)mc.player, range);
        AtomicReference<Double> newY = new AtomicReference<>(Double.valueOf(256.0D));
        collisionBoxes.forEach(box -> newY.set(Double.valueOf(Math.min(((Double)newY.get()).doubleValue(), box.minY - mc.player.height))));
        packet.y = Math.min(oldY, ((Double)newY.get()).doubleValue());
      } 
      if (this.mode.getValue() == Mode.DAMAGE && (
        this.format.getValue() == Format.PACKET || this.format.getValue() == Format.DELAY) && 
        event.getPacket() instanceof SPacketPlayerPosLook) {
        SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
        if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain))
          if (this.teleportId <= 0) {
            this.teleportId = packet.getTeleportId();
          } else {
            event.setCanceled(true);
          }  
      } 
    } 
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && 
      isEnabled() && !event.getSetting().equals(this.enabled))
      disable(); 
  }
  
  @SubscribeEvent
  public void onPush(PushEvent event) {
    if (event.getStage() == 1 && this.mode.getValue() == Mode.PACKET && ((Boolean)this.better.getValue()).booleanValue() && ((Boolean)this.phase.getValue()).booleanValue())
      event.setCanceled(true); 
  }
  
  private void move(double x, double y, double z) {
    CPacketPlayer.Position position2, position1 = new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
    this.packets.add(position1);
    mc.player.connection.sendPacket((Packet)position1);
    if (((Boolean)this.better.getValue()).booleanValue()) {
      CPacketPlayer bounds = createBoundsPacket(x, y, z);
    } else {
      position2 = new CPacketPlayer.Position(mc.player.posX + x, 0.0D, mc.player.posZ + z, mc.player.onGround);
    } 
    this.packets.add(position2);
    mc.player.connection.sendPacket((Packet)position2);
    this.teleportId++;
    mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId - 1));
    mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId));
    mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportId + 1));
  }
  
  public enum Mode {
    CREATIVE, VANILLA, PACKET, SPOOF, DESCEND, DAMAGE;
  }
  
  public enum Format {
    DAMAGE, SLOW, DELAY, NORMAL, PACKET;
  }
  
  private enum PacketMode {
    Up, Down, Zero, Y, X, Z, XZ;
  }
  
  private CPacketPlayer createBoundsPacket(double x, double y, double z) {
    switch ((PacketMode)this.type.getValue()) {
      case Up:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x, 10000.0D, mc.player.posZ + z, mc.player.onGround);
      case Down:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x, -10000.0D, mc.player.posZ + z, mc.player.onGround);
      case Zero:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x, 0.0D, mc.player.posZ + z, mc.player.onGround);
      case Y:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x, (mc.player.posY + y <= 10.0D) ? 255.0D : 1.0D, mc.player.posZ + z, mc.player.onGround);
      case X:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x + 75.0D, mc.player.posY + y, mc.player.posZ + z, mc.player.onGround);
      case Z:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z + 75.0D, mc.player.onGround);
      case XZ:
        return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x + 75.0D, mc.player.posY + y, mc.player.posZ + z + 75.0D, mc.player.onGround);
    } 
    return (CPacketPlayer)new CPacketPlayer.Position(mc.player.posX + x, 2000.0D, mc.player.posZ + z, mc.player.onGround);
  }
  
  private static class Fly {
    private Fly() {}
    
    protected void enable() {
      Util.mc.addScheduledTask(() -> {
            if (Util.mc.player == null || Util.mc.player.capabilities == null)
              return; 
            Util.mc.player.capabilities.allowFlying = true;
            Util.mc.player.capabilities.isFlying = true;
          });
    }
    
    protected void disable() {
      Util.mc.addScheduledTask(() -> {
            if (Util.mc.player == null || Util.mc.player.capabilities == null)
              return; 
            PlayerCapabilities gmCaps = new PlayerCapabilities();
            Util.mc.playerController.getCurrentGameType().configurePlayerCapabilities(gmCaps);
            PlayerCapabilities capabilities = Util.mc.player.capabilities;
            capabilities.allowFlying = gmCaps.allowFlying;
            capabilities.isFlying = (gmCaps.allowFlying && capabilities.isFlying);
            capabilities.setFlySpeed(gmCaps.getFlySpeed());
          });
    }
  }
}
