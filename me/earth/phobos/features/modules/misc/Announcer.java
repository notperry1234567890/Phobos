package me.earth.phobos.features.modules.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.FileManager;

public class Announcer extends Module {
  private final Setting<Boolean> join = register(new Setting("Join", Boolean.valueOf(true)));
  
  private final Setting<Boolean> leave = register(new Setting("Leave", Boolean.valueOf(true)));
  
  private final Setting<Boolean> eat = register(new Setting("Eat", Boolean.valueOf(true)));
  
  private final Setting<Boolean> walk = register(new Setting("Walk", Boolean.valueOf(true)));
  
  private final Setting<Boolean> mine = register(new Setting("Mine", Boolean.valueOf(true)));
  
  private final Setting<Boolean> place = register(new Setting("Place", Boolean.valueOf(true)));
  
  private final Setting<Boolean> totem = register(new Setting("TotemPop", Boolean.valueOf(true)));
  
  private final Setting<Boolean> random = register(new Setting("Random", Boolean.valueOf(true)));
  
  private final Setting<Boolean> greentext = register(new Setting("Greentext", Boolean.valueOf(false)));
  
  private final Setting<Boolean> loadFiles = register(new Setting("LoadFiles", Boolean.valueOf(false)));
  
  private final Setting<Integer> delay = register(new Setting("SendDelay", Integer.valueOf(40)));
  
  private final Setting<Integer> queueSize = register(new Setting("QueueSize", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(100)));
  
  private final Setting<Integer> mindistance = register(new Setting("Min Distance", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(100)));
  
  private final Setting<Boolean> clearQueue = register(new Setting("ClearQueue", Boolean.valueOf(false)));
  
  private static final String directory = "phobos/announcer/";
  
  private Map<Action, ArrayList<String>> loadedMessages = new HashMap<>();
  
  private Map<Action, Message> queue = new HashMap<>();
  
  public Announcer() {
    super("Announcer", "How to get muted quick.", Module.Category.MISC, true, false, false);
  }
  
  public void onLoad() {
    loadMessages();
  }
  
  public void onEnable() {
    loadMessages();
  }
  
  public void onUpdate() {
    if (((Boolean)this.loadFiles.getValue()).booleanValue()) {
      loadMessages();
      Command.sendMessage("<Announcer> Loaded messages.");
      this.loadFiles.setValue(Boolean.valueOf(false));
    } 
  }
  
  public void loadMessages() {
    HashMap<Action, ArrayList<String>> newLoadedMessages = new HashMap<>();
    for (Action action : Action.values()) {
      String fileName = "phobos/announcer/" + action.getName() + ".txt";
      List<String> fileInput = FileManager.readTextFileAllLines(fileName);
      Iterator<String> i = fileInput.iterator();
      ArrayList<String> msgs = new ArrayList<>();
      while (i.hasNext()) {
        String string = i.next();
        if (!string.replaceAll("\\s", "").isEmpty())
          msgs.add(string); 
      } 
      if (msgs.isEmpty())
        msgs.add(action.getStandartMessage()); 
      newLoadedMessages.put(action, msgs);
    } 
    this.loadedMessages = newLoadedMessages;
  }
  
  private String getMessage(Action action, int number, String info) {
    return "";
  }
  
  private Action getRandomAction() {
    Random rnd = new Random();
    int index = rnd.nextInt(7);
    int i = 0;
    for (Action action : Action.values()) {
      if (i == index)
        return action; 
      i++;
    } 
    return Action.WALK;
  }
  
  public static class Message {
    public final Announcer.Action action;
    
    public final String name;
    
    public final int amount;
    
    public Message(Announcer.Action action, String name, int amount) {
      this.action = action;
      this.name = name;
      this.amount = amount;
    }
  }
  
  public enum Action {
    JOIN("Join", "Welcome _!"),
    LEAVE("Leave", "Goodbye _!"),
    EAT("Eat", "I just ate % _!"),
    WALK("Walk", "I just walked % Blocks!"),
    MINE("Mine", "I mined % _!"),
    PLACE("Place", "I just placed % _!"),
    TOTEM("Totem", "_ just popped % Totems!");
    
    private final String name;
    
    private final String standartMessage;
    
    Action(String name, String standartMessage) {
      this.name = name;
      this.standartMessage = standartMessage;
    }
    
    public String getName() {
      return this.name;
    }
    
    public String getStandartMessage() {
      return this.standartMessage;
    }
  }
}
