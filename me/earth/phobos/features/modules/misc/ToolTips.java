//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ToolTips extends Module {
  public Setting<Boolean> maps = register(new Setting("Maps", Boolean.valueOf(true)));
  
  public Setting<Boolean> shulkers = register(new Setting("ShulkerViewer", Boolean.valueOf(true)));
  
  public Setting<Bind> peek = register(new Setting("Peek", new Bind(-1)));
  
  public Setting<Boolean> shulkerSpy = register(new Setting("ShulkerSpy", Boolean.valueOf(true)));
  
  public Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> ((Boolean)this.shulkerSpy.getValue()).booleanValue()));
  
  public Setting<Boolean> own = register(new Setting("OwnShulker", Boolean.valueOf(true), v -> ((Boolean)this.shulkerSpy.getValue()).booleanValue()));
  
  public Setting<Integer> cooldown = register(new Setting("ShowForS", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(5), v -> ((Boolean)this.shulkerSpy.getValue()).booleanValue()));
  
  public Setting<Boolean> textColor = register(new Setting("TextColor", Boolean.valueOf(false), v -> ((Boolean)this.shulkers.getValue()).booleanValue()));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.textColor.getValue()).booleanValue()));
  
  public Setting<Boolean> offsets = register(new Setting("Offsets", Boolean.valueOf(false)));
  
  private final Setting<Integer> yPerPlayer = register(new Setting("Y/Player", Integer.valueOf(18), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private final Setting<Integer> xOffset = register(new Setting("XOffset", Integer.valueOf(4), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private final Setting<Integer> yOffset = register(new Setting("YOffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private final Setting<Integer> trOffset = register(new Setting("TROffset", Integer.valueOf(2), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  public Setting<Integer> invH = register(new Setting("InvH", Integer.valueOf(3), v -> ((Boolean)this.offsets.getValue()).booleanValue()));
  
  private static final ResourceLocation MAP = new ResourceLocation("textures/map/map_background.png");
  
  private static final ResourceLocation SHULKER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
  
  private static ToolTips INSTANCE = new ToolTips();
  
  public Map<EntityPlayer, ItemStack> spiedPlayers = new ConcurrentHashMap<>();
  
  public Map<EntityPlayer, Timer> playerTimers = new ConcurrentHashMap<>();
  
  private int textRadarY = 0;
  
  public ToolTips() {
    super("ToolTips", "Several tweaks for tooltips.", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static ToolTips getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ToolTips(); 
    return INSTANCE;
  }
  
  public void onUpdate() {
    if (fullNullCheck() || !((Boolean)this.shulkerSpy.getValue()).booleanValue())
      return; 
    if (((Bind)this.peek.getValue()).getKey() != -1 && mc.currentScreen instanceof GuiContainer && Keyboard.isKeyDown(((Bind)this.peek.getValue()).getKey())) {
      Slot slot = ((GuiContainer)mc.currentScreen).getSlotUnderMouse();
      if (slot != null) {
        ItemStack stack = slot.getStack();
        if (stack != null && stack.getItem() instanceof ItemShulkerBox)
          displayInv(stack, (String)null); 
      } 
    } 
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player != null && 
        player.getHeldItemMainhand() != null && player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox && 
        !EntityUtil.isFakePlayer(player) && (((Boolean)this.own.getValue()).booleanValue() || !mc.player.equals(player))) {
        ItemStack stack = player.getHeldItemMainhand();
        this.spiedPlayers.put(player, stack);
      } 
    } 
  }
  
  public void onRender2D(Render2DEvent event) {
    if (fullNullCheck() || !((Boolean)this.shulkerSpy.getValue()).booleanValue() || !((Boolean)this.render.getValue()).booleanValue())
      return; 
    int x = -4 + ((Integer)this.xOffset.getValue()).intValue();
    int y = 10 + ((Integer)this.yOffset.getValue()).intValue();
    this.textRadarY = 0;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (this.spiedPlayers.get(player) != null) {
        if (player.getHeldItemMainhand() == null || !(player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox)) {
          Timer playerTimer = this.playerTimers.get(player);
          if (playerTimer == null) {
            Timer timer = new Timer();
            timer.reset();
            this.playerTimers.put(player, timer);
          } else if (playerTimer.passedS(((Integer)this.cooldown.getValue()).intValue())) {
            continue;
          } 
        } else if (player.getHeldItemMainhand().getItem() instanceof ItemShulkerBox) {
          Timer playerTimer = this.playerTimers.get(player);
          if (playerTimer != null) {
            playerTimer.reset();
            this.playerTimers.put(player, playerTimer);
          } 
        } 
        ItemStack stack = this.spiedPlayers.get(player);
        renderShulkerToolTip(stack, x, y, player.getName());
        y += ((Integer)this.yPerPlayer.getValue()).intValue() + 60;
        this.textRadarY = y - 10 - ((Integer)this.yOffset.getValue()).intValue() + ((Integer)this.trOffset.getValue()).intValue();
      } 
    } 
  }
  
  public int getTextRadarY() {
    return this.textRadarY;
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void makeTooltip(ItemTooltipEvent event) {}
  
  @SubscribeEvent
  public void renderTooltip(RenderTooltipEvent.PostText event) {
    if (((Boolean)this.maps.getValue()).booleanValue() && !event.getStack().isEmpty() && event.getStack().getItem() instanceof net.minecraft.item.ItemMap) {
      MapData mapData = Items.FILLED_MAP.getMapData(event.getStack(), (World)mc.world);
      if (mapData != null) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        RenderHelper.disableStandardItemLighting();
        mc.getTextureManager().bindTexture(MAP);
        Tessellator instance = Tessellator.getInstance();
        BufferBuilder buffer = instance.getBuffer();
        int n = 7;
        float n2 = 135.0F;
        float n3 = 0.5F;
        GlStateManager.translate(event.getX(), event.getY() - n2 * n3 - 5.0F, 0.0F);
        GlStateManager.scale(n3, n3, n3);
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(-n, n2, 0.0D).tex(0.0D, 1.0D).endVertex();
        buffer.pos(n2, n2, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(n2, -n, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(-n, -n, 0.0D).tex(0.0D, 0.0D).endVertex();
        instance.draw();
        mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
      } 
    } 
  }
  
  public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
    NBTTagCompound tagCompound = stack.getTagCompound();
    if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
      NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
      if (blockEntityTag.hasKey("Items", 9)) {
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        mc.getTextureManager().bindTexture(SHULKER_GUI_TEXTURE);
        RenderUtil.drawTexturedRect(x, y, 0, 0, 176, 16, 500);
        RenderUtil.drawTexturedRect(x, y + 16, 0, 16, 176, 54 + ((Integer)this.invH.getValue()).intValue(), 500);
        RenderUtil.drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
        GlStateManager.disableDepth();
        Color color = new Color(0, 0, 0, 255);
        if (((Boolean)this.textColor.getValue()).booleanValue())
          color = new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()); 
        this.renderer.drawStringWithShadow((name == null) ? stack.getDisplayName() : name, (x + 8), (y + 6), ColorUtil.toRGBA(color));
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);
        for (int i = 0; i < nonnulllist.size(); i++) {
          int iX = x + i % 9 * 18 + 8;
          int iY = y + i / 9 * 18 + 18;
          ItemStack itemStack = (ItemStack)nonnulllist.get(i);
          (mc.getRenderItem()).zLevel = 501.0F;
          RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
          RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, iY, null);
          (mc.getRenderItem()).zLevel = 0.0F;
        } 
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      } 
    } 
  }
  
  public static void displayInv(ItemStack stack, String name) {
    try {
      Item item = stack.getItem();
      TileEntityShulkerBox entityBox = new TileEntityShulkerBox();
      ItemShulkerBox shulker = (ItemShulkerBox)item;
      entityBox.blockType = shulker.getBlock();
      entityBox.setWorld((World)mc.world);
      ItemStackHelper.loadAllItems(stack.getTagCompound().getCompoundTag("BlockEntityTag"), entityBox.items);
      entityBox.readFromNBT(stack.getTagCompound().getCompoundTag("BlockEntityTag"));
      entityBox.setCustomName((name == null) ? stack.getDisplayName() : name);
      (new Thread(() -> {
            try {
              Thread.sleep(200L);
            } catch (InterruptedException interruptedException) {}
            mc.player.displayGUIChest((IInventory)entityBox);
          })).start();
    } catch (Exception exception) {}
  }
}
