//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Timer;
import net.minecraft.util.EnumParticleTypes;

public class Trails extends Module {
  private final Setting<ParticleType> type = register(new Setting("Type", ParticleType.HEART));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500)));
  
  private final Setting<Double> xOffset = register(new Setting("XOffset", Double.valueOf(0.0D), Double.valueOf(-10.0D), Double.valueOf(10.0D)));
  
  private final Setting<Double> yOffset = register(new Setting("YOffset", Double.valueOf(2.7D), Double.valueOf(-10.0D), Double.valueOf(10.0D)));
  
  private final Setting<Double> zOffset = register(new Setting("ZOffset", Double.valueOf(0.0D), Double.valueOf(-10.0D), Double.valueOf(10.0D)));
  
  public Timer timer = new Timer();
  
  public Trails() {
    super("Trails", "Renders trails.", Module.Category.RENDER, true, false, false);
  }
  
  public void onUpdate() {
    if (this.timer.passedMs(((Integer)this.delay.getValue()).intValue())) {
      mc.world.spawnParticle(((ParticleType)this.type.getValue()).particleType, mc.player.posX + ((Double)this.xOffset.getValue()).doubleValue(), mc.player.posY + ((Double)this.yOffset.getValue()).doubleValue(), mc.player.posZ + ((Double)this.zOffset.getValue()).doubleValue(), 0.0D, 0.0D, 0.0D, new int[0]);
      this.timer.reset();
    } 
  }
  
  public enum ParticleType {
    HEART((String)EnumParticleTypes.HEART),
    MOB_APPEARANCE((String)EnumParticleTypes.MOB_APPEARANCE),
    WATER_DROP((String)EnumParticleTypes.WATER_DROP),
    SLIME((String)EnumParticleTypes.SLIME),
    SNOW_SHOVEL((String)EnumParticleTypes.SNOW_SHOVEL),
    SNOWBALL((String)EnumParticleTypes.SNOWBALL),
    REDSTONE((String)EnumParticleTypes.REDSTONE),
    FOOTSTEP((String)EnumParticleTypes.FOOTSTEP),
    LAVA((String)EnumParticleTypes.LAVA),
    FLAME((String)EnumParticleTypes.FLAME),
    ENCHANTMENT_TABLE((String)EnumParticleTypes.ENCHANTMENT_TABLE),
    PORTAL((String)EnumParticleTypes.PORTAL),
    NOTE((String)EnumParticleTypes.NOTE),
    TOWN_AURA((String)EnumParticleTypes.TOWN_AURA),
    VILLAGER_HAPPY((String)EnumParticleTypes.VILLAGER_HAPPY),
    VILLAGER_ANGRY((String)EnumParticleTypes.VILLAGER_ANGRY),
    SPELL((String)EnumParticleTypes.SPELL),
    SPELL_INSTANT((String)EnumParticleTypes.SPELL_INSTANT),
    SPELL_MOB((String)EnumParticleTypes.SPELL_MOB),
    SPELL_MOB_AMBIENT((String)EnumParticleTypes.SPELL_MOB_AMBIENT),
    SPELL_WITCH((String)EnumParticleTypes.SPELL_WITCH),
    SMOKE_LARGE((String)EnumParticleTypes.SMOKE_LARGE),
    SMOKE_NORMAL((String)EnumParticleTypes.SMOKE_NORMAL),
    CRIT_MAGIC((String)EnumParticleTypes.CRIT_MAGIC),
    SUSPENDED_DEPTH((String)EnumParticleTypes.SUSPENDED_DEPTH),
    WATER_WAKE((String)EnumParticleTypes.WATER_WAKE),
    WATER_SPLASH((String)EnumParticleTypes.WATER_SPLASH),
    FIREWORKS_SPARK((String)EnumParticleTypes.FIREWORKS_SPARK),
    BARRIER((String)EnumParticleTypes.BARRIER),
    CLOUD((String)EnumParticleTypes.CLOUD),
    CRIT((String)EnumParticleTypes.CRIT),
    EXPLOSION_NORMAL((String)EnumParticleTypes.EXPLOSION_NORMAL),
    EXPLOSION_LARGE((String)EnumParticleTypes.EXPLOSION_LARGE),
    EXPLOSION_HUGE((String)EnumParticleTypes.EXPLOSION_HUGE),
    DRIP_LAVA((String)EnumParticleTypes.DRIP_LAVA),
    DRIP_WATER((String)EnumParticleTypes.DRIP_WATER);
    
    public EnumParticleTypes particleType;
    
    ParticleType(EnumParticleTypes particleType) {
      this.particleType = particleType;
    }
  }
}
