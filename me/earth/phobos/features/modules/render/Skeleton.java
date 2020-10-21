//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.RenderEntityModelEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Skeleton extends Module {
  private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.5F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  private final Setting<Boolean> colorFriends = register(new Setting("Friends", Boolean.valueOf(true)));
  
  private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  private static Skeleton INSTANCE = new Skeleton();
  
  private final Map<EntityPlayer, float[][]> rotationList = (Map)new HashMap<>();
  
  public Skeleton() {
    super("Skeleton", "Draws a nice Skeleton.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Skeleton getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Skeleton(); 
    return INSTANCE;
  }
  
  public void onRender3D(Render3DEvent event) {
    RenderUtil.GLPre(((Float)this.lineWidth.getValue()).floatValue());
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player != null && player != mc.getRenderViewEntity() && player.isEntityAlive() && !player.isPlayerSleeping() && (!player.isInvisible() || ((Boolean)this.invisibles.getValue()).booleanValue()) && 
        this.rotationList.get(player) != null && mc.player.getDistanceSq((Entity)player) < 2500.0D)
        renderSkeletonTest(player, this.rotationList.get(player), new Color(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(0))).intValue()), new Color(((Integer)(HUD.getInstance()).colorMap.get(Integer.valueOf(this.renderer.scaledHeight / 2))).intValue())); 
    } 
    RenderUtil.GlPost();
  }
  
  public void onRenderModel(RenderEntityModelEvent event) {
    if (event.getStage() == 0 && event.entity instanceof EntityPlayer && 
      event.modelBase instanceof ModelBiped) {
      ModelBiped biped = (ModelBiped)event.modelBase;
      float[][] rotations = RenderUtil.getBipedRotations(biped);
      EntityPlayer player = (EntityPlayer)event.entity;
      this.rotationList.put(player, rotations);
    } 
  }
  
  private void renderSkeleton(EntityPlayer player, float[][] rotations, Color color) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.pushMatrix();
    GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    Vec3d interp = EntityUtil.getInterpolatedRenderPos((Entity)player, mc.getRenderPartialTicks());
    double pX = interp.x;
    double pY = interp.y;
    double pZ = interp.z;
    GlStateManager.translate(pX, pY, pZ);
    GlStateManager.rotate(-player.renderYawOffset, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? -0.235D : 0.0D);
    float sneak = player.isSneaking() ? 0.6F : 0.75F;
    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.125D, sneak, 0.0D);
    if (rotations[3][0] != 0.0F)
      GlStateManager.rotate(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[3][1] != 0.0F)
      GlStateManager.rotate(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[3][2] != 0.0F)
      GlStateManager.rotate(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GL11.glVertex3d(0.0D, -sneak, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.125D, sneak, 0.0D);
    if (rotations[4][0] != 0.0F)
      GlStateManager.rotate(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[4][1] != 0.0F)
      GlStateManager.rotate(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[4][2] != 0.0F)
      GlStateManager.rotate(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GL11.glVertex3d(0.0D, -sneak, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? 0.25D : 0.0D);
    GlStateManager.pushMatrix();
    double sneakOffset = 0.0D;
    if (player.isSneaking())
      sneakOffset = -0.05D; 
    GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.01725D : 0.0D);
    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.375D, sneak + 0.55D, 0.0D);
    if (rotations[1][0] != 0.0F)
      GlStateManager.rotate(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[1][1] != 0.0F)
      GlStateManager.rotate(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[1][2] != 0.0F)
      GlStateManager.rotate(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GL11.glVertex3d(0.0D, -0.5D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.375D, sneak + 0.55D, 0.0D);
    if (rotations[2][0] != 0.0F)
      GlStateManager.rotate(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[2][1] != 0.0F)
      GlStateManager.rotate(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[2][2] != 0.0F)
      GlStateManager.rotate(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GL11.glVertex3d(0.0D, -0.5D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
    if (rotations[0][0] != 0.0F)
      GlStateManager.rotate(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GL11.glVertex3d(0.0D, 0.3D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    GlStateManager.rotate(player.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
    if (player.isSneaking())
      sneakOffset = -0.16175D; 
    GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.48025D : 0.0D);
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak, 0.0D);
    GlStateManager.glBegin(3);
    GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
    GL11.glVertex3d(0.125D, 0.0D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak, 0.0D);
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GL11.glVertex3d(0.0D, 0.55D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
    GlStateManager.glBegin(3);
    GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
    GL11.glVertex3d(0.375D, 0.0D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
  }
  
  private void renderSkeletonTest(EntityPlayer player, float[][] rotations, Color startColor, Color endColor) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.pushMatrix();
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    Vec3d interp = EntityUtil.getInterpolatedRenderPos((Entity)player, mc.getRenderPartialTicks());
    double pX = interp.x;
    double pY = interp.y;
    double pZ = interp.z;
    GlStateManager.translate(pX, pY, pZ);
    GlStateManager.rotate(-player.renderYawOffset, 0.0F, 1.0F, 0.0F);
    GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? -0.235D : 0.0D);
    float sneak = player.isSneaking() ? 0.6F : 0.75F;
    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.125D, sneak, 0.0D);
    if (rotations[3][0] != 0.0F)
      GlStateManager.rotate(rotations[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[3][1] != 0.0F)
      GlStateManager.rotate(rotations[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[3][2] != 0.0F)
      GlStateManager.rotate(rotations[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, -sneak, 0.0D);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.125D, sneak, 0.0D);
    if (rotations[4][0] != 0.0F)
      GlStateManager.rotate(rotations[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[4][1] != 0.0F)
      GlStateManager.rotate(rotations[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[4][2] != 0.0F)
      GlStateManager.rotate(rotations[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, -sneak, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.translate(0.0D, 0.0D, player.isSneaking() ? 0.25D : 0.0D);
    GlStateManager.pushMatrix();
    double sneakOffset = 0.0D;
    if (player.isSneaking())
      sneakOffset = -0.05D; 
    GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.01725D : 0.0D);
    GlStateManager.pushMatrix();
    GlStateManager.translate(-0.375D, sneak + 0.55D, 0.0D);
    if (rotations[1][0] != 0.0F)
      GlStateManager.rotate(rotations[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[1][1] != 0.0F)
      GlStateManager.rotate(rotations[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[1][2] != 0.0F)
      GlStateManager.rotate(-rotations[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, -0.5D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.375D, sneak + 0.55D, 0.0D);
    if (rotations[2][0] != 0.0F)
      GlStateManager.rotate(rotations[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    if (rotations[2][1] != 0.0F)
      GlStateManager.rotate(rotations[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F); 
    if (rotations[2][2] != 0.0F)
      GlStateManager.rotate(-rotations[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F); 
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, -0.5D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
    if (rotations[0][0] != 0.0F)
      GlStateManager.rotate(rotations[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F); 
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.3D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
    GlStateManager.rotate(player.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
    if (player.isSneaking())
      sneakOffset = -0.16175D; 
    GlStateManager.translate(0.0D, sneakOffset, player.isSneaking() ? -0.48025D : 0.0D);
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak, 0.0D);
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.125D, 0.0D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak, 0.0D);
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.0D, 0.55D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.pushMatrix();
    GlStateManager.translate(0.0D, sneak + 0.55D, 0.0D);
    GlStateManager.glBegin(3);
    GlStateManager.color(startColor.getRed() / 255.0F, startColor.getGreen() / 255.0F, startColor.getBlue() / 255.0F, startColor.getAlpha() / 255.0F);
    GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
    GlStateManager.color(endColor.getRed() / 255.0F, endColor.getGreen() / 255.0F, endColor.getBlue() / 255.0F, endColor.getAlpha() / 255.0F);
    GL11.glVertex3d(0.375D, 0.0D, 0.0D);
    GlStateManager.glEnd();
    GlStateManager.popMatrix();
    GlStateManager.popMatrix();
  }
}
