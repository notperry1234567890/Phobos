//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoReconnect extends Module {
  private static ServerData serverData;
  
  private static AutoReconnect INSTANCE = new AutoReconnect();
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(5)));
  
  public AutoReconnect() {
    super("AutoReconnect", "Reconnects you if you disconnect.", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static AutoReconnect getInstance() {
    if (INSTANCE == null)
      INSTANCE = new AutoReconnect(); 
    return INSTANCE;
  }
  
  @SubscribeEvent
  public void sendPacket(GuiOpenEvent event) {
    if (event.getGui() instanceof GuiDisconnected) {
      updateLastConnectedServer();
      if (AutoLog.getInstance().isOff()) {
        GuiDisconnected disconnected = (GuiDisconnected)event.getGui();
        event.setGui((GuiScreen)new GuiDisconnectedHook(disconnected));
      } 
    } 
  }
  
  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    updateLastConnectedServer();
  }
  
  public void updateLastConnectedServer() {
    ServerData data = mc.getCurrentServerData();
    if (data != null)
      serverData = data; 
  }
  
  private class GuiDisconnectedHook extends GuiDisconnected {
    private final Timer timer = new Timer();
    
    public GuiDisconnectedHook(GuiDisconnected disconnected) {
      super(disconnected.parentScreen, disconnected.reason, disconnected.message);
      this.timer.reset();
    }
    
    public void updateScreen() {
      if (this.timer.passedS(((Integer)AutoReconnect.this.delay.getValue()).intValue()))
        this.mc.displayGuiScreen((GuiScreen)new GuiConnecting(this.parentScreen, this.mc, (AutoReconnect.serverData == null) ? this.mc.currentServerData : AutoReconnect.serverData)); 
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      String s = "Reconnecting in " + MathUtil.round(((((Integer)AutoReconnect.this.delay.getValue()).intValue() * 1000) - this.timer.getPassedTimeMs()) / 1000.0D, 1);
      AutoReconnect.this.renderer.drawString(s, (this.width / 2 - AutoReconnect.this.renderer.getStringWidth(s) / 2), (this.height - 16), 16777215, true);
    }
  }
}
