//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class Step extends Module {
  public Setting<Boolean> vanilla = register(new Setting("Vanilla", Boolean.valueOf(false)));
  
  public Setting<Float> stepHeightVanilla = register(new Setting("VHeight", Float.valueOf(2.0F), Float.valueOf(0.0F), Float.valueOf(4.0F), v -> ((Boolean)this.vanilla.getValue()).booleanValue()));
  
  public Setting<Integer> stepHeight = register(new Setting("Height", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(4), v -> !((Boolean)this.vanilla.getValue()).booleanValue()));
  
  public Setting<Boolean> spoof = register(new Setting("Spoof", Boolean.valueOf(true), v -> !((Boolean)this.vanilla.getValue()).booleanValue()));
  
  public Setting<Integer> ticks = register(new Setting("Delay", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(25), v -> (((Boolean)this.spoof.getValue()).booleanValue() && !((Boolean)this.vanilla.getValue()).booleanValue())));
  
  public Setting<Boolean> turnOff = register(new Setting("Disable", Boolean.valueOf(false), v -> !((Boolean)this.vanilla.getValue()).booleanValue()));
  
  public Setting<Boolean> check = register(new Setting("Check", Boolean.valueOf(true), v -> !((Boolean)this.vanilla.getValue()).booleanValue()));
  
  public Setting<Boolean> small = register(new Setting("Offset", Boolean.valueOf(false), v -> (((Integer)this.stepHeight.getValue()).intValue() > 1 && !((Boolean)this.vanilla.getValue()).booleanValue())));
  
  private final double[] oneblockPositions = new double[] { 0.42D, 0.75D };
  
  private final double[] twoblockPositions = new double[] { 0.4D, 0.75D, 0.5D, 0.41D, 0.83D, 1.16D, 1.41D, 1.57D, 1.58D, 1.42D };
  
  private final double[] futurePositions = new double[] { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D };
  
  final double[] twoFiveOffset = new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D };
  
  private final double[] threeBlockPositions = new double[] { 
      0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 
      1.51D, 1.9D, 2.21D, 2.45D, 2.43D };
  
  private final double[] fourBlockPositions = new double[] { 
      0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D, 1.78D, 1.63D, 
      1.51D, 1.9D, 2.21D, 2.45D, 2.43D, 2.78D, 2.63D, 2.51D, 2.9D, 3.21D, 
      3.45D, 3.43D };
  
  private double[] selectedPositions = new double[0];
  
  private int packets;
  
  private static Step instance;
  
  public Step() {
    super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
    instance = this;
  }
  
  public static Step getInstance() {
    if (instance == null)
      instance = new Step(); 
    return instance;
  }
  
  public void onToggle() {
    mc.player.stepHeight = 0.6F;
  }
  
  public void onUpdate() {
    if (((Boolean)this.vanilla.getValue()).booleanValue()) {
      mc.player.stepHeight = ((Float)this.stepHeightVanilla.getValue()).floatValue();
      return;
    } 
    switch (((Integer)this.stepHeight.getValue()).intValue()) {
      case 1:
        this.selectedPositions = this.oneblockPositions;
        break;
      case 2:
        this.selectedPositions = ((Boolean)this.small.getValue()).booleanValue() ? this.twoblockPositions : this.futurePositions;
        break;
      case 3:
        this.selectedPositions = this.twoFiveOffset;
      case 4:
        this.selectedPositions = this.fourBlockPositions;
        break;
    } 
    if (mc.player.collidedHorizontally && mc.player.onGround)
      this.packets++; 
    AxisAlignedBB bb = mc.player.getEntityBoundingBox();
    if (((Boolean)this.check.getValue()).booleanValue())
      for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1.0D); x++) {
        for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1.0D); z++) {
          Block block = mc.world.getBlockState(new BlockPos(x, bb.maxY + 1.0D, z)).getBlock();
          if (!(block instanceof net.minecraft.block.BlockAir))
            return; 
        } 
      }  
    if (mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.collidedVertically && mc.player.fallDistance == 0.0F && !mc.gameSettings.keyBindJump.pressed && mc.player.collidedHorizontally && !mc.player.isOnLadder() && (this.packets > this.selectedPositions.length - 2 || (((Boolean)this.spoof.getValue()).booleanValue() && this.packets > ((Integer)this.ticks.getValue()).intValue()))) {
      for (double position : this.selectedPositions)
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + position, mc.player.posZ, true)); 
      mc.player.setPosition(mc.player.posX, mc.player.posY + this.selectedPositions[this.selectedPositions.length - 1], mc.player.posZ);
      this.packets = 0;
      if (((Boolean)this.turnOff.getValue()).booleanValue())
        disable(); 
    } 
  }
}
