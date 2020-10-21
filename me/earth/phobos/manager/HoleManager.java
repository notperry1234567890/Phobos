//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.client.Managers;
import me.earth.phobos.features.modules.combat.HoleFiller;
import me.earth.phobos.features.modules.movement.HoleTP;
import me.earth.phobos.features.modules.render.HoleESP;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleManager extends Feature implements Runnable {
  private static final BlockPos[] surroundOffset = BlockUtil.toBlockPos(EntityUtil.getOffsets(0, true));
  
  private List<BlockPos> holes = new ArrayList<>();
  
  private final List<BlockPos> midSafety = new ArrayList<>();
  
  private final Timer syncTimer = new Timer();
  
  private ScheduledExecutorService executorService;
  
  private int lastUpdates = 0;
  
  private Thread thread;
  
  private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
  
  private final Timer holeTimer = new Timer();
  
  public void update() {
    if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.WHILE) {
      if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncTimer.passedMs(((Integer)(Managers.getInstance()).holeSync.getValue()).intValue())) {
        if (this.thread == null) {
          this.thread = new Thread(this);
        } else if (this.syncTimer.passedMs(((Integer)(Managers.getInstance()).holeSync.getValue()).intValue()) && !this.shouldInterrupt.get()) {
          this.shouldInterrupt.set(true);
          this.syncTimer.reset();
          return;
        } 
        if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive()))
          this.thread = new Thread(this); 
        if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
          try {
            this.thread.start();
          } catch (Exception e) {
            e.printStackTrace();
          } 
          this.syncTimer.reset();
        } 
      } 
    } else if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.WHILE) {
      if (this.executorService == null || this.executorService.isTerminated() || this.executorService.isShutdown() || this.syncTimer.passedMs(10000L) || this.lastUpdates != ((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue()) {
        this.lastUpdates = ((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue();
        if (this.executorService != null)
          this.executorService.shutdown(); 
        this.executorService = getExecutor();
      } 
    } else if (this.holeTimer.passedMs(((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue()) && !fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn() || HoleTP.getInstance().isOn())) {
      this.holes = calcHoles();
      this.holeTimer.reset();
    } 
  }
  
  public void settingChanged() {
    if (this.executorService != null)
      this.executorService.shutdown(); 
    if (this.thread != null)
      this.shouldInterrupt.set(true); 
  }
  
  private ScheduledExecutorService getExecutor() {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    service.scheduleAtFixedRate(this, 0L, ((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue(), TimeUnit.MILLISECONDS);
    return service;
  }
  
  public void run() {
    if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.WHILE)
      while (true) {
        if (this.shouldInterrupt.get()) {
          this.shouldInterrupt.set(false);
          this.syncTimer.reset();
          Thread.currentThread().interrupt();
          return;
        } 
        if (!fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn() || HoleTP.getInstance().isOn()))
          this.holes = calcHoles(); 
        try {
          Thread.sleep(((Integer)(Managers.getInstance()).holeUpdates.getValue()).intValue());
        } catch (InterruptedException e) {
          this.thread.interrupt();
          e.printStackTrace();
        } 
      }  
    if ((Managers.getInstance()).holeThread.getValue() == Managers.ThreadMode.POOL && 
      !fullNullCheck() && (HoleESP.getInstance().isOn() || HoleFiller.getInstance().isOn()))
      this.holes = calcHoles(); 
  }
  
  public List<BlockPos> getHoles() {
    return this.holes;
  }
  
  public List<BlockPos> getMidSafety() {
    return this.midSafety;
  }
  
  public List<BlockPos> getSortedHoles() {
    this.holes.sort(Comparator.comparingDouble(hole -> mc.player.getDistanceSq(hole)));
    return getHoles();
  }
  
  public List<BlockPos> calcHoles() {
    List<BlockPos> safeSpots = new ArrayList<>();
    this.midSafety.clear();
    List<BlockPos> positions = BlockUtil.getSphere(EntityUtil.getPlayerPos((EntityPlayer)mc.player), ((Float)(Managers.getInstance()).holeRange.getValue()).floatValue(), ((Float)(Managers.getInstance()).holeRange.getValue()).intValue(), false, true, 0);
    for (BlockPos pos : positions) {
      if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
        continue; 
      if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR))
        continue; 
      if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR))
        continue; 
      boolean isSafe = true;
      boolean midSafe = true;
      for (BlockPos offset : surroundOffset) {
        Block block = mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
        if (BlockUtil.isBlockUnSolid(block))
          midSafe = false; 
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL)
          isSafe = false; 
      } 
      if (isSafe)
        safeSpots.add(pos); 
      if (midSafe)
        this.midSafety.add(pos); 
    } 
    return safeSpots;
  }
  
  public boolean isSafe(BlockPos pos) {
    boolean isSafe = true;
    for (BlockPos offset : surroundOffset) {
      Block block = mc.world.getBlockState(pos.add((Vec3i)offset)).getBlock();
      if (block != Blocks.BEDROCK) {
        isSafe = false;
        break;
      } 
    } 
    return isSafe;
  }
}
