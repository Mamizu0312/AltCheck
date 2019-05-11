package com.github.mamizu0312.altcheck;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CommandManager implements CommandExecutor {
    AltCheck plugin;
    MySQLManager sql;
    public CommandManager(AltCheck plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(plugin.prefix + "altcheck MCID: 同一IPのプレイヤー検出します");
            return true;
            //コンソール側のコマンドを実装
        }
        Player p = (Player)sender;
        if(!p.hasPermission("altcheck.admin")) p.sendMessage("コマンドが違います。 Unknown command. Type "+"/help"+" for help.");
        if(args.length == 0) {
            p.sendMessage(plugin.prefix + "/altcheck MCID: 同一IPのプレイヤーを検出します");
            return true;
        }
        if(args.length == 1) {
            if(args[0].length() > 17) {
                p.sendMessage(plugin.prefix + "MCIDは最大16文字です");
                return true;
            }
            sql = plugin.sql;
            if(sql == null) {
                p.sendMessage(plugin.prefix + "MySQLとの接続がありません");
                return true;
            }
            ResultSet findPlayer = sql.query("SELECT * FROM PLAYERDATA WHERE MCID = '"+args[0]+"';");
            try {
                if(!findPlayer.next()) {
                    p.sendMessage(plugin.prefix + "そのプレイヤーは存在しません");
                    return true;
                }
            } catch (SQLException e) {
                p.sendMessage(plugin.prefix + "エラーが発生しました。ログを確認してください");
                e.printStackTrace();
                return true;
            }
            ResultSet pipresult = sql.query("SELECT * FROM PLAYERDATA WHERE UUID = '"+p.getUniqueId()+"';");
            String pip;
            try {
                pipresult.next();
                pip = pipresult.getString("ADDRESS");
            } catch(SQLException e1) {
                p.sendMessage(plugin.prefix + "そのプレイヤーは存在しません");
                e1.printStackTrace();
                return true;
            }
            ResultSet sameIpMCIDs = sql.query("SELECT * FROM PLAYERDATA WHERE ADDRESS = '"+pip+"';");
            try {
                p.sendMessage(plugin.prefix +"同一IPから接続されているアカウント");
                while(sameIpMCIDs.next()) {
                    p.sendMessage("§l"+sameIpMCIDs.getString("MCID"));
                }
            } catch(SQLException e2) {
                p.sendMessage(plugin.prefix + "エラーが発生しました。ログを確認してください");
                e2.printStackTrace();
                return true;
            }
        }
        return true;
    }
}
