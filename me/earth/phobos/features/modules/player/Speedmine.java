//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import java.awt.Color;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.BlockEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speedmine extends Module {
  public Setting<Boolean> tweaks = register(new Setting("Tweaks", Boolean.valueOf(true)));
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET, v -> ((Boolean)this.tweaks.getValue()).booleanValue()));
  
  public Setting<Boolean> reset = register(new Setting("Reset", Boolean.valueOf(true)));
  
  public Setting<Float> damage = register(new Setting("Damage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(1.0F), v -> (this.mode.getValue() == Mode.DAMAGE && ((Boolean)this.tweaks.getValue()).booleanValue())));
  
  public Setting<Boolean> noBreakAnim = register(new Setting("NoBreakAnim", Boolean.valueOf(false)));
  
  public Setting<Boolean> noDelay = register(new Setting("NoDelay", Boolean.valueOf(false)));
  
  public Setting<Boolean> noSwing = register(new Setting("NoSwing", Boolean.valueOf(false)));
  
  public Setting<Boolean> noTrace = register(new Setting("NoTrace", Boolean.valueOf(false)));
  
  public Setting<Boolean> allow = register(new Setting("AllowMultiTask", Boolean.valueOf(false)));
  
  public Setting<Boolean> pickaxe = register(new Setting("Pickaxe", Boolean.valueOf(true), v -> ((Boolean)this.noTrace.getValue()).booleanValue()));
  
  public Setting<Boolean> doubleBreak = register(new Setting("DoubleBreak", Boolean.valueOf(false)));
  
  public Setting<Boolean> webSwitch = register(new Setting("WebSwitch", Boolean.valueOf(false)));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(85), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private static Speedmine INSTANCE = new Speedmine();
  
  public BlockPos currentPos;
  
  public IBlockState currentBlockState;
  
  private final Timer timer = new Timer();
  
  private boolean isMining = false;
  
  private BlockPos lastPos = null;
  
  private EnumFacing lastFacing = null;
  
  public Speedmine() {
    super("Speedmine", "Speeds up mining.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Speedmine getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Speedmine(); 
    return INSTANCE;
  }
  
  public void onTick() {
    if (this.currentPos != null) {
      if (mc.player != null && mc.player.getDistanceSq(this.currentPos) > 2500.0D) {
        this.currentPos = null;
        this.currentBlockState = null;
        return;
      } 
      if (!mc.world.getBlockState(this.currentPos).equals(this.currentBlockState) || mc.world.getBlockState(this.currentPos).getBlock() == Blocks.AIR) {
        this.currentPos = null;
        this.currentBlockState = null;
      } else if (((Boolean)this.webSwitch.getValue()).booleanValue() && this.currentBlockState.getBlock() == Blocks.WEB && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe) {
        InventoryUtil.switchToHotbarSlot(ItemSword.class, false);
      } 
    } 
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (((Boolean)this.noDelay.getValue()).booleanValue())
      mc.playerController.blockHitDelay = 0; 
    if (this.isMining && this.lastPos != null && this.lastFacing != null && ((Boolean)this.noBreakAnim.getValue()).booleanValue())
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.lastPos, this.lastFacing)); 
    if (((Boolean)this.reset.getValue()).booleanValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && !((Boolean)this.allow.getValue()).booleanValue())
      mc.playerController.isHittingBlock = false; 
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue() && this.currentPos != null) {
      Color color = new Color(this.timer.passedMs((int)(2000.0F * Phobos.serverManager.getTpsFactor())) ? 0 : 255, this.timer.passedMs((int)(2000.0F * Phobos.serverManager.getTpsFactor())) ? 255 : 0, 0, 255);
      RenderUtil.drawBoxESP(this.currentPos, color, false, color, ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (fullNullCheck())
      return; 
    if (event.getStage() == 0) {
      if (((Boolean)this.noSwing.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation)
        event.setCanceled(true); 
      if (((Boolean)this.noBreakAnim.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayerDigging) {
        CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
        if (packet != null && packet.getPosition() != null) {
          try {
            for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(packet.getPosition()))) {
              if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
                showAnimation();
                return;
              } 
            } 
          } catch (Exception exception) {}
          if (packet.getAction().equals(CPacketPlayerDigging.Action.START_DESTROY_BLOCK))
            showAnimation(true, packet.getPosition(), packet.getFacing()); 
          if (packet.getAction().equals(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK))
            showAnimation(); 
        } 
      } 
    } 
  }
  
  @SubscribeEvent
  public void onBlockEvent(BlockEvent event) {
    if (fullNullCheck())
      return; 
    if (event.getStage() == 3 && ((Boolean)this.reset.getValue()).booleanValue() && 
      mc.playerController.curBlockDamageMP > 0.1F)
      mc.playerController.isHittingBlock = true; 
    if (event.getStage() == 4 && ((Boolean)this.tweaks.getValue()).booleanValue()) {
      if (BlockUtil.canBreak(event.pos)) {
        if (((Boolean)this.reset.getValue()).booleanValue())
          mc.playerController.isHittingBlock = false; 
        switch ((Mode)this.mode.getValue()) {
          case PACKET:
            if (this.currentPos == null) {
              this.currentPos = event.pos;
              this.currentBlockState = mc.world.getBlockState(this.currentPos);
              this.timer.reset();
            } 
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            event.setCanceled(true);
            break;
          case DAMAGE:
            if (mc.playerController.curBlockDamageMP >= ((Float)this.damage.getValue()).floatValue())
              mc.playerController.curBlockDamageMP = 1.0F; 
            break;
          case INSTANT:
            mc.player.swingArm(EnumHand.MAIN_HAND);
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            mc.playerController.onPlayerDestroyBlock(event.pos);
            mc.world.setBlockToAir(event.pos);
            break;
        } 
      } 
      if (((Boolean)this.doubleBreak.getValue()).booleanValue()) {
        BlockPos above = event.pos.add(0, 1, 0);
        if (BlockUtil.canBreak(above) && mc.player.getDistance(above.getX(), above.getY(), above.getZ()) <= 5.0D) {
          mc.player.swingArm(EnumHand.MAIN_HAND);
          mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, above, event.facing));
          mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, above, event.facing));
          mc.playerController.onPlayerDestroyBlock(above);
          mc.world.setBlockToAir(above);
        } 
      } 
    } 
  }
  
  private void showAnimation(boolean isMining, BlockPos lastPos, EnumFacing lastFacing) {
    this.isMining = isMining;
    this.lastPos = lastPos;
    this.lastFacing = lastFacing;
  }
  
  public void showAnimation() {
    showAnimation(false, (BlockPos)null, (EnumFacing)null);
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public enum Mode {
    PACKET, DAMAGE, INSTANT;
  }
}
