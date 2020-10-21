//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class StorageESP extends Module {
  private final Setting<Float> range = register(new Setting("Range", Float.valueOf(50.0F), Float.valueOf(1.0F), Float.valueOf(300.0F)));
  
  private final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  private final Setting<Boolean> chest = register(new Setting("Chest", Boolean.valueOf(true)));
  
  private final Setting<Boolean> dispenser = register(new Setting("Dispenser", Boolean.valueOf(false)));
  
  private final Setting<Boolean> shulker = register(new Setting("Shulker", Boolean.valueOf(true)));
  
  private final Setting<Boolean> echest = register(new Setting("Ender Chest", Boolean.valueOf(true)));
  
  private final Setting<Boolean> furnace = register(new Setting("Furnace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> hopper = register(new Setting("Hopper", Boolean.valueOf(false)));
  
  private final Setting<Boolean> cart = register(new Setting("Minecart", Boolean.valueOf(false)));
  
  private final Setting<Boolean> frame = register(new Setting("Item Frame", Boolean.valueOf(false)));
  
  private final Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.box.getValue()).booleanValue()));
  
  private final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public StorageESP() {
    super("StorageESP", "Highlights Containers.", Module.Category.RENDER, false, false, false);
  }
  
  public void onRender3D(Render3DEvent event) {
    Map<BlockPos, Integer> positions = new HashMap<>();
    for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
      if ((tileEntity instanceof net.minecraft.tileentity.TileEntityChest && ((Boolean)this.chest.getValue()).booleanValue()) || (tileEntity instanceof net.minecraft.tileentity.TileEntityDispenser && ((Boolean)this.dispenser.getValue()).booleanValue()) || (tileEntity instanceof net.minecraft.tileentity.TileEntityShulkerBox && ((Boolean)this.shulker.getValue()).booleanValue()) || (tileEntity instanceof net.minecraft.tileentity.TileEntityEnderChest && ((Boolean)this.echest.getValue()).booleanValue()) || (tileEntity instanceof net.minecraft.tileentity.TileEntityFurnace && ((Boolean)this.furnace.getValue()).booleanValue()) || (tileEntity instanceof net.minecraft.tileentity.TileEntityHopper && ((Boolean)this.hopper.getValue()).booleanValue())) {
        BlockPos pos = tileEntity.getPos();
        if (mc.player.getDistanceSq(pos) <= MathUtil.square(((Float)this.range.getValue()).floatValue())) {
          int color = getTileEntityColor(tileEntity);
          if (color != -1)
            positions.put(pos, Integer.valueOf(color)); 
        } 
      } 
    } 
    for (Entity entity : mc.world.loadedEntityList) {
      if ((entity instanceof EntityItemFrame && ((Boolean)this.frame.getValue()).booleanValue()) || (entity instanceof net.minecraft.entity.item.EntityMinecartChest && ((Boolean)this.cart.getValue()).booleanValue())) {
        BlockPos pos = entity.getPosition();
        if (mc.player.getDistanceSq(pos) <= MathUtil.square(((Float)this.range.getValue()).floatValue())) {
          int color = getEntityColor(entity);
          if (color != -1)
            positions.put(pos, Integer.valueOf(color)); 
        } 
      } 
    } 
    for (Map.Entry<BlockPos, Integer> entry : positions.entrySet()) {
      BlockPos blockPos = entry.getKey();
      int color = ((Integer)entry.getValue()).intValue();
      RenderUtil.drawBoxESP(blockPos, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(color), false, new Color(color), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
    } 
  }
  
  private int getTileEntityColor(TileEntity tileEntity) {
    if (tileEntity instanceof net.minecraft.tileentity.TileEntityChest)
      return ColorUtil.Colors.BLUE; 
    if (tileEntity instanceof net.minecraft.tileentity.TileEntityShulkerBox)
      return ColorUtil.Colors.RED; 
    if (tileEntity instanceof net.minecraft.tileentity.TileEntityEnderChest)
      return ColorUtil.Colors.PURPLE; 
    if (tileEntity instanceof net.minecraft.tileentity.TileEntityFurnace)
      return ColorUtil.Colors.GRAY; 
    if (tileEntity instanceof net.minecraft.tileentity.TileEntityHopper)
      return ColorUtil.Colors.DARK_RED; 
    if (tileEntity instanceof net.minecraft.tileentity.TileEntityDispenser)
      return ColorUtil.Colors.ORANGE; 
    return -1;
  }
  
  private int getEntityColor(Entity entity) {
    if (entity instanceof net.minecraft.entity.item.EntityMinecartChest)
      return ColorUtil.Colors.ORANGE; 
    if (entity instanceof EntityItemFrame && ((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof net.minecraft.item.ItemShulkerBox)
      return ColorUtil.Colors.YELLOW; 
    if (entity instanceof EntityItemFrame && !(((EntityItemFrame)entity).getDisplayedItem().getItem() instanceof net.minecraft.item.ItemShulkerBox))
      return ColorUtil.Colors.ORANGE; 
    return -1;
  }
}
