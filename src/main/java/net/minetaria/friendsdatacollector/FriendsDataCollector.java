package net.minetaria.friendsdatacollector;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.minetaria.friendsdatacollector.commands.FriendCommand;
import net.minetaria.friendsdatacollector.listener.PlayerConnectListener;
import net.minetaria.friendsdatacollector.listener.PlayerDisconnectListener;
import net.minetaria.friendsdatacollector.listener.PlayerLoginListener;
import net.minetaria.friendsdatacollector.listener.PlayerSwitchServerListener;
import net.minetaria.friendsdatacollector.utils.SQLUtil;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class FriendsDataCollector extends Plugin implements Listener {

    public static FriendsDataCollector instance;
    private SQLUtil sqlUtil;

    public final static String prefix = "§5§lFriends §8• §7";

    @Override
    public void onEnable() {
        instance = this;

        sqlUtil = new SQLUtil("jdbc:mysql://116.202.235.165:3306/FriendsAPI", "minetaria", "2S3jYDYC9rfLi4P7");
        try {
            sqlUtil.executeUpdate("CREATE TABLE IF NOT EXISTS `friendsAPI_playerData` (`uuid` VARCHAR(100) NOT NULL , `name` VARCHAR(100) NOT NULL , `online` INT NOT NULL , `server` VARCHAR(100) NOT NULL , `lastLogin` BIGINT NOT NULL ) ENGINE = InnoDB;");
            getProxy().getScheduler().schedule(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        sqlUtil.executeQuery("SELECT 1");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 30, TimeUnit.SECONDS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        getProxy().getPluginManager().registerCommand(this, new FriendCommand());

        getProxy().registerChannel("BungeeCord");
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getPluginManager().registerListener(this, new PlayerConnectListener());
        getProxy().getPluginManager().registerListener(this, new PlayerDisconnectListener());
        getProxy().getPluginManager().registerListener(this, new PlayerSwitchServerListener());
        getProxy().getPluginManager().registerListener(this, new PlayerLoginListener());
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if(channel.equals("bungeeCommandExecutionFriendsAPI")){
                    StringBuilder input = new StringBuilder(in.readUTF());
                    input.deleteCharAt(0);
                    getProxy().getPluginManager().dispatchCommand(getProxy().getPlayer(event.getReceiver().toString()), input.toString());
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

    @Override
    public void onDisable() {
        try {
            sqlUtil.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public final static UUID getUUIDfromName(String name) {
        UUID endReturn = null;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `name` = '" + name + "';");
            if (rs.next()) {
                endReturn = UUID.fromString(rs.getString("uuid"));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static String getNamefromUUID(UUID uuid) {
        String endReturn = null;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getString("name");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static boolean isOnline(UUID uuid) {
        boolean endReturn = false;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getBoolean("online");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static String getServerName(UUID uuid) {
        String endReturn = "";
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getString("server");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static long getLastLogin(UUID uuid) {
        long endReturn = System.currentTimeMillis();
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getLong("lastLogin");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    //CREATE FRIENDS
    public final static boolean pairFriends(String uuid1, String uuid2) {
        boolean succes = false;
        succes = addUuidToFriendList(uuid1,uuid2);
        succes = addUuidToFriendList(uuid2,uuid1);
        return succes;
    }
    private final static boolean addUuidToFriendList(String uuid1, String uuid2) {
        boolean succes = false;
        ArrayList<String> gettedFriends = getFriends(uuid1);
        StringBuilder newFriends = new StringBuilder("");
        if (!gettedFriends.isEmpty()) {
            for (String s:gettedFriends) {
                newFriends.append(s + ";");
            }
        }
        if (!newFriends.toString().contains(uuid2)) {
            newFriends.append(uuid2 + ";");
            try {
                getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_list` SET `friendsAPI_list`.`friends` = '" + newFriends.toString() + "' WHERE `friendsAPI_list`.`uuid` = '" + uuid1 + "';");
                succes = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return succes;
    }

    //DESTROY FRIENDS
    public final static boolean removeFriends(String uuid1, String uuid2) {
        boolean succes = false;
        succes = removeUuidToFriendList(uuid1,uuid2);
        succes = removeUuidToFriendList(uuid2,uuid1);
        return succes;
    }
    private final static boolean removeUuidToFriendList(String uuid1, String uuid2) {
        boolean succes = false;
        ArrayList<String> gettedFriends = getFriends(uuid1);
        StringBuilder newFriends = new StringBuilder("");
        if (!gettedFriends.isEmpty()) {
            for (String s:gettedFriends) {
                newFriends.append(s + ";");
            }
        }
        if (newFriends.toString().contains(uuid2)) {
            String temp = newFriends.toString();
            temp = temp.replaceAll(uuid2 + ";", "");
            try {
                getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_list` SET `friendsAPI_list`.`friends` = '" + temp + "' WHERE `friendsAPI_list`.`uuid` = '" + uuid1 + "';");
                succes = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return succes;
    }

    //GET FRIENDS
    public final static ArrayList<String> getFriends(String uuid) {
        ArrayList<String> endReturn = new ArrayList<>();
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_list` WHERE `friendsAPI_list`.`uuid` = '" + uuid + "';");
            if (rs.next()) {
                String[] temp = rs.getString("friends").split(";");
                for (String t:temp) {
                    if (t.length() > 5) {
                        endReturn.add(t);
                    }
                }
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    //SEND FRIEND REQUEST
    public final static boolean sendFriendRequest(String senderUUID, String targetUUID) {
        boolean succes = false;
        boolean exists = false;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_requests` WHERE (`friendsAPI_requests`.`senderUUID` = '" + senderUUID + "' AND `friendsAPI_requests`.`targetUUID` = '" + targetUUID + "') OR (`friendsAPI_requests`.`targetUUID` = '" + senderUUID + "' AND `friendsAPI_requests`.`senderUUID` = '" + targetUUID + "');");
            exists = rs.next();
            rs.close();
            if (!exists) {
                getInstance().getSqlUtil().executeUpdate("INSERT INTO `friendsAPI_requests` (`senderUUID`, `targetUUID`) VALUES ('" + senderUUID + "', '" + targetUUID + "')");
                succes = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return succes;
    }

    //REMOVE FRIEND REQUEST
    public final static boolean removeFriendRequest(String senderUUID, String targetUUID) {
        boolean succes = false;
        boolean exists = false;
        try {
            getInstance().getSqlUtil().executeUpdate("DELETE FROM `friendsAPI_requests` WHERE `friendsAPI_requests`.`senderUUID` = '" + senderUUID + "' AND `friendsAPI_requests`.`targetUUID` = '" + targetUUID + "';");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return succes;
    }

    //GET FRIEND REQUESTS
    public final static ArrayList<String> getFriendRequests(String uuid) {
        ArrayList<String> endReturn = new ArrayList<>();
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_requests` WHERE `friendsAPI_requests`.`targetUUID` = '" + uuid + "';");
            if (rs.next()) {
                endReturn.add(rs.getString("senderUUID"));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public static FriendsDataCollector getInstance() {
        return instance;
    }

    public SQLUtil getSqlUtil() {
        return sqlUtil;
    }
}
