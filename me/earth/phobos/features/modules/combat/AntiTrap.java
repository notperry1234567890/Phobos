//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiTrap extends Module {
  public Setting<Rotate> rotate = register(new Setting("Rotate", Rotate.NORMAL));
  
  private final Setting<Integer> coolDown = register(new Setting("CoolDown", Integer.valueOf(400), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
  
  public Setting<Boolean> sortY = register(new Setting("SortY", Boolean.valueOf(true)));
  
  public static Set<BlockPos> placedPos = new HashSet<>();
  
  private final Vec3d[] surroundTargets = new Vec3d[] { 
      new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, -1.0D), new Vec3d(1.0D, 0.0D, 1.0D), new Vec3d(-1.0D, 0.0D, -1.0D), new Vec3d(-1.0D, 0.0D, 1.0D), new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), 
      new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, -1.0D), new Vec3d(1.0D, 1.0D, 1.0D), new Vec3d(-1.0D, 1.0D, -1.0D), new Vec3d(-1.0D, 1.0D, 1.0D) };
  
  private int lastHotbarSlot = -1;
  
  private boolean switchedItem;
  
  private boolean offhand = false;
  
  private final Timer timer = new Timer();
  
  public AntiTrap() {
    super("AntiTrap", "Places a crystal to prevent you getting trapped.", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck() || !this.timer.passedMs(((Integer)this.coolDown.getValue()).intValue())) {
      disable();
      return;
    } 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
  }
  
  public void onDisable() {
    if (fullNullCheck())
      return; 
    switchItem(true);
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (!fullNullCheck() && event.getStage() == 0)
      doAntiTrap(); 
  }
  
  public void doAntiTrap() {
    this.offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
    if (!this.offhand && InventoryUtil.findHotbarBlock(ItemEndCrystal.class) == -1) {
      disable();
      return;
    } 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    List<Vec3d> targets = new ArrayList<>();
    Collections.addAll(targets, BlockUtil.convertVec3ds(mc.player.getPositionVector(), this.surroundTargets));
    EntityPlayer closestPlayer = EntityUtil.getClosestEnemy(6.0D);
    if (closestPlayer != null) {
      targets.sort((vec3d, vec3d2) -> Double.compare(closestPlayer.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), closestPlayer.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
      if (((Boolean)this.sortY.getValue()).booleanValue())
        targets.sort(Comparator.comparingDouble(vec3d -> vec3d.y)); 
    } 
    for (Vec3d vec3d : targets) {
      BlockPos pos = new BlockPos(vec3d);
      if (BlockUtil.canPlaceCrystal(pos)) {
        placeCrystal(pos);
        disable();
        break;
      } 
    } 
  }
  
  private void placeCrystal(BlockPos pos) {
    boolean mainhand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
    if (!mainhand && !this.offhand && 
      !switchItem(false)) {
      disable();
      return;
    } 
    RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D));
    EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
    float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
    switch ((Rotate)this.rotate.getValue()) {
      case NORMAL:
        Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
        break;
      case PACKET:
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], MathHelper.normalizeAngle((int)angle[1], 360), mc.player.onGround));
        break;
    } 
    placedPos.add(pos);
    mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(pos, facing, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
    mc.player.swingArm(EnumHand.MAIN_HAND);
    this.timer.reset();
  }
  
  private boolean switchItem(boolean back) {
    if (this.offhand)
      return true; 
    boolean[] value = InventoryUtil.switchItemToItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), Items.END_CRYSTAL);
    this.switchedItem = value[0];
    return value[1];
  }
  
  public enum Rotate {
    NONE, NORMAL, PACKET;
  }
}
