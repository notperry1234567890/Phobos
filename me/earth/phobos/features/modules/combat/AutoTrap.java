//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTrap extends Module {
  private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> blocksPerPlace = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Double> targetRange = register(new Setting("TargetRange", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(20.0D)));
  
  private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D)));
  
  private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.CLOSEST));
  
  private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<Pattern> pattern = register(new Setting("Pattern", Pattern.STATIC));
  
  private final Setting<Integer> extend = register(new Setting("Extend", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4), v -> (this.pattern.getValue() != Pattern.STATIC), "Extending the Trap."));
  
  private final Setting<Boolean> antiScaffold = register(new Setting("AntiScaffold", Boolean.valueOf(false)));
  
  private final Setting<Boolean> antiStep = register(new Setting("AntiStep", Boolean.valueOf(false)));
  
  private final Setting<Boolean> legs = register(new Setting("Legs", Boolean.valueOf(false), v -> (this.pattern.getValue() != Pattern.OPEN)));
  
  private final Setting<Boolean> platform = register(new Setting("Platform", Boolean.valueOf(false), v -> (this.pattern.getValue() != Pattern.OPEN)));
  
  private final Setting<Boolean> antiDrop = register(new Setting("AntiDrop", Boolean.valueOf(false)));
  
  private final Setting<Double> speed = register(new Setting("Speed", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(30.0D)));
  
  private final Setting<Boolean> antiSelf = register(new Setting("AntiSelf", Boolean.valueOf(false)));
  
  private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3)));
  
  private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
  
  private final Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(false)));
  
  private final Setting<Boolean> entityCheck = register(new Setting("NoBlock", Boolean.valueOf(true)));
  
  private final Setting<Boolean> disable = register(new Setting("TSelfMove", Boolean.valueOf(false)));
  
  private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
  
  private final Setting<Integer> retryer = register(new Setting("Retries", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(15)));
  
  private final Timer timer = new Timer();
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  public EntityPlayer target;
  
  private boolean isSneaking;
  
  private int lastHotbarSlot;
  
  private int placements = 0;
  
  public static boolean isPlacing = false;
  
  private boolean smartRotate = false;
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private final Timer retryTimer = new Timer();
  
  private BlockPos startPos = null;
  
  public AutoTrap() {
    super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      return; 
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.retries.clear();
  }
  
  public void onTick() {
    if (((Integer)this.eventMode.getValue()).intValue() == 3) {
      this.smartRotate = false;
      doTrap();
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Integer)this.eventMode.getValue()).intValue() == 2) {
      this.smartRotate = (((Boolean)this.rotate.getValue()).booleanValue() && ((Integer)this.blocksPerPlace.getValue()).intValue() == 1);
      doTrap();
    } 
  }
  
  public void onUpdate() {
    if (((Integer)this.eventMode.getValue()).intValue() == 1) {
      this.smartRotate = false;
      doTrap();
    } 
  }
  
  public String getDisplayInfo() {
    if (((Boolean)this.info.getValue()).booleanValue() && this.target != null)
      return this.target.getName(); 
    return null;
  }
  
  public void onDisable() {
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    switchItem(true);
  }
  
  private void doTrap() {
    if (check())
      return; 
    switch ((Pattern)this.pattern.getValue()) {
      case STATIC:
        doStaticTrap();
        break;
      case SMART:
      case OPEN:
        doSmartTrap();
        break;
    } 
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void doSmartTrap() {
    List<Vec3d> placeTargets = EntityUtil.getUntrappedBlocksExtended(((Integer)this.extend.getValue()).intValue(), this.target, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.raytrace.getValue()).booleanValue());
    placeList(placeTargets);
  }
  
  private void doStaticTrap() {
    List<Vec3d> placeTargets = EntityUtil.targets(this.target.getPositionVector(), ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.raytrace.getValue()).booleanValue());
    placeList(placeTargets);
  }
  
  private void placeList(List<Vec3d> list) {
    list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
    list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
    for (Vec3d vec3d : list) {
      BlockPos position = new BlockPos(vec3d);
      int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
      if (((Boolean)this.entityCheck.getValue()).booleanValue() && placeability == 1 && (this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && ((Boolean)(BlockTweaks.getINSTANCE()).noBlock.getValue()).booleanValue())) && (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < ((Integer)this.retryer.getValue()).intValue())) {
        placeBlock(position);
        this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
        this.retryTimer.reset();
        continue;
      } 
      if (placeability == 3 && (!((Boolean)this.antiSelf.getValue()).booleanValue() || !MathUtil.areVec3dsAligned(mc.player.getPositionVector(), vec3d)))
        placeBlock(position); 
    } 
  }
  
  private boolean check() {
    isPlacing = false;
    this.didPlace = false;
    this.placements = 0;
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (isOff())
      return true; 
    if (((Boolean)this.disable.getValue()).booleanValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    if (this.retryTimer.passedMs(2000L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (obbySlot == -1) {
      if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
        if (((Boolean)this.info.getValue()).booleanValue())
          Command.sendMessage("<" + getDisplayName() + "> " + "§c" + "You are out of Obsidian."); 
        disable();
      } 
      return true;
    } 
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    switchItem(true);
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.target = getTarget(((Double)this.targetRange.getValue()).doubleValue(), (this.targetMode.getValue() == TargetMode.UNTRAPPED));
    return (this.target == null || (Phobos.moduleManager.isModuleEnabled("Freecam") && !((Boolean)this.freecam.getValue()).booleanValue()) || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockObsidian.class)));
  }
  
  private EntityPlayer getTarget(double range, boolean trapped) {
    EntityPlayer target = null;
    double distance = Math.pow(range, 2.0D) + 1.0D;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, range))
        continue; 
      if (this.pattern.getValue() == Pattern.STATIC && trapped && EntityUtil.isTrapped(player, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue()))
        continue; 
      if (this.pattern.getValue() != Pattern.STATIC && trapped && EntityUtil.isTrappedExtended(((Integer)this.extend.getValue()).intValue(), player, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.raytrace.getValue()).booleanValue()))
        continue; 
      if (EntityUtil.getRoundedBlockPos((Entity)mc.player).equals(EntityUtil.getRoundedBlockPos((Entity)player)) && ((Boolean)this.antiSelf.getValue()).booleanValue())
        continue; 
      if (Phobos.speedManager.getPlayerSpeed(player) > ((Double)this.speed.getValue()).doubleValue())
        continue; 
      if (target == null) {
        target = player;
        distance = mc.player.getDistanceSq((Entity)player);
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) < distance) {
        target = player;
        distance = mc.player.getDistanceSq((Entity)player);
      } 
    } 
    return target;
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(((Double)this.range.getValue()).doubleValue()) && switchItem(false)) {
      isPlacing = true;
      if (this.smartRotate) {
        this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      } else {
        this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      } 
      this.didPlace = true;
      this.placements++;
    } 
  }
  
  private boolean switchItem(boolean back) {
    boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), BlockObsidian.class);
    this.switchedItem = value[0];
    return value[1];
  }
  
  public enum Pattern {
    STATIC, SMART, OPEN;
  }
  
  public enum TargetMode {
    CLOSEST, UNTRAPPED;
  }
}
