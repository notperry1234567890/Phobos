//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Blink extends Module {
  public Setting<Boolean> cPacketPlayer = register(new Setting("CPacketPlayer", Boolean.valueOf(true)));
  
  public Setting<Mode> autoOff = register(new Setting("AutoOff", Mode.MANUAL));
  
  public Setting<Integer> timeLimit = register(new Setting("Time", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> (this.autoOff.getValue() == Mode.TIME)));
  
  public Setting<Integer> packetLimit = register(new Setting("Packets", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> (this.autoOff.getValue() == Mode.PACKETS)));
  
  public Setting<Float> distance = register(new Setting("Distance", Float.valueOf(10.0F), Float.valueOf(1.0F), Float.valueOf(100.0F), v -> (this.autoOff.getValue() == Mode.DISTANCE)));
  
  private Timer timer = new Timer();
  
  private Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
  
  private EntityOtherPlayerMP entity;
  
  private int packetsCanceled = 0;
  
  private BlockPos startPos = null;
  
  private static Blink INSTANCE = new Blink();
  
  public Blink() {
    super("Blink", "Fakelag.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Blink getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Blink(); 
    return INSTANCE;
  }
  
  public void onEnable() {
    if (!fullNullCheck()) {
      this.entity = new EntityOtherPlayerMP((World)mc.world, mc.session.getProfile());
      this.entity.copyLocationAndAnglesFrom((Entity)mc.player);
      this.entity.rotationYaw = mc.player.rotationYaw;
      this.entity.rotationYawHead = mc.player.rotationYawHead;
      this.entity.inventory.copyInventory(mc.player.inventory);
      mc.world.addEntityToWorld(6942069, (Entity)this.entity);
      this.startPos = mc.player.getPosition();
    } else {
      disable();
    } 
    this.packetsCanceled = 0;
    this.timer.reset();
  }
  
  public void onUpdate() {
    if (nullCheck() || (this.autoOff.getValue() == Mode.TIME && this.timer.passedS(((Integer)this.timeLimit.getValue()).intValue())) || (this.autoOff.getValue() == Mode.DISTANCE && this.startPos != null && mc.player.getDistanceSq(this.startPos) >= MathUtil.square(((Float)this.distance.getValue()).floatValue())) || (this.autoOff.getValue() == Mode.PACKETS && this.packetsCanceled >= ((Integer)this.packetLimit.getValue()).intValue()))
      disable(); 
  }
  
  public void onLogout() {
    if (isOn())
      disable(); 
  }
  
  @SubscribeEvent
  public void onSendPacket(PacketEvent.Send event) {
    if (event.getStage() == 0 && mc.world != null && !mc.isSingleplayer()) {
      Packet<?> packet = event.getPacket();
      if (((Boolean)this.cPacketPlayer.getValue()).booleanValue() && packet instanceof net.minecraft.network.play.client.CPacketPlayer) {
        event.setCanceled(true);
        this.packets.add(packet);
        this.packetsCanceled++;
      } 
      if (!((Boolean)this.cPacketPlayer.getValue()).booleanValue()) {
        if (packet instanceof net.minecraft.network.play.client.CPacketChatMessage || packet instanceof net.minecraft.network.play.client.CPacketConfirmTeleport || packet instanceof net.minecraft.network.play.client.CPacketKeepAlive || packet instanceof net.minecraft.network.play.client.CPacketTabComplete || packet instanceof net.minecraft.network.play.client.CPacketClientStatus)
          return; 
        this.packets.add(packet);
        event.setCanceled(true);
        this.packetsCanceled++;
      } 
    } 
  }
  
  public void onDisable() {
    if (!fullNullCheck()) {
      mc.world.removeEntity((Entity)this.entity);
      while (!this.packets.isEmpty())
        mc.player.connection.sendPacket(this.packets.poll()); 
    } 
    this.startPos = null;
  }
  
  public enum Mode {
    MANUAL, TIME, DISTANCE, PACKETS;
  }
}
