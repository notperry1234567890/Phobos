//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PingSpoof extends Module {
  private Setting<Boolean> seconds = register(new Setting("Seconds", Boolean.valueOf(false)));
  
  private Setting<Integer> delay = register(new Setting("DelayMS", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(1000), v -> !((Boolean)this.seconds.getValue()).booleanValue()));
  
  private Setting<Integer> secondDelay = register(new Setting("DelayS", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(30), v -> ((Boolean)this.seconds.getValue()).booleanValue()));
  
  private Setting<Boolean> extraPacket = register(new Setting("Packet", Boolean.valueOf(true)));
  
  private Setting<Boolean> offOnLogout = register(new Setting("Logout", Boolean.valueOf(false)));
  
  private Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
  
  private Timer timer = new Timer();
  
  private boolean receive = true;
  
  public PingSpoof() {
    super("PingSpoof", "Spoofs your ping!", Module.Category.MISC, true, false, false);
  }
  
  public void onLoad() {
    if (((Boolean)this.offOnLogout.getValue()).booleanValue())
      disable(); 
  }
  
  public void onLogout() {
    if (((Boolean)this.offOnLogout.getValue()).booleanValue())
      disable(); 
  }
  
  public void onUpdate() {
    clearQueue();
  }
  
  public void onDisable() {
    clearQueue();
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (this.receive && mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && event.getStage() == 0 && event.getPacket() instanceof CPacketKeepAlive) {
      this.packets.add(event.getPacket());
      event.setCanceled(true);
    } 
  }
  
  public void clearQueue() {
    if (mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && ((!((Boolean)this.seconds.getValue()).booleanValue() && this.timer.passedMs(((Integer)this.delay.getValue()).intValue())) || (((Boolean)this.seconds.getValue()).booleanValue() && this.timer.passedS(((Integer)this.secondDelay.getValue()).intValue())))) {
      double limit = MathUtil.getIncremental(Math.random() * 10.0D, 1.0D);
      this.receive = false;
      for (int i = 0; i < limit; i++) {
        Packet<?> packet = this.packets.poll();
        if (packet != null)
          mc.player.connection.sendPacket(packet); 
      } 
      if (((Boolean)this.extraPacket.getValue()).booleanValue())
        mc.player.connection.sendPacket((Packet)new CPacketKeepAlive(10000L)); 
      this.timer.reset();
      this.receive = true;
    } 
  }
}
