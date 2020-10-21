//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;

public class EchestBP extends Module {
  private GuiScreen echestScreen = null;
  
  public EchestBP() {
    super("EchestBP", "Allows to open your echest later.", Module.Category.PLAYER, false, false, false);
  }
  
  public void onUpdate() {
    if (mc.currentScreen instanceof GuiContainer) {
      Container container = ((GuiContainer)mc.currentScreen).inventorySlots;
      if (container instanceof ContainerChest && ((ContainerChest)container).getLowerChestInventory() instanceof InventoryBasic) {
        InventoryBasic basic = (InventoryBasic)((ContainerChest)container).getLowerChestInventory();
        if (basic.getName().equalsIgnoreCase("Ender Chest")) {
          this.echestScreen = mc.currentScreen;
          mc.currentScreen = null;
        } 
      } 
    } 
  }
  
  public void onDisable() {
    if (!fullNullCheck() && this.echestScreen != null)
      mc.displayGuiScreen(this.echestScreen); 
    this.echestScreen = null;
  }
}
