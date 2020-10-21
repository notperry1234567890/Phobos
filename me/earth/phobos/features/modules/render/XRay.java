//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XRay extends Module {
  public Setting<String> newBlock = register(new Setting("NewBlock", "Add Block..."));
  
  public Setting<Boolean> showBlocks = register(new Setting("ShowBlocks", Boolean.valueOf(false)));
  
  private static XRay INSTANCE = new XRay();
  
  public XRay() {
    super("XRay", "Lets you look through walls.", Module.Category.RENDER, false, false, true);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static XRay getInstance() {
    if (INSTANCE == null)
      INSTANCE = new XRay(); 
    return INSTANCE;
  }
  
  public void onEnable() {
    mc.renderGlobal.loadRenderers();
  }
  
  public void onDisable() {
    mc.renderGlobal.loadRenderers();
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this))
      if (event.getSetting().equals(this.newBlock) && !shouldRender((String)this.newBlock.getPlannedValue())) {
        register(new Setting((String)this.newBlock.getPlannedValue(), Boolean.valueOf(true), v -> ((Boolean)this.showBlocks.getValue()).booleanValue()));
        Command.sendMessage("<Xray> Added new Block: " + (String)this.newBlock.getPlannedValue());
        if (isOn())
          mc.renderGlobal.loadRenderers(); 
        event.setCanceled(true);
      } else {
        Setting setting = event.getSetting();
        if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newBlock) || setting.equals(this.showBlocks))
          return; 
        if (setting.getValue() instanceof Boolean && 
          !((Boolean)setting.getPlannedValue()).booleanValue()) {
          unregister(setting);
          if (isOn())
            mc.renderGlobal.loadRenderers(); 
          event.setCanceled(true);
        } 
      }  
  }
  
  public boolean shouldRender(Block block) {
    return shouldRender(block.getLocalizedName());
  }
  
  public boolean shouldRender(String name) {
    for (Setting setting : getSettings()) {
      if (name.equalsIgnoreCase(setting.getName()))
        return true; 
    } 
    return false;
  }
}
