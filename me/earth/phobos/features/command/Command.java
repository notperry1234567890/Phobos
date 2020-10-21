//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentBase;

public abstract class Command extends Feature {
  protected String name;
  
  protected String[] commands;
  
  public Command(String name) {
    super(name);
    this.name = name;
    this.commands = new String[] { "" };
  }
  
  public Command(String name, String[] commands) {
    super(name);
    this.name = name;
    this.commands = commands;
  }
  
  public abstract void execute(String[] paramArrayOfString);
  
  public static void sendMessage(String message, boolean notification) {
    sendSilentMessage(Phobos.commandManager.getClientMessage() + " " + "§r" + message);
    if (notification)
      Phobos.notificationManager.addNotification(message, 3000L); 
  }
  
  public static void sendMessage(String message) {
    sendSilentMessage(Phobos.commandManager.getClientMessage() + " " + "§r" + message);
  }
  
  public static void sendSilentMessage(String message) {
    if (nullCheck())
      return; 
    mc.player.sendMessage((ITextComponent)new ChatMessage(message));
  }
  
  public String getName() {
    return this.name;
  }
  
  public String[] getCommands() {
    return this.commands;
  }
  
  public static String getCommandPrefix() {
    return Phobos.commandManager.getPrefix();
  }
  
  public static class ChatMessage extends TextComponentBase {
    private final String text;
    
    public ChatMessage(String text) {
      Pattern pattern = Pattern.compile("&[0123456789abcdefrlosmk]");
      Matcher matcher = pattern.matcher(text);
      StringBuffer stringBuffer = new StringBuffer();
      while (matcher.find()) {
        String replacement = "§" + matcher.group().substring(1);
        matcher.appendReplacement(stringBuffer, replacement);
      } 
      matcher.appendTail(stringBuffer);
      this.text = stringBuffer.toString();
    }
    
    public String getUnformattedComponentText() {
      return this.text;
    }
    
    public ITextComponent createCopy() {
      return (ITextComponent)new ChatMessage(this.text);
    }
  }
}
