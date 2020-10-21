//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.gui.components.items.buttons;

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

public class UnlimitedSlider extends Button {
  public Setting setting;
  
  public UnlimitedSlider(Setting setting) {
    super(setting.getName());
    this.setting = setting;
    this.width = 15;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue()) {
      int color = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
      int color1 = ColorUtil.changeAlpha(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp((int)this.y + this.height, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue());
      RenderUtil.drawGradientRect((int)this.x, (int)this.y, this.width + 7.4F, this.height, color, color1);
    } else {
      RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, !isHovering(mouseX, mouseY) ? Phobos.colorManager.getColorWithAlpha(((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).hoverAlpha.getValue()).intValue()) : Phobos.colorManager.getColorWithAlpha(((Integer)((ClickGui)Phobos.moduleManager.getModuleByClass(ClickGui.class)).alpha.getValue()).intValue()));
    } 
    Phobos.textManager.drawStringWithShadow(" - " + this.setting.getName() + " " + "§7" + this.setting.getValue() + "§r" + " +", this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY)) {
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      if (isRight(mouseX)) {
        if (this.setting.getValue() instanceof Double) {
          this.setting.setValue(Double.valueOf(((Double)this.setting.getValue()).doubleValue() + 1.0D));
        } else if (this.setting.getValue() instanceof Float) {
          this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() + 1.0F));
        } else if (this.setting.getValue() instanceof Integer) {
          this.setting.setValue(Integer.valueOf(((Integer)this.setting.getValue()).intValue() + 1));
        } 
      } else if (this.setting.getValue() instanceof Double) {
        this.setting.setValue(Double.valueOf(((Double)this.setting.getValue()).doubleValue() - 1.0D));
      } else if (this.setting.getValue() instanceof Float) {
        this.setting.setValue(Float.valueOf(((Float)this.setting.getValue()).floatValue() - 1.0F));
      } else if (this.setting.getValue() instanceof Integer) {
        this.setting.setValue(Integer.valueOf(((Integer)this.setting.getValue()).intValue() - 1));
      } 
    } 
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  public int getHeight() {
    return 14;
  }
  
  public void toggle() {}
  
  public boolean getState() {
    return true;
  }
  
  public boolean isRight(int x) {
    return (x > this.x + (this.width + 7.4F) / 2.0F);
  }
}
