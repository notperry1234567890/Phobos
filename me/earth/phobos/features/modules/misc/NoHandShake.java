//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import io.netty.buffer.Unpooled;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoHandShake extends Module {
  public NoHandShake() {
    super("NoHandshake", "Doesnt send your modlist to the server.", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof net.minecraftforge.fml.common.network.internal.FMLProxyPacket && !mc.isSingleplayer())
      event.setCanceled(true); 
    if (event.getPacket() instanceof CPacketCustomPayload) {
      CPacketCustomPayload packet = (CPacketCustomPayload)event.getPacket();
      if (packet.getChannelName().equals("MC|Brand"))
        packet.data = (new PacketBuffer(Unpooled.buffer())).writeString("vanilla"); 
    } 
  }
}
