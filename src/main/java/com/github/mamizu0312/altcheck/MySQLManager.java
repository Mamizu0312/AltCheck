package com.github.mamizu0312.altcheck;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.*;

/**
 * Created by takatronix on 2017/03/05.
 */


import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;



public class MySQLManager {

    public  Boolean debugMode = false;
    private JavaPlugin plugin;
    private String HOST = null;
    private String DB = null;
    private String USER = null;
    private String PASS = null;
    private String PORT = null;
    private boolean connected = false;
    private Statement st = null;
    private Connection con = null;
    private String conName;
    private MySQLFunc MySQL;
    private ConfigManager config;
    HashMap<String,String> configs;
    ////////////////////////////////
    //      コンストラクタ
    ////////////////////////////////
    public MySQLManager(AltCheck plugin, String name) {
        this.plugin = plugin;
        this.conName = name;
        this.connected = false;
        this.config = plugin.config;
        HOST = plugin.HOST;
        DB = plugin.DB;
        USER = plugin.USER;
        PASS = plugin.PASS;
        PORT = plugin.PORT;

        this.connected = Connect(HOST, DB, USER, PASS,PORT);
        execute("create table if not exists PLAYERDATA ("+
                "MCID varchar(40),"+
                "UUID varchar(40),"+
                "ADDRESS varchar(40)"+
                ");");
        if(!this.connected) {
            plugin.getLogger().info("MySQLへの接続が確立できませんでした。");
        }
    }

    /////////////////////////////////
    //       設定ファイル読み込み
    /////////////////////////////////

    ////////////////////////////////
    //       接続
    ////////////////////////////////
    public Boolean Connect(String host, String db, String user, String pass,String port) {
        this.HOST = host;
        this.DB = db;
        this.USER = user;
        this.PASS = pass;
        this.MySQL = new MySQLFunc(host, db, user, pass,port);
        this.con = this.MySQL.open();
        if(this.con == null){
            Bukkit.getLogger().info("MySQLを開けませんでした。");
            return false;
        }

        try {
            this.st = this.con.createStatement();
            this.connected = true;
            this.plugin.getLogger().info("[" + this.conName + "] がデータベースに接続しました。");
        } catch (SQLException var6) {
            this.connected = false;
            this.plugin.getLogger().info("[" + this.conName + "] はデータベースに接続できませんでした。");
        }

        this.MySQL.close(this.con);
        return Boolean.valueOf(this.connected);
    }

    ////////////////////////////////
    //     行数を数える
    ////////////////////////////////
    public int countRows(String table) {
        int count = 0;
        ResultSet set = this.query(String.format("SELECT * FROM %s", new Object[]{table}));

        try {
            while(set.next()) {
                ++count;
            }
        } catch (SQLException var5) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not select all rows from table: " + table + ", error: " + var5.getErrorCode());
        }

        return count;
    }
    ////////////////////////////////
    //     レコード数
    ////////////////////////////////
    public int count(String table) {
        int count = 0;
        ResultSet set = this.query(String.format("SELECT count(*) from %s", table));

        try {
            count = set.getInt("count(*)");

        } catch (SQLException var5) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not select all rows from table: " + table + ", error: " + var5.getErrorCode());
            return -1;
        }

        return count;
    }
    ////////////////////////////////
    //      実行
    ////////////////////////////////
    public boolean execute(String query) {
        this.MySQL = new MySQLFunc(this.HOST, this.DB, this.USER, this.PASS,this.PORT);
        this.con = this.MySQL.open();
        if(this.con == null){
            Bukkit.getLogger().info("MySQLを開けませんでした。");
            return false;
        }
        boolean ret = true;
        if (debugMode){
            plugin.getLogger().info("query:" + query);
        }

        try {
            this.st = this.con.createStatement();
            this.st.execute(query);
        } catch (SQLException var3) {
            this.plugin.getLogger().info("[" + this.conName + "] Error executing statement: " +var3.getErrorCode() +":"+ var3.getLocalizedMessage());
            this.plugin.getLogger().info(query);
            ret = false;

        }

        this.MySQL.close(this.con);
        return ret;
    }

    ////////////////////////////////
    //      クエリ
    ////////////////////////////////
    public ResultSet query(String query) {
        this.MySQL = new  MySQLFunc(this.HOST, this.DB, this.USER, this.PASS,this.PORT);
        this.con = this.MySQL.open();
        ResultSet rs = null;
        if(this.con == null){
            Bukkit.getLogger().info("MySQLを開けませんでした。");
            return rs;
        }




        if (debugMode){
            plugin.getLogger().info("query:" + query);
        }

        try {
            this.st = this.con.createStatement();
            rs = this.st.executeQuery(query);
        } catch (SQLException var4) {
            this.plugin.getLogger().info("[" + this.conName + "] Error executing query: " + var4.getErrorCode());
        }

        return rs;
    }
    public void sendPlayerData(Player p) {
        try {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO PLAYERDATA VALUES(?,?,?)");
            pstmt.setString(1,p.getName());
            pstmt.setString(2,p.getUniqueId().toString());
            if(p.getAddress().toString().contains("127.0.0.1")) {
                pstmt.setString(3,"/127.0.0.1");
            } else {
                pstmt.setString(3,p.getAddress().toString());
            }
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            plugin.getLogger().warning("SQLとの接続に失敗しました");
            e.printStackTrace();
        }
    }
}