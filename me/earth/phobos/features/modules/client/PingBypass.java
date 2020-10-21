//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.client;

import java.util.Objects;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PingBypass extends Module {
  public PingBypass() {
    super("PingBypass", "Big Hack", Module.Category.CLIENT, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketUseEntity) {
      CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
      Command.sendMessage(((Entity)Objects.<Entity>requireNonNull(packet.getEntityFromWorld((World)mc.world))).getEntityId() + "");
    } 
  }
}
