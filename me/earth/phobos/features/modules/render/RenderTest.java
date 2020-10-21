//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.render;

import java.awt.Color;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.RenderUtil;

public class RenderTest extends Module {
  public RenderTest() {
    super("RenderTest", "RenderTest", Module.Category.RENDER, true, false, false);
  }
  
  public void onRender3D(Render3DEvent event) {
    RenderUtil.drawBetterGradientBox(mc.player.getPosition(), new Color(255, 0, 0, 255), new Color(0, 255, 0, 255), new Color(0, 0, 255, 255));
  }
}
