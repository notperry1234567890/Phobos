//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.modules.client.Managers;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionManager extends Feature {
  private final Map<EntityPlayer, PotionList> potions = new ConcurrentHashMap<>();
  
  public void onLogout() {
    this.potions.clear();
  }
  
  public void updatePlayer() {
    PotionList list = new PotionList();
    for (PotionEffect effect : mc.player.getActivePotionEffects())
      list.addEffect(effect); 
    this.potions.put(mc.player, list);
  }
  
  public void update() {
    updatePlayer();
    if (HUD.getInstance().isOn() && ((Boolean)(HUD.getInstance()).textRadar.getValue()).booleanValue() && ((Boolean)(Managers.getInstance()).potions.getValue()).booleanValue()) {
      ArrayList<EntityPlayer> removeList = new ArrayList<>();
      for (Map.Entry<EntityPlayer, PotionList> potionEntry : this.potions.entrySet()) {
        boolean notFound = true;
        for (EntityPlayer player : mc.world.playerEntities) {
          if (this.potions.get(player) == null) {
            PotionList list = new PotionList();
            for (PotionEffect effect : player.getActivePotionEffects())
              list.addEffect(effect); 
            this.potions.put(player, list);
            notFound = false;
          } 
          if (((EntityPlayer)potionEntry.getKey()).equals(player))
            notFound = false; 
        } 
        if (notFound)
          removeList.add(potionEntry.getKey()); 
      } 
      for (EntityPlayer player : removeList)
        this.potions.remove(player); 
    } 
  }
  
  public List<PotionEffect> getOwnPotions() {
    return getPlayerPotions((EntityPlayer)mc.player);
  }
  
  public List<PotionEffect> getPlayerPotions(EntityPlayer player) {
    PotionList list = this.potions.get(player);
    List<PotionEffect> potions = new ArrayList<>();
    if (list != null)
      potions = list.getEffects(); 
    return potions;
  }
  
  public void onTotemPop(EntityPlayer player) {
    PotionList list = new PotionList();
    this.potions.put(player, list);
  }
  
  public PotionEffect[] getImportantPotions(EntityPlayer player) {
    PotionEffect[] array = new PotionEffect[3];
    for (PotionEffect effect : getPlayerPotions(player)) {
      Potion potion = effect.getPotion();
      switch (I18n.format(potion.getName(), new Object[0]).toLowerCase()) {
        case "strength":
          array[0] = effect;
        case "weakness":
          array[1] = effect;
        case "speed":
          array[2] = effect;
      } 
    } 
    return array;
  }
  
  public String getPotionString(PotionEffect effect) {
    Potion potion = effect.getPotion();
    return I18n.format(potion.getName(), new Object[0]) + " " + ((!((Boolean)(HUD.getInstance()).potions1.getValue()).booleanValue() && effect.getAmplifier() == 0) ? "" : ((effect.getAmplifier() + 1) + " ")) + "§f" + Potion.getPotionDurationString(effect, 1.0F);
  }
  
  public String getColoredPotionString(PotionEffect effect) {
    Potion potion = effect.getPotion();
    switch (I18n.format(potion.getName(), new Object[0])) {
      case "Jump Boost":
      case "Speed":
        return "§b" + getPotionString(effect);
      case "Resistance":
      case "Strength":
        return "§c" + getPotionString(effect);
      case "Wither":
      case "Slowness":
      case "Weakness":
        return "§0" + getPotionString(effect);
      case "Absorption":
        return "§9" + getPotionString(effect);
      case "Haste":
      case "Fire Resistance":
        return "§6" + getPotionString(effect);
      case "Regeneration":
        return "§d" + getPotionString(effect);
      case "Night Vision":
      case "Poison":
        return "§a" + getPotionString(effect);
    } 
    return "§f" + getPotionString(effect);
  }
  
  public String getTextRadarPotionWithDuration(EntityPlayer player) {
    PotionEffect[] array = getImportantPotions(player);
    PotionEffect strength = array[0];
    PotionEffect weakness = array[1];
    PotionEffect speed = array[2];
    return "" + ((strength != null) ? ("§c S" + (strength.getAmplifier() + 1) + " " + Potion.getPotionDurationString(strength, 1.0F)) : "") + ((weakness != null) ? ("§8 W " + 
      Potion.getPotionDurationString(weakness, 1.0F)) : "") + ((speed != null) ? ("§b S" + (speed
      .getAmplifier() + 1) + " " + Potion.getPotionDurationString(weakness, 1.0F)) : "");
  }
  
  public String getTextRadarPotion(EntityPlayer player) {
    PotionEffect[] array = getImportantPotions(player);
    PotionEffect strength = array[0];
    PotionEffect weakness = array[1];
    PotionEffect speed = array[2];
    return "" + ((strength != null) ? ("§c S" + (strength.getAmplifier() + 1) + " ") : "") + ((weakness != null) ? "§8 W " : "") + ((speed != null) ? ("§b S" + (speed
      
      .getAmplifier() + 1) + " ") : "");
  }
  
  public static class PotionList {
    private List<PotionEffect> effects = new ArrayList<>();
    
    public void addEffect(PotionEffect effect) {
      if (effect != null)
        this.effects.add(effect); 
    }
    
    public List<PotionEffect> getEffects() {
      return this.effects;
    }
  }
}
