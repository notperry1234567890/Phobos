//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Surround extends Module {
  private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> blocksPerTick = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
  
  private final Setting<Boolean> center = register(new Setting("Center", Boolean.valueOf(false)));
  
  private final Setting<Boolean> helpingBlocks = register(new Setting("HelpingBlocks", Boolean.valueOf(true)));
  
  private final Setting<Boolean> intelligent = register(new Setting("Intelligent", Boolean.valueOf(false), v -> ((Boolean)this.helpingBlocks.getValue()).booleanValue()));
  
  private final Setting<Boolean> antiPedo = register(new Setting("NoPedo", Boolean.valueOf(false)));
  
  private final Setting<Integer> extender = register(new Setting("Extend", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(4)));
  
  private final Setting<Boolean> extendMove = register(new Setting("MoveExtend", Boolean.valueOf(false), v -> (((Integer)this.extender.getValue()).intValue() > 1)));
  
  private final Setting<MovementMode> movementMode = register(new Setting("Movement", MovementMode.STATIC));
  
  private final Setting<Double> speed = register(new Setting("Speed", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(30.0D), v -> (this.movementMode.getValue() == MovementMode.LIMIT || this.movementMode.getValue() == MovementMode.OFF), "Maximum Movement Speed"));
  
  private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3)));
  
  private final Setting<Boolean> floor = register(new Setting("Floor", Boolean.valueOf(false)));
  
  private final Setting<Boolean> echests = register(new Setting("Echests", Boolean.valueOf(false)));
  
  private final Setting<Boolean> noGhost = register(new Setting("Packet", Boolean.valueOf(false)));
  
  private final Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(false)));
  
  private final Setting<Integer> retryer = register(new Setting("Retries", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(15)));
  
  private final Timer timer = new Timer();
  
  private final Timer retryTimer = new Timer();
  
  private int isSafe;
  
  private BlockPos startPos;
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  private int lastHotbarSlot;
  
  private boolean isSneaking;
  
  private int placements = 0;
  
  private final Set<Vec3d> extendingBlocks = new HashSet<>();
  
  private int extenders = 1;
  
  public static boolean isPlacing = false;
  
  private int obbySlot = -1;
  
  private boolean offHand = false;
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  public Surround() {
    super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      disable(); 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    if (((Boolean)this.center.getValue()).booleanValue() && !Phobos.moduleManager.isModuleEnabled("Freecam"))
      if (mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.WEB) {
        Phobos.positionManager.setPositionPacket(mc.player.posX, this.startPos.getY(), mc.player.posZ, true, true, true);
      } else {
        Phobos.positionManager.setPositionPacket(this.startPos.getX() + 0.5D, this.startPos.getY(), this.startPos.getZ() + 0.5D, true, true, true);
      }  
    this.retries.clear();
    this.retryTimer.reset();
  }
  
  public void onTick() {
    if (((Integer)this.eventMode.getValue()).intValue() == 3)
      doFeetPlace(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Integer)this.eventMode.getValue()).intValue() == 2)
      doFeetPlace(); 
  }
  
  public void onUpdate() {
    if (((Integer)this.eventMode.getValue()).intValue() == 1)
      doFeetPlace(); 
  }
  
  public void onDisable() {
    if (nullCheck())
      return; 
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    switchItem(true);
  }
  
  public String getDisplayInfo() {
    if (!((Boolean)this.info.getValue()).booleanValue())
      return null; 
    switch (this.isSafe) {
      case 0:
        return "§cUnsafe";
      case 1:
        return "§eSecure";
    } 
    return "§aSecure";
  }
  
  private void doFeetPlace() {
    if (check())
      return; 
    if (!EntityUtil.isSafe((Entity)mc.player, 0, ((Boolean)this.floor.getValue()).booleanValue())) {
      this.isSafe = 0;
      placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)mc.player, 0, ((Boolean)this.floor.getValue()).booleanValue()), ((Boolean)this.helpingBlocks.getValue()).booleanValue(), false, false);
    } else if (!EntityUtil.isSafe((Entity)mc.player, -1, false)) {
      this.isSafe = 1;
      if (((Boolean)this.antiPedo.getValue()).booleanValue())
        placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray((Entity)mc.player, -1, false), false, false, true); 
    } else {
      this.isSafe = 2;
    } 
    processExtendingBlocks();
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void processExtendingBlocks() {
    if (this.extendingBlocks.size() == 2 && this.extenders < ((Integer)this.extender.getValue()).intValue()) {
      Vec3d[] array = new Vec3d[2];
      int i = 0;
      for (Vec3d vec3d : this.extendingBlocks) {
        array[i] = vec3d;
        i++;
      } 
      int placementsBefore = this.placements;
      if (areClose(array) != null)
        placeBlocks(areClose(array), EntityUtil.getUnsafeBlockArrayFromVec3d(areClose(array), 0, ((Boolean)this.floor.getValue()).booleanValue()), ((Boolean)this.helpingBlocks.getValue()).booleanValue(), false, true); 
      if (placementsBefore < this.placements)
        this.extendingBlocks.clear(); 
    } else if (this.extendingBlocks.size() > 2 || this.extenders >= ((Integer)this.extender.getValue()).intValue()) {
      this.extendingBlocks.clear();
    } 
  }
  
  private Vec3d areClose(Vec3d[] vec3ds) {
    int matches = 0;
    for (Vec3d vec3d : vec3ds) {
      for (Vec3d pos : EntityUtil.getUnsafeBlockArray((Entity)mc.player, 0, ((Boolean)this.floor.getValue()).booleanValue())) {
        if (vec3d.equals(pos))
          matches++; 
      } 
    } 
    if (matches == 2)
      return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1])); 
    return null;
  }
  
  private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
    int helpings = 0;
    boolean gotHelp = true;
    for (Vec3d vec3d : vec3ds) {
      gotHelp = true;
      helpings++;
      if (isHelping && !((Boolean)this.intelligent.getValue()).booleanValue() && helpings > 1)
        return false; 
      BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
      switch (BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue())) {
        case 1:
          if ((this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && ((Boolean)(BlockTweaks.getINSTANCE()).noBlock.getValue()).booleanValue())) && (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < ((Integer)this.retryer.getValue()).intValue())) {
            placeBlock(position);
            this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
            this.retryTimer.reset();
            break;
          } 
          if ((((Boolean)this.extendMove.getValue()).booleanValue() || Phobos.speedManager.getSpeedKpH() == 0.0D) && !isExtending && this.extenders < ((Integer)this.extender.getValue()).intValue()) {
            placeBlocks(mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d(mc.player.getPositionVector().add(vec3d), 0, ((Boolean)this.floor.getValue()).booleanValue()), hasHelpingBlocks, false, true);
            this.extendingBlocks.add(vec3d);
            this.extenders++;
          } 
          break;
        case 2:
          if (hasHelpingBlocks) {
            gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
          } else {
            break;
          } 
        case 3:
          if (gotHelp)
            placeBlock(position); 
          if (isHelping)
            return true; 
          break;
      } 
    } 
    return false;
  }
  
  private boolean check() {
    if (nullCheck())
      return true; 
    this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
    isPlacing = false;
    this.didPlace = false;
    this.extenders = 1;
    this.placements = 0;
    this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
    if (isOff())
      return true; 
    if (this.retryTimer.passedMs(2500L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    switchItem(true);
    if (this.obbySlot == -1 && !this.offHand && (
      !((Boolean)this.echests.getValue()).booleanValue() || echestSlot == -1)) {
      if (((Boolean)this.info.getValue()).booleanValue())
        Command.sendMessage("<" + getDisplayName() + "> " + "§c" + "You are out of Obsidian."); 
      disable();
      return true;
    } 
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != this.obbySlot && mc.player.inventory.currentItem != echestSlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    switch ((MovementMode)this.movementMode.getValue()) {
      case STATIC:
        if (!this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
          disable();
          return true;
        } 
      case LIMIT:
        if (Phobos.speedManager.getSpeedKpH() > ((Double)this.speed.getValue()).doubleValue())
          return true; 
        break;
      case OFF:
        if (Phobos.speedManager.getSpeedKpH() > ((Double)this.speed.getValue()).doubleValue()) {
          disable();
          return true;
        } 
        break;
    } 
    return (Phobos.moduleManager.isModuleEnabled("Freecam") || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockObsidian.class)));
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerTick.getValue()).intValue() && switchItem(false)) {
      isPlacing = true;
      this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.noGhost.getValue()).booleanValue(), this.isSneaking);
      this.didPlace = true;
      this.placements++;
    } 
  }
  
  private boolean switchItem(boolean back) {
    if (this.offHand)
      return true; 
    boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), (this.obbySlot == -1) ? BlockEnderChest.class : BlockObsidian.class);
    this.switchedItem = value[0];
    return value[1];
  }
  
  public enum MovementMode {
    NONE, STATIC, LIMIT, OFF;
  }
}
