package com.github.mamizu0312.altcheck;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AltCheck extends JavaPlugin {
    String prefix = "§7[§c§lAltCheck§7]§r";
    MySQLManager sql;
    ConfigManager config;
    String HOST;
    String DB;
    String USER;
    String PASS;
    String PORT;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getServer().getPluginManager().registerEvents(new EventManager(this), this);
        getCommand("altcheck").setExecutor(new CommandManager(this));
        config = new ConfigManager(this);
        config.loadConfig();
        sql = new MySQLManager(this,"AltCheck");
        //まだ開発中だからところどころコードがおかしい
    }
}
