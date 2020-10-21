//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClickGui extends Module {
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  public Setting<Boolean> rainbowRolling = register(new Setting("RollingRainbow", Boolean.valueOf(false), v -> (((Boolean)this.colorSync.getValue()).booleanValue() && ((Boolean)Colors.INSTANCE.rainbow.getValue()).booleanValue())));
  
  public Setting<String> prefix = register((new Setting("Prefix", ".")).setRenderName(true));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> hoverAlpha = register(new Setting("Alpha", Integer.valueOf(180), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Integer> alpha = register(new Setting("HoverAlpha", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(255)));
  
  public Setting<Boolean> customFov = register(new Setting("CustomFov", Boolean.valueOf(false)));
  
  public Setting<Float> fov = register(new Setting("Fov", Float.valueOf(150.0F), Float.valueOf(-180.0F), Float.valueOf(180.0F), v -> ((Boolean)this.customFov.getValue()).booleanValue()));
  
  public Setting<Boolean> openCloseChange = register(new Setting("Open/Close", Boolean.valueOf(false)));
  
  public Setting<String> open = register((new Setting("Open:", "", v -> ((Boolean)this.openCloseChange.getValue()).booleanValue())).setRenderName(true));
  
  public Setting<String> close = register((new Setting("Close:", "", v -> ((Boolean)this.openCloseChange.getValue()).booleanValue())).setRenderName(true));
  
  public Setting<String> moduleButton = register((new Setting("Buttons:", "", v -> !((Boolean)this.openCloseChange.getValue()).booleanValue())).setRenderName(true));
  
  public Setting<Boolean> devSettings = register(new Setting("DevSettings", Boolean.valueOf(false)));
  
  public Setting<Integer> topRed = register(new Setting("TopRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
  
  public Setting<Integer> topGreen = register(new Setting("TopGreen", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
  
  public Setting<Integer> topBlue = register(new Setting("TopBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
  
  public Setting<Integer> topAlpha = register(new Setting("TopAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.devSettings.getValue()).booleanValue()));
  
  private static ClickGui INSTANCE = new ClickGui();
  
  public ClickGui() {
    super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static ClickGui getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ClickGui(); 
    return INSTANCE;
  }
  
  public void onUpdate() {
    if (((Boolean)this.customFov.getValue()).booleanValue())
      mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, ((Float)this.fov.getValue()).floatValue()); 
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting().getFeature().equals(this)) {
      if (event.getSetting().equals(this.prefix)) {
        Phobos.commandManager.setPrefix((String)this.prefix.getPlannedValue());
        Command.sendMessage("Prefix set to §a" + Phobos.commandManager.getPrefix());
      } 
      Phobos.colorManager.setColor(((Integer)this.red.getPlannedValue()).intValue(), ((Integer)this.green.getPlannedValue()).intValue(), ((Integer)this.blue.getPlannedValue()).intValue(), ((Integer)this.hoverAlpha.getPlannedValue()).intValue());
    } 
  }
  
  public void onEnable() {
    mc.displayGuiScreen((GuiScreen)new PhobosGui());
  }
  
  public void onLoad() {
    if (((Boolean)this.colorSync.getValue()).booleanValue()) {
      Phobos.colorManager.setColor(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), ((Integer)this.hoverAlpha.getValue()).intValue());
    } else {
      Phobos.colorManager.setColor(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.hoverAlpha.getValue()).intValue());
    } 
    Phobos.commandManager.setPrefix((String)this.prefix.getValue());
  }
  
  public void onTick() {
    if (!(mc.currentScreen instanceof PhobosGui))
      disable(); 
  }
  
  public void onDisable() {
    if (mc.currentScreen instanceof PhobosGui)
      mc.displayGuiScreen(null); 
  }
}
