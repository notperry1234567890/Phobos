//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.combat.AutoCrystal;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Tracer extends Module {
  public Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
  
  public Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
  
  public Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
  
  public Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  public Setting<Boolean> drawFromSky = register(new Setting("DrawFromSky", Boolean.valueOf(false)));
  
  public Setting<Float> width = register(new Setting("Width", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  public Setting<Integer> distance = register(new Setting("Radius", Integer.valueOf(300), Integer.valueOf(0), Integer.valueOf(300)));
  
  public Setting<Boolean> crystalCheck = register(new Setting("CrystalCheck", Boolean.valueOf(false)));
  
  public Tracer() {
    super("Tracers", "Draws lines to other players.", Module.Category.RENDER, false, false, false);
  }
  
  public void onRender3D(Render3DEvent event) {
    if (fullNullCheck())
      return; 
    GlStateManager.pushMatrix();
    mc.world.loadedEntityList.stream()
      .filter(EntityUtil::isLiving)
      .filter(entity -> (entity instanceof net.minecraft.entity.player.EntityPlayer) ? ((((Boolean)this.players.getValue()).booleanValue() && mc.player != entity)) : (EntityUtil.isPassive(entity) ? ((Boolean)this.animals.getValue()).booleanValue() : ((Boolean)this.mobs.getValue()).booleanValue()))
      .filter(entity -> (mc.player.getDistanceSq(entity) < MathUtil.square(((Integer)this.distance.getValue()).intValue())))
      .filter(entity -> (((Boolean)this.invisibles.getValue()).booleanValue() || !entity.isInvisible()))
      .forEach(entity -> {
          float[] colour = getColorByDistance(entity);
          drawLineToEntity(entity, colour[0], colour[1], colour[2], colour[3]);
        });
    GlStateManager.popMatrix();
  }
  
  public double interpolate(double now, double then) {
    return then + (now - then) * mc.getRenderPartialTicks();
  }
  
  public double[] interpolate(Entity entity) {
    double posX = interpolate(entity.posX, entity.lastTickPosX) - (mc.getRenderManager()).renderPosX;
    double posY = interpolate(entity.posY, entity.lastTickPosY) - (mc.getRenderManager()).renderPosY;
    double posZ = interpolate(entity.posZ, entity.lastTickPosZ) - (mc.getRenderManager()).renderPosZ;
    return new double[] { posX, posY, posZ };
  }
  
  public void drawLineToEntity(Entity e, float red, float green, float blue, float opacity) {
    double[] xyz = interpolate(e);
    drawLine(xyz[0], xyz[1], xyz[2], e.height, red, green, blue, opacity);
  }
  
  public void drawLine(double posx, double posy, double posz, double up, float red, float green, float blue, float opacity) {
    Vec3d eyes = (new Vec3d(0.0D, 0.0D, 1.0D)).rotatePitch(-((float)Math.toRadians(mc.player.rotationPitch))).rotateYaw(
        -((float)Math.toRadians(mc.player.rotationYaw)));
    if (!((Boolean)this.drawFromSky.getValue()).booleanValue()) {
      drawLineFromPosToPos(eyes.x, eyes.y + mc.player.getEyeHeight(), eyes.z, posx, posy, posz, up, red, green, blue, opacity);
    } else {
      drawLineFromPosToPos(posx, 256.0D, posz, posx, posy, posz, up, red, green, blue, opacity);
    } 
  }
  
  public void drawLineFromPosToPos(double posx, double posy, double posz, double posx2, double posy2, double posz2, double up, float red, float green, float blue, float opacity) {
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glLineWidth(((Float)this.width.getValue()).floatValue());
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glDepthMask(false);
    GL11.glColor4f(red, green, blue, opacity);
    GlStateManager.disableLighting();
    GL11.glLoadIdentity();
    mc.entityRenderer.orientCamera(mc.getRenderPartialTicks());
    GL11.glBegin(1);
    GL11.glVertex3d(posx, posy, posz);
    GL11.glVertex3d(posx2, posy2, posz2);
    GL11.glVertex3d(posx2, posy2, posz2);
    GL11.glEnd();
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDepthMask(true);
    GL11.glDisable(3042);
    GL11.glColor3d(1.0D, 1.0D, 1.0D);
    GlStateManager.enableLighting();
  }
  
  public float[] getColorByDistance(Entity entity) {
    if (entity instanceof net.minecraft.entity.player.EntityPlayer && Phobos.friendManager.isFriend(entity.getName()))
      return new float[] { 0.0F, 0.5F, 1.0F, 1.0F }; 
    AutoCrystal autoCrystal = (AutoCrystal)Phobos.moduleManager.getModuleByClass(AutoCrystal.class);
    Color col = new Color(Color.HSBtoRGB((float)(Math.max(0.0D, Math.min(mc.player.getDistanceSq(entity), ((Boolean)this.crystalCheck.getValue()).booleanValue() ? (((Float)autoCrystal.placeRange.getValue()).floatValue() * ((Float)autoCrystal.placeRange.getValue()).floatValue()) : 2500.0D) / (((Boolean)this.crystalCheck.getValue()).booleanValue() ? (((Float)autoCrystal.placeRange.getValue()).floatValue() * ((Float)autoCrystal.placeRange.getValue()).floatValue()) : 2500.0F)) / 3.0D), 1.0F, 0.8F) | 0xFF000000);
    return new float[] { col.getRed() / 255.0F, col.getGreen() / 255.0F, col.getBlue() / 255.0F, 1.0F };
  }
}
