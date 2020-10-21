//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import java.awt.Font;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.gui.font.CustomFont;
import me.earth.phobos.features.modules.client.FontMod;
import me.earth.phobos.util.Timer;
import net.minecraft.util.math.MathHelper;

public class TextManager extends Feature {
  private CustomFont customFont = new CustomFont(new Font("Verdana", 0, 17), true, false);
  
  public int scaledWidth;
  
  public int scaledHeight;
  
  public int scaleFactor;
  
  private final Timer idleTimer = new Timer();
  
  private boolean idling;
  
  public TextManager() {
    updateResolution();
  }
  
  public void init(boolean startup) {
    FontMod cFont = Phobos.moduleManager.<FontMod>getModuleByClass(FontMod.class);
    try {
      setFontRenderer(new Font((String)cFont.fontName.getValue(), ((Integer)cFont.fontStyle.getValue()).intValue(), ((Integer)cFont.fontSize.getValue()).intValue()), ((Boolean)cFont.antiAlias.getValue()).booleanValue(), ((Boolean)cFont.fractionalMetrics.getValue()).booleanValue());
    } catch (Exception exception) {}
  }
  
  public void drawStringWithShadow(String text, float x, float y, int color) {
    drawString(text, x, y, color, true);
  }
  
  public void drawString(String text, float x, float y, int color, boolean shadow) {
    if (Phobos.moduleManager.isModuleEnabled(FontMod.class)) {
      if (shadow) {
        this.customFont.drawStringWithShadow(text, x, y, color);
      } else {
        this.customFont.drawString(text, x, y, color);
      } 
      return;
    } 
    mc.fontRenderer.drawString(text, x, y, color, shadow);
  }
  
  public int getStringWidth(String text) {
    if (Phobos.moduleManager.isModuleEnabled(FontMod.class))
      return this.customFont.getStringWidth(text); 
    return mc.fontRenderer.getStringWidth(text);
  }
  
  public int getFontHeight() {
    if (Phobos.moduleManager.isModuleEnabled(FontMod.class)) {
      String text = "A";
      return this.customFont.getStringHeight(text);
    } 
    return mc.fontRenderer.FONT_HEIGHT;
  }
  
  public void setFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
    this.customFont = new CustomFont(font, antiAlias, fractionalMetrics);
  }
  
  public Font getCurrentFont() {
    return this.customFont.getFont();
  }
  
  public void updateResolution() {
    this.scaledWidth = mc.displayWidth;
    this.scaledHeight = mc.displayHeight;
    this.scaleFactor = 1;
    boolean flag = mc.isUnicode();
    int i = mc.gameSettings.guiScale;
    if (i == 0)
      i = 1000; 
    while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240)
      this.scaleFactor++; 
    if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1)
      this.scaleFactor--; 
    double scaledWidthD = this.scaledWidth / this.scaleFactor;
    double scaledHeightD = this.scaledHeight / this.scaleFactor;
    this.scaledWidth = MathHelper.ceil(scaledWidthD);
    this.scaledHeight = MathHelper.ceil(scaledHeightD);
  }
  
  public String getIdleSign() {
    if (this.idleTimer.passedMs(500L)) {
      this.idling = !this.idling;
      this.idleTimer.reset();
    } 
    if (this.idling)
      return "_"; 
    return "";
  }
}
