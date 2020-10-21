//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoRespawn extends Module {
  public Setting<Boolean> antiDeathScreen = register(new Setting("AntiDeathScreen", Boolean.valueOf(true)));
  
  public Setting<Boolean> deathCoords = register(new Setting("DeathCoords", Boolean.valueOf(false)));
  
  public Setting<Boolean> respawn = register(new Setting("Respawn", Boolean.valueOf(true)));
  
  public AutoRespawn() {
    super("AutoRespawn", "Respawns you when you die.", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onDisplayDeathScreen(GuiOpenEvent event) {
    if (event.getGui() instanceof net.minecraft.client.gui.GuiGameOver) {
      if (((Boolean)this.deathCoords.getValue()).booleanValue() && 
        event.getGui() instanceof net.minecraft.client.gui.GuiGameOver)
        Command.sendMessage(String.format("You died at x %d y %d z %d", new Object[] { Integer.valueOf((int)mc.player.posX), Integer.valueOf((int)mc.player.posY), Integer.valueOf((int)mc.player.posZ) })); 
      if ((((Boolean)this.respawn.getValue()).booleanValue() && mc.player.getHealth() <= 0.0F) || (((Boolean)this.antiDeathScreen.getValue()).booleanValue() && mc.player.getHealth() > 0.0F)) {
        event.setCanceled(true);
        mc.player.respawnPlayer();
      } 
    } 
  }
}
