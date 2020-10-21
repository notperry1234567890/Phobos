//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.Auto32k;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class Replenish extends Module {
  private final Setting<Integer> threshold = register(new Setting("Threshold", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(63)));
  
  private final Setting<Integer> replenishments = register(new Setting("RUpdates", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private final Setting<Integer> updates = register(new Setting("HBUpdates", Integer.valueOf(100), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private final Setting<Integer> actions = register(new Setting("Actions", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Boolean> pauseInv = register(new Setting("PauseInv", Boolean.valueOf(true)));
  
  private final Setting<Boolean> putBack = register(new Setting("PutBack", Boolean.valueOf(true)));
  
  private final Timer timer = new Timer();
  
  private final Timer replenishTimer = new Timer();
  
  private Map<Integer, ItemStack> hotbar = new ConcurrentHashMap<>();
  
  private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
  
  public Replenish() {
    super("Replenish", "Replenishes your hotbar", Module.Category.PLAYER, false, false, false);
  }
  
  public void onUpdate() {
    if (Auto32k.getInstance().isOn() && (!((Boolean)(Auto32k.getInstance()).autoSwitch.getValue()).booleanValue() || (Auto32k.getInstance()).switching))
      return; 
    if (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && (!(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory) || ((Boolean)this.pauseInv.getValue()).booleanValue()))
      return; 
    if (this.timer.passedMs(((Integer)this.updates.getValue()).intValue()))
      mapHotbar(); 
    if (this.replenishTimer.passedMs(((Integer)this.replenishments.getValue()).intValue())) {
      for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
        InventoryUtil.Task task = this.taskList.poll();
        if (task != null)
          task.run(); 
      } 
      this.replenishTimer.reset();
    } 
  }
  
  public void onDisable() {
    this.hotbar.clear();
  }
  
  public void onLogout() {
    onDisable();
  }
  
  private void mapHotbar() {
    Map<Integer, ItemStack> map = new ConcurrentHashMap<>();
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      map.put(Integer.valueOf(i), stack);
    } 
    if (this.hotbar.isEmpty()) {
      this.hotbar = map;
      return;
    } 
    Map<Integer, Integer> fromTo = new ConcurrentHashMap<>();
    for (Map.Entry<Integer, ItemStack> hotbarItem : map.entrySet()) {
      ItemStack stack = hotbarItem.getValue();
      Integer slotKey = hotbarItem.getKey();
      if (slotKey != null && stack != null && (stack.isEmpty || stack.getItem() == Items.AIR || (stack.stackSize <= ((Integer)this.threshold.getValue()).intValue() && stack.stackSize < stack.getMaxStackSize()))) {
        ItemStack previousStack = hotbarItem.getValue();
        if (stack.isEmpty || stack.getItem() != Items.AIR)
          previousStack = this.hotbar.get(slotKey); 
        if (previousStack != null && !previousStack.isEmpty && previousStack.getItem() != Items.AIR) {
          int replenishSlot = getReplenishSlot(previousStack);
          if (replenishSlot == -1)
            continue; 
          fromTo.put(Integer.valueOf(replenishSlot), Integer.valueOf(InventoryUtil.convertHotbarToInv(slotKey.intValue())));
        } 
      } 
    } 
    if (!fromTo.isEmpty())
      for (Map.Entry<Integer, Integer> slotMove : fromTo.entrySet()) {
        this.taskList.add(new InventoryUtil.Task(((Integer)slotMove.getKey()).intValue()));
        this.taskList.add(new InventoryUtil.Task(((Integer)slotMove.getValue()).intValue()));
        this.taskList.add(new InventoryUtil.Task(((Integer)slotMove.getKey()).intValue()));
        this.taskList.add(new InventoryUtil.Task());
      }  
    this.hotbar = map;
  }
  
  private int getReplenishSlot(ItemStack stack) {
    AtomicInteger slot = new AtomicInteger();
    slot.set(-1);
    for (Map.Entry<Integer, ItemStack> entry : (Iterable<Map.Entry<Integer, ItemStack>>)InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
      if (((Integer)entry.getKey()).intValue() < 36 && 
        InventoryUtil.areStacksCompatible(stack, entry.getValue())) {
        slot.set(((Integer)entry.getKey()).intValue());
        return slot.get();
      } 
    } 
    return slot.get();
  }
}
