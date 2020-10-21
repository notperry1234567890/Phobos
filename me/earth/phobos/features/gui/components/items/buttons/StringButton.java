//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.gui.components.items.buttons;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

public class StringButton extends Button {
  private Setting setting;
  
  public boolean isListening;
  
  private CurrentString currentString = new CurrentString("");
  
  public StringButton(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.width = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
      int color = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
      int color1 = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
      RenderUtil.drawGradientRect(this.x, this.y, this.width + 7.4F, this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue() : color) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515), getState() ? (!isHovering(mouseX, mouseY) ? ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue() : color1) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
    } else {
      RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? Phobos.colorManager.getColorWithAlpha(((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Phobos.colorManager.getColorWithAlpha(((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
    } 
    if (this.isListening) {
      Phobos.textManager.drawStringWithShadow(this.currentString.getString() + Phobos.textManager.getIdleSign(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } else {
      Phobos.textManager.drawStringWithShadow((this.setting.shouldRenderName() ? (this.setting.getName() + " " + "§7") : "") + this.setting.getValue(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY))
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)); 
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (this.isListening) {
      if (keyCode == 1)
        return; 
      if (keyCode == 28) {
        enterString();
      } else if (keyCode == 14) {
        setString(removeLastChar(this.currentString.getString()));
      } else if (keyCode == 47 && (Keyboard.isKeyDown(157) || Keyboard.isKeyDown(29))) {
        try {
          setString(this.currentString.getString() + Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
        setString(this.currentString.getString() + typedChar);
      } 
    } 
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  private void enterString() {
    if (this.currentString.getString().isEmpty()) {
      this.setting.setValue(this.setting.getDefaultValue());
    } else {
      this.setting.setValue(this.currentString.getString());
    } 
    setString("");
    onMouseClick();
  }
  
  public int getHeight() {
    return 14;
  }
  
  public void toggle() {
    this.isListening = !this.isListening;
  }
  
  public boolean getState() {
    return !this.isListening;
  }
  
  public void setString(String newString) {
    this.currentString = new CurrentString(newString);
  }
  
  public static String removeLastChar(String str) {
    String output = "";
    if (str != null && str.length() > 0)
      output = str.substring(0, str.length() - 1); 
    return output;
  }
  
  public static class CurrentString {
    private String string;
    
    public CurrentString(String string) {
      this.string = string;
    }
    
    public String getString() {
      return this.string;
    }
  }
}
