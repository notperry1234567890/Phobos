//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.PotionEffect;

public class StreamerMode extends Module {
  private SecondScreenFrame window = null;
  
  public Setting<Integer> width;
  
  public Setting<Integer> height;
  
  public StreamerMode() {
    super("StreamerMode", "Displays client info in a second window.", Module.Category.CLIENT, false, false, false);
    this.width = register(new Setting("Width", Integer.valueOf(600), Integer.valueOf(100), Integer.valueOf(3160)));
    this.height = register(new Setting("Height", Integer.valueOf(900), Integer.valueOf(100), Integer.valueOf(2140)));
  }
  
  public void onEnable() {
    EventQueue.invokeLater(() -> {
          if (this.window == null)
            this.window = new SecondScreenFrame(); 
          this.window.setVisible(true);
        });
  }
  
  public void onDisable() {
    if (this.window != null)
      this.window.setVisible(false); 
    this.window = null;
  }
  
  public void onLogout() {
    if (this.window != null) {
      ArrayList<String> drawInfo = new ArrayList<>();
      drawInfo.add("Phobos v1.5.4");
      drawInfo.add("");
      drawInfo.add("No Connection.");
      this.window.setToDraw(drawInfo);
    } 
  }
  
  public void onUnload() {
    disable();
  }
  
  public void onLoad() {
    disable();
  }
  
  public void onUpdate() {
    if (this.window != null) {
      ArrayList<String> drawInfo = new ArrayList<>();
      drawInfo.add("Phobos v1.5.4");
      drawInfo.add("");
      drawInfo.add("Fps: " + Minecraft.debugFPS);
      drawInfo.add("TPS: " + Phobos.serverManager.getTPS());
      drawInfo.add("Ping: " + Phobos.serverManager.getPing() + "ms");
      drawInfo.add("Speed: " + Phobos.speedManager.getSpeedKpH() + "km/h");
      drawInfo.add("Time: " + (new SimpleDateFormat("h:mm a")).format(new Date()));
      boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
      int posX = (int)mc.player.posX;
      int posY = (int)mc.player.posY;
      int posZ = (int)mc.player.posZ;
      float nether = !inHell ? 0.125F : 8.0F;
      int hposX = (int)(mc.player.posX * nether);
      int hposZ = (int)(mc.player.posZ * nether);
      String coordinates = "XYZ " + posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]";
      String text = Phobos.rotationManager.getDirection4D(false);
      drawInfo.add("");
      drawInfo.add(text);
      drawInfo.add(coordinates);
      drawInfo.add("");
      for (Module module : Phobos.moduleManager.sortedModules) {
        String moduleName = TextUtil.stripColor(module.getFullArrayString());
        drawInfo.add(moduleName);
      } 
      drawInfo.add("");
      for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
        String potionText = TextUtil.stripColor(Phobos.potionManager.getColoredPotionString(effect));
        drawInfo.add(potionText);
      } 
      drawInfo.add("");
      Map<String, Integer> map = EntityUtil.getTextRadarPlayers();
      if (!map.isEmpty())
        for (Map.Entry<String, Integer> player : map.entrySet()) {
          String playerText = TextUtil.stripColor(player.getKey());
          drawInfo.add(playerText);
        }  
      this.window.setToDraw(drawInfo);
    } 
  }
  
  public class SecondScreenFrame extends JFrame {
    private StreamerMode.SecondScreen panel;
    
    public SecondScreenFrame() {
      initUI();
    }
    
    private void initUI() {
      this.panel = new StreamerMode.SecondScreen();
      add(this.panel);
      setResizable(true);
      pack();
      setTitle("Phobos - Info");
      setLocationRelativeTo((Component)null);
      setDefaultCloseOperation(2);
    }
    
    public void setToDraw(ArrayList<String> list) {
      this.panel.setToDraw(list);
    }
  }
  
  public class SecondScreen extends JPanel {
    private final int B_WIDTH = ((Integer)StreamerMode.this.width.getValue()).intValue();
    
    private final int B_HEIGHT = ((Integer)StreamerMode.this.height.getValue()).intValue();
    
    private Font font = new Font("Verdana", 0, 20);
    
    private ArrayList<String> toDraw = new ArrayList<>();
    
    public void setToDraw(ArrayList<String> list) {
      this.toDraw = list;
      repaint();
    }
    
    public void setFont(Font font) {
      this.font = font;
    }
    
    public SecondScreen() {
      initBoard();
    }
    
    public void setWindowSize(int width, int height) {
      setPreferredSize(new Dimension(width, height));
    }
    
    private void initBoard() {
      setBackground(Color.black);
      setFocusable(true);
      setPreferredSize(new Dimension(this.B_WIDTH, this.B_HEIGHT));
    }
    
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawScreen(g);
    }
    
    private void drawScreen(Graphics g) {
      Font small = this.font;
      FontMetrics metr = getFontMetrics(small);
      g.setColor(Color.white);
      g.setFont(small);
      int y = 40;
      for (String msg : this.toDraw) {
        g.drawString(msg, (getWidth() - metr.stringWidth(msg)) / 2, y);
        y += 20;
      } 
      Toolkit.getDefaultToolkit().sync();
    }
  }
}
