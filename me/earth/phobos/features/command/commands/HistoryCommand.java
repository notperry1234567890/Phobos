package me.earth.phobos.features.command.commands;

import java.util.List;
import java.util.UUID;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.util.PlayerUtil;

public class HistoryCommand extends Command {
  public HistoryCommand() {
    super("history", new String[] { "<player>" });
  }
  
  public void execute(String[] commands) {
    UUID uuid;
    List<String> names;
    if (commands.length == 1 || commands.length == 0)
      sendMessage("§cPlease specify a player."); 
    try {
      uuid = PlayerUtil.getUUIDFromName(commands[0]);
    } catch (Exception e) {
      sendMessage("An error occured.");
      return;
    } 
    try {
      names = PlayerUtil.getHistoryOfNames(uuid);
    } catch (Exception e) {
      sendMessage("An error occured.");
      return;
    } 
    if (names != null) {
      sendMessage(commands[0] + "Â´s name history:");
      for (String name : names)
        sendMessage(name); 
    } else {
      sendMessage("No names found.");
    } 
  }
}
