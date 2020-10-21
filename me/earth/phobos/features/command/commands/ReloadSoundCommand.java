//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.command.commands;

import me.earth.phobos.features.command.Command;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ReloadSoundCommand extends Command {
  public ReloadSoundCommand() {
    super("sound", new String[0]);
  }
  
  public void execute(String[] commands) {
    try {
      SoundManager sndManager = (SoundManager)ObfuscationReflectionHelper.getPrivateValue(SoundHandler.class, mc.getSoundHandler(), new String[] { "sndManager", "sndManager" });
      sndManager.reloadSoundSystem();
      sendMessage("§aReloaded Sound System.");
    } catch (Exception e) {
      System.out.println("Could not restart sound manager: " + e.toString());
      e.printStackTrace();
      sendMessage("§cCouldnt Reload Sound System!");
    } 
  }
}
