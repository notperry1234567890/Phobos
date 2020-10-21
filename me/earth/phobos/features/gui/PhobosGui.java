//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.gui;

import java.io.IOException;
import java.util.ArrayList;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.components.Component;
import me.earth.phobos.features.gui.components.items.Item;
import me.earth.phobos.features.gui.components.items.buttons.Button;
import me.earth.phobos.features.gui.components.items.buttons.ModuleButton;
import me.earth.phobos.features.modules.Module;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class PhobosGui extends GuiScreen {
  private static PhobosGui phobosGui;
  
  private final ArrayList<Component> components = new ArrayList<>();
  
  private static PhobosGui INSTANCE = new PhobosGui();
  
  public PhobosGui() {
    setInstance();
    load();
  }
  
  public static PhobosGui getInstance() {
    if (INSTANCE == null)
      INSTANCE = new PhobosGui(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static PhobosGui getClickGui() {
    return getInstance();
  }
  
  private void load() {
    int x = -84;
    for (Module.Category category : Phobos.moduleManager.getCategories()) {
      x += 90;
      this.components.add(new Component(category.getName(), x, 4, true) {
            public void setupItems() {
              Phobos.moduleManager.getModulesByCategory(category).forEach(module -> {
                    if (!module.hidden)
                      addButton((Button)new ModuleButton(module)); 
                  });
            }
          });
    } 
    this.components.forEach(components -> components.getItems().sort(()));
  }
  
  public void updateModule(Module module) {
    for (Component component : this.components) {
      for (Item item : component.getItems()) {
        if (item instanceof ModuleButton) {
          ModuleButton button = (ModuleButton)item;
          Module mod = button.getModule();
          if (module != null && module.equals(mod))
            button.initSettings(); 
        } 
      } 
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    checkMouseWheel();
    drawDefaultBackground();
    this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
  }
  
  public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
    this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
  }
  
  public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public final ArrayList<Component> getComponents() {
    return this.components;
  }
  
  public void checkMouseWheel() {
    int dWheel = Mouse.getDWheel();
    if (dWheel < 0) {
      this.components.forEach(component -> component.setY(component.getY() - 10));
    } else if (dWheel > 0) {
      this.components.forEach(component -> component.setY(component.getY() + 10));
    } 
  }
  
  public int getTextOffset() {
    return -6;
  }
  
  public Component getComponentByName(String name) {
    for (Component component : this.components) {
      if (component.getName().equalsIgnoreCase(name))
        return component; 
    } 
    return null;
  }
  
  public void keyTyped(char typedChar, int keyCode) throws IOException {
    super.keyTyped(typedChar, keyCode);
    this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
  }
}
