//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fullbright extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.GAMMA));
  
  public Setting<Boolean> effects = register(new Setting("Effects", Boolean.valueOf(false)));
  
  private float previousSetting = 1.0F;
  
  public Fullbright() {
    super("Fullbright", "Makes your game brighter.", Module.Category.RENDER, true, false, false);
  }
  
  public void onEnable() {
    this.previousSetting = mc.gameSettings.gammaSetting;
  }
  
  public void onUpdate() {
    if (this.mode.getValue() == Mode.GAMMA)
      mc.gameSettings.gammaSetting = 1000.0F; 
    if (this.mode.getValue() == Mode.POTION)
      mc.player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 5210)); 
  }
  
  public void onDisable() {
    if (this.mode.getValue() == Mode.POTION)
      mc.player.removePotionEffect(MobEffects.NIGHT_VISION); 
    mc.gameSettings.gammaSetting = this.previousSetting;
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() == 0 && event.getPacket() instanceof SPacketEntityEffect && (
      (Boolean)this.effects.getValue()).booleanValue()) {
      SPacketEntityEffect packet = (SPacketEntityEffect)event.getPacket();
      if (mc.player != null && packet.getEntityId() == mc.player.getEntityId() && (packet.getEffectId() == 9 || packet.getEffectId() == 15))
        event.setCanceled(true); 
    } 
  }
  
  public enum Mode {
    GAMMA, POTION;
  }
}
