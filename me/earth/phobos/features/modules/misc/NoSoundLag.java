//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoSoundLag extends Module {
  private final Setting<Boolean> crystals = register(new Setting("Crystals", Boolean.valueOf(true)));
  
  private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(true)));
  
  private static final Set<SoundEvent> BLACKLIST = Sets.newHashSet((Object[])new SoundEvent[] { SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundEvents.ITEM_ARMOR_EQIIP_ELYTRA, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER });
  
  public NoSoundLag() {
    super("NoSoundLag", "Prevents Lag through sound spam.", Module.Category.MISC, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketReceived(PacketEvent.Receive event) {
    if (event != null && event.getPacket() != null && mc.player != null && mc.world != null && 
      event.getPacket() instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
      if (((Boolean)this.crystals.getValue()).booleanValue() && packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : mc.world.loadedEntityList) {
          if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal && entity.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0D)
            toRemove.add(entity); 
        } 
        for (Entity entity : toRemove)
          entity.setDead(); 
      } 
      if (BLACKLIST.contains(packet.getSound()) && ((Boolean)this.armor.getValue()).booleanValue())
        event.setCanceled(true); 
    } 
  }
}
