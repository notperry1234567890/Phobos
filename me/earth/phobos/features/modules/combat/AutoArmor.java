//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.player.XCarry;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AutoArmor extends Module {
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500)));
  
  private final Setting<Boolean> mendingTakeOff = register(new Setting("AutoMend", Boolean.valueOf(false)));
  
  private final Setting<Integer> closestEnemy = register(new Setting("Enemy", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Integer> helmetThreshold = register(new Setting("Helmet%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Integer> chestThreshold = register(new Setting("Chest%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Integer> legThreshold = register(new Setting("Legs%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Integer> bootsThreshold = register(new Setting("Boots%", Integer.valueOf(80), Integer.valueOf(1), Integer.valueOf(100), v -> ((Boolean)this.mendingTakeOff.getValue()).booleanValue()));
  
  private final Setting<Boolean> curse = register(new Setting("CurseOfBinding", Boolean.valueOf(false)));
  
  private final Setting<Integer> actions = register(new Setting("Actions", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(12)));
  
  private final Setting<Bind> elytraBind = register(new Setting("Elytra", new Bind(-1)));
  
  private final Setting<Boolean> tps = register(new Setting("TpsSync", Boolean.valueOf(true)));
  
  private final Setting<Boolean> updateController = register(new Setting("Update", Boolean.valueOf(true)));
  
  private final Setting<Boolean> shiftClick = register(new Setting("ShiftClick", Boolean.valueOf(false)));
  
  private final Timer timer = new Timer();
  
  private final Timer elytraTimer = new Timer();
  
  private final Queue<InventoryUtil.Task> taskList = new ConcurrentLinkedQueue<>();
  
  private final List<Integer> doneSlots = new ArrayList<>();
  
  private boolean elytraOn = false;
  
  public AutoArmor() {
    super("AutoArmor", "Puts Armor on for you.", Module.Category.COMBAT, true, false, false);
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (Keyboard.getEventKeyState() && !(mc.currentScreen instanceof me.earth.phobos.features.gui.PhobosGui) && ((Bind)this.elytraBind.getValue()).getKey() == Keyboard.getEventKey())
      this.elytraOn = !this.elytraOn; 
  }
  
  public void onLogin() {
    this.timer.reset();
    this.elytraTimer.reset();
  }
  
  public void onDisable() {
    this.taskList.clear();
    this.doneSlots.clear();
    this.elytraOn = false;
  }
  
  public void onLogout() {
    this.taskList.clear();
    this.doneSlots.clear();
  }
  
  public void onTick() {
    if (fullNullCheck() || (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory)))
      return; 
    if (this.taskList.isEmpty()) {
      if (((Boolean)this.mendingTakeOff.getValue()).booleanValue() && InventoryUtil.holdingItem(ItemExpBottle.class) && mc.gameSettings.keyBindUseItem.isKeyDown() && (isSafe() || EntityUtil.isSafe((Entity)mc.player, 1, false))) {
        ItemStack itemStack1 = mc.player.inventoryContainer.getSlot(5).getStack();
        if (!itemStack1.isEmpty) {
          int helmDamage = DamageUtil.getRoundedDamage(itemStack1);
          if (helmDamage >= ((Integer)this.helmetThreshold.getValue()).intValue())
            takeOffSlot(5); 
        } 
        ItemStack itemStack2 = mc.player.inventoryContainer.getSlot(6).getStack();
        if (!itemStack2.isEmpty) {
          int chestDamage = DamageUtil.getRoundedDamage(itemStack2);
          if (chestDamage >= ((Integer)this.chestThreshold.getValue()).intValue())
            takeOffSlot(6); 
        } 
        ItemStack itemStack3 = mc.player.inventoryContainer.getSlot(7).getStack();
        if (!itemStack3.isEmpty) {
          int leggingDamage = DamageUtil.getRoundedDamage(itemStack3);
          if (leggingDamage >= ((Integer)this.legThreshold.getValue()).intValue())
            takeOffSlot(7); 
        } 
        ItemStack itemStack4 = mc.player.inventoryContainer.getSlot(8).getStack();
        if (!itemStack4.isEmpty) {
          int bootDamage = DamageUtil.getRoundedDamage(itemStack4);
          if (bootDamage >= ((Integer)this.bootsThreshold.getValue()).intValue())
            takeOffSlot(8); 
        } 
        return;
      } 
      ItemStack helm = mc.player.inventoryContainer.getSlot(5).getStack();
      if (helm.getItem() == Items.AIR) {
        int slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.HEAD, ((Boolean)this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn());
        if (slot != -1)
          getSlotOn(5, slot); 
      } 
      ItemStack chest = mc.player.inventoryContainer.getSlot(6).getStack();
      if (chest.getItem() == Items.AIR) {
        if (this.taskList.isEmpty())
          if (this.elytraOn && this.elytraTimer.passedMs(500L)) {
            int elytraSlot = InventoryUtil.findItemInventorySlot(Items.ELYTRA, false, XCarry.getInstance().isOn());
            if (elytraSlot != -1) {
              if ((elytraSlot < 5 && elytraSlot > 1) || !((Boolean)this.shiftClick.getValue()).booleanValue()) {
                this.taskList.add(new InventoryUtil.Task(elytraSlot));
                this.taskList.add(new InventoryUtil.Task(6));
              } else {
                this.taskList.add(new InventoryUtil.Task(elytraSlot, true));
              } 
              if (((Boolean)this.updateController.getValue()).booleanValue())
                this.taskList.add(new InventoryUtil.Task()); 
              this.elytraTimer.reset();
            } 
          } else if (!this.elytraOn) {
            int slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.CHEST, ((Boolean)this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn());
            if (slot != -1)
              getSlotOn(6, slot); 
          }  
      } else if (this.elytraOn && chest.getItem() != Items.ELYTRA && this.elytraTimer.passedMs(500L)) {
        if (this.taskList.isEmpty()) {
          int slot = InventoryUtil.findItemInventorySlot(Items.ELYTRA, false, XCarry.getInstance().isOn());
          if (slot != -1) {
            this.taskList.add(new InventoryUtil.Task(slot));
            this.taskList.add(new InventoryUtil.Task(6));
            this.taskList.add(new InventoryUtil.Task(slot));
            if (((Boolean)this.updateController.getValue()).booleanValue())
              this.taskList.add(new InventoryUtil.Task()); 
          } 
          this.elytraTimer.reset();
        } 
      } else if (!this.elytraOn && chest.getItem() == Items.ELYTRA && this.elytraTimer.passedMs(500L) && this.taskList.isEmpty()) {
        int slot = InventoryUtil.findItemInventorySlot((Item)Items.DIAMOND_CHESTPLATE, false, XCarry.getInstance().isOn());
        if (slot == -1) {
          slot = InventoryUtil.findItemInventorySlot((Item)Items.IRON_CHESTPLATE, false, XCarry.getInstance().isOn());
          if (slot == -1) {
            slot = InventoryUtil.findItemInventorySlot((Item)Items.GOLDEN_CHESTPLATE, false, XCarry.getInstance().isOn());
            if (slot == -1) {
              slot = InventoryUtil.findItemInventorySlot((Item)Items.CHAINMAIL_CHESTPLATE, false, XCarry.getInstance().isOn());
              if (slot == -1)
                slot = InventoryUtil.findItemInventorySlot((Item)Items.LEATHER_CHESTPLATE, false, XCarry.getInstance().isOn()); 
            } 
          } 
        } 
        if (slot != -1) {
          this.taskList.add(new InventoryUtil.Task(slot));
          this.taskList.add(new InventoryUtil.Task(6));
          this.taskList.add(new InventoryUtil.Task(slot));
          if (((Boolean)this.updateController.getValue()).booleanValue())
            this.taskList.add(new InventoryUtil.Task()); 
        } 
        this.elytraTimer.reset();
      } 
      ItemStack legging = mc.player.inventoryContainer.getSlot(7).getStack();
      if (legging.getItem() == Items.AIR) {
        int slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.LEGS, ((Boolean)this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn());
        if (slot != -1)
          getSlotOn(7, slot); 
      } 
      ItemStack feet = mc.player.inventoryContainer.getSlot(8).getStack();
      if (feet.getItem() == Items.AIR) {
        int slot = InventoryUtil.findArmorSlot(EntityEquipmentSlot.FEET, ((Boolean)this.curse.getValue()).booleanValue(), XCarry.getInstance().isOn());
        if (slot != -1)
          getSlotOn(8, slot); 
      } 
    } 
    if (this.timer.passedMs((int)(((Integer)this.delay.getValue()).intValue() * (((Boolean)this.tps.getValue()).booleanValue() ? Phobos.serverManager.getTpsFactor() : 1.0F)))) {
      if (!this.taskList.isEmpty())
        for (int i = 0; i < ((Integer)this.actions.getValue()).intValue(); i++) {
          InventoryUtil.Task task = this.taskList.poll();
          if (task != null)
            task.run(); 
        }  
      this.timer.reset();
    } 
  }
  
  public String getDisplayInfo() {
    if (this.elytraOn)
      return "Elytra"; 
    return null;
  }
  
  private void takeOffSlot(int slot) {
    if (this.taskList.isEmpty()) {
      int target = -1;
      for (Iterator<Integer> iterator = InventoryUtil.findEmptySlots(XCarry.getInstance().isOn()).iterator(); iterator.hasNext(); ) {
        int i = ((Integer)iterator.next()).intValue();
        if (!this.doneSlots.contains(Integer.valueOf(target))) {
          target = i;
          this.doneSlots.add(Integer.valueOf(i));
        } 
      } 
      if (target != -1) {
        if ((target < 5 && target > 0) || !((Boolean)this.shiftClick.getValue()).booleanValue()) {
          this.taskList.add(new InventoryUtil.Task(slot));
          this.taskList.add(new InventoryUtil.Task(target));
        } else {
          this.taskList.add(new InventoryUtil.Task(slot, true));
        } 
        if (((Boolean)this.updateController.getValue()).booleanValue())
          this.taskList.add(new InventoryUtil.Task()); 
      } 
    } 
  }
  
  private void getSlotOn(int slot, int target) {
    if (this.taskList.isEmpty()) {
      this.doneSlots.remove(Integer.valueOf(target));
      if ((target < 5 && target > 0) || !((Boolean)this.shiftClick.getValue()).booleanValue()) {
        this.taskList.add(new InventoryUtil.Task(target));
        this.taskList.add(new InventoryUtil.Task(slot));
      } else {
        this.taskList.add(new InventoryUtil.Task(target, true));
      } 
      if (((Boolean)this.updateController.getValue()).booleanValue())
        this.taskList.add(new InventoryUtil.Task()); 
    } 
  }
  
  private boolean isSafe() {
    EntityPlayer closest = EntityUtil.getClosestEnemy(((Integer)this.closestEnemy.getValue()).intValue());
    if (closest == null)
      return true; 
    return (mc.player.getDistanceSq((Entity)closest) >= MathUtil.square(((Integer)this.closestEnemy.getValue()).intValue()));
  }
}
