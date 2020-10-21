//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.notifications;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Notifications {
  private final String text;
  
  private final long disableTime;
  
  private final float width;
  
  private final Timer timer = new Timer();
  
  public Notifications(String text, long disableTime) {
    this.text = text;
    this.disableTime = disableTime;
    this.width = ((HUD)Phobos.moduleManager.getModuleByClass(HUD.class)).renderer.getStringWidth(text);
    this.timer.reset();
  }
  
  public void onDraw(int y) {
    ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
    if (this.timer.passedMs(this.disableTime))
      Phobos.notificationManager.getNotifications().remove(this); 
    RenderUtil.drawRect((scaledResolution.getScaledWidth() - 4) - this.width, y, (scaledResolution.getScaledWidth() - 2), (y + ((HUD)Phobos.moduleManager.getModuleByClass(HUD.class)).renderer.getFontHeight() + 3), 1962934272);
    ((HUD)Phobos.moduleManager.getModuleByClass(HUD.class)).renderer.drawString(this.text, scaledResolution.getScaledWidth() - this.width - 3.0F, (y + 2), -1, true);
  }
}
