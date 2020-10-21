package me.earth.phobos.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.render.XRay;
import me.earth.phobos.features.setting.Bind;
import me.earth.phobos.features.setting.EnumConverter;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.Util;

public class ConfigManager implements Util {
  public ArrayList<Feature> features = new ArrayList<>();
  
  public String config = "phobos/config/";
  
  public void loadConfig(String name) {
    List<File> files = (List<File>)Arrays.<Object>stream((Object[])Objects.requireNonNull((new File("phobos")).listFiles())).filter(File::isDirectory).collect(Collectors.toList());
    if (files.contains(new File("phobos/" + name + "/"))) {
      this.config = "phobos/" + name + "/";
    } else {
      this.config = "phobos/config/";
    } 
    Phobos.friendManager.onLoad();
    for (Feature feature : this.features) {
      try {
        loadSettings(feature);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    saveCurrentConfig();
  }
  
  public void saveConfig(String name) {
    this.config = "phobos/" + name + "/";
    File path = new File(this.config);
    if (!path.exists())
      path.mkdir(); 
    Phobos.friendManager.saveFriends();
    for (Feature feature : this.features) {
      try {
        saveSettings(feature);
      } catch (IOException e) {
        e.printStackTrace();
      } 
    } 
    saveCurrentConfig();
  }
  
  public void saveCurrentConfig() {
    File currentConfig = new File("phobos/currentconfig.txt");
    try {
      if (currentConfig.exists()) {
        FileWriter writer = new FileWriter(currentConfig);
        String tempConfig = this.config.replaceAll("/", "");
        writer.write(tempConfig.replaceAll("phobos", ""));
        writer.close();
      } else {
        currentConfig.createNewFile();
        FileWriter writer = new FileWriter(currentConfig);
        String tempConfig = this.config.replaceAll("/", "");
        writer.write(tempConfig.replaceAll("phobos", ""));
        writer.close();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public String loadCurrentConfig() {
    File currentConfig = new File("phobos/currentconfig.txt");
    String name = "config";
    try {
      if (currentConfig.exists()) {
        Scanner reader = new Scanner(currentConfig);
        while (reader.hasNextLine())
          name = reader.nextLine(); 
        reader.close();
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return name;
  }
  
  public void resetConfig(boolean saveConfig, String name) {
    for (Feature feature : this.features)
      feature.reset(); 
    if (saveConfig)
      saveConfig(name); 
  }
  
  public void saveSettings(Feature feature) throws IOException {
    JsonObject object = new JsonObject();
    File directory = new File(this.config + getDirectory(feature));
    if (!directory.exists())
      directory.mkdir(); 
    String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
    Path outputFile = Paths.get(featureName, new String[0]);
    if (!Files.exists(outputFile, new java.nio.file.LinkOption[0]))
      Files.createFile(outputFile, (FileAttribute<?>[])new FileAttribute[0]); 
    Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
    String json = gson.toJson((JsonElement)writeSettings(feature));
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(outputFile, new java.nio.file.OpenOption[0])));
    writer.write(json);
    writer.close();
  }
  
  public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
    String str;
    switch (setting.getType()) {
      case "Boolean":
        setting.setValue(Boolean.valueOf(element.getAsBoolean()));
        return;
      case "Double":
        setting.setValue(Double.valueOf(element.getAsDouble()));
        return;
      case "Float":
        setting.setValue(Float.valueOf(element.getAsFloat()));
        return;
      case "Integer":
        setting.setValue(Integer.valueOf(element.getAsInt()));
        return;
      case "String":
        str = element.getAsString();
        setting.setValue(str.replace("_", " "));
        return;
      case "Bind":
        setting.setValue((new Bind.BindConverter()).doBackward(element));
        return;
      case "Enum":
        try {
          EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
          Enum value = converter.doBackward(element);
          setting.setValue((value == null) ? setting.getDefaultValue() : value);
        } catch (Exception e) {}
        return;
    } 
    Phobos.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
  }
  
  public void init() {
    this.features.addAll(Phobos.moduleManager.modules);
    this.features.add(Phobos.friendManager);
    String name = loadCurrentConfig();
    loadConfig(name);
    Phobos.LOGGER.info("Config loaded.");
  }
  
  private void loadSettings(Feature feature) throws IOException {
    String featureName = this.config + getDirectory(feature) + feature.getName() + ".json";
    Path featurePath = Paths.get(featureName, new String[0]);
    if (!Files.exists(featurePath, new java.nio.file.LinkOption[0]))
      return; 
    loadPath(featurePath, feature);
  }
  
  private void loadPath(Path path, Feature feature) throws IOException {
    InputStream stream = Files.newInputStream(path, new java.nio.file.OpenOption[0]);
    try {
      loadFile((new JsonParser()).parse(new InputStreamReader(stream)).getAsJsonObject(), feature);
    } catch (IllegalStateException e) {
      Phobos.LOGGER.error("Bad Config File for: " + feature.getName() + ". Resetting...");
      loadFile(new JsonObject(), feature);
    } 
    stream.close();
  }
  
  private static void loadFile(JsonObject input, Feature feature) {
    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)input.entrySet()) {
      String settingName = entry.getKey();
      JsonElement element = entry.getValue();
      if (feature instanceof FriendManager) {
        try {
          Phobos.friendManager.addFriend(new FriendManager.Friend(element.getAsString(), UUID.fromString(settingName)));
        } catch (Exception e) {
          e.printStackTrace();
        } 
      } else {
        boolean settingFound = false;
        for (Setting setting : feature.getSettings()) {
          if (settingName.equals(setting.getName())) {
            try {
              setValueFromJson(feature, setting, element);
            } catch (Exception e) {
              e.printStackTrace();
            } 
            settingFound = true;
          } 
        } 
        if (settingFound)
          continue; 
      } 
      if (feature instanceof XRay)
        feature.register(new Setting(settingName, Boolean.valueOf(true), v -> ((Boolean)((XRay)feature).showBlocks.getValue()).booleanValue())); 
    } 
  }
  
  public JsonObject writeSettings(Feature feature) {
    JsonObject object = new JsonObject();
    JsonParser jp = new JsonParser();
    for (Setting setting : feature.getSettings()) {
      if (setting.isEnumSetting()) {
        EnumConverter converter = new EnumConverter(((Enum)setting.getValue()).getClass());
        object.add(setting.getName(), converter.doForward((Enum)setting.getValue()));
        continue;
      } 
      if (setting.isStringSetting()) {
        String str = (String)setting.getValue();
        setting.setValue(str.replace(" ", "_"));
      } 
      try {
        object.add(setting.getName(), jp.parse(setting.getValueAsString()));
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
    return object;
  }
  
  public String getDirectory(Feature feature) {
    String directory = "";
    if (feature instanceof Module)
      directory = directory + ((Module)feature).getCategory().getName() + "/"; 
    return directory;
  }
}
