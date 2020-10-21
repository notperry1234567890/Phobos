//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import net.minecraft.util.math.BlockPos;

public class HoleESP extends Module {
  private Setting<Integer> holes = register(new Setting("Holes", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(500)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true)));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  public Setting<Double> height = register(new Setting("Height", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
  
  private Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.box.getValue()).booleanValue()));
  
  private Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public Setting<Boolean> safeColor = register(new Setting("SafeColor", Boolean.valueOf(false)));
  
  private Setting<Integer> safeRed = register(new Setting("SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private Setting<Integer> safeGreen = register(new Setting("SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private Setting<Integer> safeBlue = register(new Setting("SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private Setting<Integer> safeAlpha = register(new Setting("SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  public Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> safecRed = register(new Setting("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private Setting<Integer> safecGreen = register(new Setting("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private Setting<Integer> safecBlue = register(new Setting("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private Setting<Integer> safecAlpha = register(new Setting("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private static HoleESP INSTANCE = new HoleESP();
  
  public HoleESP() {
    super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static HoleESP getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleESP(); 
    return INSTANCE;
  }
  
  public void onRender3D(Render3DEvent event) {
    int drawnHoles = 0;
    for (BlockPos pos : Phobos.holeManager.getSortedHoles()) {
      if (drawnHoles >= ((Integer)this.holes.getValue()).intValue())
        break; 
      if (pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)))
        continue; 
      if (RotationUtil.isInFov(pos)) {
        if (((Boolean)this.safeColor.getValue()).booleanValue() && Phobos.holeManager.isSafe(pos)) {
          RenderUtil.drawBoxESP(pos, new Color(((Integer)this.safeRed.getValue()).intValue(), ((Integer)this.safeGreen.getValue()).intValue(), ((Integer)this.safeBlue.getValue()).intValue(), ((Integer)this.safeAlpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.safecRed.getValue()).intValue(), ((Integer)this.safecGreen.getValue()).intValue(), ((Integer)this.safecBlue.getValue()).intValue(), ((Integer)this.safecAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue());
        } else {
          RenderUtil.drawBoxESP(pos, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue());
        } 
        drawnHoles++;
      } 
    } 
  }
}
