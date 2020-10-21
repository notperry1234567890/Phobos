//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.Timer;
import me.earth.phobos.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFall extends Module {
  private Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET));
  
  private Setting<Integer> distance = register(new Setting("Distance", Integer.valueOf(15), Integer.valueOf(0), Integer.valueOf(50), v -> (this.mode.getValue() == Mode.BUCKET)));
  
  private Setting<Boolean> glide = register(new Setting("Glide", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.ELYTRA)));
  
  private Setting<Boolean> silent = register(new Setting("Silent", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.ELYTRA)));
  
  private Setting<Boolean> bypass = register(new Setting("Bypass", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.ELYTRA)));
  
  private Timer timer = new Timer();
  
  private boolean equipped = false;
  
  private boolean gotElytra = false;
  
  private State currentState = State.FALL_CHECK;
  
  private static Timer bypassTimer = new Timer();
  
  private static int ogslot = -1;
  
  public NoFall() {
    super("NoFall", "Prevents fall damage.", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onEnable() {
    ogslot = -1;
    this.currentState = State.FALL_CHECK;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (fullNullCheck())
      return; 
    if (this.mode.getValue() == Mode.ELYTRA)
      if (((Boolean)this.bypass.getValue()).booleanValue()) {
        this.currentState = this.currentState.onSend(event);
      } else if (!this.equipped && event.getPacket() instanceof CPacketPlayer && mc.player.fallDistance >= 3.0F) {
        RayTraceResult result = null;
        if (!((Boolean)this.glide.getValue()).booleanValue())
          result = mc.world.rayTraceBlocks(mc.player.getPositionVector(), mc.player.getPositionVector().add(0.0D, -3.0D, 0.0D), true, true, false); 
        if (((Boolean)this.glide.getValue()).booleanValue() || (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK))
          if (mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem().equals(Items.ELYTRA)) {
            mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
          } else if (((Boolean)this.silent.getValue()).booleanValue()) {
            int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
            if (slot != -1) {
              mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, (EntityPlayer)mc.player);
              mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            } 
            ogslot = slot;
            this.equipped = true;
          }  
      }  
    if (this.mode.getValue() == Mode.PACKET && event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      packet.onGround = true;
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (fullNullCheck())
      return; 
    if ((this.equipped || ((Boolean)this.bypass.getValue()).booleanValue()) && this.mode.getValue() == Mode.ELYTRA && (event.getPacket() instanceof net.minecraft.network.play.server.SPacketWindowItems || event.getPacket() instanceof net.minecraft.network.play.server.SPacketSetSlot))
      if (((Boolean)this.bypass.getValue()).booleanValue()) {
        this.currentState = this.currentState.onReceive(event);
      } else {
        this.gotElytra = true;
      }  
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (this.mode.getValue() == Mode.ELYTRA)
      if (((Boolean)this.bypass.getValue()).booleanValue()) {
        this.currentState = this.currentState.onUpdate();
      } else if (((Boolean)this.silent.getValue()).booleanValue() && this.equipped && this.gotElytra) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 6, ogslot, ClickType.SWAP, (EntityPlayer)mc.player);
        mc.playerController.updateController();
        this.equipped = false;
        this.gotElytra = false;
      } else if (((Boolean)this.silent.getValue()).booleanValue() && InventoryUtil.getItemHotbar(Items.ELYTRA) == -1) {
        int slot = InventoryUtil.findStackInventory(Items.ELYTRA);
        if (slot != -1 && ogslot != -1) {
          System.out.println(String.format("Moving %d to hotbar %d", new Object[] { Integer.valueOf(slot), Integer.valueOf(ogslot) }));
          mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, ogslot, ClickType.SWAP, (EntityPlayer)mc.player);
          mc.playerController.updateController();
        } 
      }  
  }
  
  public void onTick() {
    if (fullNullCheck())
      return; 
    if (this.mode.getValue() == Mode.BUCKET && mc.player.fallDistance >= ((Integer)this.distance.getValue()).intValue() && !EntityUtil.isAboveWater((Entity)mc.player) && this.timer.passedMs(100L)) {
      Vec3d posVec = mc.player.getPositionVector();
      RayTraceResult result = mc.world.rayTraceBlocks(posVec, posVec.add(0.0D, -5.329999923706055D, 0.0D), true, true, false);
      if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
        EnumHand hand = EnumHand.MAIN_HAND;
        if (mc.player.getHeldItemOffhand().getItem() == Items.WATER_BUCKET) {
          hand = EnumHand.OFF_HAND;
        } else if (mc.player.getHeldItemMainhand().getItem() != Items.WATER_BUCKET) {
          for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.WATER_BUCKET) {
              mc.player.inventory.currentItem = i;
              mc.player.rotationPitch = 90.0F;
              this.timer.reset();
              return;
            } 
          } 
          return;
        } 
        mc.player.rotationPitch = 90.0F;
        mc.playerController.processRightClick((EntityPlayer)mc.player, (World)mc.world, hand);
        this.timer.reset();
      } 
    } 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public enum Mode {
    PACKET, BUCKET, ELYTRA;
  }
  
  public enum State {
    FALL_CHECK {
      public State onSend(PacketEvent.Send event) {
        RayTraceResult result = Util.mc.world.rayTraceBlocks(Util.mc.player.getPositionVector(), Util.mc.player.getPositionVector().add(0.0D, -3.0D, 0.0D), true, true, false);
        if (event.getPacket() instanceof CPacketPlayer && Util.mc.player.fallDistance >= 3.0F && result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
          int slot = InventoryUtil.getItemHotbar(Items.ELYTRA);
          if (slot != -1) {
            Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, slot, ClickType.SWAP, (EntityPlayer)Util.mc.player);
            NoFall.ogslot = slot;
            Util.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)Util.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            return WAIT_FOR_ELYTRA_DEQUIP;
          } 
          return this;
        } 
        return this;
      }
    },
    WAIT_FOR_ELYTRA_DEQUIP {
      public State onReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketWindowItems || event.getPacket() instanceof net.minecraft.network.play.server.SPacketSetSlot)
          return REEQUIP_ELYTRA; 
        return this;
      }
    },
    REEQUIP_ELYTRA {
      public State onUpdate() {
        Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, 6, NoFall.ogslot, ClickType.SWAP, (EntityPlayer)Util.mc.player);
        Util.mc.playerController.updateController();
        int slot = InventoryUtil.findStackInventory(Items.ELYTRA, true);
        if (slot == -1) {
          Command.sendMessage("§cElytra not found after regain?");
          return WAIT_FOR_NEXT_REQUIP;
        } 
        Util.mc.playerController.windowClick(Util.mc.player.inventoryContainer.windowId, slot, NoFall.ogslot, ClickType.SWAP, (EntityPlayer)Util.mc.player);
        Util.mc.playerController.updateController();
        NoFall.bypassTimer.reset();
        return RESET_TIME;
      }
    },
    WAIT_FOR_NEXT_REQUIP {
      public State onUpdate() {
        if (NoFall.bypassTimer.passedMs(250L))
          return REEQUIP_ELYTRA; 
        return this;
      }
    },
    RESET_TIME {
      public State onUpdate() {
        if (Util.mc.player.onGround || NoFall.bypassTimer.passedMs(250L)) {
          Util.mc.player.connection.sendPacket((Packet)new CPacketClickWindow(0, 0, 0, ClickType.PICKUP, new ItemStack(Blocks.BEDROCK), (short)1337));
          return FALL_CHECK;
        } 
        return this;
      }
    };
    
    public State onSend(PacketEvent.Send e) {
      return this;
    }
    
    public State onReceive(PacketEvent.Receive e) {
      return this;
    }
    
    public State onUpdate() {
      return this;
    }
  }
}
