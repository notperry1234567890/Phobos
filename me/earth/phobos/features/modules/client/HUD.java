//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
  private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
  
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false), "Universal colors for hud."));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), "Rainbow hud."));
  
  public Setting<Integer> factor = register(new Setting("Factor", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(20), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> rolling = register(new Setting("Rolling", Boolean.valueOf(false), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> staticRainbow = register(new Setting("Static", Boolean.valueOf(false), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowSpeed = register(new Setting("Speed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowSaturation = register(new Setting("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowBrightness = register(new Setting("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> potionIcons = register(new Setting("PotionIcons", Boolean.valueOf(true), "Draws Potion Icons."));
  
  public Setting<Boolean> shadow = register(new Setting("Shadow", Boolean.valueOf(false), "Draws the text with a shadow."));
  
  private final Setting<WaterMark> watermark = register(new Setting("Logo", WaterMark.NONE, "WaterMark"));
  
  private final Setting<Boolean> modeVer = register(new Setting("Version", Boolean.valueOf(false), v -> (this.watermark.getValue() != WaterMark.NONE)));
  
  private final Setting<Boolean> arrayList = register(new Setting("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
  
  public Setting<Integer> animationHorizontalTime = register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  public Setting<Integer> animationVerticalTime = register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  private final Setting<Boolean> alphabeticalSorting = register(new Setting("AlphabeticalSorting", Boolean.valueOf(false), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  private final Setting<Boolean> serverBrand = register(new Setting("ServerBrand", Boolean.valueOf(false), "Brand of the server you are on."));
  
  private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(false), "Your response time to the server."));
  
  private final Setting<Boolean> tps = register(new Setting("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
  
  private final Setting<Boolean> fps = register(new Setting("FPS", Boolean.valueOf(false), "Your frames per second."));
  
  private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(false), "Your current coordinates"));
  
  private final Setting<Boolean> direction = register(new Setting("Direction", Boolean.valueOf(false), "The Direction you are facing."));
  
  private final Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(false), "Your Speed"));
  
  private final Setting<Boolean> potions = register(new Setting("Potions", Boolean.valueOf(false), "Your Speed"));
  
  public Setting<Boolean> textRadar = register(new Setting("TextRadar", Boolean.valueOf(false), "A TextRadar"));
  
  private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false), "ArmorHUD"));
  
  private final Setting<Boolean> durability = register(new Setting("Durability", Boolean.valueOf(false), "Durability"));
  
  private final Setting<Boolean> percent = register(new Setting("Percent", Boolean.valueOf(true), v -> ((Boolean)this.armor.getValue()).booleanValue()));
  
  private final Setting<Boolean> totems = register(new Setting("Totems", Boolean.valueOf(false), "TotemHUD"));
  
  private final Setting<Boolean> queue = register(new Setting("2b2tQueue", Boolean.valueOf(false), "Shows the 2b2t queue."));
  
  private final Setting<Greeter> greeter = register(new Setting("Greeter", Greeter.NONE, "Greets you."));
  
  private final Setting<String> spoofGreeter = register(new Setting("GreeterName", "3arthqu4ke", v -> (this.greeter.getValue() == Greeter.CUSTOM)));
  
  public Setting<Boolean> time = register(new Setting("Time", Boolean.valueOf(false), "The time"));
  
  private final Setting<LagNotify> lag = register(new Setting("Lag", LagNotify.GRAY, "Lag Notifier"));
  
  private final Setting<Boolean> hitMarkers = register(new Setting("HitMarkers", Boolean.valueOf(true)));
  
  private final Setting<Sound> sound = register(new Setting("Sound", Sound.NONE, v -> ((Boolean)this.hitMarkers.getValue()).booleanValue()));
  
  public Setting<Integer> hudRed = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> hudGreen = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> hudBlue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  private final Setting<Boolean> grayNess = register(new Setting("FutureColour", Boolean.valueOf(true)));
  
  public Setting<Boolean> potions1 = register(new Setting("LevelPotions", Boolean.valueOf(false), v -> ((Boolean)this.potions.getValue()).booleanValue()));
  
  private static HUD INSTANCE = new HUD();
  
  private Map<String, Integer> players = new HashMap<>();
  
  public Map<Module, Float> moduleProgressMap = new HashMap<>();
  
  private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
  
  private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
  
  private int color;
  
  private boolean shouldIncrement;
  
  private int hitMarkerTimer;
  
  private final Timer timer = new Timer();
  
  private final Timer moduleTimer = new Timer();
  
  public Map<Integer, Integer> colorMap = new HashMap<>();
  
  private static final ResourceLocation codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
  
  private static final ResourceLocation csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
  
  public static final SoundEvent COD_EVENT = new SoundEvent(codHitmarker);
  
  public static final SoundEvent CSGO_EVENT = new SoundEvent(csgoHitmarker);
  
  public HUD() {
    super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static HUD getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HUD(); 
    return INSTANCE;
  }
  
  public void onUpdate() {
    if (this.timer.passedMs(((Integer)(Managers.getInstance()).textRadarUpdates.getValue()).intValue())) {
      this.players = getTextRadarPlayers();
      this.timer.reset();
    } 
    if (this.shouldIncrement)
      this.hitMarkerTimer++; 
    if (this.hitMarkerTimer == 10) {
      this.hitMarkerTimer = 0;
      this.shouldIncrement = false;
    } 
  }
  
  @SubscribeEvent
  public void onAttack(AttackEntityEvent event) {
    this.shouldIncrement = true;
    switch ((Sound)this.sound.getValue()) {
      case TIME:
        mc.world.playSound((EntityPlayer)mc.player, mc.player.posX, mc.player.posY, mc.player.posZ, COD_EVENT, SoundCategory.PLAYERS, 3.0F, 1.0F);
        break;
      case LONG:
        mc.world.playSound((EntityPlayer)mc.player, mc.player.posX, mc.player.posY, mc.player.posZ, CSGO_EVENT, SoundCategory.PLAYERS, 3.0F, 1.0F);
        break;
    } 
  }
  
  @SubscribeEvent
  public void onSendPacket(PacketEvent.Send event) {
    if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) {
      this.shouldIncrement = true;
      switch ((Sound)this.sound.getValue()) {
        case TIME:
          mc.world.playSound((EntityPlayer)mc.player, mc.player.posX, mc.player.posY, mc.player.posZ, COD_EVENT, SoundCategory.PLAYERS, 3.0F, 1.0F);
          break;
        case LONG:
          mc.world.playSound((EntityPlayer)mc.player, mc.player.posX, mc.player.posY, mc.player.posZ, CSGO_EVENT, SoundCategory.PLAYERS, 3.0F, 1.0F);
          break;
      } 
    } 
  }
  
  @SubscribeEvent
  public void onModuleToggle(ClientEvent event) {
    if (event.getFeature() instanceof Module)
      if (event.getStage() == 0) {
        for (float i = 0.0F; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
          if (this.moduleTimer.passedMs(1L))
            this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf(this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i)); 
          this.timer.reset();
        } 
      } else if (event.getStage() == 1) {
        for (float i = 0.0F; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
          if (this.moduleTimer.passedMs(1L))
            this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf(this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i)); 
          this.timer.reset();
        } 
      }  
  }
  
  public void onRender2D(Render2DEvent event) {
    if (fullNullCheck())
      return; 
    int colorSpeed = 101 - ((Integer)this.rainbowSpeed.getValue()).intValue();
    float hue = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.hue : ((float)(System.currentTimeMillis() % (360 * colorSpeed)) / 360.0F * colorSpeed);
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    float tempHue = hue;
    for (int i = 0; i <= height; i++) {
      if (((Boolean)this.colorSync.getValue()).booleanValue()) {
        this.colorMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)Colors.INSTANCE.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)Colors.INSTANCE.rainbowBrightness.getValue()).intValue() / 255.0F)));
      } else {
        this.colorMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F)));
      } 
      tempHue += 1.0F / height * ((Integer)this.factor.getValue()).intValue();
    } 
    if (((Boolean)this.rainbow.getValue()).booleanValue() && !((Boolean)this.rolling.getValue()).booleanValue()) {
      this.color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F);
    } else if (!((Boolean)this.rainbow.getValue()).booleanValue()) {
      this.color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(((Integer)this.hudRed.getValue()).intValue(), ((Integer)this.hudGreen.getValue()).intValue(), ((Integer)this.hudBlue.getValue()).intValue());
    } 
    String grayString = ((Boolean)this.grayNess.getValue()).booleanValue() ? "§7" : "";
    switch ((WaterMark)this.watermark.getValue()) {
      case TIME:
        this.renderer.drawString("Phobos" + (((Boolean)this.modeVer.getValue()).booleanValue() ? " v1.5.4" : ""), 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
        break;
      case LONG:
        this.renderer.drawString("3arthh4ck" + (((Boolean)this.modeVer.getValue()).booleanValue() ? " v1.5.4" : ""), 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
        break;
    } 
    if (((Boolean)this.textRadar.getValue()).booleanValue())
      drawTextRadar((ToolTips.getInstance().isOff() || !((Boolean)(ToolTips.getInstance()).shulkerSpy.getValue()).booleanValue() || !((Boolean)(ToolTips.getInstance()).render.getValue()).booleanValue()) ? 0 : ToolTips.getInstance().getTextRadarY()); 
    int j = ((Boolean)this.renderingUp.getValue()).booleanValue() ? 0 : ((mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0);
    if (((Boolean)this.arrayList.getValue()).booleanValue())
      if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
        for (int m = 0; m < (((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); m++) {
          Module module = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(m) : Phobos.moduleManager.sortedModules.get(m);
          Module module1 = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(MathUtil.clamp(m - 1, 0, Phobos.moduleManager.alphabeticallySortedModules.size() - 1)) : Phobos.moduleManager.sortedModules.get(MathUtil.clamp(m - 1, 0, Phobos.moduleManager.sortedModules.size() - 1));
          String str = module.getDisplayName() + "§7" + ((module.getDisplayInfo() != null) ? (" [§f" + module.getDisplayInfo() + "§7" + "]") : "");
          this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)) + ((((Integer)this.animationHorizontalTime.getValue()).intValue() == 1) ? 0.0F : module.arrayListOffset), (2 + j * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(MathUtil.clamp(2 + j * 10, 0, height)))).intValue() : this.color, true);
          j++;
        } 
      } else {
        for (int m = 0; m < (((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); m++) {
          Module module = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(Phobos.moduleManager.alphabeticallySortedModules.size() - 1 - m) : Phobos.moduleManager.sortedModules.get(m);
          Module module1 = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(MathUtil.clamp(m + 1, 0, Phobos.moduleManager.alphabeticallySortedModules.size() - 1)) : Phobos.moduleManager.sortedModules.get(MathUtil.clamp(m + 1, 0, Phobos.moduleManager.sortedModules.size() - 1));
          String str = module.getDisplayName() + "§7" + ((module.getDisplayInfo() != null) ? (" [§f" + module.getDisplayInfo() + "§7" + "]") : "");
          j += 10;
          this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)) + ((((Integer)this.animationHorizontalTime.getValue()).intValue() == 1) ? 0.0F : module.arrayListOffset), (height - j), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(MathUtil.clamp(height - j, 0, height)))).intValue() : this.color, true);
        } 
      }  
    int k = !((Boolean)this.renderingUp.getValue()).booleanValue() ? 0 : ((mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 0 : 0);
    if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
      if (((Boolean)this.serverBrand.getValue()).booleanValue()) {
        String str = grayString + "Server brand " + "§f" + Phobos.serverManager.getServerBrand();
        k += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      if (((Boolean)this.potions.getValue()).booleanValue())
        for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
          String str = Phobos.potionManager.getColoredPotionString(effect);
          k += 10;
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        }  
      if (((Boolean)this.speed.getValue()).booleanValue()) {
        String str = grayString + "Speed " + "§f" + Phobos.speedManager.getSpeedKpH() + " km/h";
        k += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      if (((Boolean)this.time.getValue()).booleanValue()) {
        String str = grayString + "Time " + "§f" + (new SimpleDateFormat("h:mm a")).format(new Date());
        k += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      if (((Boolean)this.durability.getValue()).booleanValue()) {
        int itemDamage = mc.player.getHeldItemMainhand().getMaxDamage() - mc.player.getHeldItemMainhand().getItemDamage();
        if (itemDamage > 0) {
          String str = grayString + "Durability " + "§a" + itemDamage;
          k += 10;
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
      } 
      if (((Boolean)this.tps.getValue()).booleanValue()) {
        String str = grayString + "TPS " + "§f" + Phobos.serverManager.getTPS();
        k += 10;
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      String fpsText = grayString + "FPS " + "§f" + Minecraft.debugFPS;
      String str1 = grayString + "Ping " + "§f" + Phobos.serverManager.getPing();
      if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          k += 10;
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          k += 10;
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
      } else {
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          k += 10;
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          k += 10;
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) + 2), (height - 2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
      } 
    } else {
      if (((Boolean)this.serverBrand.getValue()).booleanValue()) {
        String str = grayString + "Server brand " + "§f" + Phobos.serverManager.getServerBrand();
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      if (((Boolean)this.potions.getValue()).booleanValue())
        for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
          String str = Phobos.potionManager.getColoredPotionString(effect);
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
        }  
      if (((Boolean)this.speed.getValue()).booleanValue()) {
        String str = grayString + "Speed " + "§f" + Phobos.speedManager.getSpeedKpH() + " km/h";
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      if (((Boolean)this.time.getValue()).booleanValue()) {
        String str = grayString + "Time " + "§f" + (new SimpleDateFormat("h:mm a")).format(new Date());
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      if (((Boolean)this.durability.getValue()).booleanValue()) {
        int itemDamage = mc.player.getHeldItemMainhand().getMaxDamage() - mc.player.getHeldItemMainhand().getItemDamage();
        if (itemDamage > 0) {
          String str = grayString + "Durability " + "§a" + itemDamage;
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
        } 
      } 
      if (((Boolean)this.tps.getValue()).booleanValue()) {
        String str = grayString + "TPS " + "§f" + Phobos.serverManager.getTPS();
        this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      String fpsText = grayString + "FPS " + "§f" + Minecraft.debugFPS;
      String str1 = grayString + "Ping " + "§f" + Phobos.serverManager.getPing();
      if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
        if (((Boolean)this.ping.getValue()).booleanValue())
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
        if (((Boolean)this.fps.getValue()).booleanValue())
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
      } else {
        if (((Boolean)this.fps.getValue()).booleanValue())
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
        if (((Boolean)this.ping.getValue()).booleanValue())
          this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
      } 
    } 
    boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
    int posX = (int)mc.player.posX;
    int posY = (int)mc.player.posY;
    int posZ = (int)mc.player.posZ;
    float nether = !inHell ? 0.125F : 8.0F;
    int hposX = (int)(mc.player.posX * nether);
    int hposZ = (int)(mc.player.posZ * nether);
    if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
      Phobos.notificationManager.handleNotifications(height - k + 16);
    } else {
      Phobos.notificationManager.handleNotifications(height - j + 16);
    } 
    k = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
    String coordinates = grayString + "XYZ " + "§f" + posX + ", " + posY + ", " + posZ + " " + grayString + "[" + "§f" + hposX + ", " + hposZ + grayString + "]";
    String text = (((Boolean)this.direction.getValue()).booleanValue() ? (Phobos.rotationManager.getDirection4D(false) + " ") : "") + (((Boolean)this.coords.getValue()).booleanValue() ? coordinates : "") + "";
    k += 10;
    k += 10;
    this.renderer.drawString(text, 2.0F, (height - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
    if (((Boolean)this.armor.getValue()).booleanValue())
      renderArmorHUD(((Boolean)this.percent.getValue()).booleanValue()); 
    if (((Boolean)this.totems.getValue()).booleanValue())
      renderTotemHUD(); 
    if (this.greeter.getValue() != Greeter.NONE)
      renderGreeter(); 
    if (this.lag.getValue() != LagNotify.NONE)
      renderLag(); 
    if (((Boolean)this.hitMarkers.getValue()).booleanValue() && this.hitMarkerTimer > 0)
      drawHitMarkers(); 
  }
  
  public Map<String, Integer> getTextRadarPlayers() {
    return EntityUtil.getTextRadarPlayers();
  }
  
  public void renderGreeter() {
    int width = this.renderer.scaledWidth;
    String text = "";
    switch ((Greeter)this.greeter.getValue()) {
      case TIME:
        text = text + MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
        break;
      case LONG:
        text = text + "Welcome to Phobos.eu " + mc.player.getDisplayNameString() + " :^)";
        break;
      case CUSTOM:
        text = text + (String)this.spoofGreeter.getValue();
        break;
      default:
        text = text + "Welcome " + mc.player.getDisplayNameString();
        break;
    } 
    this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
  }
  
  public void renderLag() {
    int width = this.renderer.scaledWidth;
    if (Phobos.serverManager.isServerNotResponding()) {
      String text = ((this.lag.getValue() == LagNotify.GRAY) ? "§7" : "§c") + "Server not responding: " + MathUtil.round((float)Phobos.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
      this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(20))).intValue() : this.color, true);
    } 
  }
  
  public void renderArrayList() {}
  
  public void renderTotemHUD() {
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
    if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
      totems += mc.player.getHeldItemOffhand().getCount(); 
    if (totems > 0) {
      GlStateManager.enableTexture2D();
      int i = width / 2;
      int iteration = 0;
      int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
      int x = i - 189 + 180 + 2;
      GlStateManager.enableDepth();
      RenderUtil.itemRender.zLevel = 200.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
      RenderUtil.itemRender.zLevel = 0.0F;
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      this.renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (y + 9), 16777215);
      GlStateManager.enableDepth();
      GlStateManager.disableLighting();
    } 
  }
  
  public void renderArmorHUD(boolean percent) {
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    GlStateManager.enableTexture2D();
    int i = width / 2;
    int iteration = 0;
    int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
    for (ItemStack is : mc.player.inventory.armorInventory) {
      iteration++;
      if (is.isEmpty())
        continue; 
      int x = i - 90 + (9 - iteration) * 20 + 2;
      GlStateManager.enableDepth();
      RenderUtil.itemRender.zLevel = 200.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
      RenderUtil.itemRender.zLevel = 0.0F;
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
      this.renderer.drawStringWithShadow(s, (x + 19 - 2 - this.renderer.getStringWidth(s)), (y + 9), 16777215);
      if (percent) {
        int dmg = 0;
        int itemDurability = is.getMaxDamage() - is.getItemDamage();
        float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
        float red = 1.0F - green;
        if (percent) {
          dmg = 100 - (int)(red * 100.0F);
        } else {
          dmg = itemDurability;
        } 
        this.renderer.drawStringWithShadow(dmg + "", (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
      } 
    } 
    GlStateManager.enableDepth();
    GlStateManager.disableLighting();
  }
  
  public void drawHitMarkers() {
    ScaledResolution resolution = new ScaledResolution(mc);
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F - 4.0F, resolution.getScaledHeight() / 2.0F - 4.0F, resolution.getScaledWidth() / 2.0F - 8.0F, resolution.getScaledHeight() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F + 4.0F, resolution.getScaledHeight() / 2.0F - 4.0F, resolution.getScaledWidth() / 2.0F + 8.0F, resolution.getScaledHeight() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F - 4.0F, resolution.getScaledHeight() / 2.0F + 4.0F, resolution.getScaledWidth() / 2.0F - 8.0F, resolution.getScaledHeight() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F + 4.0F, resolution.getScaledHeight() / 2.0F + 4.0F, resolution.getScaledWidth() / 2.0F + 8.0F, resolution.getScaledHeight() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
  }
  
  public void drawTextRadar(int yOffset) {
    if (!this.players.isEmpty()) {
      int y = this.renderer.getFontHeight() + 7 + yOffset;
      for (Map.Entry<String, Integer> player : this.players.entrySet()) {
        String text = (String)player.getKey() + " ";
        int textheight = this.renderer.getFontHeight() + 1;
        this.renderer.drawString(text, 2.0F, y, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(y))).intValue() : this.color, true);
        y += textheight;
      } 
    } 
  }
  
  public enum Greeter {
    NONE, NAME, TIME, LONG, CUSTOM;
  }
  
  public enum LagNotify {
    NONE, RED, GRAY;
  }
  
  public enum WaterMark {
    NONE, PHOBOS, EARTH;
  }
  
  public enum Sound {
    NONE, COD, CSGO;
  }
}
