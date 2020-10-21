//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.FileUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.StringUtils;

public class Spammer extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.PWORD));
  
  public Setting<PwordMode> type = register(new Setting("Pword", PwordMode.CHAT, v -> (this.mode.getValue() == Mode.PWORD)));
  
  public Setting<DelayType> delayType = register(new Setting("DelayType", DelayType.S));
  
  public Setting<Integer> delay = register(new Setting("DelayS", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(20), v -> (this.delayType.getValue() == DelayType.S)));
  
  public Setting<Integer> delayDS = register(new Setting("DelayDS", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(500), v -> (this.delayType.getValue() == DelayType.DS)));
  
  public Setting<Integer> delayMS = register(new Setting("DelayDS", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.delayType.getValue() == DelayType.MS)));
  
  public Setting<String> msgTarget = register(new Setting("MsgTarget", "Target...", v -> (this.mode.getValue() == Mode.PWORD && this.type.getValue() == PwordMode.MSG)));
  
  public Setting<Boolean> greentext = register(new Setting("Greentext", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.FILE)));
  
  public Setting<Boolean> random = register(new Setting("Random", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.FILE)));
  
  public Setting<Boolean> loadFile = register(new Setting("LoadFile", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.FILE)));
  
  private final Timer timer = new Timer();
  
  private final List<String> sendPlayers = new ArrayList<>();
  
  private static final String fileName = "phobos/util/Spammer.txt";
  
  private static final String defaultMessage = "gg";
  
  private static final List<String> spamMessages = new ArrayList<>();
  
  private static final Random rnd = new Random();
  
  public Spammer() {
    super("Spammer", "Spams stuff.", Module.Category.MISC, true, false, false);
  }
  
  public void onLoad() {
    readSpamFile();
    disable();
  }
  
  public void onEnable() {
    if (fullNullCheck()) {
      disable();
      return;
    } 
    readSpamFile();
  }
  
  public void onLogin() {
    disable();
  }
  
  public void onLogout() {
    disable();
  }
  
  public void onDisable() {
    spamMessages.clear();
    this.timer.reset();
  }
  
  public void onUpdate() {
    if (fullNullCheck()) {
      disable();
      return;
    } 
    if (((Boolean)this.loadFile.getValue()).booleanValue()) {
      readSpamFile();
      this.loadFile.setValue(Boolean.valueOf(false));
    } 
    switch ((DelayType)this.delayType.getValue()) {
      case MSG:
        if (!this.timer.passedMs(((Integer)this.delayMS.getValue()).intValue()))
          return; 
        break;
      case EVERYONE:
        if (!this.timer.passedS(((Integer)this.delay.getValue()).intValue()))
          return; 
        break;
      case null:
        if (!this.timer.passedDs(((Integer)this.delayDS.getValue()).intValue()))
          return; 
        break;
    } 
    if (this.mode.getValue() == Mode.PWORD) {
      String target, msg = "  ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒\n ███▒█▒█▒███▒███▒███▒███\n █▒█▒█▒█▒█▒█▒█▒█▒█▒█▒█▒▒\n ███▒███▒█▒█▒███▒█▒█▒███\n █▒▒▒█▒█▒█▒█▒█▒█▒█▒█▒▒▒█\n █▒▒▒█▒█▒███▒███▒███▒███\n ▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒▒";
      switch ((PwordMode)this.type.getValue()) {
        case MSG:
          msg = "/msg " + (String)this.msgTarget.getValue() + msg;
          break;
        case EVERYONE:
          target = null;
          if (mc.getConnection() != null && mc.getConnection().getPlayerInfoMap() != null) {
            for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
              if (info != null && info.getDisplayName() != null)
                try {
                  String str = info.getDisplayName().getFormattedText();
                  String name = StringUtils.stripControlCodes(str);
                  if (name.equals(mc.player.getName()) || this.sendPlayers.contains(name))
                    continue; 
                  target = name;
                  this.sendPlayers.add(name);
                  break;
                } catch (Exception exception) {} 
            } 
            if (target == null) {
              this.sendPlayers.clear();
              return;
            } 
            msg = "/msg " + target + msg;
            break;
          } 
          return;
      } 
      mc.player.sendChatMessage(msg);
    } else if (spamMessages.size() > 0) {
      String messageOut;
      if (((Boolean)this.random.getValue()).booleanValue()) {
        int index = rnd.nextInt(spamMessages.size());
        messageOut = spamMessages.get(index);
        spamMessages.remove(index);
      } else {
        messageOut = spamMessages.get(0);
        spamMessages.remove(0);
      } 
      spamMessages.add(messageOut);
      if (((Boolean)this.greentext.getValue()).booleanValue())
        messageOut = "> " + messageOut; 
      mc.player.connection.sendPacket((Packet)new CPacketChatMessage(messageOut.replaceAll("§", "")));
    } 
    this.timer.reset();
  }
  
  private void readSpamFile() {
    List<String> fileInput = FileUtil.readTextFileAllLines("phobos/util/Spammer.txt");
    Iterator<String> i = fileInput.iterator();
    spamMessages.clear();
    while (i.hasNext()) {
      String s = i.next();
      if (!s.replaceAll("\\s", "").isEmpty())
        spamMessages.add(s); 
    } 
    if (spamMessages.size() == 0)
      spamMessages.add("gg"); 
  }
  
  public enum Mode {
    FILE, PWORD;
  }
  
  public enum PwordMode {
    MSG, EVERYONE, CHAT;
  }
  
  public enum DelayType {
    MS, DS, S;
  }
}
