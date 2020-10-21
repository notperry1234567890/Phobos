//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.mixin.mixins;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.DeathEvent;
import me.earth.phobos.util.Util;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetHandlerPlayClient.class})
public class MixinNetHandlerPlayClient {
  @Inject(method = {"handleEntityMetadata"}, at = {@At("RETURN")}, cancellable = true)
  private void handleEntityMetadataHook(SPacketEntityMetadata packetIn, CallbackInfo info) {
    if (Util.mc.world != null) {
      Entity entity = Util.mc.world.getEntityByID(packetIn.getEntityId());
      if (entity instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer)entity;
        if (player.getHealth() <= 0.0F) {
          MinecraftForge.EVENT_BUS.post((Event)new DeathEvent(player));
          if (Phobos.totemPopManager != null)
            Phobos.totemPopManager.onDeath(player); 
        } 
      } 
    } 
  }
}
