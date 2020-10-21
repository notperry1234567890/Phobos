//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class HoleFiller extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.OBSIDIAN));
  
  public Setting<PlaceMode> placeMode = register(new Setting("PlaceMode", PlaceMode.ALL));
  
  public Setting<Bind> obbyBind = register(new Setting("Obsidian", new Bind(-1)));
  
  public Setting<Bind> webBind = register(new Setting("Webs", new Bind(-1)));
  
  private final Setting<Double> smartRange = register(new Setting("SmartRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D), v -> (this.placeMode.getValue() == PlaceMode.SMART)));
  
  private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D)));
  
  private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> blocksPerTick = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> disable = register(new Setting("Disable", Boolean.valueOf(true)));
  
  private final Setting<Integer> disableTime = register(new Setting("Ms/Disable", Integer.valueOf(200), Integer.valueOf(1), Integer.valueOf(250)));
  
  private final Setting<Boolean> offhand = register(new Setting("OffHand", Boolean.valueOf(true)));
  
  private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
  
  private final Setting<Boolean> onlySafe = register(new Setting("OnlySafe", Boolean.valueOf(true), v -> ((Boolean)this.offhand.getValue()).booleanValue()));
  
  private final Setting<Boolean> webSelf = register(new Setting("SelfWeb", Boolean.valueOf(false)));
  
  private final Setting<Boolean> highWeb = register(new Setting("HighWeb", Boolean.valueOf(false)));
  
  private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
  
  private final Setting<Boolean> midSafeHoles = register(new Setting("MidSafe", Boolean.valueOf(false)));
  
  private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
  
  private static HoleFiller INSTANCE = new HoleFiller();
  
  public Mode currentMode = Mode.OBSIDIAN;
  
  private final Timer offTimer = new Timer();
  
  private final Timer timer = new Timer();
  
  private boolean accessedViaBind = false;
  
  private int targetSlot = -1;
  
  private int blocksThisTick = 0;
  
  private Offhand.Mode offhandMode = Offhand.Mode.CRYSTALS;
  
  private Offhand.Mode2 offhandMode2 = Offhand.Mode2.CRYSTALS;
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private final Timer retryTimer = new Timer();
  
  private boolean isSneaking;
  
  private boolean hasOffhand = false;
  
  private boolean placeHighWeb = false;
  
  private int lastHotbarSlot = -1;
  
  private boolean switchedItem = false;
  
  public HoleFiller() {
    super("HoleFiller", "Fills holes around you.", Module.Category.COMBAT, true, false, true);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static HoleFiller getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleFiller(); 
    return INSTANCE;
  }
  
  public void onEnable() {
    if (fullNullCheck())
      disable(); 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    if (!this.accessedViaBind)
      this.currentMode = (Mode)this.mode.getValue(); 
    Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
    this.offhandMode = module.mode;
    this.offhandMode2 = module.currentMode;
    if (((Boolean)this.offhand.getValue()).booleanValue() && (EntityUtil.isSafe((Entity)mc.player) || !((Boolean)this.onlySafe.getValue()).booleanValue()))
      if (module.type.getValue() == Offhand.Type.NEW) {
        if (this.currentMode == Mode.WEBS) {
          module.setSwapToTotem(false);
          module.setMode(Offhand.Mode.WEBS);
        } else {
          module.setSwapToTotem(false);
          module.setMode(Offhand.Mode.OBSIDIAN);
        } 
      } else {
        if (this.currentMode == Mode.WEBS) {
          module.setMode(Offhand.Mode2.WEBS);
        } else {
          module.setMode(Offhand.Mode2.OBSIDIAN);
        } 
        if (!module.didSwitchThisTick)
          module.doOffhand(); 
      }  
    Phobos.holeManager.update();
    this.offTimer.reset();
  }
  
  public void onTick() {
    if (isOn() && (((Integer)this.blocksPerTick.getValue()).intValue() != 1 || !((Boolean)this.rotate.getValue()).booleanValue()))
      doHoleFill(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (isOn() && event.getStage() == 0 && ((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue())
      doHoleFill(); 
  }
  
  public void onDisable() {
    if (((Boolean)this.offhand.getValue()).booleanValue()) {
      ((Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode);
      ((Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class)).setMode(this.offhandMode2);
    } 
    switchItem(true);
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.retries.clear();
    this.accessedViaBind = false;
    this.hasOffhand = false;
  }
  
  @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (Keyboard.getEventKeyState()) {
      if (((Bind)this.obbyBind.getValue()).getKey() == Keyboard.getEventKey()) {
        this.accessedViaBind = true;
        this.currentMode = Mode.OBSIDIAN;
        toggle();
      } 
      if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
        this.accessedViaBind = true;
        this.currentMode = Mode.WEBS;
        toggle();
      } 
    } 
  }
  
  private void doHoleFill() {
    List<BlockPos> targets;
    if (check())
      return; 
    if (this.placeHighWeb) {
      BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
      placeBlock(pos);
      this.placeHighWeb = false;
    } 
    if (((Boolean)this.midSafeHoles.getValue()).booleanValue()) {
      synchronized (Phobos.holeManager.getMidSafety()) {
        targets = new ArrayList<>(Phobos.holeManager.getMidSafety());
      } 
    } else {
      synchronized (Phobos.holeManager.getHoles()) {
        targets = new ArrayList<>(Phobos.holeManager.getHoles());
      } 
    } 
    for (BlockPos position : targets) {
      if (mc.player.getDistanceSq(position) > MathUtil.square(((Double)this.range.getValue()).doubleValue()))
        continue; 
      if (this.placeMode.getValue() == PlaceMode.SMART && !isPlayerInRange(position))
        continue; 
      if (position.equals(new BlockPos(mc.player.getPositionVector()))) {
        if (this.currentMode != Mode.WEBS || !((Boolean)this.webSelf.getValue()).booleanValue())
          continue; 
        if (((Boolean)this.highWeb.getValue()).booleanValue())
          this.placeHighWeb = true; 
      } 
      int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
      if (placeability == 1 && (
        this.currentMode == Mode.WEBS || this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && ((Boolean)(BlockTweaks.getINSTANCE()).noBlock.getValue()).booleanValue())) && (
        this.currentMode == Mode.WEBS || this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < 4)) {
        placeBlock(position);
        if (this.currentMode != Mode.WEBS)
          this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1))); 
        continue;
      } 
      if (placeability == 3)
        placeBlock(position); 
    } 
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.blocksThisTick < ((Integer)this.blocksPerTick.getValue()).intValue() && 
      switchItem(false)) {
      boolean smartRotate = (((Integer)this.blocksPerTick.getValue()).intValue() == 1 && ((Boolean)this.rotate.getValue()).booleanValue());
      if (smartRotate) {
        this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      } else {
        this.isSneaking = BlockUtil.placeBlock(pos, this.hasOffhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
      } 
      this.timer.reset();
      this.blocksThisTick++;
    } 
  }
  
  private boolean isPlayerInRange(BlockPos pos) {
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, ((Double)this.smartRange.getValue()).doubleValue()))
        continue; 
      return true;
    } 
    return false;
  }
  
  private boolean check() {
    if (fullNullCheck() || (((Boolean)this.disable.getValue()).booleanValue() && this.offTimer.passedMs(((Integer)this.disableTime.getValue()).intValue()))) {
      disable();
      return true;
    } 
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock((this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class))
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    switchItem(true);
    if (!((Boolean)this.freecam.getValue()).booleanValue() && Phobos.moduleManager.isModuleEnabled(Freecam.class))
      return true; 
    this.blocksThisTick = 0;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    if (this.retryTimer.passedMs(2000L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    switch (this.currentMode) {
      case WEBS:
        this.hasOffhand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockWeb.class);
        this.targetSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
        break;
      case OBSIDIAN:
        this.hasOffhand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
        this.targetSlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        break;
    } 
    if (((Boolean)this.onlySafe.getValue()).booleanValue() && !EntityUtil.isSafe((Entity)mc.player)) {
      disable();
      return true;
    } 
    if (!this.hasOffhand && this.targetSlot == -1 && (!((Boolean)this.offhand.getValue()).booleanValue() || (!EntityUtil.isSafe((Entity)mc.player) && ((Boolean)this.onlySafe.getValue()).booleanValue())))
      return true; 
    if (((Boolean)this.offhand.getValue()).booleanValue() && !this.hasOffhand)
      return true; 
    return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
  }
  
  private boolean switchItem(boolean back) {
    if (((Boolean)this.offhand.getValue()).booleanValue())
      return true; 
    boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), (this.currentMode == Mode.WEBS) ? BlockWeb.class : BlockObsidian.class);
    this.switchedItem = value[0];
    return value[1];
  }
  
  public enum Mode {
    WEBS, OBSIDIAN;
  }
  
  public enum PlaceMode {
    SMART, ALL;
  }
}
