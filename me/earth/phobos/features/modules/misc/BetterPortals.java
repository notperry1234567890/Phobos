package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BetterPortals extends Module {
  public Setting<Boolean> portalChat = register(new Setting("Chat", Boolean.valueOf(true), "Allows you to chat in portals."));
  
  public Setting<Boolean> godmode = register(new Setting("Godmode", Boolean.valueOf(false), "Portal Godmode."));
  
  public Setting<Boolean> fastPortal = register(new Setting("FastPortal", Boolean.valueOf(false)));
  
  public Setting<Integer> cooldown = register(new Setting("Cooldown", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(10), v -> ((Boolean)this.fastPortal.getValue()).booleanValue(), "Portal cooldown."));
  
  public Setting<Integer> time = register(new Setting("Time", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(80), v -> ((Boolean)this.fastPortal.getValue()).booleanValue(), "Time in Portal"));
  
  private static BetterPortals INSTANCE = new BetterPortals();
  
  public BetterPortals() {
    super("BetterPortals", "Tweaks for Portals", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static BetterPortals getInstance() {
    if (INSTANCE == null)
      INSTANCE = new BetterPortals(); 
    return INSTANCE;
  }
  
  public String getDisplayInfo() {
    if (((Boolean)this.godmode.getValue()).booleanValue())
      return "Godmode"; 
    return null;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && ((Boolean)this.godmode.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.client.CPacketConfirmTeleport)
      event.setCanceled(true); 
  }
}
