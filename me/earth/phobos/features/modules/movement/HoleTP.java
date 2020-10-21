//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleTP extends Module {
  private static HoleTP INSTANCE = new HoleTP();
  
  private final double[] oneblockPositions = new double[] { 0.42D, 0.75D };
  
  private int packets;
  
  private boolean jumped = false;
  
  public HoleTP() {
    super("HoleTP", "Teleports you in a hole.", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static HoleTP getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleTP(); 
    return INSTANCE;
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 1 && (!Phobos.moduleManager.isModuleEnabled(Speed.class) || ((Speed)Phobos.moduleManager.getModuleByClass(Speed.class)).mode.getValue() == Speed.Mode.INSTANT) && !Phobos.moduleManager.isModuleEnabled(Strafe.class)) {
      if (!mc.player.onGround) {
        if (mc.gameSettings.keyBindJump.isKeyDown())
          this.jumped = true; 
      } else {
        this.jumped = false;
      } 
      if (!this.jumped && mc.player.fallDistance < 0.5D && isInHole() && mc.player.posY - getNearestBlockBelow() <= 1.125D && mc.player.posY - getNearestBlockBelow() <= 0.95D && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
        if (!mc.player.onGround)
          this.packets++; 
        if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
          BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
          for (double position : this.oneblockPositions)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position((blockPos.getX() + 0.5F), mc.player.posY - position, (blockPos.getZ() + 0.5F), true)); 
          mc.player.setPosition((blockPos.getX() + 0.5F), getNearestBlockBelow() + 0.1D, (blockPos.getZ() + 0.5F));
          this.packets = 0;
        } 
      } 
    } 
  }
  
  private boolean isInHole() {
    BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
    IBlockState blockState = mc.world.getBlockState(blockPos);
    return isBlockValid(blockState, blockPos);
  }
  
  private double getNearestBlockBelow() {
    for (double y = mc.player.posY; y > 0.0D; y -= 0.001D) {
      if (!(mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock() instanceof net.minecraft.block.BlockSlab) && mc.world.getBlockState(new BlockPos(mc.player.posX, y, mc.player.posZ)).getBlock().getDefaultState().getCollisionBoundingBox((IBlockAccess)mc.world, new BlockPos(0, 0, 0)) != null)
        return y; 
    } 
    return -1.0D;
  }
  
  private boolean isBlockValid(IBlockState blockState, BlockPos blockPos) {
    if (blockState.getBlock() != Blocks.AIR)
      return false; 
    if (mc.player.getDistanceSq(blockPos) < 1.0D)
      return false; 
    if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
      return false; 
    if (mc.world.getBlockState(blockPos.up(2)).getBlock() != Blocks.AIR)
      return false; 
    return (isBedrockHole(blockPos) || isObbyHole(blockPos) || isBothHole(blockPos) || isElseHole(blockPos));
  }
  
  private boolean isObbyHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
    for (BlockPos pos : touchingBlocks) {
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.OBSIDIAN)
        return false; 
    } 
    return true;
  }
  
  private boolean isBedrockHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
    for (BlockPos pos : touchingBlocks) {
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() == Blocks.AIR || touchingState.getBlock() != Blocks.BEDROCK)
        return false; 
    } 
    return true;
  }
  
  private boolean isBothHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
    for (BlockPos pos : touchingBlocks) {
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() == Blocks.AIR || (touchingState.getBlock() != Blocks.BEDROCK && touchingState.getBlock() != Blocks.OBSIDIAN))
        return false; 
    } 
    return true;
  }
  
  private boolean isElseHole(BlockPos blockPos) {
    BlockPos[] touchingBlocks = { blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.down() };
    for (BlockPos pos : touchingBlocks) {
      IBlockState touchingState = mc.world.getBlockState(pos);
      if (touchingState.getBlock() == Blocks.AIR || !touchingState.isFullBlock())
        return false; 
    } 
    return true;
  }
}
