//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildHeight extends Module {
  private Setting<Integer> height = register(new Setting("Height", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public BuildHeight() {
    super("BuildHeight", "Allows you to place at build height", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && 
      event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
      CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
      if (packet.getPos().getY() >= ((Integer)this.height.getValue()).intValue() && packet.getDirection() == EnumFacing.UP)
        packet.placedBlockDirection = EnumFacing.DOWN; 
    } 
  }
}
