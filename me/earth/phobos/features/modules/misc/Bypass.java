//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Bypass extends Module {
  public Setting<Boolean> illegals = register(new Setting("Illegals", Boolean.valueOf(false)));
  
  public Setting<Boolean> secretClose = register(new Setting("SecretClose", Boolean.valueOf(false), v -> ((Boolean)this.illegals.getValue()).booleanValue()));
  
  public Setting<Boolean> rotation = register(new Setting("Rotation", Boolean.valueOf(false), v -> (((Boolean)this.secretClose.getValue()).booleanValue() && ((Boolean)this.illegals.getValue()).booleanValue())));
  
  public Setting<Boolean> elytra = register(new Setting("Elytra", Boolean.valueOf(false)));
  
  public Setting<Boolean> reopen = register(new Setting("Reopen", Boolean.valueOf(false), v -> ((Boolean)this.elytra.getValue()).booleanValue()));
  
  public Setting<Integer> reopen_interval = register(new Setting("ReopenDelay", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(5000), v -> ((Boolean)this.elytra.getValue()).booleanValue()));
  
  public Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1000), v -> ((Boolean)this.elytra.getValue()).booleanValue()));
  
  public Setting<Boolean> allow_ghost = register(new Setting("Ghost", Boolean.valueOf(true), v -> ((Boolean)this.elytra.getValue()).booleanValue()));
  
  public Setting<Boolean> cancel_close = register(new Setting("Cancel", Boolean.valueOf(true), v -> ((Boolean)this.elytra.getValue()).booleanValue()));
  
  public Setting<Boolean> discreet = register(new Setting("Secret", Boolean.valueOf(true), v -> ((Boolean)this.elytra.getValue()).booleanValue()));
  
  public Setting<Boolean> packets = register(new Setting("Packets", Boolean.valueOf(false)));
  
  public Setting<Boolean> limitSwing = register(new Setting("LimitSwing", Boolean.valueOf(false), v -> ((Boolean)this.packets.getValue()).booleanValue()));
  
  public Setting<Integer> swingPackets = register(new Setting("SwingPackets", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.packets.getValue()).booleanValue()));
  
  public Setting<Boolean> noLimit = register(new Setting("NoCompression", Boolean.valueOf(false), v -> ((Boolean)this.packets.getValue()).booleanValue()));
  
  int cooldown = 0;
  
  private final Timer timer = new Timer();
  
  private float yaw;
  
  private float pitch;
  
  private boolean rotate;
  
  private BlockPos pos;
  
  private Timer swingTimer = new Timer();
  
  private int swingPacket = 0;
  
  private static Bypass instance;
  
  public Bypass() {
    super("Bypass", "Bypass for stuff", Module.Category.MISC, true, false, false);
    instance = this;
  }
  
  public static Bypass getInstance() {
    if (instance == null)
      instance = new Bypass(); 
    return instance;
  }
  
  public void onToggle() {
    this.swingPacket = 0;
  }
  
  @SubscribeEvent
  public void onGuiOpen(GuiOpenEvent event) {
    if (event.getGui() == null && ((Boolean)this.secretClose.getValue()).booleanValue() && ((Boolean)this.rotation.getValue()).booleanValue()) {
      this.pos = new BlockPos(mc.player.getPositionVector());
      this.yaw = mc.player.rotationYaw;
      this.pitch = mc.player.rotationPitch;
      this.rotate = true;
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onPacketSend(PacketEvent.Send event) {
    if (((Boolean)this.illegals.getValue()).booleanValue() && ((Boolean)this.secretClose.getValue()).booleanValue())
      if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketCloseWindow) {
        event.setCanceled(true);
      } else if (event.getPacket() instanceof CPacketPlayer && ((Boolean)this.rotation.getValue()).booleanValue() && this.rotate) {
        CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        packet.yaw = this.yaw;
        packet.pitch = this.pitch;
      }  
    if (((Boolean)this.packets.getValue()).booleanValue() && ((Boolean)this.limitSwing.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation) {
      if (this.swingPacket > ((Integer)this.swingPackets.getValue()).intValue())
        event.setCanceled(true); 
      this.swingPacket++;
    } 
  }
  
  @SubscribeEvent
  public void onIncomingPacket(PacketEvent.Receive event) {
    if (!fullNullCheck() && ((Boolean)this.elytra.getValue()).booleanValue()) {
      if (event.getPacket() instanceof SPacketSetSlot) {
        SPacketSetSlot packet = (SPacketSetSlot)event.getPacket();
        if (packet.getSlot() == 6)
          event.setCanceled(true); 
        if (!((Boolean)this.allow_ghost.getValue()).booleanValue() && packet.getStack().getItem().equals(Items.ELYTRA))
          event.setCanceled(true); 
      } 
      if (((Boolean)this.cancel_close.getValue()).booleanValue() && mc.player.isElytraFlying() && event.getPacket() instanceof SPacketEntityMetadata) {
        SPacketEntityMetadata MetadataPacket = (SPacketEntityMetadata)event.getPacket();
        if (MetadataPacket.getEntityId() == mc.player.getEntityId())
          event.setCanceled(true); 
      } 
    } 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCloseWindow)
      this.rotate = false; 
  }
  
  public void onTick() {
    if (((Boolean)this.secretClose.getValue()).booleanValue() && ((Boolean)this.rotation.getValue()).booleanValue() && this.rotate && this.pos != null && mc.player != null && mc.player.getDistanceSq(this.pos) > 400.0D)
      this.rotate = false; 
    if (((Boolean)this.elytra.getValue()).booleanValue())
      if (this.cooldown > 0) {
        this.cooldown--;
      } else if (mc.player != null && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory) && (!mc.player.onGround || !((Boolean)this.discreet.getValue()).booleanValue())) {
        for (int i = 0; i < 36; i++) {
          ItemStack item = mc.player.inventory.getStackInSlot(i);
          if (item.getItem().equals(Items.ELYTRA)) {
            mc.playerController.windowClick(0, (i < 9) ? (i + 36) : i, 0, ClickType.QUICK_MOVE, (EntityPlayer)mc.player);
            this.cooldown = ((Integer)this.delay.getValue()).intValue();
            return;
          } 
        } 
      }  
  }
  
  public void onUpdate() {
    this.swingPacket = 0;
    if (((Boolean)this.elytra.getValue()).booleanValue() && this.timer.passedMs(((Integer)this.reopen_interval.getValue()).intValue()) && ((Boolean)this.reopen.getValue()).booleanValue() && !mc.player.isElytraFlying() && mc.player.fallDistance > 0.0F)
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING)); 
  }
}
