//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import com.google.common.util.concurrent.AtomicDouble;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BedBomb extends Module {
  private final Setting<Boolean> place;
  
  private final Setting<Integer> placeDelay;
  
  private final Setting<Float> placeRange;
  
  private final Setting<Boolean> extraPacket;
  
  private final Setting<Boolean> packet;
  
  private final Setting<Boolean> explode;
  
  private final Setting<Integer> breakDelay;
  
  private final Setting<Float> breakRange;
  
  private final Setting<Float> minDamage;
  
  private final Setting<Float> range;
  
  private final Setting<Boolean> suicide;
  
  private final Setting<Boolean> removeTiles;
  
  private final Setting<Boolean> rotate;
  
  private final Setting<Logic> logic;
  
  private final Timer breakTimer;
  
  private final Timer placeTimer;
  
  private EntityPlayer target;
  
  private boolean sendRotationPacket;
  
  private final AtomicDouble yaw;
  
  private final AtomicDouble pitch;
  
  private final AtomicBoolean shouldRotate;
  
  private BlockPos maxPos;
  
  private int lastHotbarSlot;
  
  private int bedSlot;
  
  public BedBomb() {
    super("BedBomb", "AutoPlace and Break for beds", Module.Category.COMBAT, true, false, false);
    this.place = register(new Setting("Place", Boolean.valueOf(false)));
    this.placeDelay = register(new Setting("Placedelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> ((Boolean)this.place.getValue()).booleanValue()));
    this.placeRange = register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(1.0F), Float.valueOf(10.0F), v -> ((Boolean)this.place.getValue()).booleanValue()));
    this.extraPacket = register(new Setting("InsanePacket", Boolean.valueOf(false), v -> ((Boolean)this.place.getValue()).booleanValue()));
    this.packet = register(new Setting("Packet", Boolean.valueOf(false), v -> ((Boolean)this.place.getValue()).booleanValue()));
    this.explode = register(new Setting("Break", Boolean.valueOf(true)));
    this.breakDelay = register(new Setting("Breakdelay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> ((Boolean)this.explode.getValue()).booleanValue()));
    this.breakRange = register(new Setting("BreakRange", Float.valueOf(6.0F), Float.valueOf(1.0F), Float.valueOf(10.0F), v -> ((Boolean)this.explode.getValue()).booleanValue()));
    this.minDamage = register(new Setting("MinDamage", Float.valueOf(5.0F), Float.valueOf(1.0F), Float.valueOf(36.0F), v -> ((Boolean)this.explode.getValue()).booleanValue()));
    this.range = register(new Setting("Range", Float.valueOf(10.0F), Float.valueOf(1.0F), Float.valueOf(12.0F), v -> ((Boolean)this.explode.getValue()).booleanValue()));
    this.suicide = register(new Setting("Suicide", Boolean.valueOf(false), v -> ((Boolean)this.explode.getValue()).booleanValue()));
    this.removeTiles = register(new Setting("RemoveTiles", Boolean.valueOf(false)));
    this.rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
    this.logic = register(new Setting("Logic", Logic.BREAKPLACE, v -> (((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.explode.getValue()).booleanValue())));
    this.breakTimer = new Timer();
    this.placeTimer = new Timer();
    this.target = null;
    this.sendRotationPacket = false;
    this.yaw = new AtomicDouble(-1.0D);
    this.pitch = new AtomicDouble(-1.0D);
    this.shouldRotate = new AtomicBoolean(false);
    this.maxPos = null;
    this.lastHotbarSlot = -1;
    this.bedSlot = -1;
  }
  
  @SubscribeEvent
  public void onPacket(PacketEvent.Send event) {
    if (this.shouldRotate.get() && 
      event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      packet.yaw = (float)this.yaw.get();
      packet.pitch = (float)this.pitch.get();
      this.shouldRotate.set(false);
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0 || fullNullCheck() || (mc.player.dimension != -1 && mc.player.dimension != 1))
      return; 
    doBedBomb();
  }
  
  private void doBedBomb() {
    switch ((Logic)this.logic.getValue()) {
      case BREAKPLACE:
        mapBeds();
        breakBeds();
        placeBeds();
        break;
      case PLACEBREAK:
        mapBeds();
        placeBeds();
        breakBeds();
        break;
    } 
  }
  
  private void breakBeds() {
    if (((Boolean)this.explode.getValue()).booleanValue() && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue()) && 
      this.maxPos != null) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
      BlockUtil.rightClickBlockLegit(this.maxPos, ((Float)this.range.getValue()).floatValue(), (((Boolean)this.rotate.getValue()).booleanValue() && !((Boolean)this.place.getValue()).booleanValue()), EnumHand.MAIN_HAND, this.yaw, this.pitch, this.shouldRotate, true);
      if (mc.player.isSneaking())
        mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING)); 
      this.breakTimer.reset();
    } 
  }
  
  private void mapBeds() {
    this.maxPos = null;
    float maxDamage = 0.5F;
    if (((Boolean)this.removeTiles.getValue()).booleanValue()) {
      List<BedData> removedBlocks = new ArrayList<>();
      for (TileEntity tile : mc.world.loadedTileEntityList) {
        if (tile instanceof TileEntityBed) {
          TileEntityBed bed = (TileEntityBed)tile;
          BedData data = new BedData(tile.getPos(), mc.world.getBlockState(tile.getPos()), bed, bed.isHeadPiece());
          removedBlocks.add(data);
        } 
      } 
      for (BedData data : removedBlocks)
        mc.world.setBlockToAir(data.getPos()); 
      for (BedData data : removedBlocks) {
        if (data.isHeadPiece()) {
          BlockPos pos = data.getPos();
          if (mc.player.getDistanceSq(pos) <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue())) {
            float selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.player);
            if (selfDamage + 1.0D < EntityUtil.getHealth((Entity)mc.player) || !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue()))
              for (EntityPlayer player : mc.world.playerEntities) {
                if (player.getDistanceSq(pos) < MathUtil.square(((Float)this.range.getValue()).floatValue()) && EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue()))) {
                  float damage = DamageUtil.calculateDamage(pos, (Entity)player);
                  if ((damage > selfDamage || (damage > ((Float)this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) || damage > EntityUtil.getHealth((Entity)player)) && 
                    damage > maxDamage) {
                    maxDamage = damage;
                    this.maxPos = pos;
                  } 
                } 
              }  
          } 
        } 
      } 
      for (BedData data : removedBlocks)
        mc.world.setBlockState(data.getPos(), data.getState()); 
    } else {
      for (TileEntity tile : mc.world.loadedTileEntityList) {
        if (tile instanceof TileEntityBed) {
          TileEntityBed bed = (TileEntityBed)tile;
          if (bed.isHeadPiece()) {
            BlockPos pos = bed.getPos();
            if (mc.player.getDistanceSq(pos) <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue())) {
              float selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.player);
              if (selfDamage + 1.0D < EntityUtil.getHealth((Entity)mc.player) || !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue()))
                for (EntityPlayer player : mc.world.playerEntities) {
                  if (player.getDistanceSq(pos) < MathUtil.square(((Float)this.range.getValue()).floatValue()) && EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue()))) {
                    float damage = DamageUtil.calculateDamage(pos, (Entity)player);
                    if ((damage > selfDamage || (damage > ((Float)this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) || damage > EntityUtil.getHealth((Entity)player)) && 
                      damage > maxDamage) {
                      maxDamage = damage;
                      this.maxPos = pos;
                    } 
                  } 
                }  
            } 
          } 
        } 
      } 
    } 
  }
  
  private void placeBeds() {
    if (((Boolean)this.place.getValue()).booleanValue() && this.placeTimer.passedMs(((Integer)this.placeDelay.getValue()).intValue()) && this.maxPos == null) {
      this.bedSlot = findBedSlot();
      if (this.bedSlot == -1)
        if (mc.player.getHeldItemOffhand().getItem() == Items.BED) {
          this.bedSlot = -2;
        } else {
          return;
        }  
      this.lastHotbarSlot = mc.player.inventory.currentItem;
      this.target = EntityUtil.getClosestEnemy(((Float)this.placeRange.getValue()).floatValue());
      if (this.target != null) {
        BlockPos targetPos = new BlockPos(this.target.getPositionVector());
        placeBed(targetPos, true);
      } 
    } 
  }
  
  private void placeBed(BlockPos pos, boolean firstCheck) {
    if (mc.world.getBlockState(pos).getBlock() == Blocks.BED)
      return; 
    float damage = DamageUtil.calculateDamage(pos, (Entity)mc.player);
    if (damage > EntityUtil.getHealth((Entity)mc.player) + 0.5D) {
      if (firstCheck)
        placeBed(pos.up(), false); 
      return;
    } 
    if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
      if (firstCheck)
        placeBed(pos.up(), false); 
      return;
    } 
    List<BlockPos> positions = new ArrayList<>();
    Map<BlockPos, EnumFacing> facings = new HashMap<>();
    for (EnumFacing facing : EnumFacing.values()) {
      if (facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
        BlockPos position = pos.offset(facing);
        if (mc.player.getDistanceSq(position) <= MathUtil.square(((Float)this.placeRange.getValue()).floatValue()) && mc.world.getBlockState(position).getMaterial().isReplaceable() && !mc.world.getBlockState(position.down()).getMaterial().isReplaceable()) {
          positions.add(position);
          facings.put(position, facing.getOpposite());
        } 
      } 
    } 
    if (positions.isEmpty()) {
      if (firstCheck)
        placeBed(pos.up(), false); 
      return;
    } 
    positions.sort(Comparator.comparingDouble(pos2 -> mc.player.getDistanceSq(pos2)));
    BlockPos finalPos = positions.get(0);
    EnumFacing finalFacing = facings.get(finalPos);
    float[] rotation = RotationUtil.simpleFacing(finalFacing);
    if (!this.sendRotationPacket && ((Boolean)this.extraPacket.getValue()).booleanValue()) {
      RotationUtil.faceYawAndPitch(rotation[0], rotation[1]);
      this.sendRotationPacket = true;
    } 
    this.yaw.set(rotation[0]);
    this.pitch.set(rotation[1]);
    this.shouldRotate.set(true);
    Vec3d hitVec = (new Vec3d((Vec3i)finalPos.down())).add(0.5D, 0.5D, 0.5D).add((new Vec3d(finalFacing.getOpposite().getDirectionVec())).scale(0.5D));
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
    InventoryUtil.switchToHotbarSlot(this.bedSlot, false);
    BlockUtil.rightClickBlock(finalPos.down(), hitVec, (this.bedSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, EnumFacing.UP, ((Boolean)this.packet.getValue()).booleanValue());
    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    this.placeTimer.reset();
  }
  
  public String getDisplayInfo() {
    if (this.target != null)
      return this.target.getName(); 
    return null;
  }
  
  public void onToggle() {
    this.lastHotbarSlot = -1;
    this.bedSlot = -1;
    this.sendRotationPacket = false;
    this.target = null;
    this.yaw.set(-1.0D);
    this.pitch.set(-1.0D);
    this.shouldRotate.set(false);
  }
  
  private int findBedSlot() {
    for (int i = 0; i < 9; i++) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (stack != ItemStack.EMPTY)
        if (stack.getItem() == Items.BED)
          return i;  
    } 
    return -1;
  }
  
  public static class BedData {
    private final BlockPos pos;
    
    private final IBlockState state;
    
    private final boolean isHeadPiece;
    
    private final TileEntityBed entity;
    
    public BedData(BlockPos pos, IBlockState state, TileEntityBed bed, boolean isHeadPiece) {
      this.pos = pos;
      this.state = state;
      this.entity = bed;
      this.isHeadPiece = isHeadPiece;
    }
    
    public BlockPos getPos() {
      return this.pos;
    }
    
    public IBlockState getState() {
      return this.state;
    }
    
    public boolean isHeadPiece() {
      return this.isHeadPiece;
    }
    
    public TileEntityBed getEntity() {
      return this.entity;
    }
  }
  
  public enum Logic {
    BREAKPLACE, PLACEBREAK;
  }
}
