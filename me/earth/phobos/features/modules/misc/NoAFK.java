//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.util.Random;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;

public class NoAFK extends Module {
  private final Setting<Boolean> swing;
  
  private final Setting<Boolean> turn;
  
  private final Random random;
  
  public NoAFK() {
    super("NoAFK", "Prevents you from getting kicked for afk.", Module.Category.MISC, false, false, false);
    this.swing = register(new Setting("Swing", Boolean.valueOf(true)));
    this.turn = register(new Setting("Turn", Boolean.valueOf(true)));
    this.random = new Random();
  }
  
  public void onUpdate() {
    if (mc.playerController.getIsHittingBlock())
      return; 
    if (mc.player.ticksExisted % 40 == 0 && ((Boolean)this.swing.getValue()).booleanValue())
      mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND)); 
    if (mc.player.ticksExisted % 15 == 0 && ((Boolean)this.turn.getValue()).booleanValue())
      mc.player.rotationYaw = (this.random.nextInt(360) - 180); 
    if (!((Boolean)this.swing.getValue()).booleanValue() && !((Boolean)this.turn.getValue()).booleanValue() && mc.player.ticksExisted % 80 == 0)
      mc.player.jump(); 
  }
}
