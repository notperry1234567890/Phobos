//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.combat;

import java.util.Objects;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Criticals extends Module {
  private Setting<Mode> mode = register(new Setting("Mode", Mode.PACKET));
  
  public Setting<Boolean> noDesync = register(new Setting("NoDesync", Boolean.valueOf(true)));
  
  private Setting<Integer> packets = register(new Setting("Packets", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> (this.mode.getValue() == Mode.PACKET), "Amount of packets you want to send."));
  
  private Setting<Integer> desyncDelay = register(new Setting("DesyncDelay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(500), v -> (this.mode.getValue() == Mode.PACKET), "Amount of packets you want to send."));
  
  public Setting<Boolean> cancelFirst = register(new Setting("CancelFirst32k", Boolean.valueOf(true)));
  
  public Setting<Integer> delay32k = register(new Setting("32kDelay", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(500), v -> ((Boolean)this.cancelFirst.getValue()).booleanValue()));
  
  private Timer timer = new Timer();
  
  private Timer timer32k = new Timer();
  
  private boolean firstCanceled = false;
  
  private boolean resetTimer = false;
  
  public Criticals() {
    super("Criticals", "Scores criticals for you", Module.Category.COMBAT, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (Auto32k.getInstance().isOn() && ((Auto32k.getInstance()).switching || !((Boolean)(Auto32k.getInstance()).autoSwitch.getValue()).booleanValue() || (Auto32k.getInstance()).mode.getValue() == Auto32k.Mode.DISPENSER) && this.timer.passedMs(500L) && ((Boolean)this.cancelFirst.getValue()).booleanValue()) {
      this.firstCanceled = true;
    } else if (Auto32k.getInstance().isOff() || (!(Auto32k.getInstance()).switching && ((Boolean)(Auto32k.getInstance()).autoSwitch.getValue()).booleanValue() && (Auto32k.getInstance()).mode.getValue() != Auto32k.Mode.DISPENSER) || !((Boolean)this.cancelFirst.getValue()).booleanValue()) {
      this.firstCanceled = false;
    } 
    if (event.getPacket() instanceof CPacketUseEntity) {
      CPacketUseEntity packet = (CPacketUseEntity)event.getPacket();
      if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
        if (this.firstCanceled) {
          this.timer32k.reset();
          this.resetTimer = true;
          this.timer.setMs((((Integer)this.desyncDelay.getValue()).intValue() + 1));
          this.firstCanceled = false;
          return;
        } 
        if (this.resetTimer && !this.timer32k.passedMs(((Integer)this.delay32k.getValue()).intValue()))
          return; 
        if (this.resetTimer && this.timer32k.passedMs(((Integer)this.delay32k.getValue()).intValue()))
          this.resetTimer = false; 
        if (!this.timer.passedMs(((Integer)this.desyncDelay.getValue()).intValue()))
          return; 
        if (mc.player.onGround && !mc.gameSettings.keyBindJump.isKeyDown() && (packet.getEntityFromWorld((World)mc.world) instanceof net.minecraft.entity.EntityLivingBase || !((Boolean)this.noDesync.getValue()).booleanValue()) && !mc.player.isInWater() && !mc.player.isInLava()) {
          if (this.mode.getValue() == Mode.PACKET) {
            switch (((Integer)this.packets.getValue()).intValue()) {
              case 1:
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.10000000149011612D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
              case 2:
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
              case 3:
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625101D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0125D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
              case 4:
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.05D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.03D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                break;
              case 5:
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1625D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 4.0E-6D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-6D, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                mc.player.connection.sendPacket((Packet)new CPacketPlayer());
                mc.player.onCriticalHit(Objects.<Entity>requireNonNull(packet.getEntityFromWorld((World)mc.world)));
                break;
            } 
          } else if (this.mode.getValue() == Mode.BYPASS) {
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11D, mc.player.posZ, false));
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.11D, mc.player.posZ, false));
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579D, mc.player.posZ, false));
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579D, mc.player.posZ, false));
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579D, mc.player.posZ, false));
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1100013579D, mc.player.posZ, false));
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
          } else {
            mc.player.jump();
            if (this.mode.getValue() == Mode.MINIJUMP)
              mc.player.motionY /= 2.0D; 
          } 
          this.timer.reset();
        } 
      } 
    } 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public enum Mode {
    JUMP, MINIJUMP, PACKET, BYPASS;
  }
}
