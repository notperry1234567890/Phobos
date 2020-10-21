package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class Reach extends Module {
  public Setting<Boolean> override = register(new Setting("Override", Boolean.valueOf(false)));
  
  public Setting<Float> add = register(new Setting("Add", Float.valueOf(3.0F), v -> !((Boolean)this.override.getValue()).booleanValue()));
  
  public Setting<Float> reach = register(new Setting("Reach", Float.valueOf(6.0F), v -> ((Boolean)this.override.getValue()).booleanValue()));
  
  private static Reach INSTANCE = new Reach();
  
  public Reach() {
    super("Reach", "Extends your block reach", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Reach getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Reach(); 
    return INSTANCE;
  }
  
  public String getDisplayInfo() {
    return ((Boolean)this.override.getValue()).booleanValue() ? ((Float)this.reach.getValue()).toString() : ((Float)this.add.getValue()).toString();
  }
}
