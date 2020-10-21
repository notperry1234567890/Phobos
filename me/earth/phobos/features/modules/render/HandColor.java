package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class HandColor extends Module {
  public static HandColor INSTANCE;
  
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false)));
  
  public Setting<Integer> saturation = register(new Setting("Saturation", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> brightness = register(new Setting("Brightness", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> speed = register(new Setting("Speed", Integer.valueOf(40), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public HandColor() {
    super("HandColor", "Changes the color of your hands", Module.Category.RENDER, false, false, false);
    INSTANCE = this;
  }
}
