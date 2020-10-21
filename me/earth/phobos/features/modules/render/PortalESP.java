//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PortalESP extends Module {
  private int cooldownTicks;
  
  private final ArrayList<BlockPos> blockPosArrayList = new ArrayList<>();
  
  private final Setting<Integer> distance = register(new Setting("Distance", Integer.valueOf(60), Integer.valueOf(10), Integer.valueOf(100)));
  
  private final Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.box.getValue()).booleanValue()));
  
  private final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public PortalESP() {
    super("PortalESP", "Draws portals", Module.Category.RENDER, true, false, false);
  }
  
  @SubscribeEvent
  public void onTickEvent(TickEvent.ClientTickEvent event) {
    if (mc.world == null)
      return; 
    if (this.cooldownTicks < 1) {
      this.blockPosArrayList.clear();
      compileDL();
      this.cooldownTicks = 80;
    } 
    this.cooldownTicks--;
  }
  
  public void onRender3D(Render3DEvent event) {
    this;
    if (mc.world == null)
      return; 
    for (BlockPos pos : this.blockPosArrayList)
      RenderUtil.drawBoxESP(pos, new Color(204, 0, 153, 255), false, new Color(204, 0, 153, 255), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false); 
  }
  
  private void compileDL() {
    if (mc.world == null || mc.player == null)
      return; 
    for (int x = (int)mc.player.posX - ((Integer)this.distance.getValue()).intValue(); x <= (int)mc.player.posX + ((Integer)this.distance.getValue()).intValue(); x++) {
      for (int y = (int)mc.player.posZ - ((Integer)this.distance.getValue()).intValue(); y <= (int)mc.player.posZ + ((Integer)this.distance.getValue()).intValue(); y++) {
        for (int z = (int)Math.max(mc.player.posY - ((Integer)this.distance.getValue()).intValue(), 0.0D); z <= Math.min(mc.player.posY + ((Integer)this.distance.getValue()).intValue(), 255.0D); z++) {
          BlockPos pos = new BlockPos(x, y, z);
          Block block = mc.world.getBlockState(pos).getBlock();
          if (block == Blocks.PORTAL)
            this.blockPosArrayList.add(pos); 
        } 
      } 
    } 
  }
}
