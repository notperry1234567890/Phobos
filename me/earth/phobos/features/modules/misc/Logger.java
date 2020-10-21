//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.lang.reflect.Field;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Logger extends Module {
  public Setting<Packets> packets = register(new Setting("Packets", Packets.OUTGOING));
  
  public Setting<Boolean> chat = register(new Setting("Chat", Boolean.valueOf(false)));
  
  public Setting<Boolean> fullInfo = register(new Setting("FullInfo", Boolean.valueOf(false)));
  
  public Logger() {
    super("Logger", "Logs stuff", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent(receiveCanceled = true)
  public void onPacketSend(PacketEvent.Send event) {
    if (this.packets.getValue() == Packets.OUTGOING || this.packets.getValue() == Packets.ALL)
      if (((Boolean)this.chat.getValue()).booleanValue()) {
        Command.sendMessage(event.getPacket().toString());
      } else {
        writePacketOnConsole(event.getPacket(), false);
      }  
  }
  
  @SubscribeEvent(receiveCanceled = true)
  public void onPacketReceive(PacketEvent.Receive event) {
    if (this.packets.getValue() == Packets.INCOMING || this.packets.getValue() == Packets.ALL)
      if (((Boolean)this.chat.getValue()).booleanValue()) {
        Command.sendMessage(event.getPacket().toString());
      } else {
        writePacketOnConsole(event.getPacket(), true);
      }  
  }
  
  private void writePacketOnConsole(Packet<?> packet, boolean in) {
    if (((Boolean)this.fullInfo.getValue()).booleanValue()) {
      System.out.println((in ? "In: " : "Send: ") + packet.getClass().getSimpleName() + " {");
      try {
        Class<?> clazz = packet.getClass();
        while (clazz != Object.class) {
          for (Field field : clazz.getDeclaredFields()) {
            if (field != null) {
              if (!field.isAccessible())
                field.setAccessible(true); 
              System.out.println(StringUtils.stripControlCodes("      " + field.getType().getSimpleName() + " " + field.getName() + " : " + field.get(packet)));
            } 
          } 
          clazz = clazz.getSuperclass();
        } 
      } catch (Exception e) {
        e.printStackTrace();
      } 
      System.out.println("}");
    } else {
      System.out.println(packet.toString());
    } 
  }
  
  public enum Packets {
    NONE, INCOMING, OUTGOING, ALL;
  }
}
