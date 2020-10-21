//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorMessage extends Module {
  private final Setting<Integer> armorThreshhold = register(new Setting("Armor%", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(100)));
  
  private final Setting<Boolean> notifySelf = register(new Setting("NotifySelf", Boolean.valueOf(true)));
  
  private final Setting<Boolean> notification = register(new Setting("Notification", Boolean.valueOf(true)));
  
  private final Map<EntityPlayer, Integer> entityArmorArraylist = new HashMap<>();
  
  private final Timer timer = new Timer();
  
  public ArmorMessage() {
    super("ArmorMessage", "Message friends when their armor is low", Module.Category.COMBAT, true, false, false);
  }
  
  @SubscribeEvent
  public void onUpdate(UpdateWalkingPlayerEvent event) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player.isDead || !Phobos.friendManager.isFriend(player.getName()))
        continue; 
      for (ItemStack stack : player.inventory.armorInventory) {
        if (stack != ItemStack.EMPTY) {
          int percent = DamageUtil.getRoundedDamage(stack);
          if (percent <= ((Integer)this.armorThreshhold.getValue()).intValue() && !this.entityArmorArraylist.containsKey(player)) {
            if (player == mc.player && ((Boolean)this.notifySelf.getValue()).booleanValue()) {
              Command.sendMessage(player.getName() + " watchout your " + getArmorPieceName(stack) + " low dura!", ((Boolean)this.notification.getValue()).booleanValue());
            } else {
              mc.player.sendChatMessage("/msg " + player.getName() + " " + player.getName() + " watchout your " + getArmorPieceName(stack) + " low dura!");
            } 
            this.entityArmorArraylist.put(player, Integer.valueOf(player.inventory.armorInventory.indexOf(stack)));
          } 
          if (this.entityArmorArraylist.containsKey(player) && ((Integer)this.entityArmorArraylist.get(player)).intValue() == player.inventory.armorInventory.indexOf(stack) && percent > ((Integer)this.armorThreshhold.getValue()).intValue())
            this.entityArmorArraylist.remove(player); 
        } 
      } 
      if (this.entityArmorArraylist.containsKey(player) && player.inventory.armorInventory.get(((Integer)this.entityArmorArraylist.get(player)).intValue()) == ItemStack.EMPTY)
        this.entityArmorArraylist.remove(player); 
    } 
  }
  
  private String getArmorPieceName(ItemStack stack) {
    if (stack.getItem() == Items.DIAMOND_HELMET || stack.getItem() == Items.GOLDEN_HELMET || stack.getItem() == Items.IRON_HELMET || stack.getItem() == Items.CHAINMAIL_HELMET || stack.getItem() == Items.LEATHER_HELMET)
      return "helmet is"; 
    if (stack.getItem() == Items.DIAMOND_CHESTPLATE || stack.getItem() == Items.GOLDEN_CHESTPLATE || stack.getItem() == Items.IRON_CHESTPLATE || stack.getItem() == Items.CHAINMAIL_CHESTPLATE || stack.getItem() == Items.LEATHER_CHESTPLATE)
      return "chestplate is"; 
    if (stack.getItem() == Items.DIAMOND_LEGGINGS || stack.getItem() == Items.GOLDEN_LEGGINGS || stack.getItem() == Items.IRON_LEGGINGS || stack.getItem() == Items.CHAINMAIL_LEGGINGS || stack.getItem() == Items.LEATHER_LEGGINGS)
      return "leggings are"; 
    return "boots are";
  }
}
