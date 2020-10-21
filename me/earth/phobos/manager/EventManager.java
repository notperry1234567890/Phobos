//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import com.google.common.base.Strings;
import java.util.Objects;
import java.util.UUID;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ConnectionEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.TotemPopEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventManager extends Feature {
  private final Timer timer = new Timer();
  
  private final Timer logoutTimer = new Timer();
  
  private boolean keyTimeout;
  
  public void init() {
    MinecraftForge.EVENT_BUS.register(this);
  }
  
  public void onUnload() {
    MinecraftForge.EVENT_BUS.unregister(this);
  }
  
  @SubscribeEvent
  public void onUpdate(LivingEvent.LivingUpdateEvent event) {
    if (!fullNullCheck() && (event.getEntity().getEntityWorld()).isRemote && event.getEntityLiving().equals(mc.player)) {
      Phobos.potionManager.update();
      Phobos.totemPopManager.onUpdate();
      Phobos.inventoryManager.update();
      Phobos.holeManager.update();
      Phobos.safetyManager.onUpdate();
      Phobos.moduleManager.onUpdate();
      Phobos.timerManager.update();
      if (this.timer.passedMs(((Integer)(Managers.getInstance()).moduleListUpdates.getValue()).intValue())) {
        Phobos.moduleManager.sortModules(true);
        Phobos.moduleManager.alphabeticallySortModules();
        this.timer.reset();
      } 
    } 
  }
  
  @SubscribeEvent
  public void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
    this.logoutTimer.reset();
    Phobos.moduleManager.onLogin();
  }
  
  @SubscribeEvent
  public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
    Phobos.moduleManager.onLogout();
    Phobos.totemPopManager.onLogout();
    Phobos.potionManager.onLogout();
  }
  
  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (fullNullCheck())
      return; 
    Phobos.moduleManager.onTick();
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (fullNullCheck())
      return; 
    if (event.getStage() == 0) {
      Phobos.speedManager.updateValues();
      Phobos.rotationManager.updateRotations();
      Phobos.positionManager.updatePosition();
    } 
    if (event.getStage() == 1) {
      Phobos.rotationManager.restoreRotations();
      Phobos.positionManager.restorePosition();
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() != 0)
      return; 
    Phobos.serverManager.onPacketReceived();
    if (event.getPacket() instanceof SPacketEntityStatus) {
      SPacketEntityStatus packet = (SPacketEntityStatus)event.getPacket();
      if (packet.getOpCode() == 35 && 
        packet.getEntity((World)mc.world) instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer)packet.getEntity((World)mc.world);
        MinecraftForge.EVENT_BUS.post((Event)new TotemPopEvent(player));
        Phobos.totemPopManager.onTotemPop(player);
        Phobos.potionManager.onTotemPop(player);
      } 
    } else if (event.getPacket() instanceof SPacketPlayerListItem && !fullNullCheck() && this.logoutTimer.passedS(1.0D)) {
      SPacketPlayerListItem packet = (SPacketPlayerListItem)event.getPacket();
      if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction()))
        return; 
      packet.getEntries().stream().filter(Objects::nonNull).filter(data -> (!Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null))
        .forEach(data -> {
            String name;
            EntityPlayer entity;
            UUID id = data.getProfile().getId();
            switch (packet.getAction()) {
              case ADD_PLAYER:
                name = data.getProfile().getName();
                MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(0, id, name));
                break;
              case REMOVE_PLAYER:
                entity = mc.world.getPlayerEntityByUUID(id);
                if (entity != null) {
                  String logoutName = entity.getName();
                  MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(1, entity, id, logoutName));
                  break;
                } 
                MinecraftForge.EVENT_BUS.post((Event)new ConnectionEvent(2, id, null));
                break;
            } 
          });
    } else if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTimeUpdate) {
      Phobos.serverManager.update();
    } 
  }
  
  @SubscribeEvent
  public void onWorldRender(RenderWorldLastEvent event) {
    if (event.isCanceled())
      return; 
    mc.profiler.startSection("phobos");
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    GlStateManager.disableDepth();
    GlStateManager.glLineWidth(1.0F);
    Render3DEvent render3dEvent = new Render3DEvent(event.getPartialTicks());
    Phobos.moduleManager.onRender3D(render3dEvent);
    GlStateManager.glLineWidth(1.0F);
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.enableDepth();
    GlStateManager.enableCull();
    GlStateManager.enableCull();
    GlStateManager.depthMask(true);
    GlStateManager.enableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.enableDepth();
    mc.profiler.endSection();
  }
  
  @SubscribeEvent
  public void renderHUD(RenderGameOverlayEvent.Post event) {
    if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
      Phobos.textManager.updateResolution(); 
  }
  
  @SubscribeEvent(priority = EventPriority.LOW)
  public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
    if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
      ScaledResolution resolution = new ScaledResolution(mc);
      Render2DEvent render2DEvent = new Render2DEvent(event.getPartialTicks(), resolution);
      Phobos.moduleManager.onRender2D(render2DEvent);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onChatSent(ClientChatEvent event) {
    if (event.getMessage().startsWith(Command.getCommandPrefix())) {
      event.setCanceled(true);
      try {
        mc.ingameGUI.getChatGUI().addToSentMessages(event.getMessage());
        if (event.getMessage().length() > 1) {
          Phobos.commandManager.executeCommand(event.getMessage().substring(Command.getCommandPrefix().length() - 1));
        } else {
          Command.sendMessage("Please enter a command.");
        } 
      } catch (Exception e) {
        e.printStackTrace();
        Command.sendMessage("§cAn error occurred while running this command. Check the log!");
      } 
      event.setMessage("");
    } 
  }
}
