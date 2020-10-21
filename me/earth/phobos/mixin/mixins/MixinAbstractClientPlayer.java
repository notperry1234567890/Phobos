//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import java.util.UUID;
import javax.annotation.Nullable;
import me.earth.phobos.features.modules.client.Capes;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
  @Shadow
  @Nullable
  protected abstract NetworkPlayerInfo getPlayerInfo();
  
  @Inject(method = {"getLocationCape"}, at = {@At("HEAD")}, cancellable = true)
  public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
    if (Capes.getInstance().isOn()) {
      NetworkPlayerInfo info = getPlayerInfo();
      UUID uuid = null;
      if (info != null)
        uuid = getPlayerInfo().getGameProfile().getId(); 
      if (uuid != null)
        callbackInfoReturnable.setReturnValue(Capes.getCapeResource((AbstractClientPlayer)this)); 
    } 
  }
}
