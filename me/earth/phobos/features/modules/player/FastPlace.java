//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastPlace extends Module {
  private Setting<Boolean> all = register(new Setting("All", Boolean.valueOf(false)));
  
  private Setting<Boolean> obby = register(new Setting("Obsidian", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
  
  private Setting<Boolean> enderChests = register(new Setting("EnderChests", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
  
  private Setting<Boolean> crystals = register(new Setting("Crystals", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
  
  private Setting<Boolean> exp = register(new Setting("Experience", Boolean.valueOf(false), v -> !((Boolean)this.all.getValue()).booleanValue()));
  
  private Setting<Boolean> feetExp = register(new Setting("ExpFeet", Boolean.valueOf(false)));
  
  private Setting<Boolean> fastCrystal = register(new Setting("PacketCrystal", Boolean.valueOf(false)));
  
  private BlockPos mousePos = null;
  
  public FastPlace() {
    super("FastPlace", "Fast everything.", Module.Category.PLAYER, true, false, false);
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Boolean)this.feetExp.getValue()).booleanValue()) {
      boolean mainHand = (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE);
      boolean offHand = (mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE);
      if (mc.gameSettings.keyBindUseItem.isKeyDown() && ((mc.player.getActiveHand() == EnumHand.MAIN_HAND && mainHand) || (mc.player.getActiveHand() == EnumHand.OFF_HAND && offHand)))
        Phobos.rotationManager.lookAtVec3d(mc.player.getPositionVector()); 
    } 
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (InventoryUtil.holdingItem(ItemExpBottle.class) && ((Boolean)this.exp.getValue()).booleanValue())
      mc.rightClickDelayTimer = 0; 
    if (InventoryUtil.holdingItem(BlockObsidian.class) && ((Boolean)this.obby.getValue()).booleanValue())
      mc.rightClickDelayTimer = 0; 
    if (InventoryUtil.holdingItem(BlockEnderChest.class) && ((Boolean)this.enderChests.getValue()).booleanValue())
      mc.rightClickDelayTimer = 0; 
    if (((Boolean)this.all.getValue()).booleanValue())
      mc.rightClickDelayTimer = 0; 
    if (InventoryUtil.holdingItem(ItemEndCrystal.class) && (((Boolean)this.crystals.getValue()).booleanValue() || ((Boolean)this.all.getValue()).booleanValue()))
      mc.rightClickDelayTimer = 0; 
    if (((Boolean)this.fastCrystal.getValue()).booleanValue() && mc.gameSettings.keyBindUseItem.isKeyDown()) {
      boolean offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
      if (offhand || mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
        RayTraceResult result = mc.objectMouseOver;
        if (result == null)
          return; 
        switch (result.typeOfHit) {
          case MISS:
            this.mousePos = null;
            break;
          case BLOCK:
            this.mousePos = mc.objectMouseOver.getBlockPos();
            break;
          case ENTITY:
            if (this.mousePos != null) {
              Entity entity = result.entityHit;
              if (entity != null && 
                this.mousePos.equals(new BlockPos(entity.posX, entity.posY - 1.0D, entity.posZ)))
                mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(this.mousePos, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F)); 
            } 
            break;
        } 
      } 
    } 
  }
}
