//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.player.TimerSpeed;

public class TimerManager extends Feature {
  private float timer = 1.0F;
  
  private TimerSpeed module;
  
  public void init() {
    this.module = Phobos.moduleManager.<TimerSpeed>getModuleByClass(TimerSpeed.class);
  }
  
  public void unload() {
    this.timer = 1.0F;
    mc.timer.tickLength = 50.0F;
  }
  
  public void update() {
    if (this.module != null && this.module.isEnabled())
      this.timer = this.module.speed; 
    mc.timer.tickLength = 50.0F / ((this.timer <= 0.0F) ? 0.1F : this.timer);
  }
  
  public void setTimer(float timer) {
    if (timer > 0.0F)
      this.timer = timer; 
  }
  
  public float getTimer() {
    return this.timer;
  }
  
  public void reset() {
    this.timer = 1.0F;
  }
}
