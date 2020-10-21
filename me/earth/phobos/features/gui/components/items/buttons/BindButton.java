//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.gui.components.items.buttons;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class BindButton extends Button {
  private Setting setting;
  
  public boolean isListening;
  
  public BindButton(Setting setting) {
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
      RenderUtil.drawRect(this.x, this.y, this.x + this.width + 7.4F, this.y + this.height - 0.5F, getState() ? (!isHovering(mouseX, mouseY) ? Phobos.colorManager.getColorWithAlpha(((Integer)((ClickGui)Phobos.moduleManager.getModuleByName("ClickGui")).hoverAlpha.getValue()).intValue()) : Phobos.colorManager.getColorWithAlpha(((Integer)((ClickGui)Phobos.moduleManager.getModuleByName("ClickGui")).alpha.getValue()).intValue())) : (!isHovering(mouseX, mouseY) ? 290805077 : -2007673515));
    } 
    if (this.isListening) {
      Phobos.textManager.drawStringWithShadow("Listening...", this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } else {
      Phobos.textManager.drawStringWithShadow(this.setting.getName() + " " + "§7" + ((Bind)this.setting.getValue()).toString(), this.x + 2.3F, this.y - 1.7F - PhobosGui.getClickGui().getTextOffset(), getState() ? -1 : -5592406);
    } 
  }
  
  public void update() {
    setHidden(!this.setting.isVisible());
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    if (isHovering(mouseX, mouseY))
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F)); 
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (this.isListening) {
      Bind bind = new Bind(keyCode);
      if (bind.toString().equalsIgnoreCase("Escape"))
        return; 
      if (bind.toString().equalsIgnoreCase("Delete"))
        bind = new Bind(-1); 
      this.setting.setValue(bind);
      onMouseClick();
    } 
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
}
