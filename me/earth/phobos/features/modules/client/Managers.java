package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.TextUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Managers extends Module {
  public Setting<Boolean> betterFrames = register(new Setting("BetterMaxFPS", Boolean.valueOf(false)));
  
  private static Managers INSTANCE = new Managers();
  
  public Setting<String> commandBracket = register(new Setting("Bracket", "<"));
  
  public Setting<String> commandBracket2 = register(new Setting("Bracket2", ">"));
  
  public Setting<String> command = register(new Setting("Command", "Phobos.eu"));
  
  public Setting<TextUtil.Color> bracketColor = register(new Setting("BColor", TextUtil.Color.BLUE));
  
  public Setting<TextUtil.Color> commandColor = register(new Setting("CColor", TextUtil.Color.BLUE));
  
  public Setting<Integer> betterFPS = register(new Setting("MaxFPS", Integer.valueOf(300), Integer.valueOf(30), Integer.valueOf(1000), v -> ((Boolean)this.betterFrames.getValue()).booleanValue()));
  
  public Setting<Boolean> potions = register(new Setting("Potions", Boolean.valueOf(true)));
  
  public Setting<Integer> textRadarUpdates = register(new Setting("TRUpdates", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000)));
  
  public Setting<Integer> respondTime = register(new Setting("SeverTime", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000)));
  
  public Setting<Integer> moduleListUpdates = register(new Setting("ALUpdates", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(1000)));
  
  public Setting<Float> holeRange = register(new Setting("HoleRange", Float.valueOf(6.0F), Float.valueOf(1.0F), Float.valueOf(256.0F)));
  
  public Setting<Integer> holeUpdates = register(new Setting("HoleUpdates", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000)));
  
  public Setting<Integer> holeSync = register(new Setting("HoleSync", Integer.valueOf(10000), Integer.valueOf(1), Integer.valueOf(10000)));
  
  public Setting<Boolean> safety = register(new Setting("SafetyPlayer", Boolean.valueOf(false)));
  
  public Setting<Integer> safetyCheck = register(new Setting("SafetyCheck", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(150)));
  
  public Setting<Integer> safetySync = register(new Setting("SafetySync", Integer.valueOf(250), Integer.valueOf(1), Integer.valueOf(10000)));
  
  public Setting<ThreadMode> holeThread = register(new Setting("HoleThread", ThreadMode.WHILE));
  
  public Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(true)));
  
  public Setting<Boolean> oneDot15 = register(new Setting("1.15", Boolean.valueOf(false)));
  
  public Setting<Boolean> tRadarInv = register(new Setting("TRadarInv", Boolean.valueOf(true)));
  
  public Setting<Boolean> unfocusedCpu = register(new Setting("UnfocusedCPU", Boolean.valueOf(false)));
  
  public Setting<Integer> cpuFPS = register(new Setting("UnfocusedFPS", Integer.valueOf(60), Integer.valueOf(1), Integer.valueOf(60), v -> ((Boolean)this.unfocusedCpu.getValue()).booleanValue()));
  
  public Managers() {
    super("Management", "ClientManagement", Module.Category.CLIENT, false, false, true);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Managers getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Managers(); 
    return INSTANCE;
  }
  
  public void onLoad() {
    Phobos.commandManager.setClientMessage(getCommandMessage());
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting() != null && equals(event.getSetting().getFeature())) {
      if (event.getSetting().equals(this.holeThread))
        Phobos.holeManager.settingChanged(); 
      Phobos.commandManager.setClientMessage(getCommandMessage());
    } 
  }
  
  public String getCommandMessage() {
    return TextUtil.coloredString((String)this.commandBracket.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue()) + TextUtil.coloredString((String)this.command.getPlannedValue(), (TextUtil.Color)this.commandColor.getPlannedValue()) + TextUtil.coloredString((String)this.commandBracket2.getPlannedValue(), (TextUtil.Color)this.bracketColor.getPlannedValue());
  }
  
  public String getRawCommandMessage() {
    return (String)this.commandBracket.getValue() + (String)this.command.getValue() + (String)this.commandBracket2.getValue();
  }
  
  public enum ThreadMode {
    POOL, WHILE, NONE;
  }
}
