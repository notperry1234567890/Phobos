//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import me.earth.phobos.util.Util;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryManager implements Util {
  private int recoverySlot = -1;
  
  public void update() {
    if (this.recoverySlot != -1) {
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange((this.recoverySlot == 8) ? 7 : (this.recoverySlot + 1)));
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(this.recoverySlot));
      mc.player.inventory.currentItem = this.recoverySlot;
      mc.playerController.syncCurrentPlayItem();
      this.recoverySlot = -1;
    } 
  }
  
  public void recoverSilent(int slot) {
    this.recoverySlot = slot;
  }
}
