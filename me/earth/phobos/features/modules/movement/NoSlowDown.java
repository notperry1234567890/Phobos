//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.KeyEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class NoSlowDown extends Module {
  public Setting<Boolean> guiMove = register(new Setting("GuiMove", Boolean.valueOf(true)));
  
  public Setting<Boolean> noSlow = register(new Setting("NoSlow", Boolean.valueOf(true)));
  
  public Setting<Boolean> soulSand = register(new Setting("SoulSand", Boolean.valueOf(true)));
  
  public Setting<Boolean> strict = register(new Setting("Strict", Boolean.valueOf(false)));
  
  public Setting<Boolean> webs = register(new Setting("Webs", Boolean.valueOf(false)));
  
  public final Setting<Double> webHorizontalFactor = register(new Setting("WebHSpeed", Double.valueOf(2.0D), Double.valueOf(0.0D), Double.valueOf(100.0D)));
  
  public final Setting<Double> webVerticalFactor = register(new Setting("WebVSpeed", Double.valueOf(2.0D), Double.valueOf(0.0D), Double.valueOf(100.0D)));
  
  private static NoSlowDown INSTANCE = new NoSlowDown();
  
  private static KeyBinding[] keys = new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint };
  
  public NoSlowDown() {
    super("NoSlowDown", "Prevents you from getting slowed down.", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static NoSlowDown getInstance() {
    if (INSTANCE == null)
      INSTANCE = new NoSlowDown(); 
    return INSTANCE;
  }
  
  public void onUpdate() {
    if (((Boolean)this.guiMove.getValue()).booleanValue())
      if (mc.currentScreen instanceof net.minecraft.client.gui.GuiOptions || mc.currentScreen instanceof net.minecraft.client.gui.GuiVideoSettings || mc.currentScreen instanceof net.minecraft.client.gui.GuiScreenOptionsSounds || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer || mc.currentScreen instanceof net.minecraft.client.gui.GuiIngameMenu) {
        for (KeyBinding bind : keys)
          KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode())); 
      } else if (mc.currentScreen == null) {
        for (KeyBinding bind : keys) {
          if (!Keyboard.isKeyDown(bind.getKeyCode()))
            KeyBinding.setKeyBindState(bind.getKeyCode(), false); 
        } 
      }  
    if (((Boolean)this.webs.getValue()).booleanValue() && ((Flight)Phobos.moduleManager.getModuleByClass(Flight.class)).isDisabled() && ((Phase)Phobos.moduleManager.getModuleByClass(Phase.class)).isDisabled() && 
      mc.player.isInWeb) {
      mc.player.motionX *= ((Double)this.webHorizontalFactor.getValue()).doubleValue();
      mc.player.motionZ *= ((Double)this.webHorizontalFactor.getValue()).doubleValue();
      mc.player.motionY *= ((Double)this.webVerticalFactor.getValue()).doubleValue();
    } 
  }
  
  @SubscribeEvent
  public void onInput(InputUpdateEvent event) {
    if (((Boolean)this.noSlow.getValue()).booleanValue() && mc.player.isHandActive() && !mc.player.isRiding()) {
      (event.getMovementInput()).moveStrafe *= 5.0F;
      (event.getMovementInput()).moveForward *= 5.0F;
    } 
  }
  
  @SubscribeEvent
  public void onKeyEvent(KeyEvent event) {
    if (((Boolean)this.guiMove.getValue()).booleanValue() && event.getStage() == 0 && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiChat))
      event.info = event.pressed; 
  }
  
  @SubscribeEvent
  public void onPacket(PacketEvent.Send event) {
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer && ((Boolean)this.strict.getValue()).booleanValue() && ((Boolean)this.noSlow.getValue()).booleanValue() && mc.player.isHandActive() && !mc.player.isRiding())
      mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ)), EnumFacing.DOWN)); 
  }
}
