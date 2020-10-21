//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.Freecam;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class Auto32k extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.NORMAL));
  
  private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> delayDispenser = register(new Setting("Blocks/Place", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(8), v -> (this.mode.getValue() != Mode.NORMAL)));
  
  private final Setting<Integer> blocksPerPlace = register(new Setting("Actions/Place", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(3), v -> (this.mode.getValue() == Mode.NORMAL)));
  
  private final Setting<Float> range = register(new Setting("PlaceRange", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(6.0F)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
  
  public Setting<Boolean> autoSwitch = register(new Setting("AutoSwitch", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.NORMAL)));
  
  public Setting<Boolean> withBind = register(new Setting("WithBind", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.NORMAL && ((Boolean)this.autoSwitch.getValue()).booleanValue())));
  
  public Setting<Bind> switchBind = register(new Setting("SwitchBind", new Bind(-1), v -> (((Boolean)this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() == Mode.NORMAL && ((Boolean)this.withBind.getValue()).booleanValue())));
  
  private final Setting<Double> targetRange = register(new Setting("TargetRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(20.0D)));
  
  private final Setting<Boolean> extra = register(new Setting("ExtraRotation", Boolean.valueOf(false)));
  
  private final Setting<PlaceType> placeType = register(new Setting("Place", PlaceType.CLOSE));
  
  private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
  
  private final Setting<Boolean> onOtherHoppers = register(new Setting("UseHoppers", Boolean.valueOf(false)));
  
  private final Setting<Boolean> preferObby = register(new Setting("UseObby", Boolean.valueOf(false), v -> (this.mode.getValue() != Mode.NORMAL)));
  
  private final Setting<Boolean> checkForShulker = register(new Setting("CheckShulker", Boolean.valueOf(true)));
  
  private final Setting<Integer> checkDelay = register(new Setting("CheckDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> ((Boolean)this.checkForShulker.getValue()).booleanValue()));
  
  private final Setting<Boolean> drop = register(new Setting("Drop", Boolean.valueOf(false)));
  
  private final Setting<Boolean> mine = register(new Setting("Mine", Boolean.valueOf(false), v -> ((Boolean)this.drop.getValue()).booleanValue()));
  
  private final Setting<Boolean> checkStatus = register(new Setting("CheckState", Boolean.valueOf(true)));
  
  private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
  
  private final Setting<Boolean> superPacket = register(new Setting("DispExtra", Boolean.valueOf(false)));
  
  private final Setting<Boolean> secretClose = register(new Setting("SecretClose", Boolean.valueOf(false)));
  
  private final Setting<Boolean> closeGui = register(new Setting("CloseGui", Boolean.valueOf(false), v -> ((Boolean)this.secretClose.getValue()).booleanValue()));
  
  private final Setting<Boolean> repeatSwitch = register(new Setting("SwitchOnFail", Boolean.valueOf(true)));
  
  private final Setting<Boolean> simulate = register(new Setting("Simulate", Boolean.valueOf(true), v -> (this.mode.getValue() != Mode.NORMAL)));
  
  private final Setting<Float> hopperDistance = register(new Setting("HopperRange", Float.valueOf(8.0F), Float.valueOf(0.0F), Float.valueOf(20.0F)));
  
  private final Setting<Integer> trashSlot = register(new Setting("32kSlot", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(9)));
  
  private final Setting<Boolean> messages = register(new Setting("Messages", Boolean.valueOf(false)));
  
  private final Setting<Boolean> antiHopper = register(new Setting("AntiHopper", Boolean.valueOf(false)));
  
  private float yaw;
  
  private float pitch;
  
  private boolean spoof;
  
  public boolean switching;
  
  private int lastHotbarSlot = -1;
  
  private int shulkerSlot = -1;
  
  private int hopperSlot = -1;
  
  private BlockPos hopperPos;
  
  private EntityPlayer target;
  
  public Step currentStep = Step.PRE;
  
  private final Timer placeTimer = new Timer();
  
  private static Auto32k instance;
  
  private int obbySlot = -1;
  
  private int dispenserSlot = -1;
  
  private int redstoneSlot = -1;
  
  private DispenserData finalDispenserData;
  
  private int actionsThisTick = 0;
  
  private boolean checkedThisTick = false;
  
  private boolean authSneakPacket = false;
  
  private Timer disableTimer = new Timer();
  
  private boolean shouldDisable;
  
  private boolean rotationprepared = false;
  
  public Auto32k() {
    super("Auto32k", "Auto32ks", Module.Category.COMBAT, true, false, false);
    instance = this;
  }
  
  public static Auto32k getInstance() {
    if (instance == null)
      instance = new Auto32k(); 
    return instance;
  }
  
  public void onEnable() {
    this.checkedThisTick = false;
    resetFields();
    if (mc.currentScreen instanceof net.minecraft.client.gui.GuiHopper)
      this.currentStep = Step.HOPPERGUI; 
    if (this.mode.getValue() == Mode.NORMAL && ((Boolean)this.autoSwitch.getValue()).booleanValue() && !((Boolean)this.withBind.getValue()).booleanValue())
      this.switching = true; 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0)
      return; 
    if (this.shouldDisable && this.disableTimer.passedMs(1000L)) {
      this.shouldDisable = false;
      disable();
      return;
    } 
    this.checkedThisTick = false;
    this.actionsThisTick = 0;
    if (isOff() || (this.mode.getValue() == Mode.NORMAL && ((Boolean)this.autoSwitch.getValue()).booleanValue() && !this.switching))
      return; 
    if (this.mode.getValue() == Mode.NORMAL) {
      normal32k();
    } else {
      processDispenser32k();
    } 
  }
  
  @SubscribeEvent
  public void onGui(GuiOpenEvent event) {
    if (fullNullCheck() || isOff())
      return; 
    if (!((Boolean)this.secretClose.getValue()).booleanValue() && mc.currentScreen instanceof net.minecraft.client.gui.GuiHopper) {
      if (((Boolean)this.drop.getValue()).booleanValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && this.hopperPos != null) {
        mc.player.dropItem(true);
        if (((Boolean)this.mine.getValue()).booleanValue() && this.hopperPos != null) {
          int pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
          if (pickaxeSlot != -1) {
            InventoryUtil.switchToHotbarSlot(pickaxeSlot, false);
            if (((Boolean)this.rotate.getValue()).booleanValue())
              rotateToPos(this.hopperPos.up(), (Vec3d)null); 
            mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), mc.player.getHorizontalFacing());
            mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), mc.player.getHorizontalFacing());
            mc.player.swingArm(EnumHand.MAIN_HAND);
          } 
        } 
      } 
      resetFields();
      if (this.mode.getValue() != Mode.NORMAL) {
        disable();
        return;
      } 
      if (!((Boolean)this.autoSwitch.getValue()).booleanValue() || this.mode.getValue() == Mode.DISPENSER) {
        disable();
      } else if (!((Boolean)this.withBind.getValue()).booleanValue()) {
        disable();
      } 
    } else if (event.getGui() instanceof net.minecraft.client.gui.GuiHopper) {
      this.currentStep = Step.HOPPERGUI;
    } 
  }
  
  public String getDisplayInfo() {
    if (this.switching)
      return "§aSwitch"; 
    return null;
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (isOff())
      return; 
    if (Keyboard.getEventKeyState() && !(mc.currentScreen instanceof me.earth.phobos.features.gui.PhobosGui) && ((Bind)this.switchBind.getValue()).getKey() == Keyboard.getEventKey() && ((Boolean)this.withBind.getValue()).booleanValue()) {
      if (this.switching) {
        resetFields();
        this.switching = true;
      } 
      this.switching = !this.switching;
    } 
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2) {
      Setting setting = event.getSetting();
      if (setting != null && setting.getFeature().equals(this) && setting.equals(this.mode))
        resetFields(); 
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (fullNullCheck() || isOff())
      return; 
    if (event.getPacket() instanceof CPacketPlayer) {
      if (this.spoof) {
        CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        packet.yaw = this.yaw;
        packet.pitch = this.pitch;
        this.spoof = false;
      } 
    } else if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketCloseWindow) {
      if (!((Boolean)this.secretClose.getValue()).booleanValue() && mc.currentScreen instanceof net.minecraft.client.gui.GuiHopper && this.hopperPos != null) {
        if (((Boolean)this.drop.getValue()).booleanValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD) {
          mc.player.dropItem(true);
          if (((Boolean)this.mine.getValue()).booleanValue()) {
            int pickaxeSlot = InventoryUtil.findHotbarBlock(ItemPickaxe.class);
            if (pickaxeSlot != -1) {
              InventoryUtil.switchToHotbarSlot(pickaxeSlot, false);
              if (((Boolean)this.rotate.getValue()).booleanValue())
                rotateToPos(this.hopperPos.up(), (Vec3d)null); 
              mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), mc.player.getHorizontalFacing());
              mc.playerController.onPlayerDamageBlock(this.hopperPos.up(), mc.player.getHorizontalFacing());
              mc.player.swingArm(EnumHand.MAIN_HAND);
            } 
          } 
        } 
        resetFields();
        if (!((Boolean)this.autoSwitch.getValue()).booleanValue() || this.mode.getValue() == Mode.DISPENSER) {
          disable();
        } else if (!((Boolean)this.withBind.getValue()).booleanValue()) {
          disable();
        } 
      } else if (((Boolean)this.secretClose.getValue()).booleanValue() && (!((Boolean)this.autoSwitch.getValue()).booleanValue() || this.switching || this.mode.getValue() == Mode.DISPENSER) && this.currentStep == Step.HOPPERGUI) {
        event.setCanceled(true);
      } 
    } 
  }
  
  private void normal32k() {
    if (((Boolean)this.autoSwitch.getValue()).booleanValue()) {
      if (this.switching) {
        processNormal32k();
      } else {
        resetFields();
      } 
    } else {
      processNormal32k();
    } 
  }
  
  private void processNormal32k() {
    if (isOff())
      return; 
    if (this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue())) {
      check();
      switch (this.currentStep) {
        case MOUSE:
          runPreStep();
          if (this.currentStep == Step.PRE)
            return; 
        case CLOSE:
          if (this.currentStep == Step.HOPPER) {
            checkState();
            if (this.currentStep == Step.PRE) {
              if (this.checkedThisTick)
                processNormal32k(); 
              return;
            } 
            runHopperStep();
            if (this.actionsThisTick >= ((Integer)this.blocksPerPlace.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
              return; 
          } 
        case ENEMY:
          checkState();
          if (this.currentStep == Step.PRE) {
            if (this.checkedThisTick)
              processNormal32k(); 
            return;
          } 
          runShulkerStep();
          if (this.actionsThisTick >= ((Integer)this.blocksPerPlace.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
            return; 
        case MIDDLE:
          checkState();
          if (this.currentStep == Step.PRE) {
            if (this.checkedThisTick)
              processNormal32k(); 
            return;
          } 
          runClickHopper();
        case FAR:
          runHopperGuiStep();
          return;
      } 
      Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
      Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
      Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
      Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
      Command.sendMessage("§cThis shouldnt happen, report to 3arthqu4ke!!!");
      this.currentStep = Step.PRE;
    } 
  }
  
  private void runPreStep() {
    if (isOff())
      return; 
    PlaceType type = (PlaceType)this.placeType.getValue();
    if (Freecam.getInstance().isOn() && !((Boolean)this.freecam.getValue()).booleanValue()) {
      if (((Boolean)this.messages.getValue()).booleanValue())
        Command.sendMessage("§c<Auto32k> Disable Freecam."); 
      if (((Boolean)this.autoSwitch.getValue()).booleanValue()) {
        resetFields();
        if (!((Boolean)this.withBind.getValue()).booleanValue())
          disable(); 
      } else {
        disable();
      } 
      return;
    } 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
    this.shulkerSlot = InventoryUtil.findHotbarBlock(BlockShulkerBox.class);
    if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
      Block block = ((ItemBlock)mc.player.getHeldItemOffhand().getItem()).getBlock();
      if (block instanceof BlockShulkerBox) {
        this.shulkerSlot = -2;
      } else if (block instanceof BlockHopper) {
        this.hopperSlot = -2;
      } 
    } 
    if (this.shulkerSlot == -1 || this.hopperSlot == -1) {
      if (((Boolean)this.messages.getValue()).booleanValue())
        Command.sendMessage("§c<Auto32k> Materials not found."); 
      if (((Boolean)this.autoSwitch.getValue()).booleanValue()) {
        resetFields();
        if (!((Boolean)this.withBind.getValue()).booleanValue())
          disable(); 
      } else {
        disable();
      } 
      return;
    } 
    this.target = EntityUtil.getClosestEnemy(((Double)this.targetRange.getValue()).doubleValue());
    if (this.target == null) {
      if (((Boolean)this.autoSwitch.getValue()).booleanValue()) {
        if (this.switching) {
          resetFields();
          this.switching = true;
        } else {
          resetFields();
        } 
        return;
      } 
      type = (this.placeType.getValue() == PlaceType.MOUSE) ? PlaceType.MOUSE : PlaceType.CLOSE;
    } 
    this.hopperPos = findBestPos(type, this.target);
    if (this.hopperPos != null) {
      if (mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper) {
        this.currentStep = Step.SHULKER;
      } else {
        this.currentStep = Step.HOPPER;
      } 
    } else {
      if (((Boolean)this.messages.getValue()).booleanValue())
        Command.sendMessage("§c<Auto32k> Block not found."); 
      if (((Boolean)this.autoSwitch.getValue()).booleanValue()) {
        resetFields();
        if (!((Boolean)this.withBind.getValue()).booleanValue())
          disable(); 
      } else {
        disable();
      } 
    } 
  }
  
  private void runHopperStep() {
    if (isOff())
      return; 
    if (this.currentStep == Step.HOPPER) {
      runPlaceStep(this.hopperPos, this.hopperSlot);
      this.currentStep = Step.SHULKER;
    } 
  }
  
  private void runShulkerStep() {
    if (isOff())
      return; 
    if (this.currentStep == Step.SHULKER) {
      runPlaceStep(this.hopperPos.up(), this.shulkerSlot);
      this.currentStep = Step.CLICKHOPPER;
    } 
  }
  
  private void runClickHopper() {
    if (isOff())
      return; 
    if (this.currentStep != Step.CLICKHOPPER)
      return; 
    if (this.mode.getValue() == Mode.NORMAL && !(mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox) && ((Boolean)this.checkForShulker.getValue()).booleanValue()) {
      if (this.placeTimer.passedMs(((Integer)this.checkDelay.getValue()).intValue()))
        this.currentStep = Step.SHULKER; 
      return;
    } 
    clickBlock(this.hopperPos);
    this.currentStep = Step.HOPPERGUI;
  }
  
  private void runHopperGuiStep() {
    if (isOff())
      return; 
    if (this.currentStep != Step.HOPPERGUI)
      return; 
    if (mc.player.openContainer instanceof net.minecraft.inventory.ContainerHopper) {
      if (!EntityUtil.holding32k((EntityPlayer)mc.player)) {
        int swordIndex = -1;
        for (int i = 0; i < 5; i++) {
          if (EntityUtil.is32k(((Slot)mc.player.openContainer.inventorySlots.get(0)).inventory.getStackInSlot(i))) {
            swordIndex = i;
            break;
          } 
        } 
        if (swordIndex == -1)
          return; 
        if (((Integer)this.trashSlot.getValue()).intValue() != 0) {
          InventoryUtil.switchToHotbarSlot(((Integer)this.trashSlot.getValue()).intValue() - 1, false);
        } else if (this.mode.getValue() != Mode.NORMAL && this.shulkerSlot > 35 && this.shulkerSlot != 45) {
          InventoryUtil.switchToHotbarSlot(44 - this.shulkerSlot, false);
        } 
        mc.playerController.windowClick(mc.player.openContainer.windowId, swordIndex, (((Integer)this.trashSlot.getValue()).intValue() == 0) ? mc.player.inventory.currentItem : (((Integer)this.trashSlot.getValue()).intValue() - 1), ClickType.SWAP, (EntityPlayer)mc.player);
      } else if (((Boolean)this.closeGui.getValue()).booleanValue() && ((Boolean)this.secretClose.getValue()).booleanValue()) {
        mc.player.closeScreen();
      } 
    } else if (EntityUtil.holding32k((EntityPlayer)mc.player)) {
      if (((Boolean)this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() == Mode.NORMAL) {
        this.switching = false;
      } else if (!((Boolean)this.autoSwitch.getValue()).booleanValue() || this.mode.getValue() == Mode.DISPENSER) {
        this.shouldDisable = true;
        this.disableTimer.reset();
      } 
    } 
  }
  
  private void runPlaceStep(BlockPos pos, int slot) {
    if (isOff())
      return; 
    EnumFacing side = EnumFacing.UP;
    if (((Boolean)this.antiHopper.getValue()).booleanValue() && this.currentStep == Step.HOPPER) {
      boolean foundfacing = false;
      for (EnumFacing facing : EnumFacing.values()) {
        if (mc.world.getBlockState(pos.offset(facing)).getBlock() != Blocks.HOPPER)
          if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable()) {
            foundfacing = true;
            side = facing;
            break;
          }  
      } 
      if (!foundfacing) {
        resetFields();
        return;
      } 
    } else {
      side = BlockUtil.getFirstFacing(pos);
      if (side == null) {
        resetFields();
        return;
      } 
    } 
    BlockPos neighbour = pos.offset(side);
    EnumFacing opposite = side.getOpposite();
    Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
    this.authSneakPacket = true;
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
    this.authSneakPacket = false;
    if (((Boolean)this.rotate.getValue()).booleanValue())
      if (((Integer)this.blocksPerPlace.getValue()).intValue() > 1) {
        float[] angle = RotationUtil.getLegitRotations(hitVec);
        if (((Boolean)this.extra.getValue()).booleanValue())
          RotationUtil.faceYawAndPitch(angle[0], angle[1]); 
      } else {
        rotateToPos((BlockPos)null, hitVec);
      }  
    InventoryUtil.switchToHotbarSlot(slot, false);
    BlockUtil.rightClickBlock(neighbour, hitVec, (slot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, ((Boolean)this.packet.getValue()).booleanValue());
    this.authSneakPacket = true;
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    this.authSneakPacket = false;
    this.placeTimer.reset();
    this.actionsThisTick++;
  }
  
  private BlockPos findBestPos(PlaceType type, EntityPlayer target) {
    List<BlockPos> toRemove;
    NonNullList<BlockPos> copy;
    BlockPos pos = null;
    NonNullList<BlockPos> positions = NonNullList.create();
    positions.addAll((Collection)BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.player), ((Float)this.range.getValue()).floatValue(), ((Float)this.range.getValue()).intValue(), false, true, 0).stream().filter(this::canPlace).collect(Collectors.toList()));
    if (positions.isEmpty())
      return null; 
    switch (type) {
      case MOUSE:
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
          BlockPos mousePos = mc.objectMouseOver.getBlockPos();
          if (mousePos != null && !canPlace(mousePos)) {
            BlockPos mousePosUp = mousePos.up();
            if (canPlace(mousePosUp))
              pos = mousePosUp; 
          } else {
            pos = mousePos;
          } 
        } 
        if (pos != null)
          break; 
      case CLOSE:
        positions.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
        pos = (BlockPos)positions.get(0);
        break;
      case ENEMY:
        positions.sort(Comparator.comparingDouble(target::getDistanceSq));
        pos = (BlockPos)positions.get(0);
        break;
      case MIDDLE:
        toRemove = new ArrayList<>();
        copy = NonNullList.create();
        copy.addAll((Collection)positions);
        for (BlockPos position : copy) {
          double difference = mc.player.getDistanceSq(position) - target.getDistanceSq(position);
          if (difference > 1.0D || difference < -1.0D)
            toRemove.add(position); 
        } 
        copy.removeAll(toRemove);
        if (copy.isEmpty())
          copy.addAll((Collection)positions); 
        copy.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
        pos = (BlockPos)copy.get(0);
        break;
      case FAR:
        positions.sort(Comparator.comparingDouble(pos2 -> -target.getDistanceSq(pos2)));
        pos = (BlockPos)positions.get(0);
        break;
      case SAFE:
        positions.sort(Comparator.comparingInt(pos2 -> -safetyFactor(pos2)));
        pos = (BlockPos)positions.get(0);
        break;
    } 
    return pos;
  }
  
  private boolean canPlace(BlockPos pos) {
    if (pos == null)
      return false; 
    BlockPos boost = pos.up();
    if (!isGoodMaterial(mc.world.getBlockState(pos).getBlock(), ((Boolean)this.onOtherHoppers.getValue()).booleanValue()) || !isGoodMaterial(mc.world.getBlockState(boost).getBlock(), false))
      return false; 
    if (((Boolean)this.raytrace.getValue()).booleanValue() && (!BlockUtil.rayTracePlaceCheck(pos, ((Boolean)this.raytrace.getValue()).booleanValue()) || !BlockUtil.rayTracePlaceCheck(pos, ((Boolean)this.raytrace.getValue()).booleanValue())))
      return false; 
    if (badEntities(pos) || badEntities(boost))
      return false; 
    if (((Boolean)this.onOtherHoppers.getValue()).booleanValue() && mc.world.getBlockState(pos).getBlock() instanceof BlockHopper)
      return true; 
    return findFacing(pos);
  }
  
  private void check() {
    if (this.currentStep != Step.PRE && this.currentStep != Step.HOPPER && this.hopperPos != null && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiHopper) && !EntityUtil.holding32k((EntityPlayer)mc.player) && (mc.player.getDistanceSq(this.hopperPos) > MathUtil.square(((Float)this.hopperDistance.getValue()).floatValue()) || mc.world.getBlockState(this.hopperPos).getBlock() != Blocks.HOPPER)) {
      resetFields();
      if (!((Boolean)this.autoSwitch.getValue()).booleanValue() || !((Boolean)this.withBind.getValue()).booleanValue() || this.mode.getValue() != Mode.NORMAL)
        disable(); 
    } 
  }
  
  private void checkState() {
    if (!((Boolean)this.checkStatus.getValue()).booleanValue() || this.checkedThisTick || (this.currentStep != Step.HOPPER && this.currentStep != Step.SHULKER && this.currentStep != Step.CLICKHOPPER)) {
      this.checkedThisTick = false;
      return;
    } 
    if (this.hopperPos == null || !isGoodMaterial(mc.world.getBlockState(this.hopperPos).getBlock(), true) || (!isGoodMaterial(mc.world.getBlockState(this.hopperPos.up()).getBlock(), false) && !(mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox)) || badEntities(this.hopperPos) || badEntities(this.hopperPos.up())) {
      if (((Boolean)this.autoSwitch.getValue()).booleanValue() && this.mode.getValue() == Mode.NORMAL) {
        if (this.switching) {
          resetFields();
          if (((Boolean)this.repeatSwitch.getValue()).booleanValue())
            this.switching = true; 
        } else {
          resetFields();
        } 
        if (!((Boolean)this.withBind.getValue()).booleanValue())
          disable(); 
      } else {
        disable();
      } 
      this.checkedThisTick = true;
    } 
  }
  
  private void processDispenser32k() {
    if (isOff())
      return; 
    if (this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue())) {
      boolean quickCheck;
      check();
      switch (this.currentStep) {
        case MOUSE:
          runDispenserPreStep();
          if (this.currentStep == Step.PRE)
            break; 
        case CLOSE:
          runHopperStep();
          this.currentStep = Step.DISPENSER;
          if (this.actionsThisTick >= ((Integer)this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
            break; 
        case SAFE:
          runDispenserStep();
          quickCheck = !mc.world.getBlockState(this.finalDispenserData.getHelpingPos()).getMaterial().isReplaceable();
          if ((this.actionsThisTick >= ((Integer)this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue())) || (this.currentStep != Step.DISPENSER_HELPING && this.currentStep != Step.CLICK_DISPENSER))
            break; 
          if (((Boolean)this.rotate.getValue()).booleanValue() && quickCheck)
            break; 
        case null:
          runDispenserStep();
          if ((this.actionsThisTick >= ((Integer)this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue())) || (this.currentStep != Step.CLICK_DISPENSER && this.currentStep != Step.DISPENSER_HELPING))
            break; 
          if (((Boolean)this.rotate.getValue()).booleanValue())
            break; 
        case null:
          clickDispenser();
          if (this.actionsThisTick >= ((Integer)this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
            break; 
        case null:
          dispenserGui();
          if (this.currentStep == Step.DISPENSER_GUI)
            break; 
        case null:
          placeRedstone();
          if (this.actionsThisTick >= ((Integer)this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
            break; 
        case MIDDLE:
          runClickHopper();
          if (this.actionsThisTick >= ((Integer)this.delayDispenser.getValue()).intValue() && !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()))
            break; 
        case FAR:
          runHopperGuiStep();
          if (this.actionsThisTick < ((Integer)this.delayDispenser.getValue()).intValue() || !this.placeTimer.passedMs(((Integer)this.delay.getValue()).intValue()));
          break;
      } 
    } 
  }
  
  private void placeRedstone() {
    if (isOff())
      return; 
    if (badEntities(this.hopperPos.up()) && !(mc.world.getBlockState(this.hopperPos.up()).getBlock() instanceof BlockShulkerBox))
      return; 
    runPlaceStep(this.finalDispenserData.getRedStonePos(), this.redstoneSlot);
    this.currentStep = Step.CLICKHOPPER;
  }
  
  private void clickDispenser() {
    if (isOff())
      return; 
    clickBlock(this.finalDispenserData.getDispenserPos());
    this.currentStep = Step.DISPENSER_GUI;
  }
  
  private void dispenserGui() {
    if (isOff())
      return; 
    if (!(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiDispenser))
      return; 
    mc.playerController.windowClick(mc.player.openContainer.windowId, this.shulkerSlot, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
    mc.player.closeScreen();
    this.currentStep = Step.REDSTONE;
  }
  
  private void clickBlock(BlockPos pos) {
    if (isOff() || pos == null)
      return; 
    this.authSneakPacket = true;
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    this.authSneakPacket = false;
    Vec3d hitVec = (new Vec3d((Vec3i)pos)).add(0.5D, -0.5D, 0.5D);
    if (((Boolean)this.rotate.getValue()).booleanValue())
      rotateToPos((BlockPos)null, hitVec); 
    EnumFacing facing = EnumFacing.UP;
    if (this.finalDispenserData != null && this.finalDispenserData.getDispenserPos() != null && this.finalDispenserData.getDispenserPos().equals(pos) && pos.getY() > (new BlockPos(mc.player.getPositionVector())).up().getY())
      facing = EnumFacing.DOWN; 
    BlockUtil.rightClickBlock(pos, hitVec, (this.shulkerSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, facing, ((Boolean)this.packet.getValue()).booleanValue());
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.rightClickDelayTimer = 4;
    this.actionsThisTick++;
  }
  
  private void runDispenserStep() {
    if (isOff())
      return; 
    if (this.finalDispenserData == null || this.finalDispenserData.getDispenserPos() == null || this.finalDispenserData.getHelpingPos() == null) {
      resetFields();
      return;
    } 
    if (this.currentStep != Step.DISPENSER && this.currentStep != Step.DISPENSER_HELPING)
      return; 
    BlockPos dispenserPos = this.finalDispenserData.getDispenserPos();
    BlockPos helpingPos = this.finalDispenserData.getHelpingPos();
    if (mc.world.getBlockState(helpingPos).getMaterial().isReplaceable()) {
      this.currentStep = Step.DISPENSER_HELPING;
      EnumFacing facing = EnumFacing.DOWN;
      boolean foundHelpingPos = false;
      for (EnumFacing enumFacing : EnumFacing.values()) {
        BlockPos position = helpingPos.offset(enumFacing);
        if (!position.equals(this.hopperPos) && 
          !position.equals(this.hopperPos.up()) && 
          !position.equals(dispenserPos) && 
          !position.equals(this.finalDispenserData.getRedStonePos()) && mc.player
          .getDistanceSq(position) <= MathUtil.square(((Float)this.range.getValue()).floatValue()) && (
          !((Boolean)this.raytrace.getValue()).booleanValue() || BlockUtil.rayTracePlaceCheck(position, ((Boolean)this.raytrace.getValue()).booleanValue())) && 
          !mc.world.getBlockState(position).getMaterial().isReplaceable()) {
          foundHelpingPos = true;
          facing = enumFacing;
          break;
        } 
      } 
      if (!foundHelpingPos) {
        disable();
        return;
      } 
      BlockPos neighbour = helpingPos.offset(facing);
      EnumFacing opposite = facing.getOpposite();
      Vec3d hitVec = (new Vec3d((Vec3i)neighbour)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
      Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
      this.authSneakPacket = true;
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      this.authSneakPacket = false;
      if (((Boolean)this.rotate.getValue()).booleanValue())
        if (((Integer)this.blocksPerPlace.getValue()).intValue() > 1) {
          float[] angle = RotationUtil.getLegitRotations(hitVec);
          if (((Boolean)this.extra.getValue()).booleanValue())
            RotationUtil.faceYawAndPitch(angle[0], angle[1]); 
        } else {
          rotateToPos((BlockPos)null, hitVec);
        }  
      int slot = (((Boolean)this.preferObby.getValue()).booleanValue() && this.obbySlot != -1) ? this.obbySlot : this.dispenserSlot;
      InventoryUtil.switchToHotbarSlot(slot, false);
      BlockUtil.rightClickBlock(neighbour, hitVec, (slot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, ((Boolean)this.packet.getValue()).booleanValue());
      this.authSneakPacket = true;
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
      this.authSneakPacket = false;
      this.placeTimer.reset();
      this.actionsThisTick++;
      return;
    } 
    placeDispenserAgainstBlock(dispenserPos, helpingPos);
    this.actionsThisTick++;
    this.currentStep = Step.CLICK_DISPENSER;
  }
  
  private void placeDispenserAgainstBlock(BlockPos dispenserPos, BlockPos helpingPos) {
    if (isOff())
      return; 
    EnumFacing facing = EnumFacing.DOWN;
    for (EnumFacing enumFacing : EnumFacing.values()) {
      BlockPos position = dispenserPos.offset(enumFacing);
      if (position.equals(helpingPos)) {
        facing = enumFacing;
        break;
      } 
    } 
    EnumFacing opposite = facing.getOpposite();
    Vec3d hitVec = (new Vec3d((Vec3i)helpingPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    Block neighbourBlock = mc.world.getBlockState(helpingPos).getBlock();
    this.authSneakPacket = true;
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
    this.authSneakPacket = false;
    Vec3d rotationVec = null;
    EnumFacing facings = EnumFacing.UP;
    if (((Boolean)this.rotate.getValue()).booleanValue()) {
      if (((Integer)this.blocksPerPlace.getValue()).intValue() > 1) {
        float[] arrayOfFloat = RotationUtil.getLegitRotations(hitVec);
        if (((Boolean)this.extra.getValue()).booleanValue())
          RotationUtil.faceYawAndPitch(arrayOfFloat[0], arrayOfFloat[1]); 
      } else {
        rotateToPos((BlockPos)null, hitVec);
      } 
      rotationVec = (new Vec3d((Vec3i)helpingPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    } else if (dispenserPos.getY() <= (new BlockPos(mc.player.getPositionVector())).up().getY()) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
        BlockPos position = this.hopperPos.up().offset(enumFacing);
        if (position.equals(dispenserPos)) {
          facings = enumFacing;
          break;
        } 
      } 
      float[] arrayOfFloat = RotationUtil.simpleFacing(facings);
      this.yaw = arrayOfFloat[0];
      this.pitch = arrayOfFloat[1];
      this.spoof = true;
    } else {
      float[] arrayOfFloat = RotationUtil.simpleFacing(facings);
      this.yaw = arrayOfFloat[0];
      this.pitch = arrayOfFloat[1];
      this.spoof = true;
    } 
    rotationVec = (new Vec3d((Vec3i)helpingPos)).add(0.5D, 0.5D, 0.5D).add((new Vec3d(opposite.getDirectionVec())).scale(0.5D));
    float[] rotations = RotationUtil.simpleFacing(facings);
    float[] angle = RotationUtil.getLegitRotations(hitVec);
    if (((Boolean)this.superPacket.getValue()).booleanValue())
      RotationUtil.faceYawAndPitch(!((Boolean)this.rotate.getValue()).booleanValue() ? rotations[0] : angle[0], !((Boolean)this.rotate.getValue()).booleanValue() ? rotations[1] : angle[1]); 
    InventoryUtil.switchToHotbarSlot(this.dispenserSlot, false);
    BlockUtil.rightClickBlock(helpingPos, rotationVec, (this.dispenserSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, opposite, ((Boolean)this.packet.getValue()).booleanValue());
    this.authSneakPacket = true;
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    this.authSneakPacket = false;
    this.placeTimer.reset();
    this.actionsThisTick++;
    this.currentStep = Step.CLICK_DISPENSER;
  }
  
  private void runDispenserPreStep() {
    if (isOff())
      return; 
    if (Freecam.getInstance().isOn() && !((Boolean)this.freecam.getValue()).booleanValue()) {
      if (((Boolean)this.messages.getValue()).booleanValue())
        Command.sendMessage("§c<Auto32k> Disable Freecam."); 
      disable();
      return;
    } 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.hopperSlot = InventoryUtil.findHotbarBlock(BlockHopper.class);
    this.shulkerSlot = InventoryUtil.findBlockSlotInventory(BlockShulkerBox.class, false, false);
    this.dispenserSlot = InventoryUtil.findHotbarBlock(BlockDispenser.class);
    this.redstoneSlot = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
    this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock) {
      Block block = ((ItemBlock)mc.player.getHeldItemOffhand().getItem()).getBlock();
      if (block instanceof BlockHopper) {
        this.hopperSlot = -2;
      } else if (block instanceof BlockDispenser) {
        this.dispenserSlot = -2;
      } else if (block == Blocks.REDSTONE_BLOCK) {
        this.redstoneSlot = -2;
      } else if (block instanceof BlockObsidian) {
        this.obbySlot = -2;
      } 
    } 
    if (this.shulkerSlot == -1 || this.hopperSlot == -1 || this.dispenserSlot == -1 || this.redstoneSlot == -1) {
      if (((Boolean)this.messages.getValue()).booleanValue())
        Command.sendMessage("§c<Auto32k> Materials not found."); 
      disable();
      return;
    } 
    this.finalDispenserData = findBestPos();
    if (this.finalDispenserData.isPlaceable()) {
      this.hopperPos = this.finalDispenserData.getHopperPos();
      if (mc.world.getBlockState(this.hopperPos).getBlock() instanceof BlockHopper) {
        this.currentStep = Step.DISPENSER;
      } else {
        this.currentStep = Step.HOPPER;
      } 
    } else {
      if (((Boolean)this.messages.getValue()).booleanValue())
        Command.sendMessage("§c<Auto32k> Block not found."); 
      disable();
    } 
  }
  
  private DispenserData findBestPos() {
    List<BlockPos> toRemove;
    NonNullList<BlockPos> copy;
    PlaceType type = (PlaceType)this.placeType.getValue();
    this.target = EntityUtil.getClosestEnemy(((Double)this.targetRange.getValue()).doubleValue());
    if (this.target == null)
      type = (this.placeType.getValue() == PlaceType.MOUSE) ? PlaceType.MOUSE : PlaceType.CLOSE; 
    NonNullList<BlockPos> positions = NonNullList.create();
    positions.addAll(BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.player), ((Float)this.range.getValue()).floatValue(), ((Float)this.range.getValue()).intValue(), false, true, 0));
    DispenserData data = new DispenserData();
    switch (type) {
      case MOUSE:
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
          BlockPos mousePos = mc.objectMouseOver.getBlockPos();
          if (mousePos != null) {
            data = analyzePos(mousePos);
            if (!data.isPlaceable())
              data = analyzePos(mousePos.up()); 
          } 
        } 
        if (data.isPlaceable())
          return data; 
      case CLOSE:
        positions.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
        break;
      case ENEMY:
        positions.sort(Comparator.comparingDouble(this.target::getDistanceSq));
        break;
      case MIDDLE:
        toRemove = new ArrayList<>();
        copy = NonNullList.create();
        copy.addAll((Collection)positions);
        for (BlockPos position : copy) {
          double difference = mc.player.getDistanceSq(position) - this.target.getDistanceSq(position);
          if (difference > 1.0D || difference < -1.0D)
            toRemove.add(position); 
        } 
        copy.removeAll(toRemove);
        if (copy.isEmpty())
          copy.addAll((Collection)positions); 
        copy.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
        break;
      case FAR:
        positions.sort(Comparator.comparingDouble(pos2 -> -this.target.getDistanceSq(pos2)));
        break;
      case SAFE:
        positions.sort(Comparator.comparingInt(pos2 -> -safetyFactor(pos2)));
        break;
    } 
    data = findData(positions);
    return data;
  }
  
  private DispenserData findData(NonNullList<BlockPos> positions) {
    for (BlockPos position : positions) {
      DispenserData data = analyzePos(position);
      if (data.isPlaceable())
        return data; 
    } 
    return new DispenserData();
  }
  
  private DispenserData analyzePos(BlockPos pos) {
    DispenserData data = new DispenserData(pos);
    if (pos == null)
      return data; 
    if (!isGoodMaterial(mc.world.getBlockState(pos).getBlock(), ((Boolean)this.onOtherHoppers.getValue()).booleanValue()) || !isGoodMaterial(mc.world.getBlockState(pos.up()).getBlock(), false))
      return data; 
    if (((Boolean)this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(pos, ((Boolean)this.raytrace.getValue()).booleanValue()))
      return data; 
    if (badEntities(pos) || badEntities(pos.up()))
      return data; 
    if (hasAdjancedRedstone(pos))
      return data; 
    if (!findFacing(pos))
      return data; 
    BlockPos[] otherPositions = checkForDispenserPos(pos);
    if (otherPositions[0] == null || otherPositions[1] == null || otherPositions[2] == null)
      return data; 
    data.setDispenserPos(otherPositions[0]);
    data.setRedStonePos(otherPositions[1]);
    data.setHelpingPos(otherPositions[2]);
    data.setPlaceable(true);
    return data;
  }
  
  private boolean findFacing(BlockPos pos) {
    boolean foundFacing = false;
    for (EnumFacing facing : EnumFacing.values()) {
      if (facing != EnumFacing.UP) {
        if (facing == EnumFacing.DOWN && ((Boolean)this.antiHopper.getValue()).booleanValue() && mc.world.getBlockState(pos.offset(facing)).getBlock() == Blocks.HOPPER) {
          foundFacing = false;
          break;
        } 
        if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable() && (!((Boolean)this.antiHopper.getValue()).booleanValue() || mc.world.getBlockState(pos.offset(facing)).getBlock() != Blocks.HOPPER))
          foundFacing = true; 
      } 
    } 
    return foundFacing;
  }
  
  private BlockPos[] checkForDispenserPos(BlockPos posIn) {
    BlockPos[] pos = new BlockPos[3];
    BlockPos playerPos = new BlockPos(mc.player.getPositionVector());
    if (posIn.getY() < playerPos.down().getY())
      return pos; 
    List<BlockPos> possiblePositions = getDispenserPositions(posIn);
    if (posIn.getY() < playerPos.getY()) {
      possiblePositions.remove(posIn.up().up());
    } else if (posIn.getY() > playerPos.getY()) {
      possiblePositions.remove(posIn.west().up());
      possiblePositions.remove(posIn.north().up());
      possiblePositions.remove(posIn.south().up());
      possiblePositions.remove(posIn.east().up());
    } 
    if (((Boolean)this.rotate.getValue()).booleanValue() || ((Boolean)this.simulate.getValue()).booleanValue()) {
      possiblePositions.sort(Comparator.comparingDouble(pos2 -> -mc.player.getDistanceSq(pos2)));
      BlockPos posToCheck = possiblePositions.get(0);
      if (!isGoodMaterial(mc.world.getBlockState(posToCheck).getBlock(), false))
        return pos; 
      if (mc.player.getDistanceSq(posToCheck) > MathUtil.square(((Float)this.range.getValue()).floatValue()))
        return pos; 
      if (((Boolean)this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(posToCheck, ((Boolean)this.raytrace.getValue()).booleanValue()))
        return pos; 
      if (badEntities(posToCheck))
        return pos; 
      if (hasAdjancedRedstone(posToCheck))
        return pos; 
      List<BlockPos> possibleRedStonePositions = checkRedStone(posToCheck, posIn);
      if (possiblePositions.isEmpty())
        return pos; 
      BlockPos[] helpingStuff = getHelpingPos(posToCheck, posIn, possibleRedStonePositions);
      if (helpingStuff != null && helpingStuff[0] != null && helpingStuff[1] != null) {
        pos[0] = posToCheck;
        pos[1] = helpingStuff[1];
        pos[2] = helpingStuff[0];
      } 
    } else {
      possiblePositions.removeIf(position -> (mc.player.getDistanceSq(position) > MathUtil.square(((Float)this.range.getValue()).floatValue())));
      possiblePositions.removeIf(position -> !isGoodMaterial(mc.world.getBlockState(position).getBlock(), false));
      possiblePositions.removeIf(position -> (((Boolean)this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(position, ((Boolean)this.raytrace.getValue()).booleanValue())));
      possiblePositions.removeIf(this::badEntities);
      possiblePositions.removeIf(this::hasAdjancedRedstone);
      for (BlockPos position : possiblePositions) {
        List<BlockPos> possibleRedStonePositions = checkRedStone(position, posIn);
        if (possiblePositions.isEmpty())
          continue; 
        BlockPos[] helpingStuff = getHelpingPos(position, posIn, possibleRedStonePositions);
        if (helpingStuff != null && helpingStuff[0] != null && helpingStuff[1] != null) {
          pos[0] = position;
          pos[1] = helpingStuff[1];
          pos[2] = helpingStuff[0];
          break;
        } 
      } 
    } 
    return pos;
  }
  
  private List<BlockPos> checkRedStone(BlockPos pos, BlockPos hopperPos) {
    List<BlockPos> toCheck = new ArrayList<>();
    for (EnumFacing facing : EnumFacing.values())
      toCheck.add(pos.offset(facing)); 
    toCheck.removeIf(position -> position.equals(hopperPos.up()));
    toCheck.removeIf(position -> (mc.player.getDistanceSq(position) > MathUtil.square(((Float)this.range.getValue()).floatValue())));
    toCheck.removeIf(position -> !isGoodMaterial(mc.world.getBlockState(position).getBlock(), false));
    toCheck.removeIf(position -> (((Boolean)this.raytrace.getValue()).booleanValue() && !BlockUtil.rayTracePlaceCheck(position, ((Boolean)this.raytrace.getValue()).booleanValue())));
    toCheck.removeIf(this::badEntities);
    toCheck.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
    return toCheck;
  }
  
  private boolean hasAdjancedRedstone(BlockPos pos) {
    for (EnumFacing facing : EnumFacing.values()) {
      BlockPos position = pos.offset(facing);
      if (mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_BLOCK || mc.world.getBlockState(position).getBlock() == Blocks.REDSTONE_TORCH)
        return true; 
    } 
    return false;
  }
  
  private List<BlockPos> getDispenserPositions(BlockPos pos) {
    List<BlockPos> list = new ArrayList<>();
    for (EnumFacing facing : EnumFacing.values()) {
      if (facing != EnumFacing.DOWN)
        list.add(pos.offset(facing).up()); 
    } 
    return list;
  }
  
  private BlockPos[] getHelpingPos(BlockPos pos, BlockPos hopperPos, List<BlockPos> redStonePositions) {
    BlockPos[] result = new BlockPos[2];
    List<BlockPos> possiblePositions = new ArrayList<>();
    if (redStonePositions.isEmpty())
      return null; 
    for (EnumFacing facing : EnumFacing.values()) {
      BlockPos facingPos = pos.offset(facing);
      if (!facingPos.equals(hopperPos) && !facingPos.equals(hopperPos.up()))
        if (!mc.world.getBlockState(facingPos).getMaterial().isReplaceable()) {
          if (redStonePositions.contains(facingPos)) {
            redStonePositions.remove(facingPos);
            if (redStonePositions.isEmpty()) {
              redStonePositions.add(facingPos);
            } else {
              result[0] = facingPos;
              result[1] = redStonePositions.get(0);
              return result;
            } 
          } else {
            result[0] = facingPos;
            result[1] = redStonePositions.get(0);
            return result;
          } 
        } else {
          for (EnumFacing facing1 : EnumFacing.values()) {
            BlockPos facingPos1 = facingPos.offset(facing1);
            if (!facingPos1.equals(hopperPos) && !facingPos1.equals(hopperPos.up()) && !facingPos1.equals(pos) && !mc.world.getBlockState(facingPos1).getMaterial().isReplaceable())
              if (redStonePositions.contains(facingPos)) {
                redStonePositions.remove(facingPos);
                if (redStonePositions.isEmpty()) {
                  redStonePositions.add(facingPos);
                } else {
                  possiblePositions.add(facingPos);
                } 
              } else {
                possiblePositions.add(facingPos);
              }  
          } 
        }  
    } 
    possiblePositions.removeIf(position -> (mc.player.getDistanceSq(position) > MathUtil.square(((Float)this.range.getValue()).floatValue())));
    possiblePositions.sort(Comparator.comparingDouble(position -> mc.player.getDistanceSq(position)));
    if (!possiblePositions.isEmpty()) {
      redStonePositions.remove(possiblePositions.get(0));
      if (!redStonePositions.isEmpty()) {
        result[0] = possiblePositions.get(0);
        result[1] = redStonePositions.get(0);
      } 
      return result;
    } 
    return null;
  }
  
  private void rotateToPos(BlockPos pos, Vec3d vec3d) {
    float[] angle;
    if (vec3d == null) {
      angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
    } else {
      angle = RotationUtil.getLegitRotations(vec3d);
    } 
    this.yaw = angle[0];
    this.pitch = angle[1];
    this.spoof = true;
  }
  
  private boolean isGoodMaterial(Block block, boolean allowHopper) {
    return (block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid || block instanceof net.minecraft.block.BlockTallGrass || block instanceof net.minecraft.block.BlockFire || block instanceof net.minecraft.block.BlockDeadBush || block instanceof net.minecraft.block.BlockSnow || (allowHopper && block instanceof BlockHopper));
  }
  
  private void resetFields() {
    this.shouldDisable = false;
    this.spoof = false;
    this.switching = false;
    this.lastHotbarSlot = -1;
    this.shulkerSlot = -1;
    this.hopperSlot = -1;
    this.hopperPos = null;
    this.target = null;
    this.currentStep = Step.PRE;
    this.obbySlot = -1;
    this.dispenserSlot = -1;
    this.redstoneSlot = -1;
    this.finalDispenserData = null;
    this.actionsThisTick = 0;
    this.rotationprepared = false;
  }
  
  public static class DispenserData {
    private BlockPos dispenserPos;
    
    private BlockPos redStonePos;
    
    private BlockPos hopperPos;
    
    private BlockPos helpingPos;
    
    private boolean isPlaceable;
    
    public DispenserData() {
      this.isPlaceable = false;
    }
    
    public DispenserData(BlockPos pos) {
      this.isPlaceable = false;
      this.hopperPos = pos;
    }
    
    public void setPlaceable(boolean placeable) {
      this.isPlaceable = placeable;
    }
    
    public boolean isPlaceable() {
      return (this.dispenserPos != null && this.hopperPos != null && this.redStonePos != null && this.helpingPos != null);
    }
    
    public BlockPos getDispenserPos() {
      return this.dispenserPos;
    }
    
    public void setDispenserPos(BlockPos dispenserPos) {
      this.dispenserPos = dispenserPos;
    }
    
    public BlockPos getRedStonePos() {
      return this.redStonePos;
    }
    
    public void setRedStonePos(BlockPos redStonePos) {
      this.redStonePos = redStonePos;
    }
    
    public BlockPos getHopperPos() {
      return this.hopperPos;
    }
    
    public void setHopperPos(BlockPos hopperPos) {
      this.hopperPos = hopperPos;
    }
    
    public BlockPos getHelpingPos() {
      return this.helpingPos;
    }
    
    public void setHelpingPos(BlockPos helpingPos) {
      this.helpingPos = helpingPos;
    }
  }
  
  private boolean badEntities(BlockPos pos) {
    for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
      if (!(entity instanceof net.minecraft.entity.item.EntityExpBottle) && !(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb))
        return true; 
    } 
    return false;
  }
  
  private int safetyFactor(BlockPos pos) {
    return safety(pos) + safety(pos.up());
  }
  
  private int safety(BlockPos pos) {
    int safety = 0;
    for (EnumFacing facing : EnumFacing.values()) {
      if (!mc.world.getBlockState(pos.offset(facing)).getMaterial().isReplaceable())
        safety++; 
    } 
    return safety;
  }
  
  public enum PlaceType {
    MOUSE, CLOSE, ENEMY, MIDDLE, FAR, SAFE;
  }
  
  public enum Mode {
    NORMAL, DISPENSER;
  }
  
  public enum Step {
    PRE, HOPPER, SHULKER, CLICKHOPPER, HOPPERGUI, DISPENSER_HELPING, DISPENSER_GUI, DISPENSER, CLICK_DISPENSER, REDSTONE;
  }
}
