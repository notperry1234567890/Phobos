//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.command.commands;

import java.util.Map;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.misc.ToolTips;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PeekCommand extends Command {
  public PeekCommand() {
    super("peek", new String[] { "<player>" });
  }
  
  public void execute(String[] commands) {
    if (commands.length == 1) {
      ItemStack stack = mc.player.getHeldItemMainhand();
      if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemShulkerBox) {
        ToolTips.displayInv(stack, null);
      } else {
        Command.sendMessage("§cYou need to hold a Shulker in your mainhand.");
        return;
      } 
    } 
    if (commands.length > 1)
      if (ToolTips.getInstance().isOn() && ((Boolean)(ToolTips.getInstance()).shulkerSpy.getValue()).booleanValue()) {
        for (Map.Entry<EntityPlayer, ItemStack> entry : (Iterable<Map.Entry<EntityPlayer, ItemStack>>)(ToolTips.getInstance()).spiedPlayers.entrySet()) {
          if (((EntityPlayer)entry.getKey()).getName().equalsIgnoreCase(commands[0])) {
            ItemStack stack = entry.getValue();
            ToolTips.displayInv(stack, ((EntityPlayer)entry.getKey()).getName());
            break;
          } 
        } 
      } else {
        Command.sendMessage("§cYou need to turn on Tooltips - ShulkerSpy");
      }  
  }
}
