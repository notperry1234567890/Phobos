//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.ReflectionUtil;
import me.earth.phobos.util.Util;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class XCarry extends Module {
  private final Setting<Boolean> simpleMode = register(new Setting("Simple", Boolean.valueOf(false)));
  
  private final Setting<Bind> autoStore = register(new Setting("AutoDuel", new Bind(-1)));
  
  private final Setting<Integer> obbySlot = register(new Setting("ObbySlot", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(9), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
  
  private final Setting<Integer> slot1 = register(new Setting("Slot1", Integer.valueOf(22), Integer.valueOf(9), Integer.valueOf(44), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
  
  private final Setting<Integer> slot2 = register(new Setting("Slot2", Integer.valueOf(23), Integer.valueOf(9), Integer.valueOf(44), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
  
  private final Setting<Integer> slot3 = register(new Setting("Slot3", Integer.valueOf(24), Integer.valueOf(9), Integer.valueOf(44), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
  
  private final Setting<Integer> tasks = register(new Setting("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12), v -> (((Bind)this.autoStore.getValue()).getKey() != -1)));
  
  private final Setting<Boolean> store = register(new Setting("Store", Boolean.valueOf(false)));
  
  private final Setting<Boolean> shiftClicker = register(new Setting("ShiftClick", Boolean.valueOf(false)));
  
  private final Setting<Boolean> withShift = register(new Setting("WithShift", Boolean.valueOf(true), v -> ((Boolean)this.shiftClicker.getValue()).booleanValue()));
  
  private final Setting<Bind> keyBind = register(new Setting("ShiftBind", new Bind(-1), v -> ((Boolean)this.shiftClicker.getValue()).booleanValue()));
  
  private static XCarry INSTANCE = new XCarry();
  
  private GuiInventory openedGui = null;
  
  private final AtomicBoolean guiNeedsClose = new AtomicBoolean(false);
  
  private boolean guiCloseGuard = false;
  
  private boolean autoDuelOn = false;
  
  private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
  
  private boolean obbySlotDone = false;
  
  private boolean slot1done = false;
  
  private boolean slot2done = false;
  
  private boolean slot3done = false;
  
  private List<Integer> doneSlots = new ArrayList<>();
  
  public XCarry() {
    super("XCarry", "Uses the crafting inventory for storage", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static XCarry getInstance() {
    if (INSTANCE == null)
      INSTANCE = new XCarry(); 
    return INSTANCE;
  }
  
  public void onUpdate() {
    if (((Boolean)this.shiftClicker.getValue()).booleanValue() && mc.currentScreen instanceof GuiInventory) {
      boolean ourBind = (((Bind)this.keyBind.getValue()).getKey() != -1 && Keyboard.isKeyDown(((Bind)this.keyBind.getValue()).getKey()) && !Keyboard.isKeyDown(42));
      if (((Keyboard.isKeyDown(42) && ((Boolean)this.withShift.getValue()).booleanValue()) || ourBind) && Mouse.isButtonDown(0)) {
        Slot slot = ((GuiInventory)mc.currentScreen).getSlotUnderMouse();
        if (slot != null && InventoryUtil.getEmptyXCarry() != -1) {
          int slotNumber = slot.slotNumber;
          if (slotNumber > 4 && ourBind) {
            this.taskList.add(new InventoryUtil.Task(slotNumber));
            this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
          } else if (slotNumber > 4 && ((Boolean)this.withShift.getValue()).booleanValue()) {
            boolean isHotBarFull = true;
            boolean isInvFull = true;
            for (Iterator<Integer> iterator = InventoryUtil.findEmptySlots(false).iterator(); iterator.hasNext(); ) {
              int i = ((Integer)iterator.next()).intValue();
              if (i > 4 && i < 36) {
                isInvFull = false;
                continue;
              } 
              if (i > 35 && i < 45)
                isHotBarFull = false; 
            } 
            if (slotNumber > 35 && slotNumber < 45) {
              if (isInvFull) {
                this.taskList.add(new InventoryUtil.Task(slotNumber));
                this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
              } 
            } else if (isHotBarFull) {
              this.taskList.add(new InventoryUtil.Task(slotNumber));
              this.taskList.add(new InventoryUtil.Task(InventoryUtil.getEmptyXCarry()));
            } 
          } 
        } 
      } 
    } 
    if (this.autoDuelOn) {
      this.doneSlots = new ArrayList<>();
      if (InventoryUtil.getEmptyXCarry() == -1 || (this.obbySlotDone && this.slot1done && this.slot2done && this.slot3done))
        this.autoDuelOn = false; 
      if (this.autoDuelOn) {
        if (!this.obbySlotDone && !(mc.player.inventory.getStackInSlot(((Integer)this.obbySlot.getValue()).intValue() - 1)).isEmpty)
          addTasks(36 + ((Integer)this.obbySlot.getValue()).intValue() - 1); 
        this.obbySlotDone = true;
        if (!this.slot1done && !(((Slot)mc.player.inventoryContainer.inventorySlots.get(((Integer)this.slot1.getValue()).intValue())).getStack()).isEmpty)
          addTasks(((Integer)this.slot1.getValue()).intValue()); 
        this.slot1done = true;
        if (!this.slot2done && !(((Slot)mc.player.inventoryContainer.inventorySlots.get(((Integer)this.slot2.getValue()).intValue())).getStack()).isEmpty)
          addTasks(((Integer)this.slot2.getValue()).intValue()); 
        this.slot2done = true;
        if (!this.slot3done && !(((Slot)mc.player.inventoryContainer.inventorySlots.get(((Integer)this.slot3.getValue()).intValue())).getStack()).isEmpty)
          addTasks(((Integer)this.slot3.getValue()).intValue()); 
        this.slot3done = true;
      } 
    } else {
      this.obbySlotDone = false;
      this.slot1done = false;
      this.slot2done = false;
      this.slot3done = false;
    } 
    if (!this.taskList.isEmpty())
      for (int i = 0; i < ((Integer)this.tasks.getValue()).intValue(); i++) {
        InventoryUtil.Task task = this.taskList.poll();
        if (task != null)
          task.run(); 
      }  
  }
  
  private void addTasks(int slot) {
    if (InventoryUtil.getEmptyXCarry() != -1) {
      int xcarrySlot = InventoryUtil.getEmptyXCarry();
      if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
        xcarrySlot++;
        if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
          xcarrySlot++;
          if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot)) {
            xcarrySlot++;
            if (this.doneSlots.contains(Integer.valueOf(xcarrySlot)) || !InventoryUtil.isSlotEmpty(xcarrySlot))
              return; 
          } 
        } 
      } 
      if (xcarrySlot <= 4) {
        this.doneSlots.add(Integer.valueOf(xcarrySlot));
      } else {
        return;
      } 
      this.taskList.add(new InventoryUtil.Task(slot));
      this.taskList.add(new InventoryUtil.Task(xcarrySlot));
      this.taskList.add(new InventoryUtil.Task());
    } 
  }
  
  public void onDisable() {
    if (!fullNullCheck())
      if (!((Boolean)this.simpleMode.getValue()).booleanValue()) {
        closeGui();
        close();
      } else {
        mc.player.connection.sendPacket((Packet)new CPacketCloseWindow(mc.player.inventoryContainer.windowId));
      }  
  }
  
  public void onLogout() {
    onDisable();
  }
  
  @SubscribeEvent
  public void onCloseGuiScreen(PacketEvent.Send event) {
    if (((Boolean)this.simpleMode.getValue()).booleanValue() && event.getPacket() instanceof CPacketCloseWindow) {
      CPacketCloseWindow packet = (CPacketCloseWindow)event.getPacket();
      if (packet.windowId == mc.player.inventoryContainer.windowId)
        event.setCanceled(true); 
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onGuiOpen(GuiOpenEvent event) {
    if (!((Boolean)this.simpleMode.getValue()).booleanValue())
      if (this.guiCloseGuard) {
        event.setCanceled(true);
      } else if (event.getGui() instanceof GuiInventory) {
        event.setGui((GuiScreen)(this.openedGui = createGuiWrapper((GuiInventory)event.getGui())));
        this.guiNeedsClose.set(false);
      }  
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this)) {
      Setting setting = event.getSetting();
      String settingname = event.getSetting().getName();
      if (setting.equals(this.simpleMode) && setting.getPlannedValue() != setting.getValue()) {
        disable();
      } else if (settingname.equalsIgnoreCase("Store")) {
        event.setCanceled(true);
        this.autoDuelOn = !this.autoDuelOn;
        Command.sendMessage("<XCarry> §aAutostoring...");
      } 
    } 
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (Keyboard.getEventKeyState() && !(mc.currentScreen instanceof me.earth.phobos.features.gui.PhobosGui) && ((Bind)this.autoStore.getValue()).getKey() == Keyboard.getEventKey()) {
      this.autoDuelOn = !this.autoDuelOn;
      Command.sendMessage("<XCarry> §aAutostoring...");
    } 
  }
  
  private void close() {
    this.openedGui = null;
    this.guiNeedsClose.set(false);
    this.guiCloseGuard = false;
  }
  
  private void closeGui() {
    if (this.guiNeedsClose.compareAndSet(true, false) && 
      !fullNullCheck()) {
      this.guiCloseGuard = true;
      mc.player.closeScreen();
      if (this.openedGui != null) {
        this.openedGui.onGuiClosed();
        this.openedGui = null;
      } 
      this.guiCloseGuard = false;
    } 
  }
  
  private GuiInventory createGuiWrapper(GuiInventory gui) {
    try {
      GuiInventoryWrapper wrapper = new GuiInventoryWrapper();
      ReflectionUtil.copyOf(gui, wrapper);
      return wrapper;
    } catch (NoSuchFieldException|IllegalAccessException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  private class GuiInventoryWrapper extends GuiInventory {
    GuiInventoryWrapper() {
      super((EntityPlayer)Util.mc.player);
    }
    
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (XCarry.this.isEnabled() && (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode))) {
        XCarry.this.guiNeedsClose.set(true);
        this.mc.displayGuiScreen(null);
      } else {
        super.keyTyped(typedChar, keyCode);
      } 
    }
    
    public void onGuiClosed() {
      if (XCarry.this.guiCloseGuard || !XCarry.this.isEnabled())
        super.onGuiClosed(); 
    }
  }
}
