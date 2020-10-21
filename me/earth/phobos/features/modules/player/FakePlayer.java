//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.player;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class FakePlayer extends Module {
  private Setting<Boolean> copyInv = register(new Setting("CopyInv", Boolean.valueOf(true)));
  
  public Setting<Boolean> multi = register(new Setting("Multi", Boolean.valueOf(false)));
  
  private Setting<Integer> players = register(new Setting("Players", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(9), v -> ((Boolean)this.multi.getValue()).booleanValue(), "Amount of other players."));
  
  private static final String[] fitInfo = new String[] { "fdee323e-7f0c-4c15-8d1c-0f277442342a", "Fit" };
  
  public static final String[][] phobosInfo = new String[][] { { "8af022c8-b926-41a0-8b79-2b544ff00fcf", "3arthqu4ke", "3", "0" }, { "0aa3b04f-786a-49c8-bea9-025ee0dd1e85", "zb0b", "-3", "0" }, { "19bf3f1f-fe06-4c86-bea5-3dad5df89714", "3vt", "0", "-3" }, { "e47d6571-99c2-415b-955e-c4bc7b55941b", "Phobos_eu", "0", "3" }, { "b01f9bc1-cb7c-429a-b178-93d771f00926", "bakpotatisen", "6", "0" }, { "b232930c-c28a-4e10-8c90-f152235a65c5", "948", "-6", "0" }, { "ace08461-3db3-4579-98d3-390a67d5645b", "Browswer", "0", "-6" }, { "5bead5b0-3bab-460d-af1d-7929950f40c2", "fsck", "0", "6" }, { "78ee2bd6-64c4-45f0-96e5-0b6747ba7382", "Fit", "0", "9" }, { "78ee2bd6-64c4-45f0-96e5-0b6747ba7382", "deathcurz0", "0", "-9" } };
  
  public List<Integer> fakePlayerIdList = new ArrayList<>();
  
  private final List<EntityOtherPlayerMP> fakeEntities = new ArrayList<>();
  
  private static FakePlayer INSTANCE = new FakePlayer();
  
  public FakePlayer() {
    super("FakePlayer", "Spawns in a fake player", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static FakePlayer getInstance() {
    if (INSTANCE == null)
      INSTANCE = new FakePlayer(); 
    return INSTANCE;
  }
  
  public void onLoad() {
    disable();
  }
  
  public void onEnable() {
    if (fullNullCheck()) {
      disable();
      return;
    } 
    this.fakePlayerIdList = new ArrayList<>();
    if (((Boolean)this.multi.getValue()).booleanValue()) {
      int amount = 0;
      int entityId = -101;
      for (String[] data : phobosInfo) {
        addFakePlayer(data[0], data[1], entityId, Integer.parseInt(data[2]), Integer.parseInt(data[3]));
        amount++;
        if (amount >= ((Integer)this.players.getValue()).intValue())
          return; 
        entityId -= amount;
      } 
    } else {
      addFakePlayer(fitInfo[0], fitInfo[1], -100, 0, 0);
    } 
  }
  
  public void onDisable() {
    if (fullNullCheck())
      return; 
    for (Iterator<Integer> iterator = this.fakePlayerIdList.iterator(); iterator.hasNext(); ) {
      int id = ((Integer)iterator.next()).intValue();
      mc.world.removeEntityFromWorld(id);
    } 
  }
  
  public void onLogout() {
    if (isOn())
      disable(); 
  }
  
  private void addFakePlayer(String uuid, String name, int entityId, int offsetX, int offsetZ) {
    GameProfile profile = new GameProfile(UUID.fromString(uuid), name);
    EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP((World)mc.world, profile);
    fakePlayer.copyLocationAndAnglesFrom((Entity)mc.player);
    fakePlayer.posX += offsetX;
    fakePlayer.posZ += offsetZ;
    if (((Boolean)this.copyInv.getValue()).booleanValue()) {
      for (PotionEffect potionEffect : Phobos.potionManager.getOwnPotions())
        fakePlayer.addPotionEffect(potionEffect); 
      fakePlayer.inventory.copyInventory(mc.player.inventory);
    } 
    fakePlayer.setHealth(mc.player.getHealth() + mc.player.getAbsorptionAmount());
    this.fakeEntities.add(fakePlayer);
    mc.world.addEntityToWorld(entityId, (Entity)fakePlayer);
    this.fakePlayerIdList.add(Integer.valueOf(entityId));
  }
}
