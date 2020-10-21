//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import me.earth.phobos.event.events.ConnectionEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LogoutSpots extends Module {
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(300.0F), Float.valueOf(50.0F), Float.valueOf(500.0F)));
  
  private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Boolean> scaleing = register(new Setting("Scale", Boolean.valueOf(false)));
  
  private final Setting<Float> scaling = register(new Setting("Size", Float.valueOf(4.0F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
  
  private final Setting<Float> factor = register(new Setting("Factor", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(1.0F), v -> ((Boolean)this.scaleing.getValue()).booleanValue()));
  
  private final Setting<Boolean> smartScale = register(new Setting("SmartScale", Boolean.valueOf(false), v -> ((Boolean)this.scaleing.getValue()).booleanValue()));
  
  private final Setting<Boolean> rect = register(new Setting("Rectangle", Boolean.valueOf(true)));
  
  private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(true)));
  
  private final Setting<Boolean> notification = register(new Setting("Notification", Boolean.valueOf(true)));
  
  public Setting<Boolean> message = register(new Setting("Message", Boolean.valueOf(false)));
  
  private final List<LogoutPos> spots = new CopyOnWriteArrayList<>();
  
  public LogoutSpots() {
    super("LogoutSpots", "Renders LogoutSpots", Module.Category.RENDER, true, false, false);
  }
  
  public void onLogout() {
    this.spots.clear();
  }
  
  public void onDisable() {
    this.spots.clear();
  }
  
  public void onRender3D(Render3DEvent event) {
    if (!this.spots.isEmpty())
      synchronized (this.spots) {
        this.spots.forEach(spot -> {
              if (spot.getEntity() != null) {
                AxisAlignedBB bb = RenderUtil.interpolateAxis(spot.getEntity().getEntityBoundingBox());
                RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
                double x = interpolate((spot.getEntity()).lastTickPosX, (spot.getEntity()).posX, event.getPartialTicks()) - (mc.getRenderManager()).renderPosX;
                double y = interpolate((spot.getEntity()).lastTickPosY, (spot.getEntity()).posY, event.getPartialTicks()) - (mc.getRenderManager()).renderPosY;
                double z = interpolate((spot.getEntity()).lastTickPosZ, (spot.getEntity()).posZ, event.getPartialTicks()) - (mc.getRenderManager()).renderPosZ;
                renderNameTag(spot.getName(), x, y, z, event.getPartialTicks(), spot.getX(), spot.getY(), spot.getZ());
              } 
            });
      }  
  }
  
  public void onUpdate() {
    if (!fullNullCheck())
      this.spots.removeIf(spot -> (mc.player.getDistanceSq((Entity)spot.getEntity()) >= MathUtil.square(((Float)this.range.getValue()).floatValue()))); 
  }
  
  @SubscribeEvent
  public void onConnection(ConnectionEvent event) {
    if (event.getStage() == 0) {
      UUID uuid = event.getUuid();
      EntityPlayer entity = mc.world.getPlayerEntityByUUID(uuid);
      if (entity != null && (
        (Boolean)this.message.getValue()).booleanValue())
        Command.sendMessage("§a" + entity.getName() + " just logged in" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)entity.posX + ", " + (int)entity.posY + ", " + (int)entity.posZ + ")!") : "!"), ((Boolean)this.notification.getValue()).booleanValue()); 
      this.spots.removeIf(pos -> pos.getName().equalsIgnoreCase(event.getName()));
    } else if (event.getStage() == 1) {
      EntityPlayer entity = event.getEntity();
      UUID uuid = event.getUuid();
      String name = event.getName();
      if (((Boolean)this.message.getValue()).booleanValue())
        Command.sendMessage("§c" + event.getName() + " just logged out" + (((Boolean)this.coords.getValue()).booleanValue() ? (" at (" + (int)entity.posX + ", " + (int)entity.posY + ", " + (int)entity.posZ + ")!") : "!"), ((Boolean)this.notification.getValue()).booleanValue()); 
      if (name != null && entity != null && uuid != null)
        this.spots.add(new LogoutPos(name, uuid, entity)); 
    } 
  }
  
  private void renderNameTag(String name, double x, double yi, double z, float delta, double xPos, double yPos, double zPos) {
    double y = yi + 0.7D;
    Entity camera = mc.getRenderViewEntity();
    assert camera != null;
    double originalPositionX = camera.posX;
    double originalPositionY = camera.posY;
    double originalPositionZ = camera.posZ;
    camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
    camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
    camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
    String displayTag = name + " XYZ: " + (int)xPos + ", " + (int)yPos + ", " + (int)zPos;
    double distance = camera.getDistance(x + (mc.getRenderManager()).viewerPosX, y + (mc.getRenderManager()).viewerPosY, z + (mc.getRenderManager()).viewerPosZ);
    int width = this.renderer.getStringWidth(displayTag) / 2;
    double scale = (0.0018D + ((Float)this.scaling.getValue()).floatValue() * distance * ((Float)this.factor.getValue()).floatValue()) / 1000.0D;
    if (distance <= 8.0D && ((Boolean)this.smartScale.getValue()).booleanValue())
      scale = 0.0245D; 
    if (!((Boolean)this.scaleing.getValue()).booleanValue())
      scale = ((Float)this.scaling.getValue()).floatValue() / 100.0D; 
    GlStateManager.pushMatrix();
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enablePolygonOffset();
    GlStateManager.doPolygonOffset(1.0F, -1500000.0F);
    GlStateManager.disableLighting();
    GlStateManager.translate((float)x, (float)y + 1.4F, (float)z);
    GlStateManager.rotate(-(mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((mc.getRenderManager()).playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0F : 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(-scale, -scale, scale);
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.enableBlend();
    if (((Boolean)this.rect.getValue()).booleanValue())
      RenderUtil.drawRect((-width - 2), -(this.renderer.getFontHeight() + 1), width + 2.0F, 1.5F, 1426063360); 
    GlStateManager.disableBlend();
    this.renderer.drawStringWithShadow(displayTag, -width, -(this.renderer.getFontHeight() - 1), ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue())));
    camera.posX = originalPositionX;
    camera.posY = originalPositionY;
    camera.posZ = originalPositionZ;
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.disablePolygonOffset();
    GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
    GlStateManager.popMatrix();
  }
  
  private double interpolate(double previous, double current, float delta) {
    return previous + (current - previous) * delta;
  }
  
  private static class LogoutPos {
    private final String name;
    
    private final UUID uuid;
    
    private final EntityPlayer entity;
    
    private final double x;
    
    private final double y;
    
    private final double z;
    
    public LogoutPos(String name, UUID uuid, EntityPlayer entity) {
      this.name = name;
      this.uuid = uuid;
      this.entity = entity;
      this.x = entity.posX;
      this.y = entity.posY;
      this.z = entity.posZ;
    }
    
    public String getName() {
      return this.name;
    }
    
    public UUID getUuid() {
      return this.uuid;
    }
    
    public EntityPlayer getEntity() {
      return this.entity;
    }
    
    public double getX() {
      return this.x;
    }
    
    public double getY() {
      return this.y;
    }
    
    public double getZ() {
      return this.z;
    }
  }
}
