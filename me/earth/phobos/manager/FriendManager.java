//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;

public class FriendManager extends Feature {
  private final Map<String, UUID> friends = new HashMap<>();
  
  public FriendManager() {
    super("Friends");
  }
  
  public boolean isFriend(String name) {
    return (this.friends.get(name) != null);
  }
  
  public boolean isFriend(EntityPlayer player) {
    return isFriend(player.getName());
  }
  
  public void addFriend(String name) {
    Friend friend = getFriendByName(name);
    if (friend != null)
      this.friends.put(friend.getUsername(), friend.getUuid()); 
  }
  
  public void removeFriend(String name) {
    this.friends.remove(name);
  }
  
  public void onLoad() {
    this.friends.clear();
    clearSettings();
  }
  
  public void saveFriends() {
    clearSettings();
    for (Map.Entry<String, UUID> entry : this.friends.entrySet())
      register(new Setting(((UUID)entry.getValue()).toString(), entry.getKey())); 
  }
  
  public Map<String, UUID> getFriends() {
    return this.friends;
  }
  
  public static class Friend {
    private final String username;
    
    private final UUID uuid;
    
    public Friend(String username, UUID uuid) {
      this.username = username;
      this.uuid = uuid;
    }
    
    public String getUsername() {
      return this.username;
    }
    
    public UUID getUuid() {
      return this.uuid;
    }
    
    public boolean equals(Object other) {
      return (other instanceof Friend && ((Friend)other).getUsername().equals(this.username) && ((Friend)other).getUuid().equals(this.uuid));
    }
    
    public int hashCode() {
      return this.username.hashCode() + this.uuid.hashCode();
    }
  }
  
  public Friend getFriendByName(String input) {
    UUID uuid = PlayerUtil.getUUIDFromName(input);
    if (uuid != null)
      return new Friend(input, uuid); 
    return null;
  }
  
  public void addFriend(Friend friend) {
    this.friends.put(friend.getUsername(), friend.getUuid());
  }
}
