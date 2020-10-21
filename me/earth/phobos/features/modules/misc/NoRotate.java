//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module {
  private Setting<Integer> waitDelay = register(new Setting("Delay", Integer.valueOf(2500), Integer.valueOf(0), Integer.valueOf(10000)));
  
  private Timer timer = new Timer();
  
  private boolean cancelPackets = true;
  
  private boolean timerReset = false;
  
  public NoRotate() {
    super("NoRotate", "Dangerous to use might desync you.", Module.Category.MISC, true, false, false);
  }
  
  public void onLogout() {
    this.cancelPackets = false;
  }
  
  public void onLogin() {
    this.timer.reset();
    this.timerReset = true;
  }
  
  public void onUpdate() {
    if (this.timerReset && !this.cancelPackets && this.timer.passedMs(((Integer)this.waitDelay.getValue()).intValue())) {
      Command.sendMessage("<NoRotate> §cThis module might desync you!");
      this.cancelPackets = true;
      this.timerReset = false;
    } 
  }
  
  public void onEnable() {
    Command.sendMessage("<NoRotate> §cThis module might desync you!");
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() == 0 && this.cancelPackets && 
      event.getPacket() instanceof SPacketPlayerPosLook) {
      SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
      packet.yaw = mc.player.rotationYaw;
      packet.pitch = mc.player.rotationPitch;
    } 
  }
}
