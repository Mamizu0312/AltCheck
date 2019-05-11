package com.github.mamizu0312.altcheck;

import java.util.HashMap;

public class ConfigManager {
    AltCheck plugin;
    MySQLManager sql;
    public ConfigManager(AltCheck plugin) {
        this.plugin = plugin;
    }
    public ConfigManager(AltCheck plugin, MySQLManager sql) {
        this.plugin = plugin;
        this.sql = sql;
    }
    public void loadConfig() {
        plugin.reloadConfig();
        plugin.HOST = plugin.getConfig().getString("mysql.host");
        plugin.USER = plugin.getConfig().getString("mysql.user");
        plugin.DB = plugin.getConfig().getString("mysql.db");
        plugin.PASS = plugin.getConfig().getString("mysql.pass");
        plugin.PORT = plugin.getConfig().getString("mysql.port");
    }
}
