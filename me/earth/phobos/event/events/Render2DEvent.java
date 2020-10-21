//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.event.events;

import me.earth.phobos.event.EventStage;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends EventStage {
  public float partialTicks;
  
  public ScaledResolution scaledResolution;
  
  public Render2DEvent(float partialTicks, ScaledResolution scaledResolution) {
    this.partialTicks = partialTicks;
    this.scaledResolution = scaledResolution;
  }
  
  public void setPartialTicks(float partialTicks) {
    this.partialTicks = partialTicks;
  }
  
  public void setScaledResolution(ScaledResolution scaledResolution) {
    this.scaledResolution = scaledResolution;
  }
  
  public double getScreenWidth() {
    return this.scaledResolution.getScaledWidth_double();
  }
  
  public double getScreenHeight() {
    return this.scaledResolution.getScaledHeight_double();
  }
}