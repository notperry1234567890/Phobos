package me.earth.phobos.features.modules.player;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;

public class TimerSpeed extends Module {
  public Setting<Boolean> autoOff = register(new Setting("AutoOff", Boolean.valueOf(false)));
  
  public Setting<Integer> timeLimit = register(new Setting("Limit", Integer.valueOf(250), Integer.valueOf(1), Integer.valueOf(2500), v -> ((Boolean)this.autoOff.getValue()).booleanValue()));
  
  public Setting<TimerMode> mode = register(new Setting("Mode", TimerMode.NORMAL));
  
  public Setting<Float> timerSpeed = register(new Setting("Speed", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
  
  public Setting<Float> fastSpeed = register(new Setting("Fast", Float.valueOf(10.0F), Float.valueOf(0.1F), Float.valueOf(100.0F), v -> (this.mode.getValue() == TimerMode.SWITCH), "Fast Speed for switch."));
  
  public Setting<Integer> fastTime = register(new Setting("FastTime", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> (this.mode.getValue() == TimerMode.SWITCH), "How long you want to go fast.(ms * 10)"));
  
  public Setting<Integer> slowTime = register(new Setting("SlowTime", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(500), v -> (this.mode.getValue() == TimerMode.SWITCH), "Recover from too fast.(ms * 10)"));
  
  public Setting<Boolean> startFast = register(new Setting("StartFast", Boolean.valueOf(false), v -> (this.mode.getValue() == TimerMode.SWITCH)));
  
  public float speed = 1.0F;
  
  private Timer timer = new Timer();
  
  private Timer turnOffTimer = new Timer();
  
  private boolean fast = false;
  
  public TimerSpeed() {
    super("Timer", "Will speed up the game.", Module.Category.PLAYER, false, false, false);
  }
  
  public void onEnable() {
    this.turnOffTimer.reset();
    this.speed = ((Float)this.timerSpeed.getValue()).floatValue();
    if (!((Boolean)this.startFast.getValue()).booleanValue())
      this.timer.reset(); 
  }
  
  public void onUpdate() {
    if (((Boolean)this.autoOff.getValue()).booleanValue() && this.turnOffTimer.passedMs(((Integer)this.timeLimit.getValue()).intValue())) {
      disable();
      return;
    } 
    if (this.mode.getValue() == TimerMode.NORMAL) {
      this.speed = ((Float)this.timerSpeed.getValue()).floatValue();
      return;
    } 
    if (!this.fast && this.timer.passedDms(((Integer)this.slowTime.getValue()).intValue())) {
      this.fast = true;
      this.speed = ((Float)this.fastSpeed.getValue()).floatValue();
      this.timer.reset();
    } 
    if (this.fast && this.timer.passedDms(((Integer)this.fastTime.getValue()).intValue())) {
      this.fast = false;
      this.speed = ((Float)this.timerSpeed.getValue()).floatValue();
      this.timer.reset();
    } 
  }
  
  public void onDisable() {
    this.speed = 1.0F;
    Phobos.timerManager.reset();
    this.fast = false;
  }
  
  public String getDisplayInfo() {
    return this.timerSpeed.getValueAsString();
  }
  
  public enum TimerMode {
    NORMAL, SWITCH;
  }
}
