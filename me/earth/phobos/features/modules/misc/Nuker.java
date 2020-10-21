//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.BlockEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Nuker extends Module {
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
  
  public Setting<Float> distance = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(10.0F)));
  
  public Setting<Integer> blockPerTick = register(new Setting("Blocks/Attack", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(100)));
  
  public Setting<Integer> delay = register(new Setting("Delay/Attack", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(1000)));
  
  public Setting<Boolean> nuke = register(new Setting("Nuke", Boolean.valueOf(false)));
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.NUKE, v -> ((Boolean)this.nuke.getValue()).booleanValue()));
  
  public Setting<Boolean> antiRegear = register(new Setting("AntiRegear", Boolean.valueOf(false)));
  
  public Setting<Boolean> hopperNuker = register(new Setting("HopperAura", Boolean.valueOf(false)));
  
  private Setting<Boolean> autoSwitch = register(new Setting("AutoSwitch", Boolean.valueOf(false)));
  
  private int oldSlot = -1;
  
  private boolean isMining = false;
  
  private final Timer timer = new Timer();
  
  private Block selected;
  
  public Nuker() {
    super("Nuker", "Mines many blocks", Module.Category.MISC, true, false, false);
  }
  
  public void onToggle() {
    this.selected = null;
  }
  
  @SubscribeEvent
  public void onClickBlock(BlockEvent event) {
    if (event.getStage() == 3 && (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.NUKE)) {
      Block block = mc.world.getBlockState(event.pos).getBlock();
      if (block != null && block != this.selected) {
        this.selected = block;
        event.setCanceled(true);
      } 
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayerPre(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0) {
      if (((Boolean)this.nuke.getValue()).booleanValue()) {
        BlockPos pos = null;
        switch ((Mode)this.mode.getValue()) {
          case SELECTION:
          case NUKE:
            pos = getClosestBlockSelection();
            break;
          case ALL:
            pos = getClosestBlockAll();
            break;
        } 
        if (pos != null)
          if (this.mode.getValue() == Mode.SELECTION || this.mode.getValue() == Mode.ALL) {
            if (((Boolean)this.rotate.getValue()).booleanValue()) {
              float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() + 0.5F), (pos.getZ() + 0.5F)));
              Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
            } 
            if (canBreak(pos)) {
              mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
              mc.player.swingArm(EnumHand.MAIN_HAND);
            } 
          } else {
            for (int i = 0; i < ((Integer)this.blockPerTick.getValue()).intValue(); i++) {
              pos = getClosestBlockSelection();
              if (pos != null) {
                if (((Boolean)this.rotate.getValue()).booleanValue()) {
                  float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() + 0.5F), (pos.getZ() + 0.5F)));
                  Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
                } 
                if (this.timer.passedMs(((Integer)this.delay.getValue()).intValue())) {
                  mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
                  mc.player.swingArm(EnumHand.MAIN_HAND);
                  this.timer.reset();
                } 
              } 
            } 
          }  
      } 
      if (((Boolean)this.antiRegear.getValue()).booleanValue())
        breakBlocks(BlockUtil.shulkerList); 
      if (((Boolean)this.hopperNuker.getValue()).booleanValue()) {
        List<Block> blocklist = new ArrayList<>();
        blocklist.add(Blocks.HOPPER);
        breakBlocks(blocklist);
      } 
    } 
  }
  
  public void breakBlocks(List<Block> blocks) {
    BlockPos pos = getNearestBlock(blocks);
    if (pos != null) {
      if (!this.isMining) {
        this.oldSlot = mc.player.inventory.currentItem;
        this.isMining = true;
      } 
      if (((Boolean)this.rotate.getValue()).booleanValue()) {
        float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() + 0.5F), (pos.getZ() + 0.5F)));
        Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
      } 
      if (canBreak(pos)) {
        if (((Boolean)this.autoSwitch.getValue()).booleanValue()) {
          int newSlot = -1;
          for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY)
              if (stack.getItem() instanceof net.minecraft.item.ItemPickaxe) {
                newSlot = i;
                break;
              }  
          } 
          if (newSlot != -1)
            mc.player.inventory.currentItem = newSlot; 
        } 
        mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
        mc.player.swingArm(EnumHand.MAIN_HAND);
      } 
    } else if (((Boolean)this.autoSwitch.getValue()).booleanValue() && this.oldSlot != -1) {
      mc.player.inventory.currentItem = this.oldSlot;
      this.oldSlot = -1;
      this.isMining = false;
    } 
  }
  
  private boolean canBreak(BlockPos pos) {
    IBlockState blockState = mc.world.getBlockState(pos);
    Block block = blockState.getBlock();
    return (block.getBlockHardness(blockState, (World)mc.world, pos) != -1.0F);
  }
  
  private BlockPos getNearestBlock(List<Block> blocks) {
    double maxDist = MathUtil.square(((Float)this.distance.getValue()).floatValue());
    BlockPos ret = null;
    double x;
    for (x = maxDist; x >= -maxDist; x--) {
      double y;
      for (y = maxDist; y >= -maxDist; y--) {
        double z;
        for (z = maxDist; z >= -maxDist; z--) {
          BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
          double dist = mc.player.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
          if (dist <= maxDist && blocks.contains(mc.world.getBlockState(pos).getBlock()) && canBreak(pos)) {
            maxDist = dist;
            ret = pos;
          } 
        } 
      } 
    } 
    return ret;
  }
  
  private BlockPos getClosestBlockAll() {
    float maxDist = ((Float)this.distance.getValue()).floatValue();
    BlockPos ret = null;
    for (float x = maxDist; x >= -maxDist; x--) {
      float y;
      for (y = maxDist; y >= -maxDist; y--) {
        float z;
        for (z = maxDist; z >= -maxDist; z--) {
          BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
          double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
          if (dist <= maxDist && mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid) && canBreak(pos) && 
            pos.getY() >= mc.player.posY) {
            maxDist = (float)dist;
            ret = pos;
          } 
        } 
      } 
    } 
    return ret;
  }
  
  private BlockPos getClosestBlockSelection() {
    float maxDist = ((Float)this.distance.getValue()).floatValue();
    BlockPos ret = null;
    for (float x = maxDist; x >= -maxDist; x--) {
      float y;
      for (y = maxDist; y >= -maxDist; y--) {
        float z;
        for (z = maxDist; z >= -maxDist; z--) {
          BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
          double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
          if (dist <= maxDist && mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockLiquid) && mc.world.getBlockState(pos).getBlock() == this.selected && canBreak(pos) && 
            pos.getY() >= mc.player.posY) {
            maxDist = (float)dist;
            ret = pos;
          } 
        } 
      } 
    } 
    return ret;
  }
  
  public enum Mode {
    SELECTION, ALL, NUKE;
  }
}
