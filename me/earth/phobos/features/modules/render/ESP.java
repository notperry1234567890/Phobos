//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.RenderEntityModelEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class ESP extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.OUTLINE));
  
  private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  private final Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true)));
  
  private final Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false)));
  
  private final Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false)));
  
  private final Setting<Boolean> items = register(new Setting("Items", Boolean.valueOf(false)));
  
  private final Setting<Boolean> xporbs = register(new Setting("XpOrbs", Boolean.valueOf(false)));
  
  private final Setting<Boolean> xpbottles = register(new Setting("XpBottles", Boolean.valueOf(false)));
  
  private final Setting<Boolean> pearl = register(new Setting("Pearls", Boolean.valueOf(false)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(2.0F), Float.valueOf(0.1F), Float.valueOf(5.0F)));
  
  private final Setting<Boolean> colorFriends = register(new Setting("Friends", Boolean.valueOf(true)));
  
  private final Setting<Boolean> self = register(new Setting("Self", Boolean.valueOf(true)));
  
  private final Setting<Boolean> onTop = register(new Setting("onTop", Boolean.valueOf(true)));
  
  private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  private static ESP INSTANCE = new ESP();
  
  public ESP() {
    super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static ESP getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ESP(); 
    return INSTANCE;
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.items.getValue()).booleanValue()) {
      int i = 0;
      for (Entity entity : mc.world.loadedEntityList) {
        if (entity instanceof net.minecraft.entity.item.EntityItem && mc.player.getDistanceSq(entity) < 2500.0D) {
          Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
          AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
          GlStateManager.pushMatrix();
          GlStateManager.enableBlend();
          GlStateManager.disableDepth();
          GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
          GlStateManager.disableTexture2D();
          GlStateManager.depthMask(false);
          GL11.glEnable(2848);
          GL11.glHint(3154, 4354);
          GL11.glLineWidth(1.0F);
          RenderGlobal.renderFilledBox(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor().getAlpha() : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
          GL11.glDisable(2848);
          GlStateManager.depthMask(true);
          GlStateManager.enableDepth();
          GlStateManager.enableTexture2D();
          GlStateManager.disableBlend();
          GlStateManager.popMatrix();
          RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
          i++;
          if (i >= 50)
            break; 
        } 
      } 
    } 
    if (((Boolean)this.xporbs.getValue()).booleanValue()) {
      int i = 0;
      for (Entity entity : mc.world.loadedEntityList) {
        if (entity instanceof net.minecraft.entity.item.EntityXPOrb && mc.player.getDistanceSq(entity) < 2500.0D) {
          Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
          AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
          GlStateManager.pushMatrix();
          GlStateManager.enableBlend();
          GlStateManager.disableDepth();
          GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
          GlStateManager.disableTexture2D();
          GlStateManager.depthMask(false);
          GL11.glEnable(2848);
          GL11.glHint(3154, 4354);
          GL11.glLineWidth(1.0F);
          RenderGlobal.renderFilledBox(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F) : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
          GL11.glDisable(2848);
          GlStateManager.depthMask(true);
          GlStateManager.enableDepth();
          GlStateManager.enableTexture2D();
          GlStateManager.disableBlend();
          GlStateManager.popMatrix();
          RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
          i++;
          if (i >= 50)
            break; 
        } 
      } 
    } 
    if (((Boolean)this.pearl.getValue()).booleanValue()) {
      int i = 0;
      for (Entity entity : mc.world.loadedEntityList) {
        if (entity instanceof net.minecraft.entity.item.EntityEnderPearl && mc.player.getDistanceSq(entity) < 2500.0D) {
          Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
          AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
          GlStateManager.pushMatrix();
          GlStateManager.enableBlend();
          GlStateManager.disableDepth();
          GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
          GlStateManager.disableTexture2D();
          GlStateManager.depthMask(false);
          GL11.glEnable(2848);
          GL11.glHint(3154, 4354);
          GL11.glLineWidth(1.0F);
          RenderGlobal.renderFilledBox(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F) : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
          GL11.glDisable(2848);
          GlStateManager.depthMask(true);
          GlStateManager.enableDepth();
          GlStateManager.enableTexture2D();
          GlStateManager.disableBlend();
          GlStateManager.popMatrix();
          RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
          i++;
          if (i >= 50)
            break; 
        } 
      } 
    } 
    if (((Boolean)this.xpbottles.getValue()).booleanValue()) {
      int i = 0;
      for (Entity entity : mc.world.loadedEntityList) {
        if (entity instanceof net.minecraft.entity.item.EntityExpBottle && mc.player.getDistanceSq(entity) < 2500.0D) {
          Vec3d interp = EntityUtil.getInterpolatedRenderPos(entity, mc.getRenderPartialTicks());
          AxisAlignedBB bb = new AxisAlignedBB((entity.getEntityBoundingBox()).minX - 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).minY - 0.0D - entity.posY + interp.y, (entity.getEntityBoundingBox()).minZ - 0.05D - entity.posZ + interp.z, (entity.getEntityBoundingBox()).maxX + 0.05D - entity.posX + interp.x, (entity.getEntityBoundingBox()).maxY + 0.1D - entity.posY + interp.y, (entity.getEntityBoundingBox()).maxZ + 0.05D - entity.posZ + interp.z);
          GlStateManager.pushMatrix();
          GlStateManager.enableBlend();
          GlStateManager.disableDepth();
          GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
          GlStateManager.disableTexture2D();
          GlStateManager.depthMask(false);
          GL11.glEnable(2848);
          GL11.glHint(3154, 4354);
          GL11.glLineWidth(1.0F);
          RenderGlobal.renderFilledBox(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getRed() / 255.0F) : (((Integer)this.red.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getGreen() / 255.0F) : (((Integer)this.green.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getBlue() / 255.0F) : (((Integer)this.blue.getValue()).intValue() / 255.0F), ((Boolean)this.colorSync.getValue()).booleanValue() ? (Colors.INSTANCE.getCurrentColor().getAlpha() / 255.0F) : (((Integer)this.boxAlpha.getValue()).intValue() / 255.0F));
          GL11.glDisable(2848);
          GlStateManager.depthMask(true);
          GlStateManager.enableDepth();
          GlStateManager.enableTexture2D();
          GlStateManager.disableBlend();
          GlStateManager.popMatrix();
          RenderUtil.drawBlockOutline(bb, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), 1.0F);
          i++;
          if (i >= 50)
            break; 
        } 
      } 
    } 
  }
  
  public void onRenderModel(RenderEntityModelEvent event) {
    if (event.getStage() != 0 || event.entity == null || (event.entity.isInvisible() && !((Boolean)this.invisibles.getValue()).booleanValue()) || (!((Boolean)this.self.getValue()).booleanValue() && event.entity.equals(mc.player)) || (!((Boolean)this.players.getValue()).booleanValue() && event.entity instanceof net.minecraft.entity.player.EntityPlayer) || (!((Boolean)this.animals.getValue()).booleanValue() && EntityUtil.isPassive(event.entity)) || (!((Boolean)this.mobs.getValue()).booleanValue() && !EntityUtil.isPassive(event.entity) && !(event.entity instanceof net.minecraft.entity.player.EntityPlayer)))
      return; 
    Color color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : EntityUtil.getColor(event.entity, ((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue(), ((Boolean)this.colorFriends.getValue()).booleanValue());
    boolean fancyGraphics = mc.gameSettings.fancyGraphics;
    mc.gameSettings.fancyGraphics = false;
    float gamma = mc.gameSettings.gammaSetting;
    mc.gameSettings.gammaSetting = 10000.0F;
    if (((Boolean)this.onTop.getValue()).booleanValue() && (!Chams.getInstance().isEnabled() || !((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue()))
      event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale); 
    if (this.mode.getValue() == Mode.OUTLINE) {
      RenderUtil.renderOne(((Float)this.lineWidth.getValue()).floatValue());
      event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
      GlStateManager.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
      RenderUtil.renderTwo();
      event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
      GlStateManager.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
      RenderUtil.renderThree();
      RenderUtil.renderFour(color);
      event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
      GlStateManager.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
      RenderUtil.renderFive();
    } else {
      GL11.glPushMatrix();
      GL11.glPushAttrib(1048575);
      if (this.mode.getValue() == Mode.WIREFRAME) {
        GL11.glPolygonMode(1032, 6913);
      } else {
        GL11.glPolygonMode(1028, 6913);
      } 
      GL11.glDisable(3553);
      GL11.glDisable(2896);
      GL11.glDisable(2929);
      GL11.glEnable(2848);
      GL11.glEnable(3042);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
      GlStateManager.glLineWidth(((Float)this.lineWidth.getValue()).floatValue());
      event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale);
      GL11.glPopAttrib();
      GL11.glPopMatrix();
    } 
    if (!((Boolean)this.onTop.getValue()).booleanValue() && (!Chams.getInstance().isEnabled() || !((Boolean)(Chams.getInstance()).colored.getValue()).booleanValue()))
      event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.age, event.headYaw, event.headPitch, event.scale); 
    try {
      mc.gameSettings.fancyGraphics = fancyGraphics;
      mc.gameSettings.gammaSetting = gamma;
    } catch (Exception exception) {}
    event.setCanceled(true);
  }
  
  public enum Mode {
    WIREFRAME, OUTLINE;
  }
}
