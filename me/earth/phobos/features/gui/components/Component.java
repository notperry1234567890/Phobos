//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.gui.components;

import java.util.ArrayList;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.gui.PhobosGui;
import me.earth.phobos.features.gui.components.items.Item;
import me.earth.phobos.features.gui.components.items.buttons.Button;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Component extends Feature {
  private int x;
  
  private int y;
  
  private int x2;
  
  private int y2;
  
  private int width;
  
  private int height;
  
  private boolean open;
  
  public boolean drag;
  
  private final ArrayList<Item> items = new ArrayList<>();
  
  private boolean hidden = false;
  
  public Component(String name, int x, int y, boolean open) {
    super(name);
    this.x = x;
    this.y = y;
    this.width = 88;
    this.height = 18;
    this.open = open;
    setupItems();
  }
  
  public void setupItems() {}
  
  private void drag(int mouseX, int mouseY) {
    if (!this.drag)
      return; 
    this.x = this.x2 + mouseX;
    this.y = this.y2 + mouseY;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drag(mouseX, mouseY);
    float totalItemHeight = this.open ? (getTotalItemHeight() - 2.0F) : 0.0F;
    int color = -7829368;
    if (((Boolean)(ClickGui.getInstance()).devSettings.getValue()).booleanValue())
      color = ((Boolean)(ClickGui.getInstance()).colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toARGB(((Integer)(ClickGui.getInstance()).topRed.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topGreen.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topBlue.getValue()).intValue(), ((Integer)(ClickGui.getInstance()).topAlpha.getValue()).intValue()); 
    if (((Boolean)(ClickGui.getInstance()).rainbowRolling.getValue()).booleanValue() && ((Boolean)(ClickGui.getInstance()).colorSync.getValue()).booleanValue() && ((Boolean)Colors.INSTANCE.rainbow.getValue()).booleanValue()) {
      RenderUtil.drawGradientRect(this.x, this.y - 1.5F, this.width, (this.height - 4), ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp(this.y, 0, this.renderer.scaledHeight)))).intValue(), ((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(MathUtil.clamp(this.y + this.height - 4, 0, this.renderer.scaledHeight)))).intValue());
    } else {
      RenderUtil.drawRect(this.x, this.y - 1.5F, (this.x + this.width), (this.y + this.height - 6), color);
    } 
    if (this.open)
      RenderUtil.drawRect(this.x, this.y + 12.5F, (this.x + this.width), (this.y + this.height) + totalItemHeight, 1996488704); 
    Phobos.textManager.drawStringWithShadow(getName(), this.x + 3.0F, this.y - 4.0F - PhobosGui.getClickGui().getTextOffset(), -1);
    if (this.open) {
      float y = (getY() + getHeight()) - 3.0F;
      for (Item item : getItems()) {
        if (!item.isHidden()) {
          item.setLocation(this.x + 2.0F, y);
          item.setWidth(getWidth() - 4);
          item.drawScreen(mouseX, mouseY, partialTicks);
          y += item.getHeight() + 1.5F;
        } 
      } 
    } 
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
      this.x2 = this.x - mouseX;
      this.y2 = this.y - mouseY;
      PhobosGui.getClickGui().getComponents().forEach(component -> {
            if (component.drag)
              component.drag = false; 
          });
      this.drag = true;
      return;
    } 
    if (mouseButton == 1 && isHovering(mouseX, mouseY)) {
      this.open = !this.open;
      mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
      return;
    } 
    if (!this.open)
      return; 
    getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
  }
  
  public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    if (releaseButton == 0)
      this.drag = false; 
    if (!this.open)
      return; 
    getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
  }
  
  public void onKeyTyped(char typedChar, int keyCode) {
    if (!this.open)
      return; 
    getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
  }
  
  public void addButton(Button button) {
    this.items.add(button);
  }
  
  public void setX(int x) {
    this.x = x;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public void setHeight(int height) {
    this.height = height;
  }
  
  public void setWidth(int width) {
    this.width = width;
  }
  
  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }
  
  public boolean isHidden() {
    return this.hidden;
  }
  
  public boolean isOpen() {
    return this.open;
  }
  
  public final ArrayList<Item> getItems() {
    return this.items;
  }
  
  private boolean isHovering(int mouseX, int mouseY) {
    return (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getHeight() - (this.open ? 2 : 0));
  }
  
  private float getTotalItemHeight() {
    float height = 0.0F;
    for (Item item : getItems())
      height += item.getHeight() + 1.5F; 
    return height;
  }
}
