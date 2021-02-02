package me.setup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Configs {
	private Plugin plugin;
	private String name;
	private File file;
	private FileConfiguration config;
	
	public String getName() {
		return this.name;
	}
	
	public Location getLocation(String path) {
	ConfigurationSection section = this.getSection(path);
	World world = Bukkit.getWorld(section.getString("world"));
	double x = section.getDouble("x");
	double y = section.getDouble("y");
	double z = section.getDouble("z");
	float yaw = (float)section.getDouble("yaw");
	float pitch = (float)section.getDouble("pitch");
	return new Location(world, x, y, z, yaw, pitch);
}
	
	public void setLocation(String path, Location location) {
		ConfigurationSection section = this.getSection(path);
		section.set("x", location.getX());
		section.set("y", location.getY());
		section.set("z", location.getZ());
		section.set("yaw", location.getYaw());
		section.set("pitch", location.getPitch());
		section.set("world", location.getWorld().getName());
	}
	  public void setPlugin(Plugin plugin) {
	      this.plugin = plugin;
	   }

	   public File getFile() {
	      return this.file;
	   }

	   public FileConfiguration getConfig() {
	      return this.config;
	   }

	   public Configs(String name, Plugin plugin) {
	      this.plugin = plugin;
	      if (plugin == null) {
	         this.plugin = JavaPlugin.getProvidingPlugin(this.getClass());
	      }

	      this.name = name;
	      this.reloadConfig();
	   }

	   public Configs(String name) {
	      this(name, (Plugin)null);
	   }

	   public Configs reloadConfig() {
	      this.file = new File(this.plugin.getDataFolder(), this.name);
	      this.config = YamlConfiguration.loadConfiguration(this.file);
	      InputStream defaults = this.plugin.getResource(this.file.getName());
	      if (defaults != null) {
	         YamlConfiguration loadConfig = YamlConfiguration.loadConfiguration(defaults);
	         this.config.setDefaults(loadConfig);
	      }

	      return this;
	   }

	   public Configs saveConfig() {
	      try {
	         this.config.save(this.file);
	      } catch (IOException var2) {
	         var2.printStackTrace();
	      }

	      return this;
	   }

	   public String message(String path) {
	      return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(path));
	   }

	   public List<String> getMessages(String path) {
	      List<String> messages = new ArrayList();
	      Iterator var3 = this.getStringList(path).iterator();

	      while(var3.hasNext()) {
	         String line = (String)var3.next();
	         messages.add(toChatMessage(line));
	      }

	      return messages;
	   }

	   public void saveDefaultConfig() {
	      if (this.plugin.getResource(this.name) != null) {
	         this.plugin.saveResource(this.name, false);
	      }

	      this.reloadConfig();
	   }

	   public void saveResource() {
	      this.plugin.saveResource(this.name, true);
	   }

	   public void remove(String path) {
	      this.config.set(path, (Object)null);
	   }

	   public Configs saveDefault() {
	      this.config.options().copyDefaults(true);
	      this.saveConfig();
	      return this;
	   }

	   public ItemStack getItem(String path) {
	      return (ItemStack)this.get(path);
	   }

	   public void setItem(String path, ItemStack item) {
	      ConfigurationSection section = this.getSection(path);
	      section.set("id", item.getTypeId());
	      section.set("data", item.getDurability());
	      if (item.hasItemMeta()) {
	         ItemMeta meta = item.getItemMeta();
	         if (meta.hasDisplayName()) {
	            section.set("name", meta.getDisplayName());
	         }

	         if (meta.hasLore()) {
	            List<String> lines = new ArrayList();
	            Iterator var6 = meta.getLore().iterator();

	            while(var6.hasNext()) {
	               String line = (String)var6.next();
	               lines.add(line);
	            }

	            section.set("lore", lines);
	         }
	      }

	      StringBuilder text = new StringBuilder();
	      Iterator var9 = item.getEnchantments().entrySet().iterator();

	      while(var9.hasNext()) {
	         Entry<Enchantment, Integer> enchant = (Entry)var9.next();
	         text.append(((Enchantment)enchant.getKey()).getId() + "-" + enchant.getValue() + ",");
	      }

	      section.set("enchant", text.toString());
	   }

	   public Location toLocation(String path) {
	      String text = this.getString(path);
	      String[] split = text.split(",");
	      World world = Bukkit.getWorld(split[0]);
	      double x = Double.parseDouble(split[1]);
	      double y = Double.parseDouble(split[2]);
	      double z = Double.parseDouble(split[3]);
	      float yaw = Float.parseFloat(split[4]);
	      float pitch = Float.parseFloat(split[5]);
	      return new Location(world, x, y, z, yaw, pitch);
	   }

	   public void saveLocation(String path, Location location) {
	      StringBuilder text = new StringBuilder();
	      text.append(location.getWorld().getName() + ",");
	      text.append(location.getX() + ",");
	      text.append(location.getY() + ",");
	      text.append(location.getZ() + ",");
	      text.append(location.getYaw() + ",");
	      text.append(location.getPitch());
	      this.set(path, text.toString());
	   }

	   public static String toChatMessage(String text) {
	      return ChatColor.translateAlternateColorCodes('&', text);
	   }

	   public static String toConfigMessage(String text) {
	      return text.replace("§", "&");
	   }

	   public boolean delete() {
	      return this.file.delete();
	   }

	   public boolean exists() {
	      return this.file.exists();
	   }

	   public void add(String path, Object value) {
	      this.config.addDefault(path, value);
	   }

	   public boolean contains(String path) {
	      return this.config.contains(path);
	   }

	   public ConfigurationSection create(String path) {
	      return this.config.createSection(path);
	   }

	   public Object get(String path) {
	      return this.config.get(path);
	   }

	   public boolean getBoolean(String path) {
	      return this.config.getBoolean(path);
	   }

	   public ConfigurationSection getSection(String path) {
	      if (!this.config.isConfigurationSection(path)) {
	         this.config.createSection(path);
	      }

	      return this.config.getConfigurationSection(path);
	   }

	   public double getDouble(String path) {
	      return this.config.getDouble(path);
	   }

	   public int getInt(String path) {
	      return this.config.getInt(path);
	   }

	   public List<Integer> getIntegerList(String path) {
	      return this.config.getIntegerList(path);
	   }

	   public ItemStack getItemStack(String path) {
	      return this.config.getItemStack(path);
	   }

	   public Set<String> getKeys(boolean deep) {
	      return this.config.getKeys(deep);
	   }

	   public List<?> getList(String path) {
	      return this.config.getList(path);
	   }

	   public long getLong(String path) {
	      return this.config.getLong(path);
	   }

	   public List<Long> getLongList(String path) {
	      return this.config.getLongList(path);
	   }

	   public List<Map<?, ?>> getMapList(String path) {
	      return this.config.getMapList(path);
	   }

	   public String getString(String path) {
	      return this.config.getString(path);
	   }

	   public List<String> getStringList(String path) {
	      return this.config.getStringList(path);
	   }

	   public Map<String, Object> getValues(boolean deep) {
	      return this.config.getValues(deep);
	   }

	   public void set(String path, Object value) {
	      this.config.set(path, value);
	   }
	}

	
	
