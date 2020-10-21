//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLog extends Module {
  private Setting<Float> health = register(new Setting("Health", Float.valueOf(16.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  private Setting<Boolean> bed = register(new Setting("Beds", Boolean.valueOf(true)));
  
  private Setting<Float> range = register(new Setting("BedRange", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> ((Boolean)this.bed.getValue()).booleanValue()));
  
  private Setting<Boolean> logout = register(new Setting("LogoutOff", Boolean.valueOf(true)));
  
  private static AutoLog INSTANCE = new AutoLog();
  
  public AutoLog() {
    super("AutoLog", "Logs when in danger.", Module.Category.MISC, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static AutoLog getInstance() {
    if (INSTANCE == null)
      INSTANCE = new AutoLog(); 
    return INSTANCE;
  }
  
  public void onTick() {
    if (!nullCheck() && 
      mc.player.getHealth() <= ((Float)this.health.getValue()).floatValue()) {
      Phobos.moduleManager.disableModule("AutoReconnect");
      mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
      if (((Boolean)this.logout.getValue()).booleanValue())
        disable(); 
    } 
  }
  
  @SubscribeEvent
  public void onReceivePacket(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketBlockChange && ((Boolean)this.bed.getValue()).booleanValue()) {
      SPacketBlockChange packet = (SPacketBlockChange)event.getPacket();
      if (packet.getBlockState().getBlock() == Blocks.BED && 
        mc.player.getDistanceSqToCenter(packet.getBlockPosition()) <= MathUtil.square(((Float)this.range.getValue()).floatValue())) {
        Phobos.moduleManager.disableModule("AutoReconnect");
        mc.player.connection.sendPacket((Packet)new SPacketDisconnect((ITextComponent)new TextComponentString("AutoLogged")));
        if (((Boolean)this.logout.getValue()).booleanValue())
          disable(); 
      } 
    } 
  }
}
