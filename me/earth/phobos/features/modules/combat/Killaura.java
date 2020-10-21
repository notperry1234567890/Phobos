//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura extends Module {
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F)));
  
  private Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.CLOSEST));
  
  public Setting<Float> health = register(new Setting("Health", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.targetMode.getValue() == TargetMode.SMART)));
  
  public Setting<Boolean> delay = register(new Setting("Delay", Boolean.valueOf(true)));
  
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  public Setting<Boolean> armorBreak = register(new Setting("ArmorBreak", Boolean.valueOf(false)));
  
  public Setting<Boolean> eating = register(new Setting("Eating", Boolean.valueOf(true)));
  
  public Setting<Boolean> onlySharp = register(new Setting("Axe/Sword", Boolean.valueOf(true)));
  
  public Setting<Boolean> teleport = register(new Setting("Teleport", Boolean.valueOf(false)));
  
  public Setting<Float> raytrace = register(new Setting("Raytrace", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), v -> !((Boolean)this.teleport.getValue()).booleanValue(), "Wall Range."));
  
  public Setting<Float> teleportRange = register(new Setting("TpRange", Float.valueOf(15.0F), Float.valueOf(0.1F), Float.valueOf(50.0F), v -> ((Boolean)this.teleport.getValue()).booleanValue(), "Teleport Range."));
  
  public Setting<Boolean> lagBack = register(new Setting("LagBack", Boolean.valueOf(true), v -> ((Boolean)this.teleport.getValue()).booleanValue()));
  
  public Setting<Boolean> teekaydelay = register(new Setting("32kDelay", Boolean.valueOf(false)));
  
  public Setting<Integer> time32k = register(new Setting("32kTime", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(50)));
  
  public Setting<Integer> multi = register(new Setting("32kPackets", Integer.valueOf(2), v -> !((Boolean)this.teekaydelay.getValue()).booleanValue()));
  
  public Setting<Boolean> multi32k = register(new Setting("Multi32k", Boolean.valueOf(false)));
  
  public Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
  
  public Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
  
  public Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
  
  public Setting<Boolean> vehicles = register(new Setting("Entities", Boolean.valueOf(false)));
  
  public Setting<Boolean> projectiles = register(new Setting("Projectiles", Boolean.valueOf(false)));
  
  public Setting<Boolean> tps = register(new Setting("TpsSync", Boolean.valueOf(true)));
  
  public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
  
  public Setting<Boolean> swing = register(new Setting("Swing", Boolean.valueOf(true)));
  
  public Setting<Boolean> sneak = register(new Setting("State", Boolean.valueOf(false)));
  
  public Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(true)));
  
  private final Timer timer = new Timer();
  
  public static Entity target;
  
  public Killaura() {
    super("Killaura", "Kills aura.", Module.Category.COMBAT, true, false, false);
  }
  
  public void onTick() {
    if (!((Boolean)this.rotate.getValue()).booleanValue())
      doKillaura(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Boolean)this.rotate.getValue()).booleanValue())
      doKillaura(); 
  }
  
  private void doKillaura() {
    if (((Boolean)this.onlySharp.getValue()).booleanValue() && !EntityUtil.holdingWeapon((EntityPlayer)mc.player)) {
      target = null;
      return;
    } 
    int wait = (!((Boolean)this.delay.getValue()).booleanValue() || (EntityUtil.holding32k((EntityPlayer)mc.player) && !((Boolean)this.teekaydelay.getValue()).booleanValue())) ? 0 : (int)(DamageUtil.getCooldownByWeapon((EntityPlayer)mc.player) * (((Boolean)this.tps.getValue()).booleanValue() ? Phobos.serverManager.getTpsFactor() : 1.0F));
    if (!this.timer.passedMs(wait) || (!((Boolean)this.eating.getValue()).booleanValue() && mc.player.isHandActive() && (!mc.player.getHeldItemOffhand().getItem().equals(Items.SHIELD) || mc.player.getActiveHand() != EnumHand.OFF_HAND)))
      return; 
    if (this.targetMode.getValue() != TargetMode.FOCUS || target == null || (mc.player.getDistanceSq(target) >= MathUtil.square(((Float)this.range.getValue()).floatValue()) && (!((Boolean)this.teleport.getValue()).booleanValue() || mc.player.getDistanceSq(target) >= MathUtil.square(((Float)this.teleportRange.getValue()).floatValue()))) || (!mc.player.canEntityBeSeen(target) && !EntityUtil.canEntityFeetBeSeen(target) && mc.player.getDistanceSq(target) >= MathUtil.square(((Float)this.raytrace.getValue()).floatValue()) && !((Boolean)this.teleport.getValue()).booleanValue()))
      target = getTarget(); 
    if (target == null)
      return; 
    if (((Boolean)this.rotate.getValue()).booleanValue())
      Phobos.rotationManager.lookAtEntity(target); 
    if (((Boolean)this.teleport.getValue()).booleanValue())
      Phobos.positionManager.setPositionPacket(target.posX, EntityUtil.canEntityFeetBeSeen(target) ? target.posY : (target.posY + target.getEyeHeight()), target.posZ, true, true, !((Boolean)this.lagBack.getValue()).booleanValue()); 
    if (EntityUtil.holding32k((EntityPlayer)mc.player) && !((Boolean)this.teekaydelay.getValue()).booleanValue()) {
      if (((Boolean)this.multi32k.getValue()).booleanValue()) {
        for (EntityPlayer player : mc.world.playerEntities) {
          if (EntityUtil.isValid((Entity)player, ((Float)this.range.getValue()).floatValue()))
            teekayAttack((Entity)player); 
        } 
      } else {
        teekayAttack(target);
      } 
      this.timer.reset();
      return;
    } 
    if (((Boolean)this.armorBreak.getValue()).booleanValue()) {
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)mc.player);
      EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), ((Boolean)this.swing.getValue()).booleanValue());
      mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, mc.player.inventory.currentItem, ClickType.SWAP, (EntityPlayer)mc.player);
      EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), ((Boolean)this.swing.getValue()).booleanValue());
    } else {
      boolean sneaking = mc.player.isSneaking();
      boolean sprint = mc.player.isSprinting();
      if (((Boolean)this.sneak.getValue()).booleanValue()) {
        if (sneaking)
          mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
        if (sprint)
          mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SPRINTING)); 
      } 
      EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), ((Boolean)this.swing.getValue()).booleanValue());
      if (((Boolean)this.sneak.getValue()).booleanValue()) {
        if (sprint)
          mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SPRINTING)); 
        if (sneaking)
          mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING)); 
      } 
    } 
    this.timer.reset();
  }
  
  private void teekayAttack(Entity entity) {
    for (int i = 0; i < ((Integer)this.multi.getValue()).intValue(); i++)
      startEntityAttackThread(entity, i * ((Integer)this.time32k.getValue()).intValue()); 
  }
  
  private void startEntityAttackThread(Entity entity, int time) {
    (new Thread(() -> {
          Timer timer = new Timer();
          timer.reset();
          try {
            Thread.sleep(time);
          } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
          } 
          EntityUtil.attackEntity(entity, true, ((Boolean)this.swing.getValue()).booleanValue());
        })).start();
  }
  
  private Entity getTarget() {
    Entity target = null;
    double distance = ((Boolean)this.teleport.getValue()).booleanValue() ? ((Float)this.teleportRange.getValue()).floatValue() : ((Float)this.range.getValue()).floatValue();
    double maxHealth = 36.0D;
    for (Entity entity : mc.world.loadedEntityList) {
      if (((!((Boolean)this.players.getValue()).booleanValue() || !(entity instanceof EntityPlayer)) && (!((Boolean)this.animals.getValue()).booleanValue() || !EntityUtil.isPassive(entity)) && (!((Boolean)this.mobs.getValue()).booleanValue() || !EntityUtil.isMobAggressive(entity)) && (!((Boolean)this.vehicles.getValue()).booleanValue() || !EntityUtil.isVehicle(entity)) && (!((Boolean)this.projectiles.getValue()).booleanValue() || !EntityUtil.isProjectile(entity))) || (
        entity instanceof net.minecraft.entity.EntityLivingBase && EntityUtil.isntValid(entity, distance)))
        continue; 
      if (!((Boolean)this.teleport.getValue()).booleanValue() && !mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && mc.player.getDistanceSq(entity) > MathUtil.square(((Float)this.raytrace.getValue()).floatValue()))
        continue; 
      if (target == null) {
        target = entity;
        distance = mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
        continue;
      } 
      if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer)entity, 18)) {
        target = entity;
        break;
      } 
      if (this.targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < ((Float)this.health.getValue()).floatValue()) {
        target = entity;
        break;
      } 
      if (this.targetMode.getValue() != TargetMode.HEALTH && mc.player.getDistanceSq(entity) < distance) {
        target = entity;
        distance = mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
      } 
      if (this.targetMode.getValue() == TargetMode.HEALTH && EntityUtil.getHealth(entity) < maxHealth) {
        target = entity;
        distance = mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
      } 
    } 
    return target;
  }
  
  public String getDisplayInfo() {
    if (((Boolean)this.info.getValue()).booleanValue() && target instanceof EntityPlayer)
      return target.getName(); 
    return null;
  }
  
  public enum TargetMode {
    FOCUS, CLOSEST, HEALTH, SMART;
  }
}
