//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.features.Feature;
import net.minecraft.network.Packet;

public class PacketManager extends Feature {
  private final List<Packet<?>> noEventPackets = new ArrayList<>();
  
  public void sendPacketNoEvent(Packet<?> packet) {
    if (packet != null && !nullCheck()) {
      this.noEventPackets.add(packet);
      mc.player.connection.sendPacket(packet);
    } 
  }
  
  public boolean shouldSendPacket(Packet<?> packet) {
    if (this.noEventPackets.contains(packet)) {
      this.noEventPackets.remove(packet);
      return false;
    } 
    return true;
  }
}
