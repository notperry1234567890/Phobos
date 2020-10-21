//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TestPhase extends Module {
  public Setting<Boolean> flight = register(new Setting("Flight", Boolean.valueOf(true)));
  
  public Setting<Integer> flightMode = register(new Setting("FMode", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1)));
  
  public Setting<Boolean> doAntiFactor = register(new Setting("Factorize", Boolean.valueOf(true)));
  
  public Setting<Double> antiFactor = register(new Setting("AntiFactor", Double.valueOf(2.5D), Double.valueOf(0.1D), Double.valueOf(3.0D)));
  
  public Setting<Double> extraFactor = register(new Setting("ExtraFactor", Double.valueOf(1.0D), Double.valueOf(0.1D), Double.valueOf(3.0D)));
  
  public Setting<Boolean> strafeFactor = register(new Setting("StrafeFactor", Boolean.valueOf(true)));
  
  public Setting<Integer> loops = register(new Setting("Loops", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10)));
  
  public Setting<Boolean> clearTeleMap = register(new Setting("ClearMap", Boolean.valueOf(true)));
  
  public Setting<Integer> mapTime = register(new Setting("ClearTime", Integer.valueOf(30), Integer.valueOf(1), Integer.valueOf(500)));
  
  public Setting<Boolean> clearIDs = register(new Setting("ClearIDs", Boolean.valueOf(true)));
  
  public Setting<Boolean> setYaw = register(new Setting("SetYaw", Boolean.valueOf(true)));
  
  public Setting<Boolean> setID = register(new Setting("SetID", Boolean.valueOf(true)));
  
  public Setting<Boolean> setMove = register(new Setting("SetMove", Boolean.valueOf(false)));
  
  public Setting<Boolean> nocliperino = register(new Setting("NoClip", Boolean.valueOf(false)));
  
  public Setting<Boolean> sendTeleport = register(new Setting("Teleport", Boolean.valueOf(true)));
  
  public Setting<Boolean> resetID = register(new Setting("ResetID", Boolean.valueOf(true)));
  
  public Setting<Boolean> setPos = register(new Setting("SetPos", Boolean.valueOf(false)));
  
  public Setting<Boolean> invalidPacket = register(new Setting("InvalidPacket", Boolean.valueOf(true)));
  
  private final Set<CPacketPlayer> packets = (Set<CPacketPlayer>)new ConcurrentSet();
  
  private final Map<Integer, IDtime> teleportmap = new ConcurrentHashMap<>();
  
  private int flightCounter = 0;
  
  private int teleportID = 0;
  
  private static TestPhase instance;
  
  public TestPhase() {
    super("Packetfly", "Uses packets to fly!", Module.Category.MOVEMENT, true, false, false);
    instance = this;
  }
  
  public static TestPhase getInstance() {
    if (instance == null)
      instance = new TestPhase(); 
    return instance;
  }
  
  public void onToggle() {}
  
  public void onTick() {
    this.teleportmap.entrySet().removeIf(idTime -> (((Boolean)this.clearTeleMap.getValue()).booleanValue() && ((IDtime)idTime.getValue()).getTimer().passedS(((Integer)this.mapTime.getValue()).intValue())));
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 1)
      return; 
    mc.player.setVelocity(0.0D, 0.0D, 0.0D);
    double speed = 0.0D;
    boolean checkCollisionBoxes = checkHitBoxes();
    if (mc.player.movementInput.jump && (checkCollisionBoxes || !EntityUtil.isMoving())) {
      if (((Boolean)this.flight.getValue()).booleanValue() && !checkCollisionBoxes) {
        if (((Integer)this.flightMode.getValue()).intValue() == 0) {
          speed = resetCounter(10) ? -0.032D : 0.062D;
        } else {
          speed = resetCounter(20) ? -0.032D : 0.062D;
        } 
      } else {
        speed = 0.062D;
      } 
    } else if (mc.player.movementInput.sneak) {
      speed = -0.062D;
    } else if (!checkCollisionBoxes) {
      speed = resetCounter(4) ? (((Boolean)this.flight.getValue()).booleanValue() ? -0.04D : 0.0D) : 0.0D;
    } else {
      speed = 0.0D;
    } 
    if (((Boolean)this.doAntiFactor.getValue()).booleanValue() && checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0D)
      speed /= ((Double)this.antiFactor.getValue()).doubleValue(); 
    double[] strafing = getMotion((((Boolean)this.strafeFactor.getValue()).booleanValue() && checkCollisionBoxes) ? 0.031D : 0.26D);
    for (int i = 1; i < ((Integer)this.loops.getValue()).intValue() + 1; i++) {
      mc.player.motionX = strafing[0] * i * ((Double)this.extraFactor.getValue()).doubleValue();
      mc.player.motionY = speed * i;
      mc.player.motionZ = strafing[1] * i * ((Double)this.extraFactor.getValue()).doubleValue();
      sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ, ((Boolean)this.sendTeleport.getValue()).booleanValue());
    } 
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (((Boolean)this.setMove.getValue()).booleanValue() && this.flightCounter != 0) {
      event.setX(mc.player.motionX);
      event.setY(mc.player.motionY);
      event.setZ(mc.player.motionZ);
      if (((Boolean)this.nocliperino.getValue()).booleanValue() && checkHitBoxes())
        mc.player.noClip = true; 
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      if (this.packets.contains(packet)) {
        this.packets.remove(packet);
      } else {
        event.setCanceled(true);
      } 
    } 
  }
  
  @SubscribeEvent
  public void onPushOutOfBlocks(PushEvent event) {
    if (event.getStage() == 1)
      event.setCanceled(true); 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketPlayerPosLook && !fullNullCheck()) {
      SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
      if (mc.player.isEntityAlive()) {
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        if (mc.world.isBlockLoaded(pos, false) && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain) && ((Boolean)this.clearIDs.getValue()).booleanValue())
          this.teleportmap.remove(Integer.valueOf(packet.getTeleportId())); 
      } 
      if (((Boolean)this.setYaw.getValue()).booleanValue()) {
        packet.yaw = mc.player.rotationYaw;
        packet.pitch = mc.player.rotationPitch;
      } 
      if (((Boolean)this.setID.getValue()).booleanValue())
        this.teleportID = packet.getTeleportId(); 
    } 
  }
  
  private boolean checkHitBoxes() {
    return !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, -0.0625D, -0.0625D)).isEmpty();
  }
  
  private boolean resetCounter(int counter) {
    if (++this.flightCounter >= counter) {
      this.flightCounter = 0;
      return true;
    } 
    return false;
  }
  
  private double[] getMotion(double speed) {
    float moveForward = mc.player.movementInput.moveForward;
    float moveStrafe = mc.player.movementInput.moveStrafe;
    float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
    if (moveForward != 0.0F) {
      if (moveStrafe > 0.0F) {
        rotationYaw += ((moveForward > 0.0F) ? -45 : 45);
      } else if (moveStrafe < 0.0F) {
        rotationYaw += ((moveForward > 0.0F) ? 45 : -45);
      } 
      moveStrafe = 0.0F;
      if (moveForward > 0.0F) {
        moveForward = 1.0F;
      } else if (moveForward < 0.0F) {
        moveForward = -1.0F;
      } 
    } 
    double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
    double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
    return new double[] { posX, posZ };
  }
  
  private void sendPackets(double x, double y, double z, boolean teleport) {
    Vec3d vec = new Vec3d(x, y, z);
    Vec3d position = mc.player.getPositionVector().add(vec);
    Vec3d outOfBoundsVec = outOfBoundsVec(vec, position);
    packetSender((CPacketPlayer)new CPacketPlayer.Position(position.x, position.y, position.z, mc.player.onGround));
    if (((Boolean)this.invalidPacket.getValue()).booleanValue())
      packetSender((CPacketPlayer)new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, mc.player.onGround)); 
    if (((Boolean)this.setPos.getValue()).booleanValue())
      mc.player.setPosition(position.x, position.y, position.z); 
    teleportPacket(position, teleport);
  }
  
  private void teleportPacket(Vec3d pos, boolean shouldTeleport) {
    if (shouldTeleport) {
      mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(++this.teleportID));
      this.teleportmap.put(Integer.valueOf(this.teleportID), new IDtime(pos, new Timer()));
    } 
  }
  
  private Vec3d outOfBoundsVec(Vec3d offset, Vec3d position) {
    return position.add(0.0D, 1337.0D, 0.0D);
  }
  
  private void packetSender(CPacketPlayer packet) {
    this.packets.add(packet);
    mc.player.connection.sendPacket((Packet)packet);
  }
  
  private void clean() {
    this.teleportmap.clear();
    this.flightCounter = 0;
    if (((Boolean)this.resetID.getValue()).booleanValue())
      this.teleportID = 0; 
    this.packets.clear();
  }
  
  public static class IDtime {
    private final Vec3d pos;
    
    private final Timer timer;
    
    public IDtime(Vec3d pos, Timer timer) {
      this.pos = pos;
      this.timer = timer;
      this.timer.reset();
    }
    
    public Vec3d getPos() {
      return this.pos;
    }
    
    public Timer getTimer() {
      return this.timer;
    }
  }
}
