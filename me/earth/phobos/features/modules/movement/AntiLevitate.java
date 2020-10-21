//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.movement;

import java.util.Objects;
import me.earth.phobos.features.modules.Module;
import net.minecraft.potion.Potion;

public class AntiLevitate extends Module {
  public AntiLevitate() {
    super("AntiLevitate", "Removes shulker levitation", Module.Category.MOVEMENT, false, false, false);
  }
  
  public void onUpdate() {
    if (mc.player.isPotionActive(Objects.<Potion>requireNonNull(Potion.getPotionFromResourceLocation("levitation"))))
      mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("levitation")); 
  }
}
