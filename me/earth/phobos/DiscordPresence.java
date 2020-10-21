//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.phobos.features.modules.misc.RPC;
import net.minecraft.client.Minecraft;

public class DiscordPresence {
  public static DiscordRichPresence presence;
  
  private static final DiscordRPC rpc = DiscordRPC.INSTANCE;
  
  private static Thread thread;
  
  static {
    presence = new DiscordRichPresence();
  }
  
  public static void start() {
    DiscordEventHandlers handlers = new DiscordEventHandlers();
    rpc.Discord_Initialize("737779695134834695", handlers, true, "");
    presence.startTimestamp = System.currentTimeMillis() / 1000L;
    presence.details = ((Minecraft.getMinecraft()).currentScreen instanceof net.minecraft.client.gui.GuiMainMenu) ? "In the main menu." : ("Playing " + (((Minecraft.getMinecraft()).currentServerData != null) ? (((Boolean)RPC.INSTANCE.showIP.getValue()).booleanValue() ? ("on " + (Minecraft.getMinecraft()).currentServerData.serverIP + ".") : " multiplayer.") : " singleplayer."));
    presence.state = (String)RPC.INSTANCE.state.getValue();
    presence.largeImageKey = "phobos";
    presence.largeImageText = "3arthh4ck 1.5.4";
    rpc.Discord_UpdatePresence(presence);
    thread = new Thread(() -> {
          while (!Thread.currentThread().isInterrupted()) {
            rpc.Discord_RunCallbacks();
            presence.details = "Playing " + (((Minecraft.getMinecraft()).currentServerData != null) ? (((Boolean)RPC.INSTANCE.showIP.getValue()).booleanValue() ? ("on " + (Minecraft.getMinecraft()).currentServerData.serverIP + ".") : " multiplayer.") : " singleplayer.");
            presence.state = (String)RPC.INSTANCE.state.getValue();
            rpc.Discord_UpdatePresence(presence);
            try {
              Thread.sleep(2000L);
            } catch (InterruptedException interruptedException) {}
          } 
        }"RPC-Callback-Handler");
    thread.start();
  }
  
  public static void stop() {
    if (thread != null && !thread.isInterrupted())
      thread.interrupt(); 
    rpc.Discord_Shutdown();
  }
}
