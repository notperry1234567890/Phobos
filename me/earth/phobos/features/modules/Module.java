package me.earth.phobos.features.modules;

import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Module extends Feature {
  private final String description;
  
  private final Category category;
  
  public Setting<Boolean> enabled = register(new Setting("Enabled", Boolean.valueOf(false)));
  
  public Setting<Boolean> drawn = register(new Setting("Drawn", Boolean.valueOf(true)));
  
  public Setting<Bind> bind = register(new Setting("Bind", new Bind(-1)));
  
  public Setting<String> displayName;
  
  public boolean hasListener;
  
  public boolean alwaysListening;
  
  public boolean hidden;
  
  public float arrayListOffset = 0.0F;
  
  public float arrayListVOffset = 0.0F;
  
  public float offset;
  
  public float vOffset;
  
  public boolean sliding;
  
  public Module(String name, String description, Category category, boolean hasListener, boolean hidden, boolean alwaysListening) {
    super(name);
    this.displayName = register(new Setting("DisplayName", name));
    this.description = description;
    this.category = category;
    this.hasListener = hasListener;
    this.hidden = hidden;
    this.alwaysListening = alwaysListening;
  }
  
  public enum Category {
    COMBAT("Combat"),
    MISC("Misc"),
    RENDER("Render"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    CLIENT("Client");
    
    private final String name;
    
    Category(String name) {
      this.name = name;
    }
    
    public String getName() {
      return this.name;
    }
  }
  
  public void onEnable() {}
  
  public void onDisable() {}
  
  public void onToggle() {}
  
  public void onLoad() {}
  
  public void onTick() {}
  
  public void onLogin() {}
  
  public void onLogout() {}
  
  public void onUpdate() {}
  
  public void onRender2D(Render2DEvent event) {}
  
  public void onRender3D(Render3DEvent event) {}
  
  public void onUnload() {}
  
  public String getDisplayInfo() {
    return null;
  }
  
  public boolean isOn() {
    return ((Boolean)this.enabled.getValue()).booleanValue();
  }
  
  public boolean isOff() {
    return !((Boolean)this.enabled.getValue()).booleanValue();
  }
  
  public void setEnabled(boolean enabled) {
    if (enabled) {
      enable();
    } else {
      disable();
    } 
  }
  
  public void enable() {
    this.enabled.setValue(Boolean.valueOf(true));
    onToggle();
    onEnable();
    if (isOn() && this.hasListener && !this.alwaysListening)
      MinecraftForge.EVENT_BUS.register(this); 
  }
  
  public void disable() {
    if (this.hasListener && !this.alwaysListening)
      MinecraftForge.EVENT_BUS.unregister(this); 
    this.enabled.setValue(Boolean.valueOf(false));
    onToggle();
    onDisable();
  }
  
  public void toggle() {
    ClientEvent event = new ClientEvent(!isEnabled() ? 1 : 0, this);
    MinecraftForge.EVENT_BUS.post((Event)event);
    if (!event.isCanceled())
      setEnabled(!isEnabled()); 
  }
  
  public String getDisplayName() {
    return (String)this.displayName.getValue();
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDisplayName(String name) {
    Module module = Phobos.moduleManager.getModuleByDisplayName(name);
    Module originalModule = Phobos.moduleManager.getModuleByName(name);
    if (module == null && originalModule == null) {
      Command.sendMessage(getDisplayName() + ", Original name: " + getName() + ", has been renamed to: " + name);
      this.displayName.setValue(name);
      return;
    } 
    Command.sendMessage("§cA module of this name already exists.");
  }
  
  public boolean isSliding() {
    return this.sliding;
  }
  
  public boolean isDrawn() {
    return ((Boolean)this.drawn.getValue()).booleanValue();
  }
  
  public void setDrawn(boolean drawn) {
    this.drawn.setValue(Boolean.valueOf(drawn));
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  public String getInfo() {
    return null;
  }
  
  public Bind getBind() {
    return (Bind)this.bind.getValue();
  }
  
  public void setBind(int key) {
    this.bind.setValue(new Bind(key));
  }
  
  public boolean listening() {
    return ((this.hasListener && isOn()) || this.alwaysListening);
  }
  
  public String getFullArrayString() {
    return getDisplayName() + "§8" + ((getDisplayInfo() != null) ? (" [§r" + getDisplayInfo() + "§8" + "]") : "");
  }
}
