//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class VoidESP extends Module {
  private final Setting<Float> radius = register(new Setting("Radius", Float.valueOf(8.0F), Float.valueOf(0.0F), Float.valueOf(50.0F)));
  
  public Setting<Boolean> air = register(new Setting("OnlyAir", Boolean.valueOf(true)));
  
  public Setting<Boolean> noEnd = register(new Setting("NoEnd", Boolean.valueOf(true)));
  
  private Setting<Integer> updates = register(new Setting("Updates", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private Setting<Integer> voidCap = register(new Setting("VoidCap", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true)));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false)));
  
  public Setting<Double> height = register(new Setting("Height", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
  
  private Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.box.getValue()).booleanValue()));
  
  private Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Timer timer = new Timer();
  
  private List<BlockPos> voidHoles = new CopyOnWriteArrayList<>();
  
  public VoidESP() {
    super("VoidEsp", "Esps the void", Module.Category.RENDER, true, false, false);
  }
  
  public void onToggle() {
    this.timer.reset();
  }
  
  public void onLogin() {
    this.timer.reset();
  }
  
  public void onTick() {
    if (!fullNullCheck() && (!((Boolean)this.noEnd.getValue()).booleanValue() || mc.player.dimension != 1) && 
      this.timer.passedMs(((Integer)this.updates.getValue()).intValue())) {
      this.voidHoles.clear();
      this.voidHoles = findVoidHoles();
      if (this.voidHoles.size() > ((Integer)this.voidCap.getValue()).intValue())
        this.voidHoles.clear(); 
      this.timer.reset();
    } 
  }
  
  public void onRender3D(Render3DEvent event) {
    if (fullNullCheck() || (((Boolean)this.noEnd.getValue()).booleanValue() && mc.player.dimension == 1))
      return; 
    for (BlockPos pos : this.voidHoles) {
      if (RotationUtil.isInFov(pos))
        RenderUtil.drawBoxESP(pos, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue()); 
    } 
  }
  
  private List<BlockPos> findVoidHoles() {
    BlockPos playerPos = EntityUtil.getPlayerPos((EntityPlayer)mc.player);
    return (List<BlockPos>)BlockUtil.getDisc(playerPos.add(0, -playerPos.getY(), 0), ((Float)this.radius.getValue()).floatValue()).stream().filter(this::isVoid).collect(Collectors.toList());
  }
  
  private boolean isVoid(BlockPos pos) {
    return ((mc.world.getBlockState(pos).getBlock() == Blocks.AIR || (!((Boolean)this.air.getValue()).booleanValue() && mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK)) && pos.getY() < 1 && pos.getY() >= 0);
  }
}
