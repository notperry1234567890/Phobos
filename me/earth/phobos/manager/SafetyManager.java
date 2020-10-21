package me.earth.phobos.manager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.util.Timer;

public class SafetyManager extends Feature implements Runnable {
  private final Timer syncTimer = new Timer();
  
  private ScheduledExecutorService service;
  
  private final AtomicBoolean SAFE = new AtomicBoolean(false);
  
  public void run() {}
  
  public void onUpdate() {
    run();
  }
  
  public String getSafetyString() {
    if (this.SAFE.get())
      return "§aSecure"; 
    return "§cUnsafe";
  }
  
  public boolean isSafe() {
    return this.SAFE.get();
  }
  
  public ScheduledExecutorService getService() {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(this, 0L, ((Integer)(Managers.getInstance()).safetyCheck.getValue()).intValue(), TimeUnit.MILLISECONDS);
    return service;
  }
}
