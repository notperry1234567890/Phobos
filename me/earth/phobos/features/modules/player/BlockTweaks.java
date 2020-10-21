//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockTweaks extends Module {
  public Setting<Boolean> autoTool = register(new Setting("AutoTool", Boolean.valueOf(false)));
  
  public Setting<Boolean> autoWeapon = register(new Setting("AutoWeapon", Boolean.valueOf(false)));
  
  public Setting<Boolean> noFriendAttack = register(new Setting("NoFriendAttack", Boolean.valueOf(false)));
  
  public Setting<Boolean> noBlock = register(new Setting("NoHitboxBlock", Boolean.valueOf(true)));
  
  public Setting<Boolean> noGhost = register(new Setting("NoGlitchBlocks", Boolean.valueOf(false)));
  
  public Setting<Boolean> destroy = register(new Setting("Destroy", Boolean.valueOf(false), v -> ((Boolean)this.noGhost.getValue()).booleanValue()));
  
  private static BlockTweaks INSTANCE = new BlockTweaks();
  
  private int lastHotbarSlot = -1;
  
  private int currentTargetSlot = -1;
  
  private boolean switched = false;
  
  public BlockTweaks() {
    super("BlockTweaks", "Some tweaks for blocks.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static BlockTweaks getINSTANCE() {
    if (INSTANCE == null)
      INSTANCE = new BlockTweaks(); 
    return INSTANCE;
  }
  
  public void onDisable() {
    if (this.switched)
      equip(this.lastHotbarSlot, false); 
    this.lastHotbarSlot = -1;
    this.currentTargetSlot = -1;
  }
  
  @SubscribeEvent
  public void onBreak(BlockEvent.BreakEvent event) {
    if (fullNullCheck() || !((Boolean)this.noGhost.getValue()).booleanValue() || !((Boolean)this.destroy.getValue()).booleanValue())
      return; 
    if (!(mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBlock)) {
      BlockPos pos = mc.player.getPosition();
      removeGlitchBlocks(pos);
    } 
  }
  
  @SubscribeEvent
  public void onBlockInteract(PlayerInteractEvent.LeftClickBlock event) {
    if (((Boolean)this.autoTool.getValue()).booleanValue() && ((Speedmine.getInstance()).mode.getValue() != Speedmine.Mode.PACKET || Speedmine.getInstance().isOff() || !((Boolean)(Speedmine.getInstance()).tweaks.getValue()).booleanValue()) && 
      !fullNullCheck() && event.getPos() != null)
      equipBestTool(mc.world.getBlockState(event.getPos())); 
  }
  
  @SubscribeEvent
  public void onAttack(AttackEntityEvent event) {
    if (((Boolean)this.autoWeapon.getValue()).booleanValue() && 
      !fullNullCheck() && event.getTarget() != null)
      equipBestWeapon(event.getTarget()); 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (fullNullCheck())
      return; 
    if (((Boolean)this.noFriendAttack.getValue()).booleanValue() && event.getPacket() instanceof CPacketUseEntity) {
      CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
      Entity entity = packet.getEntityFromWorld((World)mc.world);
      if (entity != null && Phobos.friendManager.isFriend(entity.getName()))
        event.setCanceled(true); 
    } 
  }
  
  public void onUpdate() {
    if (!fullNullCheck()) {
      if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != this.currentTargetSlot)
        this.lastHotbarSlot = mc.player.inventory.currentItem; 
      if (!mc.gameSettings.keyBindAttack.isKeyDown() && this.switched)
        equip(this.lastHotbarSlot, false); 
    } 
  }
  
  private void removeGlitchBlocks(BlockPos pos) {
    for (int dx = -4; dx <= 4; dx++) {
      for (int dy = -4; dy <= 4; dy++) {
        for (int dz = -4; dz <= 4; dz++) {
          BlockPos blockPos = new BlockPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
          if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.AIR))
            mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, EnumFacing.DOWN, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND); 
        } 
      } 
    } 
  }
  
  private void equipBestTool(IBlockState blockState) {
    int bestSlot = -1;
    double max = 0.0D;
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (!stack.isEmpty) {
        float speed = stack.getDestroySpeed(blockState);
        if (speed > 1.0F) {
          int eff;
          speed = (float)(speed + (((eff = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack)) > 0) ? (Math.pow(eff, 2.0D) + 1.0D) : 0.0D));
          if (speed > max) {
            max = speed;
            bestSlot = i;
          } 
        } 
      } 
    } 
    equip(bestSlot, true);
  }
  
  public void equipBestWeapon(Entity entity) {
    int bestSlot = -1;
    double maxDamage = 0.0D;
    EnumCreatureAttribute creatureAttribute = EnumCreatureAttribute.UNDEFINED;
    if (EntityUtil.isLiving(entity)) {
      EntityLivingBase base = (EntityLivingBase)entity;
      creatureAttribute = base.getCreatureAttribute();
    } 
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (!stack.isEmpty)
        if (stack.getItem() instanceof ItemTool) {
          double damage = ((ItemTool)stack.getItem()).attackDamage + EnchantmentHelper.getModifierForCreature(stack, creatureAttribute);
          if (damage > maxDamage) {
            maxDamage = damage;
            bestSlot = i;
          } 
        } else if (stack.getItem() instanceof ItemSword) {
          double damage = ((ItemSword)stack.getItem()).getAttackDamage() + EnchantmentHelper.getModifierForCreature(stack, creatureAttribute);
          if (damage > maxDamage) {
            maxDamage = damage;
            bestSlot = i;
          } 
        }  
    } 
    equip(bestSlot, true);
  }
  
  private void equip(int slot, boolean equipTool) {
    if (slot != -1) {
      if (slot != mc.player.inventory.currentItem)
        this.lastHotbarSlot = mc.player.inventory.currentItem; 
      this.currentTargetSlot = slot;
      mc.player.inventory.currentItem = slot;
      mc.playerController.syncCurrentPlayItem();
      this.switched = equipTool;
    } 
  }
}
