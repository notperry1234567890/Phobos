//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.PushEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Phase extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.PACKETFLY));
  
  public Setting<PacketFlyMode> type = register(new Setting("Type", PacketFlyMode.SETBACK, v -> (this.mode.getValue() == Mode.PACKETFLY)));
  
  public Setting<Integer> xMove = register(new Setting("HMove", Integer.valueOf(625), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK), "XMovement speed."));
  
  public Setting<Integer> yMove = register(new Setting("YMove", Integer.valueOf(625), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK), "YMovement speed."));
  
  public Setting<Boolean> extra = register(new Setting("ExtraPacket", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Integer> offset = register(new Setting("Offset", Integer.valueOf(1337), Integer.valueOf(-1337), Integer.valueOf(1337), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && ((Boolean)this.extra.getValue()).booleanValue()), "Up speed."));
  
  public Setting<Boolean> fallPacket = register(new Setting("FallPacket", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> teleporter = register(new Setting("Teleport", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> boundingBox = register(new Setting("BoundingBox", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Integer> teleportConfirm = register(new Setting("Confirm", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(4), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> ultraPacket = register(new Setting("DoublePacket", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> updates = register(new Setting("Update", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> setOnMove = register(new Setting("SetMove", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> cliperino = register(new Setting("NoClip", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && ((Boolean)this.setOnMove.getValue()).booleanValue())));
  
  public Setting<Boolean> scanPackets = register(new Setting("ScanPackets", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> resetConfirm = register(new Setting("Reset", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> posLook = register(new Setting("PosLook", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK)));
  
  public Setting<Boolean> cancel = register(new Setting("Cancel", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && ((Boolean)this.posLook.getValue()).booleanValue())));
  
  public Setting<Boolean> cancelType = register(new Setting("SetYaw", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && ((Boolean)this.posLook.getValue()).booleanValue() && ((Boolean)this.cancel.getValue()).booleanValue())));
  
  public Setting<Boolean> onlyY = register(new Setting("OnlyY", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && ((Boolean)this.posLook.getValue()).booleanValue())));
  
  public Setting<Integer> cancelPacket = register(new Setting("Packets", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(20), v -> (this.mode.getValue() == Mode.PACKETFLY && this.type.getValue() == PacketFlyMode.SETBACK && ((Boolean)this.posLook.getValue()).booleanValue())));
  
  private static Phase INSTANCE = new Phase();
  
  private Set<CPacketPlayer> packets = (Set<CPacketPlayer>)new ConcurrentSet();
  
  private boolean teleport = true;
  
  private int teleportIds = 0;
  
  private int posLookPackets;
  
  public Phase() {
    super("Phase", "Makes you able to phase through blocks.", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Phase getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Phase(); 
    return INSTANCE;
  }
  
  public void onDisable() {
    this.packets.clear();
    this.posLookPackets = 0;
    if (mc.player != null) {
      if (((Boolean)this.resetConfirm.getValue()).booleanValue())
        this.teleportIds = 0; 
      mc.player.noClip = false;
    } 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (((Boolean)this.setOnMove.getValue()).booleanValue() && this.type.getValue() == PacketFlyMode.SETBACK && event.getStage() == 0 && !mc.isSingleplayer() && this.mode.getValue() == Mode.PACKETFLY) {
      event.setX(mc.player.motionX);
      event.setY(mc.player.motionY);
      event.setZ(mc.player.motionZ);
      if (((Boolean)this.cliperino.getValue()).booleanValue())
        mc.player.noClip = true; 
    } 
    if (this.type.getValue() == PacketFlyMode.NONE || event.getStage() != 0 || mc.isSingleplayer() || this.mode.getValue() != Mode.PACKETFLY)
      return; 
    if (!((Boolean)this.boundingBox.getValue()).booleanValue() && !((Boolean)this.updates.getValue()).booleanValue())
      doPhase(event); 
  }
  
  @SubscribeEvent
  public void onPush(PushEvent event) {
    if (event.getStage() == 1 && this.type.getValue() != PacketFlyMode.NONE)
      event.setCanceled(true); 
  }
  
  @SubscribeEvent
  public void onMove(UpdateWalkingPlayerEvent event) {
    if (fullNullCheck() || event.getStage() != 0 || this.type.getValue() != PacketFlyMode.SETBACK || this.mode.getValue() != Mode.PACKETFLY)
      return; 
    if (((Boolean)this.boundingBox.getValue()).booleanValue()) {
      doBoundingBox();
    } else if (((Boolean)this.updates.getValue()).booleanValue()) {
      doPhase((MoveEvent)null);
    } 
  }
  
  private void doPhase(MoveEvent event) {
    if (this.type.getValue() == PacketFlyMode.SETBACK && 
      !((Boolean)this.boundingBox.getValue()).booleanValue()) {
      double[] dirSpeed = getMotion(this.teleport ? (((Integer)this.yMove.getValue()).intValue() / 10000.0D) : ((((Integer)this.yMove.getValue()).intValue() - 1) / 10000.0D));
      double posX = mc.player.posX + dirSpeed[0];
      double posY = mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? (((Integer)this.yMove.getValue()).intValue() / 10000.0D) : ((((Integer)this.yMove.getValue()).intValue() - 1) / 10000.0D)) : 1.0E-8D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? (((Integer)this.yMove.getValue()).intValue() / 10000.0D) : ((((Integer)this.yMove.getValue()).intValue() - 1) / 10000.0D)) : 2.0E-8D);
      double posZ = mc.player.posZ + dirSpeed[1];
      CPacketPlayer.PositionRotation positionRotation = new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw, mc.player.rotationPitch, false);
      this.packets.add(positionRotation);
      mc.player.connection.sendPacket((Packet)positionRotation);
      if (((Integer)this.teleportConfirm.getValue()).intValue() != 3) {
        mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds - 1));
        this.teleportIds++;
      } 
      if (((Boolean)this.extra.getValue()).booleanValue()) {
        CPacketPlayer.PositionRotation positionRotation1 = new CPacketPlayer.PositionRotation(mc.player.posX, ((Integer)this.offset.getValue()).intValue() + mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true);
        this.packets.add(positionRotation1);
        mc.player.connection.sendPacket((Packet)positionRotation1);
      } 
      if (((Integer)this.teleportConfirm.getValue()).intValue() != 1) {
        mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds + 1));
        this.teleportIds++;
      } 
      if (((Boolean)this.ultraPacket.getValue()).booleanValue()) {
        CPacketPlayer.PositionRotation positionRotation1 = new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw, mc.player.rotationPitch, false);
        this.packets.add(positionRotation1);
        mc.player.connection.sendPacket((Packet)positionRotation1);
      } 
      if (((Integer)this.teleportConfirm.getValue()).intValue() == 4) {
        mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds));
        this.teleportIds++;
      } 
      if (((Boolean)this.fallPacket.getValue()).booleanValue())
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING)); 
      mc.player.setPosition(posX, posY, posZ);
      this.teleport = (!((Boolean)this.teleporter.getValue()).booleanValue() || !this.teleport);
      if (event != null) {
        event.setX(0.0D);
        event.setY(0.0D);
        event.setX(0.0D);
      } else {
        mc.player.motionX = 0.0D;
        mc.player.motionY = 0.0D;
        mc.player.motionZ = 0.0D;
      } 
    } 
  }
  
  private void doBoundingBox() {
    double[] dirSpeed = getMotion(this.teleport ? 0.02250000089406967D : 0.02239999920129776D);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 1.0E-8D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 2.0E-8D), mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, -1337.0D, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
    mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 1.0E-8D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 2.0E-8D), mc.player.posZ + dirSpeed[1]);
    this.teleport = !this.teleport;
    mc.player.motionX = mc.player.motionY = mc.player.motionZ = 0.0D;
    mc.player.noClip = this.teleport;
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (((Boolean)this.posLook.getValue()).booleanValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
      SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
      if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain)) {
        if (this.teleportIds <= 0)
          this.teleportIds = packet.getTeleportId(); 
        if (((Boolean)this.cancel.getValue()).booleanValue() && ((Boolean)this.cancelType.getValue()).booleanValue()) {
          packet.yaw = mc.player.rotationYaw;
          packet.pitch = mc.player.rotationPitch;
          return;
        } 
        if (((Boolean)this.cancel.getValue()).booleanValue() && this.posLookPackets >= ((Integer)this.cancelPacket.getValue()).intValue() && (!((Boolean)this.onlyY.getValue()).booleanValue() || (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown()))) {
          this.posLookPackets = 0;
          event.setCanceled(true);
        } 
        this.posLookPackets++;
      } 
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Send event) {
    if (((Boolean)this.scanPackets.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
      if (this.packets.contains(packetPlayer)) {
        this.packets.remove(packetPlayer);
      } else {
        event.setCanceled(true);
      } 
    } 
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
  
  public enum Mode {
    PACKETFLY;
  }
  
  public enum PacketFlyMode {
    NONE, SETBACK;
  }
}
