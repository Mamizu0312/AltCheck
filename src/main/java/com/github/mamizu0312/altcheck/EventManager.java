package com.github.mamizu0312.altcheck;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventManager implements Listener {
    AltCheck plugin;
    public EventManager(AltCheck plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if(!p.isOnline()) {
            return;
        }
        if(p.getAddress() == null) {
            p.kickPlayer("IPアドレスの取得に失敗したため、接続は拒否されました");
            return;
        }
        String ip = p.getAddress().toString();
        if(ip.contains("/127.0.0.1"))  {
            ip = "/127.0.0.1";
        }
        MySQLManager sql = plugin.sql;
        ResultSet playerData = sql.query("SELECT * FROM PLAYERDATA WHERE UUID = '"+p.getUniqueId()+"';");
        try {
            if(!playerData.next()) {
                sql.sendPlayerData(p);
                playerData.close();
            }
        } catch (SQLException se) {
            plugin.getLogger().warning("SQLとの接続に失敗しました");
            se.printStackTrace();
            return;
        }
        sql = new MySQLManager(plugin,"AltCheck");
        ResultSet sameIpMCIDs = sql.query("SELECT * FROM PLAYERDATA WHERE ADDRESS = '"+ip+"';");
        List<OfflinePlayer> sameIpPlayers = new ArrayList<>();

        try {
            sameIpMCIDs.first();
            while(sameIpMCIDs.next()) {
                sameIpPlayers.add(Bukkit.getPlayer(UUID.fromString(sameIpMCIDs.getString("UUID"))));
            }
            if(sameIpPlayers.isEmpty()){
                Bukkit.broadcastMessage("プレイヤーは存在しない");
                return;
            }
            Bukkit.broadcastMessage("プレイヤーは存在した");
            for(OfflinePlayer sameIpPlayer : sameIpPlayers) {
                Bukkit.broadcastMessage("チェックなう");
                if(sameIpPlayer == null) {
                    Bukkit.broadcastMessage("ぶれいく");
                    continue;
                }
                if(sameIpPlayer.isBanned()) {
                    Bukkit.broadcastMessage("§7[§c§l*警告*§r§7]§r§c§l"+p.getName()+"は別のアカウントでBANされています！("+sameIpPlayer.getName()+")§r");
                    break;
                }
            }
            sameIpMCIDs.close();
        } catch (SQLException se) {
            plugin.getLogger().warning("SQLとの接続に失敗しました");
            se.printStackTrace();
        }

    }
}
