package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiPackets extends Module {
  private Setting<Mode> mode = register(new Setting("Packets", Mode.CLIENT));
  
  private Setting<Integer> page = register(new Setting("SPackets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10), v -> (this.mode.getValue() == Mode.SERVER)));
  
  private Setting<Integer> pages = register(new Setting("CPackets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(4), v -> (this.mode.getValue() == Mode.CLIENT)));
  
  private Setting<Boolean> AdvancementInfo = register(new Setting("AdvancementInfo", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> Animation = register(new Setting("Animation", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> BlockAction = register(new Setting("BlockAction", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> BlockBreakAnim = register(new Setting("BlockBreakAnim", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> BlockChange = register(new Setting("BlockChange", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> Camera = register(new Setting("Camera", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ChangeGameState = register(new Setting("ChangeGameState", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> Chat = register(new Setting("Chat", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ChunkData = register(new Setting("ChunkData", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> CloseWindow = register(new Setting("CloseWindow", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> CollectItem = register(new Setting("CollectItem", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> CombatEvent = register(new Setting("Combatevent", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> ConfirmTransaction = register(new Setting("ConfirmTransaction", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> Cooldown = register(new Setting("Cooldown", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> CustomPayload = register(new Setting("CustomPayload", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> CustomSound = register(new Setting("CustomSound", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 2)));
  
  private Setting<Boolean> DestroyEntities = register(new Setting("DestroyEntities", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> Disconnect = register(new Setting("Disconnect", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> DisplayObjective = register(new Setting("DisplayObjective", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> Effect = register(new Setting("Effect", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> Entity = register(new Setting("Entity", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> EntityAttach = register(new Setting("EntityAttach", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> EntityEffect = register(new Setting("EntityEffect", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> EntityEquipment = register(new Setting("EntityEquipment", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> EntityHeadLook = register(new Setting("EntityHeadLook", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> EntityMetadata = register(new Setting("EntityMetadata", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> EntityProperties = register(new Setting("EntityProperties", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> EntityStatus = register(new Setting("EntityStatus", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> EntityTeleport = register(new Setting("EntityTeleport", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> EntityVelocity = register(new Setting("EntityVelocity", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> Explosion = register(new Setting("Explosion", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> HeldItemChange = register(new Setting("HeldItemChange", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 4)));
  
  private Setting<Boolean> JoinGame = register(new Setting("JoinGame", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> KeepAlive = register(new Setting("KeepAlive", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> Maps = register(new Setting("Maps", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> MoveVehicle = register(new Setting("MoveVehicle", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> MultiBlockChange = register(new Setting("MultiBlockChange", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> OpenWindow = register(new Setting("OpenWindow", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> Particles = register(new Setting("Particles", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> PlaceGhostRecipe = register(new Setting("PlaceGhostRecipe", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 5)));
  
  private Setting<Boolean> PlayerAbilities = register(new Setting("PlayerAbilities", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> PlayerListHeaderFooter = register(new Setting("PlayerListHeaderFooter", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> PlayerListItem = register(new Setting("PlayerListItem", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> PlayerPosLook = register(new Setting("PlayerPosLook", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> RecipeBook = register(new Setting("RecipeBook", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> RemoveEntityEffect = register(new Setting("RemoveEntityEffect", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> ResourcePackSend = register(new Setting("ResourcePackSend", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> Respawn = register(new Setting("Respawn", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 6)));
  
  private Setting<Boolean> ScoreboardObjective = register(new Setting("ScoreboardObjective", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SelectAdvancementsTab = register(new Setting("SelectAdvancementsTab", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> ServerDifficulty = register(new Setting("ServerDifficulty", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SetExperience = register(new Setting("SetExperience", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SetPassengers = register(new Setting("SetPassengers", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SetSlot = register(new Setting("SetSlot", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SignEditorOpen = register(new Setting("SignEditorOpen", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SoundEffect = register(new Setting("SoundEffect", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 7)));
  
  private Setting<Boolean> SpawnExperienceOrb = register(new Setting("SpawnExperienceOrb", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> SpawnGlobalEntity = register(new Setting("SpawnGlobalEntity", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> SpawnMob = register(new Setting("SpawnMob", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> SpawnObject = register(new Setting("SpawnObject", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> SpawnPainting = register(new Setting("SpawnPainting", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> SpawnPlayer = register(new Setting("SpawnPlayer", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> SpawnPosition = register(new Setting("SpawnPosition", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> Statistics = register(new Setting("Statistics", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 8)));
  
  private Setting<Boolean> TabComplete = register(new Setting("TabComplete", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> Teams = register(new Setting("Teams", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> TimeUpdate = register(new Setting("TimeUpdate", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> Title = register(new Setting("Title", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> UnloadChunk = register(new Setting("UnloadChunk", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> UpdateBossInfo = register(new Setting("UpdateBossInfo", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> UpdateHealth = register(new Setting("UpdateHealth", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> UpdateScore = register(new Setting("UpdateScore", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 9)));
  
  private Setting<Boolean> UpdateTileEntity = register(new Setting("UpdateTileEntity", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 10)));
  
  private Setting<Boolean> UseBed = register(new Setting("UseBed", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 10)));
  
  private Setting<Boolean> WindowItems = register(new Setting("WindowItems", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 10)));
  
  private Setting<Boolean> WindowProperty = register(new Setting("WindowProperty", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 10)));
  
  private Setting<Boolean> WorldBorder = register(new Setting("WorldBorder", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.SERVER && ((Integer)this.page.getValue()).intValue() == 10)));
  
  private Setting<Boolean> Animations = register(new Setting("Animations", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ChatMessage = register(new Setting("ChatMessage", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ClickWindow = register(new Setting("ClickWindow", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ClientSettings = register(new Setting("ClientSettings", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ClientStatus = register(new Setting("ClientStatus", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> CloseWindows = register(new Setting("CloseWindows", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ConfirmTeleport = register(new Setting("ConfirmTeleport", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> ConfirmTransactions = register(new Setting("ConfirmTransactions", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 1)));
  
  private Setting<Boolean> CreativeInventoryAction = register(new Setting("CreativeInventoryAction", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> CustomPayloads = register(new Setting("CustomPayloads", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> EnchantItem = register(new Setting("EnchantItem", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> EntityAction = register(new Setting("EntityAction", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> HeldItemChanges = register(new Setting("HeldItemChanges", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> Input = register(new Setting("Input", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> KeepAlives = register(new Setting("KeepAlives", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> PlaceRecipe = register(new Setting("PlaceRecipe", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 2)));
  
  private Setting<Boolean> Player = register(new Setting("Player", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> PlayerAbility = register(new Setting("PlayerAbility", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> PlayerDigging = register(new Setting("PlayerDigging", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.page.getValue()).intValue() == 3)));
  
  private Setting<Boolean> PlayerTryUseItem = register(new Setting("PlayerTryUseItem", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> PlayerTryUseItemOnBlock = register(new Setting("TryUseItemOnBlock", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> RecipeInfo = register(new Setting("RecipeInfo", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> ResourcePackStatus = register(new Setting("ResourcePackStatus", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> SeenAdvancements = register(new Setting("SeenAdvancements", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 3)));
  
  private Setting<Boolean> PlayerPackets = register(new Setting("PlayerPackets", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private Setting<Boolean> Spectate = register(new Setting("Spectate", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private Setting<Boolean> SteerBoat = register(new Setting("SteerBoat", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private Setting<Boolean> TabCompletion = register(new Setting("TabCompletion", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private Setting<Boolean> UpdateSign = register(new Setting("UpdateSign", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private Setting<Boolean> UseEntity = register(new Setting("UseEntity", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private Setting<Boolean> VehicleMove = register(new Setting("VehicleMove", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.CLIENT && ((Integer)this.pages.getValue()).intValue() == 4)));
  
  private int hudAmount = 0;
  
  public AntiPackets() {
    super("AntiPackets", "Blocks Packets", Module.Category.MISC, true, false, false);
  }
  
  public enum Mode {
    CLIENT, SERVER;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (!isEnabled())
      return; 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketAnimation && ((Boolean)this.Animations.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketChatMessage && ((Boolean)this.ChatMessage.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketClickWindow && ((Boolean)this.ClickWindow.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketClientSettings && ((Boolean)this.ClientSettings.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketClientStatus && ((Boolean)this.ClientStatus.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketCloseWindow && ((Boolean)this.CloseWindows.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketConfirmTeleport && ((Boolean)this.ConfirmTeleport.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketConfirmTransaction && ((Boolean)this.ConfirmTransactions.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketCreativeInventoryAction && ((Boolean)this.CreativeInventoryAction.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketCustomPayload && ((Boolean)this.CustomPayloads.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketEnchantItem && ((Boolean)this.EnchantItem.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketEntityAction && ((Boolean)this.EntityAction.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketHeldItemChange && ((Boolean)this.HeldItemChanges.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketInput && ((Boolean)this.Input.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketKeepAlive && ((Boolean)this.KeepAlives.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlaceRecipe && ((Boolean)this.PlaceRecipe.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer && ((Boolean)this.Player.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerAbilities && ((Boolean)this.PlayerAbility.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerDigging && ((Boolean)this.PlayerDigging.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItem && ((Boolean)this.PlayerTryUseItem.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock && ((Boolean)this.PlayerTryUseItemOnBlock.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketRecipeInfo && ((Boolean)this.RecipeInfo.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketResourcePackStatus && ((Boolean)this.ResourcePackStatus.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketSeenAdvancements && ((Boolean)this.SeenAdvancements.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketSpectate && ((Boolean)this.Spectate.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketSteerBoat && ((Boolean)this.SteerBoat.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketTabComplete && ((Boolean)this.TabCompletion.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketUpdateSign && ((Boolean)this.UpdateSign.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketUseEntity && ((Boolean)this.UseEntity.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketVehicleMove && ((Boolean)this.VehicleMove.getValue()).booleanValue())
      event.setCanceled(true); 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (!isEnabled())
      return; 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketAdvancementInfo && ((Boolean)this.AdvancementInfo.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketAnimation && ((Boolean)this.Animation.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketBlockAction && ((Boolean)this.BlockAction.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketBlockBreakAnim && ((Boolean)this.BlockBreakAnim.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketBlockChange && ((Boolean)this.BlockChange.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCamera && ((Boolean)this.Camera.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketChangeGameState && ((Boolean)this.ChangeGameState.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketChat && ((Boolean)this.Chat.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketChunkData && ((Boolean)this.ChunkData.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCloseWindow && ((Boolean)this.CloseWindow.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCollectItem && ((Boolean)this.CollectItem.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCombatEvent && ((Boolean)this.CombatEvent.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketConfirmTransaction && ((Boolean)this.ConfirmTransaction.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCooldown && ((Boolean)this.Cooldown.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCustomPayload && ((Boolean)this.CustomPayload.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCustomSound && ((Boolean)this.CustomSound.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketDestroyEntities && ((Boolean)this.DestroyEntities.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketDisconnect && ((Boolean)this.Disconnect.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketChunkData && ((Boolean)this.ChunkData.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCloseWindow && ((Boolean)this.CloseWindow.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketCollectItem && ((Boolean)this.CollectItem.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketDisplayObjective && ((Boolean)this.DisplayObjective.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEffect && ((Boolean)this.Effect.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntity && ((Boolean)this.Entity.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityAttach && ((Boolean)this.EntityAttach.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityEffect && ((Boolean)this.EntityEffect.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityEquipment && ((Boolean)this.EntityEquipment.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityHeadLook && ((Boolean)this.EntityHeadLook.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityMetadata && ((Boolean)this.EntityMetadata.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityProperties && ((Boolean)this.EntityProperties.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityStatus && ((Boolean)this.EntityStatus.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityTeleport && ((Boolean)this.EntityTeleport.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketEntityVelocity && ((Boolean)this.EntityVelocity.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketExplosion && ((Boolean)this.Explosion.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketHeldItemChange && ((Boolean)this.HeldItemChange.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketJoinGame && ((Boolean)this.JoinGame.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketKeepAlive && ((Boolean)this.KeepAlive.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketMaps && ((Boolean)this.Maps.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketMoveVehicle && ((Boolean)this.MoveVehicle.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketMultiBlockChange && ((Boolean)this.MultiBlockChange.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketOpenWindow && ((Boolean)this.OpenWindow.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketParticles && ((Boolean)this.Particles.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlaceGhostRecipe && ((Boolean)this.PlaceGhostRecipe.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerAbilities && ((Boolean)this.PlayerAbilities.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerListHeaderFooter && ((Boolean)this.PlayerListHeaderFooter.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerListItem && ((Boolean)this.PlayerListItem.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerPosLook && ((Boolean)this.PlayerPosLook.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketRecipeBook && ((Boolean)this.RecipeBook.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketRemoveEntityEffect && ((Boolean)this.RemoveEntityEffect.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketResourcePackSend && ((Boolean)this.ResourcePackSend.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketRespawn && ((Boolean)this.Respawn.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketScoreboardObjective && ((Boolean)this.ScoreboardObjective.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSelectAdvancementsTab && ((Boolean)this.SelectAdvancementsTab.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketServerDifficulty && ((Boolean)this.ServerDifficulty.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSetExperience && ((Boolean)this.SetExperience.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSetPassengers && ((Boolean)this.SetPassengers.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSetSlot && ((Boolean)this.SetSlot.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSignEditorOpen && ((Boolean)this.SignEditorOpen.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSoundEffect && ((Boolean)this.SoundEffect.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnExperienceOrb && ((Boolean)this.SpawnExperienceOrb.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnGlobalEntity && ((Boolean)this.SpawnGlobalEntity.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnMob && ((Boolean)this.SpawnMob.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnObject && ((Boolean)this.SpawnObject.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnPainting && ((Boolean)this.SpawnPainting.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnPlayer && ((Boolean)this.SpawnPlayer.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketSpawnPosition && ((Boolean)this.SpawnPosition.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketStatistics && ((Boolean)this.Statistics.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTabComplete && ((Boolean)this.TabComplete.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTeams && ((Boolean)this.Teams.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTimeUpdate && ((Boolean)this.TimeUpdate.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketTitle && ((Boolean)this.Title.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketUnloadChunk && ((Boolean)this.UnloadChunk.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketUpdateBossInfo && ((Boolean)this.UpdateBossInfo.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketUpdateHealth && ((Boolean)this.UpdateHealth.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketUpdateScore && ((Boolean)this.UpdateScore.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketUpdateTileEntity && ((Boolean)this.UpdateTileEntity.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketUseBed && ((Boolean)this.UseBed.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketWindowItems && ((Boolean)this.WindowItems.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketWindowProperty && ((Boolean)this.WindowProperty.getValue()).booleanValue())
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.server.SPacketWorldBorder && ((Boolean)this.WorldBorder.getValue()).booleanValue())
      event.setCanceled(true); 
  }
  
  public void onEnable() {
    String standart = "§aAntiPackets On!§f Cancelled Packets: ";
    StringBuilder text = new StringBuilder(standart);
    if (!this.settings.isEmpty())
      for (Setting setting : this.settings) {
        if (!(setting.getValue() instanceof Boolean) || !((Boolean)setting.getValue()).booleanValue() || setting.getName().equalsIgnoreCase("Enabled") || setting.getName().equalsIgnoreCase("drawn"))
          continue; 
        String name = setting.getName();
        text.append(name).append(", ");
      }  
    if (text.toString().equals(standart)) {
      Command.sendMessage("§aAntiPackets On!§f Currently not cancelling any Packets.");
    } else {
      String output = removeLastChar(removeLastChar(text.toString()));
      Command.sendMessage(output);
    } 
  }
  
  public void onUpdate() {
    int amount = 0;
    if (!this.settings.isEmpty())
      for (Setting setting : this.settings) {
        if (!(setting.getValue() instanceof Boolean) || !((Boolean)setting.getValue()).booleanValue() || setting.getName().equalsIgnoreCase("Enabled") || setting.getName().equalsIgnoreCase("drawn"))
          continue; 
        amount++;
      }  
    this.hudAmount = amount;
  }
  
  public String getDisplayInfo() {
    if (this.hudAmount == 0)
      return ""; 
    return this.hudAmount + "";
  }
  
  public String removeLastChar(String str) {
    if (str != null && str.length() > 0)
      str = str.substring(0, str.length() - 1); 
    return str;
  }
}
