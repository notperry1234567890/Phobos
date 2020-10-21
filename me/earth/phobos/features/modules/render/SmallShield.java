//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class SmallShield extends Module {
  public Setting<Boolean> normalOffset = register(new Setting("OffNormal", Boolean.valueOf(false)));
  
  public Setting<Float> offset = register(new Setting("Offset", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> ((Boolean)this.normalOffset.getValue()).booleanValue()));
  
  public Setting<Float> offX = register(new Setting("OffX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F), v -> !((Boolean)this.normalOffset.getValue()).booleanValue()));
  
  public Setting<Float> offY = register(new Setting("OffY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F), v -> !((Boolean)this.normalOffset.getValue()).booleanValue()));
  
  public Setting<Float> mainX = register(new Setting("MainX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public Setting<Float> mainY = register(new Setting("MainY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  private static SmallShield INSTANCE = new SmallShield();
  
  public SmallShield() {
    super("SmallShield", "Makes you offhand lower.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    if (((Boolean)this.normalOffset.getValue()).booleanValue())
      mc.entityRenderer.itemRenderer.equippedProgressOffHand = ((Float)this.offset.getValue()).floatValue(); 
  }
  
  public static SmallShield getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new SmallShield(); 
    return INSTANCE;
  }
}
