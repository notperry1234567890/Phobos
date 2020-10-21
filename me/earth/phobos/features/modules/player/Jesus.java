//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import me.earth.phobos.event.events.JesusEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Jesus extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.NORMAL));
  
  public Setting<Boolean> cancelVehicle = register(new Setting("NoVehicle", Boolean.valueOf(false)));
  
  public Setting<EventMode> eventMode = register(new Setting("Jump", EventMode.PRE, v -> (this.mode.getValue() == Mode.TRAMPOLINE)));
  
  public Setting<Boolean> fall = register(new Setting("NoFall", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.TRAMPOLINE)));
  
  public static AxisAlignedBB offset = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9999D, 1.0D);
  
  private static Jesus INSTANCE = new Jesus();
  
  private boolean grounded = false;
  
  public Jesus() {
    super("Jesus", "Allows you to walk on water", Module.Category.PLAYER, true, false, false);
    INSTANCE = this;
  }
  
  public static Jesus getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Jesus(); 
    return INSTANCE;
  }
  
  @SubscribeEvent
  public void updateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (fullNullCheck() || Freecam.getInstance().isOn())
      return; 
    if (event.getStage() == 0 && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.VANILLA || this.mode.getValue() == Mode.NORMAL) && 
      !mc.player.isSneaking() && !mc.player.noClip && !mc.gameSettings.keyBindJump.isKeyDown() && EntityUtil.isInLiquid())
      mc.player.motionY = 0.10000000149011612D; 
    if (event.getStage() == 0 && this.mode.getValue() == Mode.TRAMPOLINE && (this.eventMode.getValue() == EventMode.ALL || this.eventMode.getValue() == EventMode.PRE)) {
      doTrampoline();
    } else if (event.getStage() == 1 && this.mode.getValue() == Mode.TRAMPOLINE && (this.eventMode.getValue() == EventMode.ALL || this.eventMode.getValue() == EventMode.POST)) {
      doTrampoline();
    } 
  }
  
  @SubscribeEvent
  public void sendPacket(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketPlayer && Freecam.getInstance().isOff() && (
      this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.NORMAL) && mc.player.getRidingEntity() == null && !mc.gameSettings.keyBindJump.isKeyDown()) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      if (!EntityUtil.isInLiquid() && EntityUtil.isOnLiquid(0.05000000074505806D) && EntityUtil.checkCollide() && mc.player.ticksExisted % 3 == 0)
        packet.y -= 0.05000000074505806D; 
    } 
  }
  
  @SubscribeEvent
  public void onLiquidCollision(JesusEvent event) {
    if (fullNullCheck() || Freecam.getInstance().isOn())
      return; 
    if (event.getStage() == 0 && (this.mode.getValue() == Mode.BOUNCE || this.mode.getValue() == Mode.VANILLA || this.mode.getValue() == Mode.NORMAL) && 
      mc.world != null && mc.player != null && 
      EntityUtil.checkCollide() && mc.player.motionY < 0.10000000149011612D && event.getPos().getY() < mc.player.posY - 0.05000000074505806D) {
      if (mc.player.getRidingEntity() != null) {
        event.setBoundingBox(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.949999988079071D, 1.0D));
      } else {
        event.setBoundingBox(Block.FULL_BLOCK_AABB);
      } 
      event.setCanceled(true);
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceived(PacketEvent.Receive event) {
    if (((Boolean)this.cancelVehicle.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.server.SPacketMoveVehicle)
      event.setCanceled(true); 
  }
  
  public String getDisplayInfo() {
    if (this.mode.getValue() == Mode.NORMAL)
      return null; 
    return this.mode.currentEnumName();
  }
  
  private void doTrampoline() {
    if (mc.player.isSneaking())
      return; 
    if (EntityUtil.isAboveLiquid((Entity)mc.player) && !mc.player.isSneaking() && !mc.gameSettings.keyBindJump.pressed) {
      mc.player.motionY = 0.1D;
      return;
    } 
    if (mc.player.onGround || mc.player.isOnLadder())
      this.grounded = false; 
    if (mc.player.motionY > 0.0D)
      if (mc.player.motionY < 0.03D && this.grounded) {
        mc.player.motionY += 0.06713D;
      } else if (mc.player.motionY <= 0.05D && this.grounded) {
        mc.player.motionY *= 1.20000000999D;
        mc.player.motionY += 0.06D;
      } else if (mc.player.motionY <= 0.08D && this.grounded) {
        mc.player.motionY *= 1.20000003D;
        mc.player.motionY += 0.055D;
      } else if (mc.player.motionY <= 0.112D && this.grounded) {
        mc.player.motionY += 0.0535D;
      } else if (this.grounded) {
        mc.player.motionY *= 1.000000000002D;
        mc.player.motionY += 0.0517D;
      }  
    if (this.grounded && mc.player.motionY < 0.0D && mc.player.motionY > -0.3D)
      mc.player.motionY += 0.045835D; 
    if (!((Boolean)this.fall.getValue()).booleanValue())
      mc.player.fallDistance = 0.0F; 
    if (!EntityUtil.checkForLiquid((Entity)mc.player, true))
      return; 
    if (EntityUtil.checkForLiquid((Entity)mc.player, true))
      mc.player.motionY = 0.5D; 
    this.grounded = true;
  }
  
  public enum EventMode {
    PRE, POST, ALL;
  }
  
  public enum Mode {
    TRAMPOLINE, BOUNCE, VANILLA, NORMAL;
  }
}
