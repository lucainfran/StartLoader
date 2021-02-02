package me.setup;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActivatorMain extends JavaPlugin {
   public Configs commands;

   public void onEnable() {
      this.commands = new Configs("commands.yml", this);
      this.commands.saveDefaultConfig();
      (new BukkitRunnable() {
         public void run() {
            Iterator var1 = ActivatorMain.this.commands.getStringList("Commands").iterator();

            while(var1.hasNext()) {
               String commandsList = (String)var1.next();
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandsList);
            }

         }
      }).runTaskLater(this, 20L);
   }

   public void onDisable() {
   }
}