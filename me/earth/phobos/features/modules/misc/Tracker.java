//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import me.earth.phobos.event.events.ConnectionEvent;
import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AntiTrap;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.TextUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tracker extends Module {
  public Setting<TextUtil.Color> color = register(new Setting("Color", TextUtil.Color.RED));
  
  public Setting<Boolean> autoEnable = register(new Setting("AutoEnable", Boolean.valueOf(false)));
  
  public Setting<Boolean> autoDisable = register(new Setting("AutoDisable", Boolean.valueOf(true)));
  
  private EntityPlayer trackedPlayer;
  
  private static Tracker instance;
  
  private int usedExp = 0;
  
  private int usedStacks = 0;
  
  private int usedCrystals = 0;
  
  private int usedCStacks = 0;
  
  private boolean shouldEnable = false;
  
  private final Timer timer = new Timer();
  
  private final Set<BlockPos> manuallyPlaced = new HashSet<>();
  
  public Tracker() {
    super("Tracker", "Tracks players in 1v1s. Only good in duels tho!", Module.Category.MISC, true, false, true);
    instance = this;
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (!fullNullCheck() && (((Boolean)this.autoEnable.getValue()).booleanValue() || ((Boolean)this.autoDisable.getValue()).booleanValue()) && 
      event.getPacket() instanceof SPacketChat) {
      SPacketChat packet = (SPacketChat)event.getPacket();
      String message = packet.getChatComponent().getFormattedText();
      if (((Boolean)this.autoEnable.getValue()).booleanValue() && (message.contains("has accepted your duel request") || message.contains("Accepted the duel request from")) && !message.contains("<")) {
        Command.sendMessage("Tracker will enable in 5 seconds.");
        this.timer.reset();
        this.shouldEnable = true;
      } else if (((Boolean)this.autoDisable.getValue()).booleanValue() && message.contains("has defeated") && message.contains(mc.player.getName()) && !message.contains("<")) {
        disable();
      } 
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (!fullNullCheck() && isOn() && 
      event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
      CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
      if (mc.player.getHeldItem(packet.hand).getItem() == Items.END_CRYSTAL && 
        !AntiTrap.placedPos.contains(packet.position) && !AutoCrystal.placedPos.contains(packet.position))
        this.manuallyPlaced.add(packet.position); 
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (this.shouldEnable && this.timer.passedS(5.0D) && isOff())
      enable(); 
  }
  
  public void onUpdate() {
    if (isOff())
      return; 
    if (this.trackedPlayer == null) {
      this.trackedPlayer = EntityUtil.getClosestEnemy(1000.0D);
    } else {
      if (this.usedStacks != this.usedExp / 64) {
        this.usedStacks = this.usedExp / 64;
        Command.sendMessage(TextUtil.coloredString(this.trackedPlayer.getName() + " used: " + this.usedStacks + " Stacks of EXP.", (TextUtil.Color)this.color.getValue()));
      } 
      if (this.usedCStacks != this.usedCrystals / 64) {
        this.usedCStacks = this.usedCrystals / 64;
        Command.sendMessage(TextUtil.coloredString(this.trackedPlayer.getName() + " used: " + this.usedCStacks + " Stacks of Crystals.", (TextUtil.Color)this.color.getValue()));
      } 
    } 
  }
  
  public void onSpawnEntity(Entity entity) {
    if (isOff())
      return; 
    if (entity instanceof net.minecraft.entity.item.EntityExpBottle && 
      Objects.equals(mc.world.getClosestPlayerToEntity(entity, 3.0D), this.trackedPlayer))
      this.usedExp++; 
    if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal)
      if (AntiTrap.placedPos.contains(entity.getPosition().down())) {
        AntiTrap.placedPos.remove(entity.getPosition().down());
      } else if (this.manuallyPlaced.contains(entity.getPosition().down())) {
        this.manuallyPlaced.remove(entity.getPosition().down());
      } else if (!AutoCrystal.placedPos.contains(entity.getPosition().down())) {
        this.usedCrystals++;
      }  
  }
  
  @SubscribeEvent
  public void onConnection(ConnectionEvent event) {
    if (isOff() || event.getStage() != 1)
      return; 
    String name = event.getName();
    if (this.trackedPlayer != null && name != null && name.equals(this.trackedPlayer.getName()) && ((Boolean)this.autoDisable.getValue()).booleanValue()) {
      Command.sendMessage(name + " logged, Tracker disableing.");
      disable();
    } 
  }
  
  public void onToggle() {
    this.manuallyPlaced.clear();
    AntiTrap.placedPos.clear();
    this.shouldEnable = false;
    this.trackedPlayer = null;
    this.usedExp = 0;
    this.usedStacks = 0;
    this.usedCrystals = 0;
    this.usedCStacks = 0;
  }
  
  public void onLogout() {
    if (((Boolean)this.autoDisable.getValue()).booleanValue())
      disable(); 
  }
  
  @SubscribeEvent
  public void onDeath(DeathEvent event) {
    if (isOn() && (event.player.equals(this.trackedPlayer) || event.player.equals(mc.player))) {
      this.usedExp = 0;
      this.usedStacks = 0;
      this.usedCrystals = 0;
      this.usedCStacks = 0;
      if (((Boolean)this.autoDisable.getValue()).booleanValue())
        disable(); 
    } 
  }
  
  public String getDisplayInfo() {
    if (this.trackedPlayer != null)
      return this.trackedPlayer.getName(); 
    return null;
  }
  
  public static Tracker getInstance() {
    if (instance == null)
      instance = new Tracker(); 
    return instance;
  }
}
