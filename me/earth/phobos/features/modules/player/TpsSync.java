package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class TpsSync extends Module {
  public Setting<Boolean> mining = register(new Setting("Mining", Boolean.valueOf(true)));
  
  public Setting<Boolean> attack = register(new Setting("Attack", Boolean.valueOf(false)));
  
  private static TpsSync INSTANCE = new TpsSync();
  
  public TpsSync() {
    super("TpsSync", "Syncs your client with the TPS.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static TpsSync getInstance() {
    if (INSTANCE == null)
      INSTANCE = new TpsSync(); 
    return INSTANCE;
  }
}
