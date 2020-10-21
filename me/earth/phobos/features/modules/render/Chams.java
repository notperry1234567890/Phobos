package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class Chams extends Module {
  private static Chams INSTANCE = new Chams();
  
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  public Setting<Boolean> colored = register(new Setting("Colored", Boolean.valueOf(false)));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), v -> ((Boolean)this.colored.getValue()).booleanValue()));
  
  public Setting<Integer> saturation = register(new Setting("Saturation", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(100), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> brightness = register(new Setting("Brightness", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> speed = register(new Setting("Speed", Integer.valueOf(40), Integer.valueOf(1), Integer.valueOf(100), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Boolean> xqz = register(new Setting("XQZ", Boolean.valueOf(false), v -> (((Boolean)this.colored.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.colored.getValue()).booleanValue()));
  
  public Setting<Integer> hiddenRed = register(new Setting("Hidden Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> hiddenGreen = register(new Setting("Hidden Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> hiddenBlue = register(new Setting("Hidden Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Setting<Integer> hiddenAlpha = register(new Setting("Hidden Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.colored.getValue()).booleanValue() && ((Boolean)this.xqz.getValue()).booleanValue() && !((Boolean)this.rainbow.getValue()).booleanValue())));
  
  public Chams() {
    super("Chams", "Renders players through walls.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Chams getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Chams(); 
    return INSTANCE;
  }
}
