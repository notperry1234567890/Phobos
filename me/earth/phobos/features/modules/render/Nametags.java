//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.util.Objects;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class Nametags extends Module {
  private final Setting<Boolean> health = register(new Setting("Health", Boolean.valueOf(true)));
  
  private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(true)));
  
  private final Setting<Float> scaling = register(new Setting("Size", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(20.0F)));
  
  private final Setting<Boolean> invisibles = register(new Setting("Invisibles", Boolean.valueOf(false)));
  
  private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(true)));
  
  private final Setting<Boolean> totemPops = register(new Setting("TotemPops", Boolean.valueOf(true)));
  
  private final Setting<Boolean> gamemode = register(new Setting("Gamemode", Boolean.valueOf(false)));
  
  private final Setting<Boolean> entityID = register(new Setting("ID", Boolean.valueOf(false)));
  
  private final Setting<Boolean> rect = register(new Setting("Rectangle", Boolean.valueOf(true)));
  
  private final Setting<Boolean> sneak = register(new Setting("SneakColor", Boolean.valueOf(false)));
  
  private final Setting<Boolean> heldStackName = register(new Setting("StackName", Boolean.valueOf(false)));
  
  private final Setting<Boolean> whiter = register(new Setting("White", Boolean.valueOf(false)));
  
  private final Setting<Boolean> onlyFov = register(new Setting("OnlyFov", Boolean.valueOf(false)));
  
  private final Setting<Boolean> scaleing = register(new Setting("Scale", Boolean.valueOf(false)));
  
  private final Setting<Float> factor = register(new Setting("Factor", Float.valueOf(0.3F), Float.valueOf(0.1F), Float.valueOf(1.0F), v -> ((Boolean)this.scaleing.getValue()).booleanValue()));
  
  private final Setting<Boolean> smartScale = register(new Setting("SmartScale", Boolean.valueOf(false), v -> ((Boolean)this.scaleing.getValue()).booleanValue()));
  
  private static Nametags INSTANCE = new Nametags();
  
  public Nametags() {
    super("Nametags", "Better Nametags", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Nametags getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Nametags(); 
    return INSTANCE;
  }
  
  public void onRender3D(Render3DEvent event) {
    if (!fullNullCheck())
      for (EntityPlayer player : mc.world.playerEntities) {
        if (player != null && !player.equals(mc.player) && player.isEntityAlive() && (!player.isInvisible() || ((Boolean)this.invisibles.getValue()).booleanValue()) && (!((Boolean)this.onlyFov.getValue()).booleanValue() || RotationUtil.isInFov((Entity)player))) {
          double x = interpolate(player.lastTickPosX, player.posX, event.getPartialTicks()) - (mc.getRenderManager()).renderPosX;
          double y = interpolate(player.lastTickPosY, player.posY, event.getPartialTicks()) - (mc.getRenderManager()).renderPosY;
          double z = interpolate(player.lastTickPosZ, player.posZ, event.getPartialTicks()) - (mc.getRenderManager()).renderPosZ;
          renderNameTag(player, x, y, z, event.getPartialTicks());
        } 
      }  
  }
  
  private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
    double tempY = y;
    tempY += player.isSneaking() ? 0.5D : 0.7D;
    Entity camera = mc.getRenderViewEntity();
    assert camera != null;
    double originalPositionX = camera.posX;
    double originalPositionY = camera.posY;
    double originalPositionZ = camera.posZ;
    camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
    camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
    camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
    String displayTag = getDisplayTag(player);
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
    GlStateManager.translate((float)x, (float)tempY + 1.4F, (float)z);
    GlStateManager.rotate(-(mc.getRenderManager()).playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((mc.getRenderManager()).playerViewX, (mc.gameSettings.thirdPersonView == 2) ? -1.0F : 1.0F, 0.0F, 0.0F);
    GlStateManager.scale(-scale, -scale, scale);
    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.enableBlend();
    if (((Boolean)this.rect.getValue()).booleanValue())
      RenderUtil.drawRect((-width - 2), -(this.renderer.getFontHeight() + 1), width + 2.0F, 1.5F, 1426063360); 
    GlStateManager.disableBlend();
    ItemStack renderMainHand = player.getHeldItemMainhand().copy();
    if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof net.minecraft.item.ItemTool || renderMainHand.getItem() instanceof net.minecraft.item.ItemArmor))
      renderMainHand.stackSize = 1; 
    if (((Boolean)this.heldStackName.getValue()).booleanValue() && !renderMainHand.isEmpty && renderMainHand.getItem() != Items.AIR) {
      String stackName = renderMainHand.getDisplayName();
      int stackNameWidth = this.renderer.getStringWidth(stackName) / 2;
      GL11.glPushMatrix();
      GL11.glScalef(0.75F, 0.75F, 0.0F);
      this.renderer.drawStringWithShadow(stackName, -stackNameWidth, -(getBiggestArmorTag(player) + 20.0F), -1);
      GL11.glScalef(1.5F, 1.5F, 1.0F);
      GL11.glPopMatrix();
    } 
    if (((Boolean)this.armor.getValue()).booleanValue()) {
      GlStateManager.pushMatrix();
      int xOffset = -8;
      for (ItemStack stack : player.inventory.armorInventory) {
        if (stack != null)
          xOffset -= 8; 
      } 
      xOffset -= 8;
      ItemStack renderOffhand = player.getHeldItemOffhand().copy();
      if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof net.minecraft.item.ItemTool || renderOffhand.getItem() instanceof net.minecraft.item.ItemArmor))
        renderOffhand.stackSize = 1; 
      renderItemStack(renderOffhand, xOffset, -26);
      xOffset += 16;
      for (ItemStack stack : player.inventory.armorInventory) {
        if (stack != null) {
          ItemStack armourStack = stack.copy();
          if (armourStack.hasEffect() && (armourStack.getItem() instanceof net.minecraft.item.ItemTool || armourStack.getItem() instanceof net.minecraft.item.ItemArmor))
            armourStack.stackSize = 1; 
          renderItemStack(armourStack, xOffset, -26);
          xOffset += 16;
        } 
      } 
      renderItemStack(renderMainHand, xOffset, -26);
      GlStateManager.popMatrix();
    } 
    this.renderer.drawStringWithShadow(displayTag, -width, -(this.renderer.getFontHeight() - 1), getDisplayColour(player));
    camera.posX = originalPositionX;
    camera.posY = originalPositionY;
    camera.posZ = originalPositionZ;
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.disablePolygonOffset();
    GlStateManager.doPolygonOffset(1.0F, 1500000.0F);
    GlStateManager.popMatrix();
  }
  
  private void renderItemStack(ItemStack stack, int x, int y) {
    GlStateManager.pushMatrix();
    GlStateManager.depthMask(true);
    GlStateManager.clear(256);
    RenderHelper.enableStandardItemLighting();
    (mc.getRenderItem()).zLevel = -150.0F;
    GlStateManager.disableAlpha();
    GlStateManager.enableDepth();
    GlStateManager.disableCull();
    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
    mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, y);
    (mc.getRenderItem()).zLevel = 0.0F;
    RenderHelper.disableStandardItemLighting();
    GlStateManager.enableCull();
    GlStateManager.enableAlpha();
    GlStateManager.scale(0.5F, 0.5F, 0.5F);
    GlStateManager.disableDepth();
    renderEnchantmentText(stack, x, y);
    GlStateManager.enableDepth();
    GlStateManager.scale(2.0F, 2.0F, 2.0F);
    GlStateManager.popMatrix();
  }
  
  private void renderEnchantmentText(ItemStack stack, int x, int y) {
    int enchantmentY = y - 8;
    if (stack.getItem() == Items.GOLDEN_APPLE && stack.hasEffect()) {
      this.renderer.drawStringWithShadow("god", (x * 2), enchantmentY, -3977919);
      enchantmentY -= 8;
    } 
    NBTTagList enchants = stack.getEnchantmentTagList();
    for (int index = 0; index < enchants.tagCount(); index++) {
      short id = enchants.getCompoundTagAt(index).getShort("id");
      short level = enchants.getCompoundTagAt(index).getShort("lvl");
      Enchantment enc = Enchantment.getEnchantmentByID(id);
      if (enc != null) {
        String encName = enc.isCurse() ? (TextFormatting.RED + enc.getTranslatedName(level).substring(11).substring(0, 1).toLowerCase()) : enc.getTranslatedName(level).substring(0, 1).toLowerCase();
        encName = encName + level;
        this.renderer.drawStringWithShadow(encName, (x * 2), enchantmentY, -1);
        enchantmentY -= 8;
      } 
    } 
    if (DamageUtil.hasDurability(stack)) {
      String color;
      int percent = DamageUtil.getRoundedDamage(stack);
      if (percent >= 60) {
        color = "§a";
      } else if (percent >= 25) {
        color = "§e";
      } else {
        color = "§c";
      } 
      this.renderer.drawStringWithShadow(color + percent + "%", (x * 2), enchantmentY, -1);
    } 
  }
  
  private float getBiggestArmorTag(EntityPlayer player) {
    float enchantmentY = 0.0F;
    boolean arm = false;
    for (ItemStack stack : player.inventory.armorInventory) {
      float encY = 0.0F;
      if (stack != null) {
        NBTTagList enchants = stack.getEnchantmentTagList();
        for (int index = 0; index < enchants.tagCount(); index++) {
          short id = enchants.getCompoundTagAt(index).getShort("id");
          Enchantment enc = Enchantment.getEnchantmentByID(id);
          if (enc != null) {
            encY += 8.0F;
            arm = true;
          } 
        } 
      } 
      if (encY > enchantmentY)
        enchantmentY = encY; 
    } 
    ItemStack renderMainHand = player.getHeldItemMainhand().copy();
    if (renderMainHand.hasEffect()) {
      float encY = 0.0F;
      NBTTagList enchants = renderMainHand.getEnchantmentTagList();
      for (int index = 0; index < enchants.tagCount(); index++) {
        short id = enchants.getCompoundTagAt(index).getShort("id");
        Enchantment enc = Enchantment.getEnchantmentByID(id);
        if (enc != null) {
          encY += 8.0F;
          arm = true;
        } 
      } 
      if (encY > enchantmentY)
        enchantmentY = encY; 
    } 
    ItemStack renderOffHand = player.getHeldItemOffhand().copy();
    if (renderOffHand.hasEffect()) {
      float encY = 0.0F;
      NBTTagList enchants = renderOffHand.getEnchantmentTagList();
      for (int index = 0; index < enchants.tagCount(); index++) {
        short id = enchants.getCompoundTagAt(index).getShort("id");
        Enchantment enc = Enchantment.getEnchantmentByID(id);
        if (enc != null) {
          encY += 8.0F;
          arm = true;
        } 
      } 
      if (encY > enchantmentY)
        enchantmentY = encY; 
    } 
    return (arm ? false : 20) + enchantmentY;
  }
  
  private String getDisplayTag(EntityPlayer player) {
    String color, name = player.getDisplayName().getFormattedText();
    if (name.contains(mc.getSession().getUsername()))
      name = "You"; 
    if (!((Boolean)this.health.getValue()).booleanValue())
      return name; 
    float health = EntityUtil.getHealth((Entity)player);
    if (health > 18.0F) {
      color = "§a";
    } else if (health > 16.0F) {
      color = "§2";
    } else if (health > 12.0F) {
      color = "§e";
    } else if (health > 8.0F) {
      color = "§6";
    } else if (health > 5.0F) {
      color = "§c";
    } else {
      color = "§4";
    } 
    String pingStr = "";
    if (((Boolean)this.ping.getValue()).booleanValue())
      try {
        int responseTime = ((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).getPlayerInfo(player.getUniqueID()).getResponseTime();
        pingStr = pingStr + responseTime + "ms ";
      } catch (Exception exception) {} 
    String popStr = " ";
    if (((Boolean)this.totemPops.getValue()).booleanValue())
      popStr = popStr + Phobos.totemPopManager.getTotemPopString(player); 
    String idString = "";
    if (((Boolean)this.entityID.getValue()).booleanValue())
      idString = idString + "ID: " + player.getEntityId() + " "; 
    String gameModeStr = "";
    if (((Boolean)this.gamemode.getValue()).booleanValue())
      if (player.isCreative()) {
        gameModeStr = gameModeStr + "[C] ";
      } else if (player.isSpectator() || player.isInvisible()) {
        gameModeStr = gameModeStr + "[I] ";
      } else {
        gameModeStr = gameModeStr + "[S] ";
      }  
    if (Math.floor(health) == health) {
      name = name + color + " " + ((health > 0.0F) ? (String)Integer.valueOf((int)Math.floor(health)) : "dead");
    } else {
      name = name + color + " " + ((health > 0.0F) ? (String)Integer.valueOf((int)health) : "dead");
    } 
    return pingStr + idString + gameModeStr + name + popStr;
  }
  
  private int getDisplayColour(EntityPlayer player) {
    int colour = -5592406;
    if (((Boolean)this.whiter.getValue()).booleanValue())
      colour = -1; 
    if (Phobos.friendManager.isFriend(player))
      return -11157267; 
    if (player.isInvisible()) {
      colour = -1113785;
    } else if (player.isSneaking() && ((Boolean)this.sneak.getValue()).booleanValue()) {
      colour = -6481515;
    } 
    return colour;
  }
  
  private double interpolate(double previous, double current, float delta) {
    return previous + (current - previous) * delta;
  }
}
