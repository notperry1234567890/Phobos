//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class AutoCrystal extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.PLACE));
  
  public Setting<Raytrace> raytrace = register(new Setting("Raytrace", Raytrace.NONE, v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Boolean> place = register(new Setting("Place", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE)));
  
  public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Float> placeRange = register(new Setting("PlaceRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Float> minDamage = register(new Setting("MinDamage", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Float> maximumSelfDamage = register(new Setting("MaxSelfDamage", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Integer> wasteAmount = register(new Setting("WasteAmount", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(5), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Boolean> wasteMinDmgCount = register(new Setting("CountMinDmg", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Float> facePlace = register(new Setting("FacePlace", Float.valueOf(8.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Float> placetrace = register(new Setting("Placetrace", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK)));
  
  public Setting<Boolean> antiSurround = register(new Setting("AntiSurround", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Boolean> limitFacePlace = register(new Setting("LimitFacePlace", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Boolean> oneDot15 = register(new Setting("1.15", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Boolean> doublePop = register(new Setting("AntiTotem", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Float> popDamage = register(new Setting("PopDamage", Float.valueOf(4.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
  
  public Setting<Integer> popTime = register(new Setting("PopTime", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue())));
  
  public Setting<Boolean> explode = register(new Setting("Break", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK)));
  
  public Setting<Switch> switchMode = register(new Setting("Attack", Switch.BREAKSLOT, v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
  
  public Setting<Integer> breakDelay = register(new Setting("BreakDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
  
  public Setting<Float> breakRange = register(new Setting("BreakRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
  
  public Setting<Integer> packets = register(new Setting("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(6), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue())));
  
  public Setting<Float> breaktrace = register(new Setting("Breaktrace", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE)));
  
  public Setting<Boolean> manual = register(new Setting("Manual", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.BREAK)));
  
  public Setting<Boolean> manualMinDmg = register(new Setting("ManMinDmg", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.manual.getValue()).booleanValue())));
  
  public Setting<Integer> manualBreak = register(new Setting("ManualDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.manual.getValue()).booleanValue())));
  
  public Setting<Boolean> sync = register(new Setting("Sync", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.BREAK && (((Boolean)this.explode.getValue()).booleanValue() || ((Boolean)this.manual.getValue()).booleanValue()))));
  
  public Setting<Boolean> instant = register(new Setting("Predict", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.BREAK && ((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.place.getValue()).booleanValue())));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER)));
  
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  public Setting<Boolean> text = register(new Setting("Text", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.box.getValue()).booleanValue())));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  public Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue() && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(12.0F), Float.valueOf(0.1F), Float.valueOf(20.0F), v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Target> targetMode = register(new Setting("Target", Target.CLOSEST, v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Integer> minArmor = register(new Setting("MinArmor", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(125), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Integer> switchCooldown = register(new Setting("Cooldown", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<AutoSwitch> autoSwitch = register(new Setting("Switch", AutoSwitch.TOGGLE, v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Bind> switchBind = register(new Setting("SwitchBind", new Bind(-1), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE)));
  
  public Setting<Boolean> offhandSwitch = register(new Setting("Offhand", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE)));
  
  public Setting<Boolean> switchBack = register(new Setting("Switchback", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && ((Boolean)this.offhandSwitch.getValue()).booleanValue())));
  
  public Setting<Boolean> lethalSwitch = register(new Setting("LethalSwitch", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE)));
  
  public Setting<Boolean> mineSwitch = register(new Setting("MineSwitch", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE)));
  
  public Setting<Rotate> rotate = register(new Setting("Rotate", Rotate.OFF, v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Boolean> suicide = register(new Setting("Suicide", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Boolean> webAttack = register(new Setting("WebAttack", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE)));
  
  public Setting<Boolean> fullCalc = register(new Setting("ExtraCalc", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Boolean> extraSelfCalc = register(new Setting("MinSelfDmg", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<AntiFriendPop> antiFriendPop = register(new Setting("FriendPop", AntiFriendPop.NONE, v -> (this.setting.getValue() == Settings.MISC)));
  
  public Setting<Boolean> noCount = register(new Setting("AntiCount", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK))));
  
  public Setting<Boolean> calcEvenIfNoDamage = register(new Setting("BigFriendCalc", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.targetMode.getValue() != Target.DAMAGE)));
  
  public Setting<Boolean> predictFriendDmg = register(new Setting("PredictFriend", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && ((Boolean)this.instant.getValue()).booleanValue())));
  
  public Setting<Logic> logic = register(new Setting("Logic", Logic.BREAKPLACE, v -> (this.setting.getValue() == Settings.DEV)));
  
  public Setting<Boolean> doubleMap = register(new Setting("DoubleMap", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.logic.getValue() == Logic.PLACEBREAK)));
  
  public Setting<DamageSync> damageSync = register(new Setting("DamageSync", DamageSync.NONE, v -> (this.setting.getValue() == Settings.DEV)));
  
  public Setting<Integer> damageSyncTime = register(new Setting("SyncDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
  
  public Setting<Float> dropOff = register(new Setting("DropOff", Float.valueOf(5.0F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK)));
  
  public Setting<Integer> confirm = register(new Setting("Confirm", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
  
  public Setting<Boolean> syncedFeetPlace = register(new Setting("FeetSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE)));
  
  public Setting<Boolean> fullSync = register(new Setting("FullSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  public Setting<Boolean> syncCount = register(new Setting("SyncCount", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  public Setting<Boolean> hyperSync = register(new Setting("HyperSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  public Setting<Boolean> gigaSync = register(new Setting("GigaSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  public Setting<Boolean> syncySync = register(new Setting("SyncySync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  public Setting<Boolean> enormousSync = register(new Setting("EnormousSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  public Setting<Boolean> holySync = register(new Setting("UnbelievableSync", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue())));
  
  private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> (this.setting.getValue() == Settings.DEV)));
  
  private final Setting<ThreadMode> threadMode = register(new Setting("Thread", ThreadMode.NONE, v -> (this.setting.getValue() == Settings.DEV)));
  
  public Setting<Integer> threadDelay = register(new Setting("ThreadDelay", Integer.valueOf(25), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE)));
  
  public Setting<Integer> syncThreads = register(new Setting("SyncThreads", Integer.valueOf(1000), Integer.valueOf(1), Integer.valueOf(10000), v -> (this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE)));
  
  public Setting<Boolean> altPosition = register(new Setting("AltPos", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.DEV)));
  
  public Setting<Boolean> doublePopOnDamage = register(new Setting("DamagePop", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE && ((Boolean)this.place.getValue()).booleanValue() && ((Boolean)this.doublePop.getValue()).booleanValue() && this.targetMode.getValue() == Target.DAMAGE)));
  
  private Queue<Entity> attackList = new ConcurrentLinkedQueue<>();
  
  private Map<Entity, Float> crystalMap = new HashMap<>();
  
  private final Timer switchTimer = new Timer();
  
  private final Timer manualTimer = new Timer();
  
  private final Timer breakTimer = new Timer();
  
  private final Timer placeTimer = new Timer();
  
  private final Timer syncTimer = new Timer();
  
  public static EntityPlayer target = null;
  
  private Entity efficientTarget = null;
  
  private double currentDamage = 0.0D;
  
  private double renderDamage = 0.0D;
  
  private double lastDamage = 0.0D;
  
  private boolean didRotation = false;
  
  private boolean switching = false;
  
  private BlockPos placePos = null;
  
  private BlockPos renderPos = null;
  
  private boolean mainHand = false;
  
  private boolean rotating = false;
  
  private boolean offHand = false;
  
  private int crystalCount = 0;
  
  private int minDmgCount = 0;
  
  private int lastSlot = -1;
  
  private float yaw = 0.0F;
  
  private float pitch = 0.0F;
  
  private BlockPos webPos = null;
  
  private final Timer renderTimer = new Timer();
  
  private BlockPos lastPos = null;
  
  public static Set<BlockPos> placedPos = new HashSet<>();
  
  public static Set<BlockPos> brokenPos = new HashSet<>();
  
  private boolean posConfirmed = false;
  
  private boolean foundDoublePop = false;
  
  private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
  
  private ScheduledExecutorService executor;
  
  private final Timer syncroTimer = new Timer();
  
  private Thread thread;
  
  private EntityPlayer currentSyncTarget;
  
  private BlockPos syncedPlayerPos;
  
  private BlockPos syncedCrystalPos;
  
  private static AutoCrystal instance;
  
  private final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<>();
  
  public AutoCrystal() {
    super("AutoCrystal", "Best CA on the market", Module.Category.COMBAT, true, false, false);
    instance = this;
  }
  
  public static AutoCrystal getInstance() {
    if (instance == null)
      instance = new AutoCrystal(); 
    return instance;
  }
  
  public void onTick() {
    if (this.threadMode.getValue() == ThreadMode.NONE && ((Integer)this.eventMode.getValue()).intValue() == 3)
      doAutoCrystal(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0)
      return; 
    if (this.threadMode.getValue() != ThreadMode.NONE) {
      processMultiThreading();
    } else if (((Integer)this.eventMode.getValue()).intValue() == 2) {
      doAutoCrystal();
    } 
  }
  
  public void onUpdate() {
    if (this.threadMode.getValue() == ThreadMode.NONE && ((Integer)this.eventMode.getValue()).intValue() == 1)
      doAutoCrystal(); 
  }
  
  public void onToggle() {
    brokenPos.clear();
    placedPos.clear();
    this.totemPops.clear();
    this.rotating = false;
  }
  
  public void onDisable() {
    if (this.thread != null)
      this.shouldInterrupt.set(true); 
    if (this.executor != null)
      this.executor.shutdown(); 
  }
  
  public void onEnable() {
    if (this.threadMode.getValue() != ThreadMode.NONE)
      processMultiThreading(); 
  }
  
  public String getDisplayInfo() {
    if (this.switching)
      return "§aSwitch"; 
    if (target != null)
      return target.getName(); 
    return null;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && ((Integer)this.eventMode.getValue()).intValue() != 2 && 
      event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      packet.yaw = this.yaw;
      packet.pitch = this.pitch;
      this.rotating = false;
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
  public void onPacketReceive(PacketEvent.Receive event) {
    if (((Boolean)this.explode.getValue()).booleanValue() && ((Boolean)this.instant.getValue()).booleanValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !((Boolean)this.syncedFeetPlace.getValue()).booleanValue() || this.damageSync.getValue() == DamageSync.NONE)) {
      SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
      if (packet.getType() == 51) {
        BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        if (placedPos.contains(pos.down())) {
          if (((Boolean)this.predictFriendDmg.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL))
            for (EntityPlayer friend : mc.world.playerEntities) {
              if (friend == null || mc.player.equals(friend) || friend.getDistanceSq(pos) > MathUtil.square((((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue())) || !Phobos.friendManager.isFriend(friend))
                continue; 
              if (DamageUtil.calculateDamage(pos, (Entity)friend) > EntityUtil.getHealth((Entity)friend) + 0.5D)
                return; 
            }  
          CPacketUseEntity attackPacket = new CPacketUseEntity();
          attackPacket.entityId = packet.getEntityID();
          attackPacket.action = CPacketUseEntity.Action.ATTACK;
          mc.player.connection.sendPacket((Packet)attackPacket);
        } 
      } 
    } else if (event.getPacket() instanceof SPacketExplosion) {
      SPacketExplosion packet = (SPacketExplosion)event.getPacket();
      BlockPos pos = (new BlockPos(packet.getX(), packet.getY(), packet.getZ())).down();
      if (this.damageSync.getValue() == DamageSync.PLACE) {
        if (placedPos.contains(pos)) {
          placedPos.remove(pos);
          this.posConfirmed = true;
        } 
      } else if (this.damageSync.getValue() == DamageSync.BREAK && 
        brokenPos.contains(pos)) {
        brokenPos.remove(pos);
        this.posConfirmed = true;
      } 
    } else if (event.getPacket() instanceof SPacketDestroyEntities) {
      SPacketDestroyEntities packet = (SPacketDestroyEntities)event.getPacket();
      for (int id : packet.getEntityIDs()) {
        Entity entity = mc.world.getEntityByID(id);
        if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
          brokenPos.remove((new BlockPos(entity.getPositionVector())).down());
          placedPos.remove((new BlockPos(entity.getPositionVector())).down());
        } 
      } 
    } else if (event.getPacket() instanceof SPacketEntityStatus) {
      SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
      if (packet.getOpCode() == 35 && 
        packet.getEntity((World)mc.world) instanceof EntityPlayer)
        this.totemPops.put((EntityPlayer)packet.getEntity((World)mc.world), (new Timer()).reset()); 
    } 
  }
  
  public void onRender3D(Render3DEvent event) {
    if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && ((Boolean)this.render.getValue()).booleanValue() && (((Boolean)this.box.getValue()).booleanValue() || ((Boolean)this.text.getValue()).booleanValue() || ((Boolean)this.outline.getValue()).booleanValue())) {
      RenderUtil.drawBoxESP(this.renderPos, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
      if (((Boolean)this.text.getValue()).booleanValue())
        RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + ""); 
    } 
  }
  
  @SubscribeEvent
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (Keyboard.getEventKeyState() && !(mc.currentScreen instanceof me.earth.phobos.features.gui.PhobosGui) && ((Bind)this.switchBind.getValue()).getKey() == Keyboard.getEventKey()) {
      if (((Boolean)this.switchBack.getValue()).booleanValue() && ((Boolean)this.offhandSwitch.getValue()).booleanValue() && this.offHand) {
        Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
        if (module.isOff()) {
          Command.sendMessage("<" + getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
        } else if (module.type.getValue() == Offhand.Type.NEW) {
          module.setSwapToTotem(true);
          module.doOffhand();
        } else {
          module.setMode(Offhand.Mode2.TOTEMS);
          module.doSwitch();
        } 
        return;
      } 
      this.switching = !this.switching;
    } 
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (event.getStage() == 2 && 
      event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && 
      isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
      if (this.executor != null)
        this.executor.shutdown(); 
      if (this.thread != null)
        this.shouldInterrupt.set(true); 
    } 
  }
  
  private void processMultiThreading() {
    if (isOff())
      return; 
    if (this.threadMode.getValue() == ThreadMode.POOL) {
      handlePool();
    } else if (this.threadMode.getValue() == ThreadMode.WHILE) {
      handleWhile();
    } 
  }
  
  private void handlePool() {
    if (this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue())) {
      if (this.executor != null)
        this.executor.shutdown(); 
      this.executor = getExecutor();
      this.syncroTimer.reset();
    } 
  }
  
  private void handleWhile() {
    if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue())) {
      if (this.thread == null) {
        this.thread = new Thread(RAutoCrystal.getInstance(this));
      } else if (this.syncroTimer.passedMs(((Integer)this.syncThreads.getValue()).intValue()) && !this.shouldInterrupt.get()) {
        this.shouldInterrupt.set(true);
        this.syncroTimer.reset();
        return;
      } 
      if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive()))
        this.thread = new Thread(RAutoCrystal.getInstance(this)); 
      if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
        try {
          this.thread.start();
        } catch (Exception e) {
          e.printStackTrace();
        } 
        this.syncroTimer.reset();
      } 
    } 
  }
  
  private ScheduledExecutorService getExecutor() {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(RAutoCrystal.getInstance(this), 0L, ((Integer)this.threadDelay.getValue()).intValue(), TimeUnit.MILLISECONDS);
    return service;
  }
  
  private static class RAutoCrystal implements Runnable {
    private static RAutoCrystal instance;
    
    private AutoCrystal autoCrystal;
    
    public static RAutoCrystal getInstance(AutoCrystal autoCrystal) {
      if (instance == null)
        instance = new RAutoCrystal(); 
      instance.autoCrystal = autoCrystal;
      return instance;
    }
    
    public void run() {
      if (this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.POOL) {
        if (this.autoCrystal.isOn())
          this.autoCrystal.doAutoCrystal(); 
      } else if (this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
        while (this.autoCrystal.isOn() && this.autoCrystal.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
          if (this.autoCrystal.shouldInterrupt.get()) {
            this.autoCrystal.shouldInterrupt.set(false);
            this.autoCrystal.syncroTimer.reset();
            this.autoCrystal.thread.interrupt();
            break;
          } 
          this.autoCrystal.doAutoCrystal();
          try {
            Thread.sleep(((Integer)this.autoCrystal.threadDelay.getValue()).intValue());
          } catch (InterruptedException e) {
            this.autoCrystal.thread.interrupt();
            e.printStackTrace();
          } 
        } 
      } 
    }
  }
  
  public void doAutoCrystal() {
    if (check()) {
      switch ((Logic)this.logic.getValue()) {
        case OFF:
          placeCrystal();
          if (((Boolean)this.doubleMap.getValue()).booleanValue())
            mapCrystals(); 
          breakCrystal();
          break;
        case PLACE:
          breakCrystal();
          placeCrystal();
          break;
      } 
      manualBreaker();
    } 
  }
  
  private boolean check() {
    if (fullNullCheck())
      return false; 
    if (this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue())) {
      this.currentSyncTarget = null;
      this.syncedCrystalPos = null;
      this.syncedPlayerPos = null;
    } else if (((Boolean)this.syncySync.getValue()).booleanValue() && this.syncedCrystalPos != null) {
      this.posConfirmed = true;
    } 
    this.foundDoublePop = false;
    if (this.renderTimer.passedMs(500L)) {
      this.renderPos = null;
      this.renderTimer.reset();
    } 
    this.mainHand = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL);
    this.offHand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
    this.currentDamage = 0.0D;
    this.placePos = null;
    if (this.lastSlot != mc.player.inventory.currentItem || AutoTrap.isPlacing || Surround.isPlacing) {
      this.lastSlot = mc.player.inventory.currentItem;
      this.switchTimer.reset();
    } 
    if (this.offHand || this.mainHand)
      this.switching = false; 
    if ((!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching) || !DamageUtil.canBreakWeakness((EntityPlayer)mc.player) || !this.switchTimer.passedMs(((Integer)this.switchCooldown.getValue()).intValue())) {
      this.renderPos = null;
      target = null;
      this.rotating = false;
      return false;
    } 
    if (((Boolean)this.mineSwitch.getValue()).booleanValue() && mc.gameSettings.keyBindAttack.isKeyDown() && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemPickaxe)
      switchItem(); 
    mapCrystals();
    if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(((Integer)this.confirm.getValue()).intValue()))
      this.syncTimer.setMs((((Integer)this.damageSyncTime.getValue()).intValue() + 1)); 
    return true;
  }
  
  private void mapCrystals() {
    this.efficientTarget = null;
    if (((Integer)this.packets.getValue()).intValue() != 1) {
      this.attackList = new ConcurrentLinkedQueue<>();
      this.crystalMap = new HashMap<>();
    } 
    this.crystalCount = 0;
    this.minDmgCount = 0;
    Entity maxCrystal = null;
    float maxDamage = 0.5F;
    for (Entity crystal : mc.world.loadedEntityList) {
      if (crystal instanceof net.minecraft.entity.item.EntityEnderCrystal && 
        isValid(crystal)) {
        if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && crystal.getPosition().down().equals(this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
          this.minDmgCount++;
          this.crystalCount++;
          if (((Boolean)this.syncCount.getValue()).booleanValue()) {
            this.minDmgCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
            this.crystalCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
          } 
          if (((Boolean)this.hyperSync.getValue()).booleanValue()) {
            maxCrystal = null;
            break;
          } 
          continue;
        } 
        boolean count = false;
        boolean countMin = false;
        float selfDamage = 0.0F;
        if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue()))
          if (((Boolean)this.altPosition.getValue()).booleanValue()) {
            selfDamage = DamageUtil.calculateDamageAlt(crystal, (Entity)mc.player);
          } else {
            selfDamage = DamageUtil.calculateDamage(crystal, (Entity)mc.player);
          }  
        if (selfDamage + 0.5D < EntityUtil.getHealth((Entity)mc.player) || !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) {
          Entity beforeCrystal = maxCrystal;
          float beforeDamage = maxDamage;
          for (EntityPlayer player : mc.world.playerEntities) {
            if (player.getDistanceSq(crystal) <= MathUtil.square(((Float)this.range.getValue()).floatValue())) {
              if (EntityUtil.isValid((Entity)player, (((Float)this.range.getValue()).floatValue() + ((Float)this.breakRange.getValue()).floatValue()))) {
                float damage = DamageUtil.calculateDamage(crystal, (Entity)player);
                if (damage > selfDamage || (damage > ((Float)this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) || damage > EntityUtil.getHealth((Entity)player)) {
                  if (damage > maxDamage) {
                    maxDamage = damage;
                    maxCrystal = crystal;
                  } 
                  if (((Integer)this.packets.getValue()).intValue() == 1) {
                    if (damage >= ((Float)this.minDamage.getValue()).floatValue() || !((Boolean)this.wasteMinDmgCount.getValue()).booleanValue())
                      count = true; 
                    countMin = true;
                    continue;
                  } 
                  if (this.crystalMap.get(crystal) == null || ((Float)this.crystalMap.get(crystal)).floatValue() < damage)
                    this.crystalMap.put(crystal, Float.valueOf(damage)); 
                } 
                continue;
              } 
              if ((this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL) && Phobos.friendManager.isFriend(player.getName())) {
                float damage = DamageUtil.calculateDamage(crystal, (Entity)player);
                if (damage > EntityUtil.getHealth((Entity)player) + 0.5D) {
                  maxCrystal = beforeCrystal;
                  maxDamage = beforeDamage;
                  this.crystalMap.remove(crystal);
                  if (((Boolean)this.noCount.getValue()).booleanValue()) {
                    count = false;
                    countMin = false;
                  } 
                  break;
                } 
              } 
            } 
          } 
        } 
        if (countMin) {
          this.minDmgCount++;
          if (count)
            this.crystalCount++; 
        } 
      } 
    } 
    if (this.damageSync.getValue() == DamageSync.BREAK && (maxDamage > this.lastDamage || this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == DamageSync.NONE))
      this.lastDamage = maxDamage; 
    if (((Boolean)this.enormousSync.getValue()).booleanValue() && ((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
      if (((Boolean)this.syncCount.getValue()).booleanValue()) {
        this.minDmgCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
        this.crystalCount = ((Integer)this.wasteAmount.getValue()).intValue() + 1;
      } 
      return;
    } 
    if (((Boolean)this.webAttack.getValue()).booleanValue() && this.webPos != null)
      if (mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square(((Float)this.breakRange.getValue()).floatValue())) {
        this.webPos = null;
      } else {
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.webPos.up()))) {
          if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
            this.attackList.add(entity);
            this.efficientTarget = entity;
            this.webPos = null;
            this.lastDamage = 0.5D;
            return;
          } 
        } 
      }  
    if (((Boolean)this.manual.getValue()).booleanValue() && ((Boolean)this.manualMinDmg.getValue()).booleanValue() && mc.gameSettings.keyBindUseItem.isKeyDown() && ((this.offHand && mc.player.getActiveHand() == EnumHand.OFF_HAND) || (this.mainHand && mc.player.getActiveHand() == EnumHand.MAIN_HAND)) && maxDamage < ((Float)this.minDamage.getValue()).floatValue()) {
      this.efficientTarget = null;
      return;
    } 
    if (((Integer)this.packets.getValue()).intValue() == 1) {
      this.efficientTarget = maxCrystal;
    } else {
      this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
      for (Map.Entry<Entity, Float> entry : this.crystalMap.entrySet()) {
        Entity crystal = entry.getKey();
        float damage = ((Float)entry.getValue()).floatValue();
        if (damage >= ((Float)this.minDamage.getValue()).floatValue() || !((Boolean)this.wasteMinDmgCount.getValue()).booleanValue())
          this.crystalCount++; 
        this.attackList.add(crystal);
        this.minDmgCount++;
      } 
    } 
  }
  
  private void placeCrystal() {
    int crystalLimit = ((Integer)this.wasteAmount.getValue()).intValue();
    if (this.placeTimer.passedMs(((Integer)this.placeDelay.getValue()).intValue()) && ((Boolean)this.place.getValue()).booleanValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || (this.switchMode.getValue() == Switch.BREAKSLOT && this.switching))) {
      if ((this.offHand || this.mainHand || (this.switchMode.getValue() != Switch.ALWAYS && !this.switching)) && this.crystalCount >= crystalLimit && (!((Boolean)this.antiSurround.getValue()).booleanValue() || this.lastPos == null || !this.lastPos.equals(this.placePos)))
        return; 
      calculateDamage(getTarget((this.targetMode.getValue() == Target.UNSAFE)));
      if (target != null && this.placePos != null) {
        if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || (((Boolean)this.lethalSwitch.getValue()).booleanValue() && EntityUtil.getHealth((Entity)target) < ((Float)this.facePlace.getValue()).floatValue())) && !switchItem())
          return; 
        if (this.currentDamage < ((Float)this.minDamage.getValue()).floatValue() && ((Boolean)this.limitFacePlace.getValue()).booleanValue())
          crystalLimit = 1; 
        if ((this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || (((Boolean)this.antiSurround.getValue()).booleanValue() && this.lastPos != null && this.lastPos.equals(this.placePos))) && (this.currentDamage > ((Float)this.minDamage.getValue()).floatValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0D && (DamageUtil.isArmorLow(target, ((Integer)this.minArmor.getValue()).intValue()) || EntityUtil.getHealth((Entity)target) < ((Float)this.facePlace.getValue()).floatValue() || this.currentDamage > ((Float)this.minDamage.getValue()).floatValue())) {
          float damageOffset = (this.damageSync.getValue() == DamageSync.BREAK) ? (((Float)this.dropOff.getValue()).floatValue() - 5.0F) : 0.0F;
          boolean syncflag = false;
          if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && this.placePos.equals(this.lastPos) && isEligableForFeetSync(target, this.placePos) && !this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) && target.equals(this.currentSyncTarget) && target.getPosition().equals(this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
            this.syncedCrystalPos = this.placePos;
            this.lastDamage = this.currentDamage;
            if (((Boolean)this.fullSync.getValue()).booleanValue())
              this.lastDamage = 100.0D; 
            syncflag = true;
          } 
          if (syncflag || this.currentDamage - damageOffset > this.lastDamage || this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue()) || this.damageSync.getValue() == DamageSync.NONE) {
            if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK)
              this.lastDamage = this.currentDamage; 
            this.renderPos = this.placePos;
            this.renderDamage = this.currentDamage;
            if (switchItem()) {
              this.currentSyncTarget = target;
              this.syncedPlayerPos = target.getPosition();
              if (this.foundDoublePop)
                this.totemPops.put(target, (new Timer()).reset()); 
              rotateToPos(this.placePos);
              placedPos.add(this.placePos);
              BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
              this.lastPos = this.placePos;
              this.placeTimer.reset();
              this.posConfirmed = false;
              if (this.syncTimer.passedMs(((Integer)this.damageSyncTime.getValue()).intValue())) {
                this.syncedCrystalPos = null;
                this.syncTimer.reset();
              } 
            } 
          } 
        } 
      } else {
        this.renderPos = null;
      } 
    } 
  }
  
  private boolean switchItem() {
    if (this.offHand || this.mainHand)
      return true; 
    switch ((AutoSwitch)this.autoSwitch.getValue()) {
      case OFF:
        return false;
      case PLACE:
        if (!this.switching)
          return false; 
      case BREAK:
        if (doSwitch())
          return true; 
        break;
    } 
    return false;
  }
  
  private boolean doSwitch() {
    if (((Boolean)this.offhandSwitch.getValue()).booleanValue()) {
      Offhand module = (Offhand)Phobos.moduleManager.getModuleByClass(Offhand.class);
      if (module.isOff()) {
        Command.sendMessage("<" + getDisplayName() + "> " + "§c" + "Switch failed. Enable the Offhand module.");
        this.switching = false;
        return false;
      } 
      if (module.type.getValue() == Offhand.Type.NEW) {
        module.setSwapToTotem(false);
        module.setMode(Offhand.Mode.CRYSTALS);
        module.doOffhand();
      } else {
        module.setMode(Offhand.Mode2.CRYSTALS);
        module.doSwitch();
      } 
      this.switching = false;
      return true;
    } 
    if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
      this.mainHand = false;
    } else {
      InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
      this.mainHand = true;
    } 
    this.switching = false;
    return true;
  }
  
  private void calculateDamage(EntityPlayer targettedPlayer) {
    if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !((Boolean)this.fullCalc.getValue()).booleanValue())
      return; 
    float maxDamage = 0.5F;
    EntityPlayer currentTarget = null;
    BlockPos currentPos = null;
    float maxSelfDamage = 0.0F;
    this.foundDoublePop = false;
    BlockPos setToAir = null;
    IBlockState state = null;
    if (((Boolean)this.webAttack.getValue()).booleanValue() && targettedPlayer != null) {
      BlockPos playerPos = new BlockPos(targettedPlayer.getPositionVector());
      Block web = mc.world.getBlockState(playerPos).getBlock();
      if (web == Blocks.WEB) {
        setToAir = playerPos;
        state = mc.world.getBlockState(playerPos);
        mc.world.setBlockToAir(playerPos);
      } 
    } 
    for (BlockPos pos : BlockUtil.possiblePlacePositions(((Float)this.placeRange.getValue()).floatValue(), ((Boolean)this.antiSurround.getValue()).booleanValue(), ((Boolean)this.oneDot15.getValue()).booleanValue())) {
      if (BlockUtil.rayTracePlaceCheck(pos, ((this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && mc.player.getDistanceSq(pos) > MathUtil.square(((Float)this.placetrace.getValue()).floatValue())), 1.0F)) {
        float selfDamage = -1.0F;
        if (DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue()))
          selfDamage = DamageUtil.calculateDamage(pos, (Entity)mc.player); 
        if (selfDamage + 0.5D < EntityUtil.getHealth((Entity)mc.player)) {
          if (targettedPlayer != null) {
            float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
            if (((Boolean)this.calcEvenIfNoDamage.getValue()).booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)) {
              boolean friendPop = false;
              for (EntityPlayer friend : mc.world.playerEntities) {
                if (friend == null || mc.player.equals(friend) || friend.getDistanceSq(pos) > MathUtil.square((((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue())) || !Phobos.friendManager.isFriend(friend))
                  continue; 
                float friendDamage = DamageUtil.calculateDamage(pos, (Entity)friend);
                if (friendDamage > EntityUtil.getHealth((Entity)friend) + 0.5D) {
                  friendPop = true;
                  break;
                } 
              } 
              if (friendPop)
                continue; 
            } 
            if (isDoublePoppable(targettedPlayer, playerDamage) && (
              currentPos == null || targettedPlayer.getDistanceSq(pos) < targettedPlayer.getDistanceSq(currentPos))) {
              currentTarget = targettedPlayer;
              maxDamage = playerDamage;
              currentPos = pos;
              this.foundDoublePop = true;
              continue;
            } 
            if (this.foundDoublePop)
              continue; 
            if ((playerDamage > maxDamage || (((Boolean)this.extraSelfCalc.getValue()).booleanValue() && playerDamage >= maxDamage && selfDamage < maxSelfDamage)) && (playerDamage > selfDamage || (playerDamage > ((Float)this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) || playerDamage > EntityUtil.getHealth((Entity)targettedPlayer))) {
              maxDamage = playerDamage;
              currentTarget = targettedPlayer;
              currentPos = pos;
              maxSelfDamage = selfDamage;
            } 
            continue;
          } 
          float maxDamageBefore = maxDamage;
          EntityPlayer currentTargetBefore = currentTarget;
          BlockPos currentPosBefore = currentPos;
          float maxSelfDamageBefore = maxSelfDamage;
          for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isValid((Entity)player, (((Float)this.placeRange.getValue()).floatValue() + ((Float)this.range.getValue()).floatValue()))) {
              float playerDamage = DamageUtil.calculateDamage(pos, (Entity)player);
              if (((Boolean)this.doublePopOnDamage.getValue()).booleanValue() && isDoublePoppable(player, playerDamage) && (
                currentPos == null || player.getDistanceSq(pos) < player.getDistanceSq(currentPos))) {
                currentTarget = player;
                maxDamage = playerDamage;
                currentPos = pos;
                maxSelfDamage = selfDamage;
                this.foundDoublePop = true;
                if (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)
                  break; 
                continue;
              } 
              if (this.foundDoublePop)
                continue; 
              if ((playerDamage > maxDamage || (((Boolean)this.extraSelfCalc.getValue()).booleanValue() && playerDamage >= maxDamage && selfDamage < maxSelfDamage)) && (playerDamage > selfDamage || (playerDamage > ((Float)this.minDamage.getValue()).floatValue() && !DamageUtil.canTakeDamage(((Boolean)this.suicide.getValue()).booleanValue())) || playerDamage > EntityUtil.getHealth((Entity)player))) {
                maxDamage = playerDamage;
                currentTarget = player;
                currentPos = pos;
                maxSelfDamage = selfDamage;
              } 
              continue;
            } 
            if ((this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE) && player != null && player.getDistanceSq(pos) <= MathUtil.square((((Float)this.range.getValue()).floatValue() + ((Float)this.placeRange.getValue()).floatValue())) && Phobos.friendManager.isFriend(player)) {
              float friendDamage = DamageUtil.calculateDamage(pos, (Entity)player);
              if (friendDamage > EntityUtil.getHealth((Entity)player) + 0.5D) {
                maxDamage = maxDamageBefore;
                currentTarget = currentTargetBefore;
                currentPos = currentPosBefore;
                maxSelfDamage = maxSelfDamageBefore;
              } 
            } 
          } 
        } 
      } 
    } 
    if (setToAir != null) {
      mc.world.setBlockState(setToAir, state);
      this.webPos = currentPos;
    } 
    target = currentTarget;
    this.currentDamage = maxDamage;
    this.placePos = currentPos;
  }
  
  private EntityPlayer getTarget(boolean unsafe) {
    if (this.targetMode.getValue() == Target.DAMAGE)
      return null; 
    EntityPlayer currentTarget = null;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, (((Float)this.placeRange.getValue()).floatValue() + ((Float)this.range.getValue()).floatValue())))
        continue; 
      if (unsafe && EntityUtil.isSafe((Entity)player))
        continue; 
      if (((Integer)this.minArmor.getValue()).intValue() > 0 && DamageUtil.isArmorLow(player, ((Integer)this.minArmor.getValue()).intValue())) {
        currentTarget = player;
        break;
      } 
      if (currentTarget == null) {
        currentTarget = player;
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) < mc.player.getDistanceSq((Entity)currentTarget))
        currentTarget = player; 
    } 
    if (unsafe && currentTarget == null)
      return getTarget(false); 
    return currentTarget;
  }
  
  private void breakCrystal() {
    if (((Boolean)this.explode.getValue()).booleanValue() && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
      if (((Integer)this.packets.getValue()).intValue() == 1 && this.efficientTarget != null) {
        if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean)this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE)
          return; 
        rotateTo(this.efficientTarget);
        EntityUtil.attackEntity(this.efficientTarget, ((Boolean)this.sync.getValue()).booleanValue(), true);
        brokenPos.add((new BlockPos(this.efficientTarget.getPositionVector())).down());
      } else if (!this.attackList.isEmpty()) {
        if (((Boolean)this.syncedFeetPlace.getValue()).booleanValue() && ((Boolean)this.gigaSync.getValue()).booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE)
          return; 
        for (int i = 0; i < ((Integer)this.packets.getValue()).intValue(); i++) {
          Entity entity = this.attackList.poll();
          if (entity != null) {
            rotateTo(entity);
            EntityUtil.attackEntity(entity, ((Boolean)this.sync.getValue()).booleanValue(), true);
            brokenPos.add((new BlockPos(entity.getPositionVector())).down());
          } 
        } 
      } 
      this.breakTimer.reset();
    } 
  }
  
  private void manualBreaker() {
    if (this.rotate.getValue() != Rotate.OFF && ((Integer)this.eventMode.getValue()).intValue() != 2 && this.rotating)
      if (this.didRotation) {
        mc.player.rotationPitch = (float)(mc.player.rotationPitch + 4.0E-4D);
        this.didRotation = false;
      } else {
        mc.player.rotationPitch = (float)(mc.player.rotationPitch - 4.0E-4D);
        this.didRotation = true;
      }  
    if ((this.offHand || this.mainHand) && ((Boolean)this.manual.getValue()).booleanValue() && this.manualTimer.passedMs(((Integer)this.manualBreak.getValue()).intValue()) && mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && mc.player.inventory.getCurrentItem().getItem() != Items.BOW && mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE) {
      RayTraceResult result = mc.objectMouseOver;
      if (result != null) {
        Entity entity;
        BlockPos mousePos;
        switch (result.typeOfHit) {
          case OFF:
            entity = result.entityHit;
            if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal) {
              EntityUtil.attackEntity(entity, ((Boolean)this.sync.getValue()).booleanValue(), true);
              this.manualTimer.reset();
            } 
            break;
          case PLACE:
            mousePos = mc.objectMouseOver.getBlockPos().up();
            for (Entity target : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(mousePos))) {
              if (target instanceof net.minecraft.entity.item.EntityEnderCrystal) {
                EntityUtil.attackEntity(target, ((Boolean)this.sync.getValue()).booleanValue(), true);
                this.manualTimer.reset();
              } 
            } 
            break;
        } 
      } 
    } 
  }
  
  private void rotateTo(Entity entity) {
    float[] angle;
    switch ((Rotate)this.rotate.getValue()) {
      case OFF:
        this.rotating = false;
        break;
      case BREAK:
      case ALL:
        angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
        if (((Integer)this.eventMode.getValue()).intValue() == 2) {
          Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
          break;
        } 
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.rotating = true;
        break;
    } 
  }
  
  private void rotateToPos(BlockPos pos) {
    float[] angle;
    switch ((Rotate)this.rotate.getValue()) {
      case OFF:
        this.rotating = false;
        break;
      case PLACE:
      case ALL:
        angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
        if (((Integer)this.eventMode.getValue()).intValue() == 2) {
          Phobos.rotationManager.setPlayerRotations(angle[0], angle[1]);
          break;
        } 
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.rotating = true;
        break;
    } 
  }
  
  private boolean isDoublePoppable(EntityPlayer player, float damage) {
    if (((Boolean)this.doublePop.getValue()).booleanValue()) {
      float health = EntityUtil.getHealth((Entity)player);
      if (health <= 1.0D && damage > health + 0.5D && damage <= ((Float)this.popDamage.getValue()).floatValue()) {
        Timer timer = this.totemPops.get(player);
        return (timer == null || timer.passedMs(((Integer)this.popTime.getValue()).intValue()));
      } 
    } 
    return false;
  }
  
  private boolean isValid(Entity entity) {
    return (entity != null && mc.player.getDistanceSq(entity) <= MathUtil.square(((Float)this.breakRange.getValue()).floatValue()) && (this.raytrace
      .getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || mc.player
      .canEntityBeSeen(entity) || (
      !mc.player.canEntityBeSeen(entity) && mc.player.getDistanceSq(entity) <= MathUtil.square(((Float)this.breaktrace.getValue()).floatValue()))));
  }
  
  private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
    if (((Boolean)this.holySync.getValue()).booleanValue()) {
      BlockPos playerPos = new BlockPos(player.getPositionVector());
      for (EnumFacing facing : EnumFacing.values()) {
        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
          BlockPos holyPos = playerPos.down().offset(facing);
          if (pos.equals(holyPos))
            return true; 
        } 
      } 
      return false;
    } 
    return true;
  }
  
  public enum Settings {
    PLACE, BREAK, RENDER, MISC, DEV;
  }
  
  public enum DamageSync {
    NONE, PLACE, BREAK;
  }
  
  public enum Rotate {
    OFF, PLACE, BREAK, ALL;
  }
  
  public enum Target {
    CLOSEST, UNSAFE, DAMAGE;
  }
  
  public enum Logic {
    BREAKPLACE, PLACEBREAK;
  }
  
  public enum Switch {
    ALWAYS, BREAKSLOT, CALC;
  }
  
  public enum Raytrace {
    NONE, PLACE, BREAK, FULL;
  }
  
  public enum AutoSwitch {
    NONE, TOGGLE, ALWAYS;
  }
  
  public enum ThreadMode {
    NONE, WHILE, POOL;
  }
  
  public enum AntiFriendPop {
    NONE, PLACE, BREAK, ALL;
  }
}
