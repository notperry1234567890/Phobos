//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGG extends Module {
  private final Setting<Boolean> greentext = register(new Setting("Greentext", Boolean.valueOf(false)));
  
  private final Setting<Boolean> loadFiles = register(new Setting("LoadFiles", Boolean.valueOf(false)));
  
  private final Setting<Integer> targetResetTimer = register(new Setting("Reset", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(90)));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(30)));
  
  public Map<EntityPlayer, Integer> targets = new HashMap<>();
  
  public List<String> messages = new ArrayList<>();
  
  public EntityPlayer attackedPlayer;
  
  private static final String path = "phobos/autogg.txt";
  
  private Timer timer;
  
  private Timer cooldownTimer;
  
  private boolean cooldown;
  
  public AutoGG() {
    super("AutoGG", "Automatically GGs", Module.Category.MISC, true, false, false);
  }
  
  public void onEnable() {
    this.timer = new Timer();
    this.cooldownTimer = new Timer();
  }
  
  public void onTick() {
    if (((Boolean)this.loadFiles.getValue()).booleanValue()) {
      loadMessages();
      Command.sendMessage("<AutoGG> Loaded messages.");
      this.loadFiles.setValue(Boolean.valueOf(false));
    } 
    if (this.cooldownTimer.passedS(((Integer)this.delay.getValue()).intValue()) && this.cooldown) {
      this.cooldown = false;
      this.cooldownTimer.reset();
    } 
    if (this.timer.passedS(((Integer)this.targetResetTimer.getValue()).intValue())) {
      this.attackedPlayer = null;
      this.timer.reset();
    } 
    if (AutoCrystal.target != null)
      this.targets.put(AutoCrystal.target, Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L))); 
    this.targets.replaceAll((p, v) -> Integer.valueOf((int)(this.timer.getPassedTimeMs() / 1000L)));
    for (EntityPlayer player : this.targets.keySet()) {
      if (((Integer)this.targets.get(player)).intValue() > ((Integer)this.targetResetTimer.getValue()).intValue())
        this.targets.remove(player); 
    } 
  }
  
  @SubscribeEvent
  public void onEntityDeath(LivingDeathEvent event) {
    if (event.getEntity() instanceof EntityPlayer && (this.targets.containsKey(event.getEntity()) || event.getEntity() == this.attackedPlayer) && !this.cooldown) {
      announceDeath((EntityPlayer)event.getEntity());
      this.cooldown = true;
    } 
  }
  
  @SubscribeEvent
  public void onAttackEntity(AttackEntityEvent event) {
    if (event.getTarget() instanceof EntityPlayer && !Phobos.friendManager.isFriend(event.getEntityPlayer()))
      this.attackedPlayer = (EntityPlayer)event.getTarget(); 
  }
  
  @SubscribeEvent
  public void onSendAttackPacket(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketUseEntity) {
      CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
      if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)mc.world) instanceof EntityPlayer && !Phobos.friendManager.isFriend((EntityPlayer)packet.getEntityFromWorld((World)mc.world)))
        this.attackedPlayer = (EntityPlayer)packet.getEntityFromWorld((World)mc.world); 
    } 
  }
  
  public void loadMessages() {
    this.messages = FileManager.readTextFileAllLines("phobos/autogg.txt");
  }
  
  public String getRandomMessage() {
    Random rand = new Random();
    return this.messages.get(rand.nextInt(this.messages.size() - 1));
  }
  
  public void announceDeath(EntityPlayer target) {
    mc.player.connection.sendPacket((Packet)new CPacketChatMessage((((Boolean)this.greentext.getValue()).booleanValue() ? ">" : "") + getRandomMessage().replaceAll("<player>", target.getDisplayNameString())));
  }
}
