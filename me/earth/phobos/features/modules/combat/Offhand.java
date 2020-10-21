//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.ProcessRightClickBlockEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.EnumConverter;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Offhand extends Module {
  public Setting<Type> type = register(new Setting("Mode", Type.NEW));
  
  public Setting<Boolean> cycle = register(new Setting("Cycle", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Bind> cycleKey = register(new Setting("Key", new Bind(-1), v -> (((Boolean)this.cycle.getValue()).booleanValue() && this.type.getValue() == Type.OLD)));
  
  public Setting<Bind> offHandGapple = register(new Setting("Gapple", new Bind(-1)));
  
  public Setting<Float> gappleHealth = register(new Setting("G-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Float> gappleHoleHealth = register(new Setting("G-H-Health", Float.valueOf(3.5F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Bind> offHandCrystal = register(new Setting("Crystal", new Bind(-1)));
  
  public Setting<Float> crystalHealth = register(new Setting("C-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Float> crystalHoleHealth = register(new Setting("C-H-Health", Float.valueOf(3.5F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Float> cTargetDistance = register(new Setting("C-Distance", Float.valueOf(10.0F), Float.valueOf(1.0F), Float.valueOf(20.0F)));
  
  public Setting<Bind> obsidian = register(new Setting("Obsidian", new Bind(-1)));
  
  public Setting<Float> obsidianHealth = register(new Setting("O-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Float> obsidianHoleHealth = register(new Setting("O-H-Health", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Bind> webBind = register(new Setting("Webs", new Bind(-1)));
  
  public Setting<Float> webHealth = register(new Setting("W-Health", Float.valueOf(13.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Float> webHoleHealth = register(new Setting("W-H-Health", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(36.0F)));
  
  public Setting<Boolean> holeCheck = register(new Setting("Hole-Check", Boolean.valueOf(true)));
  
  public Setting<Boolean> crystalCheck = register(new Setting("Crystal-Check", Boolean.valueOf(false)));
  
  public Setting<Boolean> gapSwap = register(new Setting("Gap-Swap", Boolean.valueOf(true)));
  
  public Setting<Integer> updates = register(new Setting("Updates", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(2)));
  
  public Setting<Boolean> cycleObby = register(new Setting("CycleObby", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> cycleWebs = register(new Setting("CycleWebs", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> crystalToTotem = register(new Setting("Crystal-Totem", Boolean.valueOf(true), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> absorption = register(new Setting("Absorption", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> autoGapple = register(new Setting("AutoGapple", Boolean.valueOf(false), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> onlyWTotem = register(new Setting("OnlyWTotem", Boolean.valueOf(true), v -> (((Boolean)this.autoGapple.getValue()).booleanValue() && this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> unDrawTotem = register(new Setting("DrawTotems", Boolean.valueOf(true), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> noOffhandGC = register(new Setting("NoOGC", Boolean.valueOf(false)));
  
  public Setting<Boolean> returnToCrystal = register(new Setting("RecoverySwitch", Boolean.valueOf(false)));
  
  public Setting<Integer> timeout = register(new Setting("Timeout", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
  
  public Setting<Integer> timeout2 = register(new Setting("Timeout2", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
  
  public Setting<Integer> actions = register(new Setting("Actions", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4), v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<NameMode> displayNameChange = register(new Setting("Name", NameMode.TOTEM, v -> (this.type.getValue() == Type.OLD)));
  
  public Setting<Boolean> guis = register(new Setting("Guis", Boolean.valueOf(false)));
  
  public Mode mode = Mode.CRYSTALS;
  
  public Mode oldMode = Mode.CRYSTALS;
  
  private int oldSlot = -1;
  
  private boolean swapToTotem = false;
  
  private boolean eatingApple = false;
  
  private boolean oldSwapToTotem = false;
  
  public Mode2 currentMode = Mode2.TOTEMS;
  
  public int totems = 0;
  
  public int crystals = 0;
  
  public int gapples = 0;
  
  public int obby = 0;
  
  public int webs = 0;
  
  public int lastTotemSlot = -1;
  
  public int lastGappleSlot = -1;
  
  public int lastCrystalSlot = -1;
  
  public int lastObbySlot = -1;
  
  public int lastWebSlot = -1;
  
  public boolean holdingCrystal = false;
  
  public boolean holdingTotem = false;
  
  public boolean holdingGapple = false;
  
  public boolean holdingObby = false;
  
  public boolean holdingWeb = false;
  
  public boolean didSwitchThisTick = false;
  
  private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
  
  private boolean autoGappleSwitch = false;
  
  private static Offhand instance;
  
  private Timer timer = new Timer();
  
  private Timer secondTimer = new Timer();
  
  private boolean second = false;
  
  private boolean switchedForHealthReason = false;
  
  public Offhand() {
    super("Offhand", "Allows you to switch up your Offhand.", Module.Category.COMBAT, true, false, false);
    instance = this;
  }
  
  public static Offhand getInstance() {
    if (instance == null)
      instance = new Offhand(); 
    return instance;
  }
  
  public void onItemFinish(ItemStack stack, EntityLivingBase base) {
    if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && base.equals(mc.player) && stack.getItem() == mc.player.getHeldItemOffhand().getItem()) {
      this.secondTimer.reset();
      this.second = true;
    } 
  }
  
  public void onTick() {
    if (nullCheck() || ((Integer)this.updates.getValue()).intValue() == 1)
      return; 
    doOffhand();
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(ProcessRightClickBlockEvent event) {
    if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.objectMouseOver != null && event.pos == mc.objectMouseOver.getBlockPos()) {
      event.setCanceled(true);
      mc.player.setActiveHand(EnumHand.OFF_HAND);
      mc.playerController.processRightClick((EntityPlayer)mc.player, (World)mc.world, EnumHand.OFF_HAND);
    } 
  }
  
  public void onUpdate() {
    if (((Boolean)this.noOffhandGC.getValue()).booleanValue())
      if (this.timer.passedMs(((Integer)this.timeout.getValue()).intValue())) {
        if (mc.player != null && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
          mc.player.setActiveHand(EnumHand.OFF_HAND);
          mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
        } 
      } else if (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
        mc.gameSettings.keyBindUseItem.pressed = false;
      }  
    if (nullCheck() || ((Integer)this.updates.getValue()).intValue() == 2)
      return; 
    doOffhand();
    if (this.secondTimer.passedMs(((Integer)this.timeout2.getValue()).intValue()) && this.second) {
      this.second = false;
      this.timer.reset();
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (Keyboard.getEventKeyState())
      if (this.type.getValue() == Type.NEW) {
        if (((Bind)this.offHandCrystal.getValue()).getKey() == Keyboard.getEventKey()) {
          if (this.mode == Mode.CRYSTALS) {
            setSwapToTotem(!isSwapToTotem());
          } else {
            setSwapToTotem(false);
          } 
          setMode(Mode.CRYSTALS);
        } 
        if (((Bind)this.offHandGapple.getValue()).getKey() == Keyboard.getEventKey()) {
          if (this.mode == Mode.GAPPLES) {
            setSwapToTotem(!isSwapToTotem());
          } else {
            setSwapToTotem(false);
          } 
          setMode(Mode.GAPPLES);
        } 
        if (((Bind)this.obsidian.getValue()).getKey() == Keyboard.getEventKey()) {
          if (this.mode == Mode.OBSIDIAN) {
            setSwapToTotem(!isSwapToTotem());
          } else {
            setSwapToTotem(false);
          } 
          setMode(Mode.OBSIDIAN);
        } 
        if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey()) {
          if (this.mode == Mode.WEBS) {
            setSwapToTotem(!isSwapToTotem());
          } else {
            setSwapToTotem(false);
          } 
          setMode(Mode.WEBS);
        } 
      } else if (((Boolean)this.cycle.getValue()).booleanValue()) {
        if (((Bind)this.cycleKey.getValue()).getKey() == Keyboard.getEventKey()) {
          Mode2 newMode = (Mode2)EnumConverter.increaseEnum(this.currentMode);
          if ((newMode == Mode2.OBSIDIAN && !((Boolean)this.cycleObby.getValue()).booleanValue()) || (newMode == Mode2.WEBS && !((Boolean)this.cycleWebs.getValue()).booleanValue()))
            newMode = Mode2.TOTEMS; 
          setMode(newMode);
        } 
      } else {
        if (((Bind)this.offHandCrystal.getValue()).getKey() == Keyboard.getEventKey())
          setMode(Mode2.CRYSTALS); 
        if (((Bind)this.offHandGapple.getValue()).getKey() == Keyboard.getEventKey())
          setMode(Mode2.GAPPLES); 
        if (((Bind)this.obsidian.getValue()).getKey() == Keyboard.getEventKey())
          setMode(Mode2.OBSIDIAN); 
        if (((Bind)this.webBind.getValue()).getKey() == Keyboard.getEventKey())
          setMode(Mode2.WEBS); 
      }  
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (((Boolean)this.noOffhandGC.getValue()).booleanValue() && !fullNullCheck() && mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && mc.gameSettings.keyBindUseItem.isKeyDown())
      if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
        CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
        if (packet.getHand() == EnumHand.MAIN_HAND && 
          !AutoCrystal.placedPos.contains(packet.getPos())) {
          if (this.timer.passedMs(((Integer)this.timeout.getValue()).intValue())) {
            mc.player.setActiveHand(EnumHand.OFF_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
          } 
          event.setCanceled(true);
        } 
      } else if (event.getPacket() instanceof CPacketPlayerTryUseItem) {
        CPacketPlayerTryUseItem packet = (CPacketPlayerTryUseItem)event.getPacket();
        if (packet.getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(((Integer)this.timeout.getValue()).intValue()))
          event.setCanceled(true); 
      }  
  }
  
  public String getDisplayInfo() {
    if (this.type.getValue() == Type.NEW)
      return String.valueOf(getStackSize()); 
    switch ((NameMode)this.displayNameChange.getValue()) {
      case GAPPLES:
        return EnumConverter.getProperName(this.currentMode);
      case WEBS:
        if (this.currentMode == Mode2.TOTEMS)
          return this.totems + ""; 
        return EnumConverter.getProperName(this.currentMode);
    } 
    switch (this.currentMode) {
      case GAPPLES:
        return this.totems + "";
      case WEBS:
        return this.gapples + "";
    } 
    return this.crystals + "";
  }
  
  public String getDisplayName() {
    if (this.type.getValue() == Type.NEW) {
      if (!shouldTotem()) {
        switch (this.mode) {
          case GAPPLES:
            return "OffhandGapple";
          case WEBS:
            return "OffhandWebs";
          case OBSIDIAN:
            return "OffhandObby";
        } 
        return "OffhandCrystal";
      } 
      return "AutoTotem" + (!isSwapToTotem() ? ("-" + getModeStr()) : "");
    } 
    switch ((NameMode)this.displayNameChange.getValue()) {
      case GAPPLES:
        return (String)this.displayName.getValue();
      case WEBS:
        if (this.currentMode == Mode2.TOTEMS)
          return "AutoTotem"; 
        return (String)this.displayName.getValue();
    } 
    switch (this.currentMode) {
      case GAPPLES:
        return "AutoTotem";
      case WEBS:
        return "OffhandGapple";
      case OBSIDIAN:
        return "OffhandWebs";
      case CRYSTALS:
        return "OffhandObby";
    } 
    return "OffhandCrystal";
  }
  
  public void doOffhand() {
    if (this.type.getValue() == Type.NEW) {
      if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !((Boolean)this.guis.getValue()).booleanValue() && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
        return; 
      if (((Boolean)this.gapSwap.getValue()).booleanValue())
        if ((getSlot(Mode.GAPPLES) != -1 || mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) && mc.player.getHeldItemMainhand().getItem() != Items.GOLDEN_APPLE && mc.gameSettings.keyBindUseItem.isKeyDown()) {
          setMode(Mode.GAPPLES);
          this.eatingApple = true;
          this.swapToTotem = false;
        } else if (this.eatingApple) {
          setMode(this.oldMode);
          this.swapToTotem = this.oldSwapToTotem;
          this.eatingApple = false;
        } else {
          this.oldMode = this.mode;
          this.oldSwapToTotem = this.swapToTotem;
        }  
      if (!shouldTotem()) {
        if (mc.player.getHeldItemOffhand() == ItemStack.EMPTY || !isItemInOffhand()) {
          int slot = (getSlot(this.mode) < 9) ? (getSlot(this.mode) + 36) : getSlot(this.mode);
          if (getSlot(this.mode) != -1) {
            if (this.oldSlot != -1) {
              mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
              mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
            } 
            this.oldSlot = slot;
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          } 
        } 
      } else if (!this.eatingApple && (mc.player.getHeldItemOffhand() == ItemStack.EMPTY || mc.player.getHeldItemOffhand().getItem() != Items.TOTEM_OF_UNDYING)) {
        int slot = (getTotemSlot() < 9) ? (getTotemSlot() + 36) : getTotemSlot();
        if (getTotemSlot() != -1) {
          mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
          this.oldSlot = -1;
        } 
      } 
    } else {
      if (!((Boolean)this.unDrawTotem.getValue()).booleanValue())
        manageDrawn(); 
      this.didSwitchThisTick = false;
      this.holdingCrystal = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
      this.holdingTotem = (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
      this.holdingGapple = (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
      this.holdingObby = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
      this.holdingWeb = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockWeb.class);
      this.totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
      if (this.holdingTotem)
        this.totems += mc.player.inventory.offHandInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum(); 
      this.crystals = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum();
      if (this.holdingCrystal)
        this.crystals += mc.player.inventory.offHandInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.END_CRYSTAL)).mapToInt(ItemStack::getCount).sum(); 
      this.gapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum();
      if (this.holdingGapple)
        this.gapples += mc.player.inventory.offHandInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum(); 
      if (this.currentMode == Mode2.WEBS || this.currentMode == Mode2.OBSIDIAN) {
        this.obby = mc.player.inventory.mainInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockObsidian.class)).mapToInt(ItemStack::getCount).sum();
        if (this.holdingObby)
          this.obby += mc.player.inventory.offHandInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockObsidian.class)).mapToInt(ItemStack::getCount).sum(); 
        this.webs = mc.player.inventory.mainInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockWeb.class)).mapToInt(ItemStack::getCount).sum();
        if (this.holdingWeb)
          this.webs += mc.player.inventory.offHandInventory.stream().filter(itemStack -> InventoryUtil.isBlock(itemStack.getItem(), BlockWeb.class)).mapToInt(ItemStack::getCount).sum(); 
      } 
      doSwitch();
    } 
  }
  
  private void manageDrawn() {
    if (this.currentMode == Mode2.TOTEMS && ((Boolean)this.drawn.getValue()).booleanValue())
      this.drawn.setValue(Boolean.valueOf(false)); 
    if (this.currentMode != Mode2.TOTEMS && !((Boolean)this.drawn.getValue()).booleanValue())
      this.drawn.setValue(Boolean.valueOf(true)); 
  }
  
  public void doSwitch() {
    if (((Boolean)this.autoGapple.getValue()).booleanValue())
      if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemSword && (!((Boolean)this.onlyWTotem.getValue()).booleanValue() || mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)) {
          setMode(Mode.GAPPLES);
          this.autoGappleSwitch = true;
        } 
      } else if (this.autoGappleSwitch) {
        setMode(Mode2.TOTEMS);
        this.autoGappleSwitch = false;
      }  
    if ((this.currentMode == Mode2.GAPPLES && ((!EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.gappleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.gappleHoleHealth.getValue()).floatValue())) || (this.currentMode == Mode2.CRYSTALS && ((
      !EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.crystalHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.crystalHoleHealth.getValue()).floatValue())) || (this.currentMode == Mode2.OBSIDIAN && ((
      !EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.obsidianHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.obsidianHoleHealth.getValue()).floatValue())) || (this.currentMode == Mode2.WEBS && ((
      !EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.webHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) <= ((Float)this.webHoleHealth.getValue()).floatValue()))) {
      if (((Boolean)this.returnToCrystal.getValue()).booleanValue() && this.currentMode == Mode2.CRYSTALS)
        this.switchedForHealthReason = true; 
      setMode(Mode2.TOTEMS);
    } 
    if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity)mc.player) && EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) > ((Float)this.crystalHoleHealth.getValue()).floatValue()) || EntityUtil.getHealth((Entity)mc.player, ((Boolean)this.absorption.getValue()).booleanValue()) > ((Float)this.crystalHealth.getValue()).floatValue())) {
      setMode(Mode2.CRYSTALS);
      this.switchedForHealthReason = false;
    } 
    if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !((Boolean)this.guis.getValue()).booleanValue() && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory))
      return; 
    Item currentOffhandItem = mc.player.getHeldItemOffhand().getItem();
    switch (this.currentMode) {
      case GAPPLES:
        if (this.totems > 0 && !this.holdingTotem) {
          this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
          int lastSlot = getLastSlot(currentOffhandItem, this.lastTotemSlot);
          putItemInOffhand(this.lastTotemSlot, lastSlot);
        } 
        break;
      case WEBS:
        if (this.gapples > 0 && !this.holdingGapple) {
          this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
          int lastSlot = getLastSlot(currentOffhandItem, this.lastGappleSlot);
          putItemInOffhand(this.lastGappleSlot, lastSlot);
        } 
        break;
      case OBSIDIAN:
        if (this.webs > 0 && !this.holdingWeb) {
          this.lastWebSlot = InventoryUtil.findInventoryBlock(BlockWeb.class, false);
          int lastSlot = getLastSlot(currentOffhandItem, this.lastWebSlot);
          putItemInOffhand(this.lastWebSlot, lastSlot);
        } 
        break;
      case CRYSTALS:
        if (this.obby > 0 && !this.holdingObby) {
          this.lastObbySlot = InventoryUtil.findInventoryBlock(BlockObsidian.class, false);
          int lastSlot = getLastSlot(currentOffhandItem, this.lastObbySlot);
          putItemInOffhand(this.lastObbySlot, lastSlot);
        } 
        break;
      default:
        if (this.crystals > 0 && !this.holdingCrystal) {
          this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
          int lastSlot = getLastSlot(currentOffhandItem, this.lastCrystalSlot);
          putItemInOffhand(this.lastCrystalSlot, lastSlot);
        } 
        break;
    } 
    for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
      InventoryUtil.Task task = this.taskList.poll();
      if (task != null) {
        task.run();
        if (task.isSwitching())
          this.didSwitchThisTick = true; 
      } 
    } 
  }
  
  private int getLastSlot(Item item, int slotIn) {
    if (item == Items.END_CRYSTAL)
      return this.lastCrystalSlot; 
    if (item == Items.GOLDEN_APPLE)
      return this.lastGappleSlot; 
    if (item == Items.TOTEM_OF_UNDYING)
      return this.lastTotemSlot; 
    if (InventoryUtil.isBlock(item, BlockObsidian.class))
      return this.lastObbySlot; 
    if (InventoryUtil.isBlock(item, BlockWeb.class))
      return this.lastWebSlot; 
    if (item == Items.AIR)
      return -1; 
    return slotIn;
  }
  
  private void putItemInOffhand(int slotIn, int slotOut) {
    if (slotIn != -1 && this.taskList.isEmpty()) {
      this.taskList.add(new InventoryUtil.Task(slotIn));
      this.taskList.add(new InventoryUtil.Task(45));
      this.taskList.add(new InventoryUtil.Task(slotOut));
      this.taskList.add(new InventoryUtil.Task());
    } 
  }
  
  private boolean noNearbyPlayers() {
    return (this.mode == Mode.CRYSTALS && mc.world.playerEntities.stream().noneMatch(e -> (e != mc.player && !Phobos.friendManager.isFriend(e) && mc.player.getDistance((Entity)e) <= ((Float)this.cTargetDistance.getValue()).floatValue())));
  }
  
  private boolean isItemInOffhand() {
    switch (this.mode) {
      case GAPPLES:
        return (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
      case CRYSTALS:
        return (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
      case OBSIDIAN:
        return (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock)mc.player.getHeldItemOffhand().getItem()).block == Blocks.OBSIDIAN);
      case WEBS:
        return (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock)mc.player.getHeldItemOffhand().getItem()).block == Blocks.WEB);
    } 
    return false;
  }
  
  private boolean isHeldInMainHand() {
    switch (this.mode) {
      case GAPPLES:
        return (mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE);
      case CRYSTALS:
        return (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
      case OBSIDIAN:
        return (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock)mc.player.getHeldItemMainhand().getItem()).block == Blocks.OBSIDIAN);
      case WEBS:
        return (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock)mc.player.getHeldItemMainhand().getItem()).block == Blocks.WEB);
    } 
    return false;
  }
  
  private boolean shouldTotem() {
    if (isHeldInMainHand() || isSwapToTotem())
      return true; 
    if (((Boolean)this.holeCheck.getValue()).booleanValue() && EntityUtil.isInHole((Entity)mc.player))
      return (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= getHoleHealth() || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || mc.player.fallDistance >= 3.0F || noNearbyPlayers() || (((Boolean)this.crystalCheck.getValue()).booleanValue() && isCrystalsAABBEmpty())); 
    return (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= getHealth() || mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA || mc.player.fallDistance >= 3.0F || noNearbyPlayers() || (((Boolean)this.crystalCheck.getValue()).booleanValue() && isCrystalsAABBEmpty()));
  }
  
  private boolean isNotEmpty(BlockPos pos) {
    return mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).stream().anyMatch(e -> e instanceof net.minecraft.entity.item.EntityEnderCrystal);
  }
  
  private float getHealth() {
    switch (this.mode) {
      case CRYSTALS:
        return ((Float)this.crystalHealth.getValue()).floatValue();
      case GAPPLES:
        return ((Float)this.gappleHealth.getValue()).floatValue();
      case OBSIDIAN:
        return ((Float)this.obsidianHealth.getValue()).floatValue();
    } 
    return ((Float)this.webHealth.getValue()).floatValue();
  }
  
  private float getHoleHealth() {
    switch (this.mode) {
      case CRYSTALS:
        return ((Float)this.crystalHoleHealth.getValue()).floatValue();
      case GAPPLES:
        return ((Float)this.gappleHoleHealth.getValue()).floatValue();
      case OBSIDIAN:
        return ((Float)this.obsidianHoleHealth.getValue()).floatValue();
    } 
    return ((Float)this.webHoleHealth.getValue()).floatValue();
  }
  
  private boolean isCrystalsAABBEmpty() {
    return (isNotEmpty(mc.player.getPosition().add(1, 0, 0)) || isNotEmpty(mc.player.getPosition().add(-1, 0, 0)) || isNotEmpty(mc.player.getPosition().add(0, 0, 1)) || isNotEmpty(mc.player.getPosition().add(0, 0, -1)) || isNotEmpty(mc.player.getPosition()));
  }
  
  int getStackSize() {
    int size = 0;
    if (shouldTotem()) {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING)
          size += mc.player.inventory.getStackInSlot(i).getCount(); 
      } 
    } else if (this.mode == Mode.OBSIDIAN) {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.OBSIDIAN)
          size += mc.player.inventory.getStackInSlot(i).getCount(); 
      } 
    } else if (this.mode == Mode.WEBS) {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.WEB)
          size += mc.player.inventory.getStackInSlot(i).getCount(); 
      } 
    } else {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() == ((this.mode == Mode.CRYSTALS) ? Items.END_CRYSTAL : Items.GOLDEN_APPLE))
          size += mc.player.inventory.getStackInSlot(i).getCount(); 
      } 
    } 
    return size;
  }
  
  int getSlot(Mode m) {
    int slot = -1;
    if (m == Mode.OBSIDIAN) {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.OBSIDIAN) {
          slot = i;
          break;
        } 
      } 
    } else if (m == Mode.WEBS) {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock && ((ItemBlock)mc.player.inventory.getStackInSlot(i).getItem()).block == Blocks.WEB) {
          slot = i;
          break;
        } 
      } 
    } else {
      for (int i = 45; i > 0; i--) {
        if (mc.player.inventory.getStackInSlot(i).getItem() == ((m == Mode.CRYSTALS) ? Items.END_CRYSTAL : Items.GOLDEN_APPLE)) {
          slot = i;
          break;
        } 
      } 
    } 
    return slot;
  }
  
  int getTotemSlot() {
    int totemSlot = -1;
    for (int i = 45; i > 0; i--) {
      if (mc.player.inventory.getStackInSlot(i).getItem() == Items.TOTEM_OF_UNDYING) {
        totemSlot = i;
        break;
      } 
    } 
    return totemSlot;
  }
  
  private String getModeStr() {
    switch (this.mode) {
      case GAPPLES:
        return "G";
      case WEBS:
        return "W";
      case OBSIDIAN:
        return "O";
    } 
    return "C";
  }
  
  public void setMode(Mode mode) {
    this.mode = mode;
  }
  
  public void setMode(Mode2 mode) {
    if (this.currentMode == mode) {
      this.currentMode = Mode2.TOTEMS;
    } else if (!((Boolean)this.cycle.getValue()).booleanValue() && ((Boolean)this.crystalToTotem.getValue()).booleanValue() && (this.currentMode == Mode2.CRYSTALS || this.currentMode == Mode2.OBSIDIAN || this.currentMode == Mode2.WEBS) && mode == Mode2.GAPPLES) {
      this.currentMode = Mode2.TOTEMS;
    } else {
      this.currentMode = mode;
    } 
  }
  
  public boolean isSwapToTotem() {
    return this.swapToTotem;
  }
  
  public void setSwapToTotem(boolean swapToTotem) {
    this.swapToTotem = swapToTotem;
  }
  
  public enum Mode {
    CRYSTALS, GAPPLES, OBSIDIAN, WEBS;
  }
  
  public enum Type {
    OLD, NEW;
  }
  
  public enum Mode2 {
    TOTEMS, GAPPLES, CRYSTALS, OBSIDIAN, WEBS;
  }
  
  public enum NameMode {
    MODE, TOTEM, AMOUNT;
  }
}
