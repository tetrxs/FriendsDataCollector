package net.minetaria.friendsdatacollector.listener;

import de.gaunercools.languageapibungee.mysql.LanguageAPIBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minetaria.friendsdatacollector.FriendsDataCollector;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerConnectListener implements Listener {
    @EventHandler
    public void onPlayerConnect(ServerConnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        boolean exists = false;
        try {
            ResultSet rs = FriendsDataCollector.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `friendsAPI_playerData`.`uuid` = '" + player.getUniqueId().toString() + "';");
            exists = rs.next();
            rs.close();
            if (!exists) {
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("INSERT INTO `friendsAPI_playerData` (`uuid`, `name`, `online`, `server`, `lastLogin`) VALUES ('" + player.getUniqueId().toString() + "', '" + player.getName() + "', '1', '" + event.getTarget().getName() + "', '" + String.valueOf(System.currentTimeMillis()) + "')");
            } else {
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `online` = '1' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `server` = '" + event.getTarget().getName() + "' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `lastLogin` = '" + System.currentTimeMillis() + "' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
                refreshName(player);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void refreshName(ProxiedPlayer player) {
        String currentSavedName = null;
        try {
            ResultSet rs = FriendsDataCollector.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
            if (rs.next()) {
                currentSavedName = rs.getString("name");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (currentSavedName != null) {
            if (!currentSavedName.equals(player.getName())) {
                try {
                    FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `name` = '" + player.getName() + "' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void notifyFriends(ProxiedPlayer player) {
        ArrayList<String> friends = FriendsDataCollector.getFriends(player.getUniqueId().toString());
        for (String s:friends) {
            ProxiedPlayer friend = FriendsDataCollector.getInstance().getProxy().getPlayer(UUID.fromString(s));
            //LANGUAGE API
            friend.sendMessage();
        }
    }
}
