package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;

public class Colors extends Module {
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), "Rainbow colors."));
  
  public Setting<Integer> rainbowSpeed = register(new Setting("Speed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowSaturation = register(new Setting("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowBrightness = register(new Setting("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public float hue;
  
  public Map<Integer, Integer> colorHeightMap = new HashMap<>();
  
  public static Colors INSTANCE;
  
  public Colors() {
    super("Colors", "Universal colors.", Module.Category.CLIENT, true, false, true);
    INSTANCE = this;
  }
  
  public void onTick() {
    int colorSpeed = 101 - ((Integer)this.rainbowSpeed.getValue()).intValue();
    this.hue = (float)(System.currentTimeMillis() % (360 * colorSpeed)) / 360.0F * colorSpeed;
    float tempHue = this.hue;
    for (int i = 0; i <= 510; i++) {
      this.colorHeightMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F)));
      tempHue += 0.0013071896F;
    } 
    if (((Boolean)(ClickGui.getInstance()).colorSync.getValue()).booleanValue())
      Phobos.colorManager.setColor(INSTANCE.getCurrentColor().getRed(), INSTANCE.getCurrentColor().getGreen(), INSTANCE.getCurrentColor().getBlue(), ((Integer)(ClickGui.getInstance()).hoverAlpha.getValue()).intValue()); 
  }
  
  public int getCurrentColorHex() {
    if (((Boolean)this.rainbow.getValue()).booleanValue())
      return Color.HSBtoRGB(this.hue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F); 
    return ColorUtil.toARGB(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue());
  }
  
  public Color getCurrentColor() {
    if (((Boolean)this.rainbow.getValue()).booleanValue())
      return Color.getHSBColor(this.hue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F); 
    return new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue());
  }
}
