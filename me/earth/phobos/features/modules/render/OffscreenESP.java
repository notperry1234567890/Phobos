//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Map;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class OffscreenESP extends Module {
  private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  private final Setting<Boolean> offscreenOnly = register(new Setting("Offscreen-Only", Boolean.valueOf(true)));
  
  private final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private final Setting<Float> outlineWidth = register(new Setting("Outline-Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(3.0F)));
  
  private final Setting<Integer> fadeDistance = register(new Setting("Fade-Distance", Integer.valueOf(100), Integer.valueOf(10), Integer.valueOf(200)));
  
  private final Setting<Integer> radius = register(new Setting("Radius", Integer.valueOf(45), Integer.valueOf(10), Integer.valueOf(200)));
  
  private final Setting<Float> size = register(new Setting("Size", Float.valueOf(10.0F), Float.valueOf(5.0F), Float.valueOf(25.0F)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final EntityListener entityListener = new EntityListener();
  
  public OffscreenESP() {
    super("ArrowESP", "Shows the direction players are in with cool little triangles :3", Module.Category.RENDER, true, false, false);
  }
  
  public void onRender2D(Render2DEvent event) {
    this.entityListener.render();
    mc.world.loadedEntityList.forEach(o -> {
          if (o instanceof EntityPlayer && isValid((EntityPlayer)o)) {
            EntityPlayer entity = (EntityPlayer)o;
            Vec3d pos = this.entityListener.getEntityLowerBounds().get(entity);
            if (pos != null && !isOnScreen(pos) && (!RenderUtil.isInViewFrustrum((Entity)entity) || !((Boolean)this.offscreenOnly.getValue()).booleanValue())) {
              Color color = ((Boolean)this.colorSync.getValue()).booleanValue() ? new Color(Colors.INSTANCE.getCurrentColor().getRed(), Colors.INSTANCE.getCurrentColor().getGreen(), Colors.INSTANCE.getCurrentColor().getBlue(), (int)MathHelper.clamp(255.0F - 255.0F / ((Integer)this.fadeDistance.getValue()).intValue() * mc.player.getDistance((Entity)entity), 100.0F, 255.0F)) : EntityUtil.getColor((Entity)entity, ((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), (int)MathHelper.clamp(255.0F - 255.0F / ((Integer)this.fadeDistance.getValue()).intValue() * mc.player.getDistance((Entity)entity), 100.0F, 255.0F), true);
              int x = Display.getWidth() / 2 / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale);
              int y = Display.getHeight() / 2 / ((mc.gameSettings.guiScale == 0) ? 1 : mc.gameSettings.guiScale);
              float yaw = getRotations((EntityLivingBase)entity) - mc.player.rotationYaw;
              GL11.glTranslatef(x, y, 0.0F);
              GL11.glRotatef(yaw, 0.0F, 0.0F, 1.0F);
              GL11.glTranslatef(-x, -y, 0.0F);
              RenderUtil.drawTracerPointer(x, (y - ((Integer)this.radius.getValue()).intValue()), ((Float)this.size.getValue()).floatValue(), 2.0F, 1.0F, ((Boolean)this.outline.getValue()).booleanValue(), ((Float)this.outlineWidth.getValue()).floatValue(), color.getRGB());
              GL11.glTranslatef(x, y, 0.0F);
              GL11.glRotatef(-yaw, 0.0F, 0.0F, 1.0F);
              GL11.glTranslatef(-x, -y, 0.0F);
            } 
          } 
        });
  }
  
  private boolean isOnScreen(Vec3d pos) {
    if (pos.x > -1.0D && pos.y < 1.0D)
      return (pos.x / ((mc.gameSettings.guiScale == 0) ? true : mc.gameSettings.guiScale) >= 0.0D && pos.x / ((mc.gameSettings.guiScale == 0) ? true : mc.gameSettings.guiScale) <= Display.getWidth() && pos.y / ((mc.gameSettings.guiScale == 0) ? true : mc.gameSettings.guiScale) >= 0.0D && pos.y / ((mc.gameSettings.guiScale == 0) ? true : mc.gameSettings.guiScale) <= Display.getHeight()); 
    return false;
  }
  
  private boolean isValid(EntityPlayer entity) {
    return (entity != mc.player && (!entity.isInvisible() || ((Boolean)this.invisibles.getValue()).booleanValue()) && entity.isEntityAlive());
  }
  
  private float getRotations(EntityLivingBase ent) {
    double x = ent.posX - mc.player.posX;
    double z = ent.posZ - mc.player.posZ;
    return (float)-(Math.atan2(x, z) * 57.29577951308232D);
  }
  
  private static class EntityListener {
    private final Map<Entity, Vec3d> entityUpperBounds = Maps.newHashMap();
    
    private final Map<Entity, Vec3d> entityLowerBounds = Maps.newHashMap();
    
    private void render() {
      if (!this.entityUpperBounds.isEmpty())
        this.entityUpperBounds.clear(); 
      if (!this.entityLowerBounds.isEmpty())
        this.entityLowerBounds.clear(); 
      for (Entity e : Util.mc.world.loadedEntityList) {
        Vec3d bound = getEntityRenderPosition(e);
        bound.add(new Vec3d(0.0D, e.height + 0.2D, 0.0D));
        Vec3d upperBounds = RenderUtil.to2D(bound.x, bound.y, bound.z), lowerBounds = RenderUtil.to2D(bound.x, bound.y - 2.0D, bound.z);
        if (upperBounds != null && lowerBounds != null) {
          this.entityUpperBounds.put(e, upperBounds);
          this.entityLowerBounds.put(e, lowerBounds);
        } 
      } 
    }
    
    private Vec3d getEntityRenderPosition(Entity entity) {
      double partial = Util.mc.timer.renderPartialTicks;
      double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - (Util.mc.getRenderManager()).viewerPosX;
      double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - (Util.mc.getRenderManager()).viewerPosY;
      double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - (Util.mc.getRenderManager()).viewerPosZ;
      return new Vec3d(x, y, z);
    }
    
    public Map<Entity, Vec3d> getEntityLowerBounds() {
      return this.entityLowerBounds;
    }
    
    private EntityListener() {}
  }
}
