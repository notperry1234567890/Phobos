package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class CameraClip extends Module {
  public Setting<Boolean> extend = register(new Setting("Extend", Boolean.valueOf(false)));
  
  public Setting<Double> distance = register(new Setting("Distance", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(50.0D), v -> ((Boolean)this.extend.getValue()).booleanValue(), "By how much you want to extend the distance."));
  
  private static CameraClip INSTANCE = new CameraClip();
  
  public CameraClip() {
    super("CameraClip", "Makes your Camera clip.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static CameraClip getInstance() {
    if (INSTANCE == null)
      INSTANCE = new CameraClip(); 
    return INSTANCE;
  }
}
