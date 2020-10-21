//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class MCF extends Module {
  private final Setting<Boolean> middleClick = register(new Setting("MiddleClick", Boolean.valueOf(true)));
  
  private final Setting<Boolean> keyboard = register(new Setting("Keyboard", Boolean.valueOf(false)));
  
  private final Setting<Bind> key = register(new Setting("KeyBind", new Bind(-1), v -> ((Boolean)this.keyboard.getValue()).booleanValue()));
  
  private boolean clicked = false;
  
  public MCF() {
    super("MCF", "Middleclick Friends.", Module.Category.MISC, true, false, false);
  }
  
  public void onUpdate() {
    if (Mouse.isButtonDown(2)) {
      if (!this.clicked && ((Boolean)this.middleClick.getValue()).booleanValue() && mc.currentScreen == null)
        onClick(); 
      this.clicked = true;
    } else {
      this.clicked = false;
    } 
  }
  
  @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
  public void onKeyInput(InputEvent.KeyInputEvent event) {
    if (((Boolean)this.keyboard.getValue()).booleanValue() && Keyboard.getEventKeyState() && !(mc.currentScreen instanceof me.earth.phobos.features.gui.PhobosGui) && ((Bind)this.key.getValue()).getKey() == Keyboard.getEventKey())
      onClick(); 
  }
  
  private void onClick() {
    RayTraceResult result = mc.objectMouseOver;
    if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
      Entity entity = result.entityHit;
      if (entity instanceof net.minecraft.entity.player.EntityPlayer)
        if (Phobos.friendManager.isFriend(entity.getName())) {
          Phobos.friendManager.removeFriend(entity.getName());
          Command.sendMessage("§c" + entity.getName() + "§r" + " unfriended.");
        } else {
          Phobos.friendManager.addFriend(entity.getName());
          Command.sendMessage("§b" + entity.getName() + "§r" + " friended.");
        }  
    } 
    this.clicked = true;
  }
}
