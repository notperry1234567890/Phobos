//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.misc;

import java.text.SimpleDateFormat;
import java.util.Date;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.TextUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatModifier extends Module {
  public Setting<Suffix> suffix = register(new Setting("Suffix", Suffix.NONE, "Your Suffix."));
  
  public Setting<Boolean> clean = register(new Setting("CleanChat", Boolean.valueOf(false), "Cleans your chat"));
  
  public Setting<Boolean> infinite = register(new Setting("Infinite", Boolean.valueOf(false), "Makes your chat infinite."));
  
  public Setting<Boolean> autoQMain = register(new Setting("AutoQMain", Boolean.valueOf(false), "Spams AutoQMain"));
  
  public Setting<Boolean> qNotification = register(new Setting("QNotification", Boolean.valueOf(false), v -> ((Boolean)this.autoQMain.getValue()).booleanValue()));
  
  public Setting<Integer> qDelay = register(new Setting("QDelay", Integer.valueOf(9), Integer.valueOf(1), Integer.valueOf(90), v -> ((Boolean)this.autoQMain.getValue()).booleanValue()));
  
  public Setting<TextUtil.Color> timeStamps = register(new Setting("Time", TextUtil.Color.NONE));
  
  public Setting<TextUtil.Color> bracket = register(new Setting("Bracket", TextUtil.Color.WHITE, v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
  
  public Setting<Boolean> space = register(new Setting("Space", Boolean.valueOf(true), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
  
  public Setting<Boolean> all = register(new Setting("All", Boolean.valueOf(false), v -> (this.timeStamps.getValue() != TextUtil.Color.NONE)));
  
  public Setting<Boolean> shrug = register(new Setting("Shrug", Boolean.valueOf(false)));
  
  public Setting<Boolean> disability = register(new Setting("Disability", Boolean.valueOf(false)));
  
  private final Timer timer = new Timer();
  
  private static ChatModifier INSTANCE = new ChatModifier();
  
  public ChatModifier() {
    super("Chat", "Modifies your chat", Module.Category.MISC, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static ChatModifier getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ChatModifier(); 
    return INSTANCE;
  }
  
  public void onUpdate() {
    if (((Boolean)this.shrug.getValue()).booleanValue()) {
      mc.player.sendChatMessage(TextUtil.shrug);
      this.shrug.setValue(Boolean.valueOf(false));
    } 
    if (((Boolean)this.autoQMain.getValue()).booleanValue()) {
      if (!shouldSendMessage((EntityPlayer)mc.player))
        return; 
      if (((Boolean)this.qNotification.getValue()).booleanValue())
        Command.sendMessage("<AutoQueueMain> Sending message: /queue main"); 
      mc.player.sendChatMessage("/queue main");
      this.timer.reset();
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && event.getPacket() instanceof CPacketChatMessage) {
      CPacketChatMessage packet = (CPacketChatMessage)event.getPacket();
      String s = packet.getMessage();
      if (s.startsWith("/"))
        return; 
      switch ((Suffix)this.suffix.getValue()) {
        case EARTH:
          s = s + " �?? 3ᴀʀᴛʜʜ4ᴄᴋ";
          break;
        case PHOBOS:
          s = s + " �?? ᴘʜ�?ʙ�?ꜱ";
          break;
      } 
      if (s.length() >= 256)
        s = s.substring(0, 256); 
      packet.message = s;
    } 
  }
  
  @SubscribeEvent
  public void onChatPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() != 0 || event.getPacket() instanceof SPacketChat);
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
      if (!((SPacketChat)event.getPacket()).isSystem())
        return; 
      String originalMessage = ((SPacketChat)event.getPacket()).chatComponent.getUnformattedText();
      String message = getTimeString() + originalMessage;
      ((SPacketChat)event.getPacket()).chatComponent = (ITextComponent)new TextComponentString(message);
    } 
  }
  
  public String getTimeString() {
    String date = (new SimpleDateFormat("k:mm")).format(new Date());
    return ((this.bracket.getValue() == TextUtil.Color.NONE) ? "" : TextUtil.coloredString("<", (TextUtil.Color)this.bracket.getValue())) + 
      TextUtil.coloredString(date, (TextUtil.Color)this.timeStamps.getValue()) + (
      (this.bracket.getValue() == TextUtil.Color.NONE) ? "" : TextUtil.coloredString(">", (TextUtil.Color)this.bracket.getValue())) + (
      ((Boolean)this.space.getValue()).booleanValue() ? " " : "") + "§r";
  }
  
  private boolean shouldSendMessage(EntityPlayer player) {
    if (player.dimension != 1)
      return false; 
    if (!this.timer.passedS(((Integer)this.qDelay.getValue()).intValue()))
      return false; 
    return player.getPosition().equals(new Vec3i(0, 240, 0));
  }
  
  public enum Suffix {
    NONE, PHOBOS, EARTH;
  }
}
