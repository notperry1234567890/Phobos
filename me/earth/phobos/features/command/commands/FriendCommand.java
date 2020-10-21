package me.earth.phobos.features.command.commands;

import java.util.Map;
import java.util.UUID;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.command.Command;

public class FriendCommand extends Command {
  public FriendCommand() {
    super("friend", new String[] { "<add/del/name/clear>", "<name>" });
  }
  
  public void execute(String[] commands) {
    if (commands.length == 1) {
      if (Phobos.friendManager.getFriends().isEmpty()) {
        sendMessage("You currently dont have any friends added.");
      } else {
        sendMessage("Friends: ");
        for (Map.Entry<String, UUID> entry : (Iterable<Map.Entry<String, UUID>>)Phobos.friendManager.getFriends().entrySet())
          sendMessage(entry.getKey()); 
      } 
      return;
    } 
    if (commands.length == 2) {
      switch (commands[0]) {
        case "reset":
          Phobos.friendManager.onLoad();
          sendMessage("Friends got reset.");
          return;
      } 
      sendMessage(commands[0] + (Phobos.friendManager.isFriend(commands[0]) ? " is friended." : " isnt friended."));
      return;
    } 
    if (commands.length >= 2) {
      switch (commands[0]) {
        case "add":
          Phobos.friendManager.addFriend(commands[1]);
          sendMessage("§b" + commands[1] + " has been friended");
          return;
        case "del":
          Phobos.friendManager.removeFriend(commands[1]);
          sendMessage("§c" + commands[1] + " has been unfriended");
          return;
      } 
      sendMessage("§cBad Command, try: friend <add/del/name> <name>.");
    } 
  }
}
