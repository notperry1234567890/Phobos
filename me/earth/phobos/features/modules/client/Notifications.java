//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Notifications extends Module {
  public Setting<Boolean> totemPops = register(new Setting("TotemPops", Boolean.valueOf(false)));
  
  public Setting<Boolean> totemNoti = register(new Setting("TotemNoti", Boolean.valueOf(true), v -> ((Boolean)this.totemPops.getValue()).booleanValue()));
  
  public Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(2000), Integer.valueOf(0), Integer.valueOf(5000), v -> ((Boolean)this.totemPops.getValue()).booleanValue(), "Delays messages."));
  
  public Setting<Boolean> clearOnLogout = register(new Setting("LogoutClear", Boolean.valueOf(false)));
  
  public Setting<Boolean> moduleMessage = register(new Setting("ModuleMessage", Boolean.valueOf(false)));
  
  public Setting<Boolean> list = register(new Setting("List", Boolean.valueOf(false), v -> ((Boolean)this.moduleMessage.getValue()).booleanValue()));
  
  private Setting<Boolean> readfile = register(new Setting("LoadFile", Boolean.valueOf(false), v -> ((Boolean)this.moduleMessage.getValue()).booleanValue()));
  
  public Setting<Boolean> watermark = register(new Setting("Watermark", Boolean.valueOf(true), v -> ((Boolean)this.moduleMessage.getValue()).booleanValue()));
  
  public Setting<Boolean> visualRange = register(new Setting("VisualRange", Boolean.valueOf(false)));
  
  public Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(true), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
  
  public Setting<Boolean> leaving = register(new Setting("Leaving", Boolean.valueOf(false), v -> ((Boolean)this.visualRange.getValue()).booleanValue()));
  
  public Setting<Boolean> pearls = register(new Setting("PearlNotifs", Boolean.valueOf(false)));
  
  public Setting<Boolean> crash = register(new Setting("Crash", Boolean.valueOf(false)));
  
  private List<EntityPlayer> knownPlayers = new ArrayList<>();
  
  private static List<String> modules = new ArrayList<>();
  
  private static final String fileName = "phobos/util/ModuleMessage_List.txt";
  
  private final Timer timer = new Timer();
  
  public Timer totemAnnounce = new Timer();
  
  private boolean check;
  
  private static Notifications INSTANCE = new Notifications();
  
  public Notifications() {
    super("Notifications", "Sends Messages.", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onLoad() {
    this.check = true;
    loadFile();
    this.check = false;
  }
  
  public void onEnable() {
    this.knownPlayers = new ArrayList<>();
    if (!this.check)
      loadFile(); 
  }
  
  public void onUpdate() {
    if (((Boolean)this.readfile.getValue()).booleanValue()) {
      if (!this.check) {
        Command.sendMessage("Loading File...");
        this.timer.reset();
        loadFile();
      } 
      this.check = true;
    } 
    if (this.check && this.timer.passedMs(750L)) {
      this.readfile.setValue(Boolean.valueOf(false));
      this.check = false;
    } 
    if (((Boolean)this.visualRange.getValue()).booleanValue()) {
      List<EntityPlayer> tickPlayerList = new ArrayList<>(mc.world.playerEntities);
      if (tickPlayerList.size() > 0)
        for (EntityPlayer player : tickPlayerList) {
          if (player.getName().equals(mc.player.getName()))
            continue; 
          if (!this.knownPlayers.contains(player)) {
            this.knownPlayers.add(player);
            if (Phobos.friendManager.isFriend(player)) {
              Command.sendMessage("Player §a" + player.getName() + "§r" + " entered your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
            } else {
              Command.sendMessage("Player §c" + player.getName() + "§r" + " entered your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
            } 
            return;
          } 
        }  
      if (this.knownPlayers.size() > 0)
        for (EntityPlayer player : this.knownPlayers) {
          if (!tickPlayerList.contains(player)) {
            this.knownPlayers.remove(player);
            if (((Boolean)this.leaving.getValue()).booleanValue())
              if (Phobos.friendManager.isFriend(player)) {
                Command.sendMessage("Player §a" + player.getName() + "§r" + " left your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
              } else {
                Command.sendMessage("Player §c" + player.getName() + "§r" + " left your visual range" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)player.posX + ", " + (int)player.posY + ", " + (int)player.posZ + ")!") : "!"), true);
              }  
            return;
          } 
        }  
    } 
  }
  
  public void loadFile() {
    List<String> fileInput = FileManager.readTextFileAllLines("phobos/util/ModuleMessage_List.txt");
    Iterator<String> i = fileInput.iterator();
    modules.clear();
    while (i.hasNext()) {
      String s = i.next();
      if (!s.replaceAll("\\s", "").isEmpty())
        modules.add(s); 
    } 
  }
  
  @SubscribeEvent
  public void onReceivePacket(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketSpawnObject && ((Boolean)this.pearls.getValue()).booleanValue()) {
      SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
      EntityPlayer player = mc.world.getClosestPlayer(packet.getX(), packet.getY(), packet.getZ(), 1.0D, false);
      if (player == null)
        return; 
      if (packet.getEntityID() == 85)
        Command.sendMessage("§cPearl thrown by " + player.getName() + " at X:" + (int)packet.getX() + " Y:" + (int)packet.getY() + " Z:" + (int)packet.getZ()); 
    } 
  }
  
  @SubscribeEvent
  public void onToggleModule(ClientEvent event) {
    if (!((Boolean)this.moduleMessage.getValue()).booleanValue())
      return; 
    if (event.getStage() == 0) {
      Module module = (Module)event.getFeature();
      if (!module.equals(this) && (modules.contains(module.getDisplayName()) || !((Boolean)this.list.getValue()).booleanValue())) {
        for (ChatLine line : mc.ingameGUI.persistantChatGUI.drawnChatLines) {
          if (line.getChatComponent().getUnformattedText().equalsIgnoreCase(Managers.getInstance().getRawCommandMessage() + " " + module.getDisplayName() + " disabled. "))
            mc.ingameGUI.persistantChatGUI.deleteChatLine(line.getChatLineID()); 
        } 
        if (((Boolean)this.watermark.getValue()).booleanValue()) {
          Command.sendMessage("§c" + module.getDisplayName() + " disabled.");
        } else {
          Command.sendSilentMessage("§c" + module.getDisplayName() + " disabled.");
        } 
      } 
    } 
    if (event.getStage() == 1) {
      Module module = (Module)event.getFeature();
      if (modules.contains(module.getDisplayName()) || !((Boolean)this.list.getValue()).booleanValue())
        if (((Boolean)this.watermark.getValue()).booleanValue()) {
          Command.sendMessage("§a" + module.getDisplayName() + " enabled.");
        } else {
          Command.sendSilentMessage("§a" + module.getDisplayName() + " enabled.");
        }  
    } 
  }
  
  public static Notifications getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Notifications(); 
    return INSTANCE;
  }
  
  public static void displayCrash(Exception e) {
    Command.sendMessage("§cException caught: " + e.getMessage());
  }
}
