package net.minetaria.friendsdatacollector.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minetaria.friendsdatacollector.FriendsDataCollector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class PlayerDisconnectListener implements Listener {
    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        boolean exists = false;
        try {
            ResultSet rs = FriendsDataCollector.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `friendsAPI_playerData`.`uuid` = '" + player.getUniqueId().toString() + "';");
            exists = rs.next();
            rs.close();
            if (exists) {
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `online` = '0' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `server` = 'none' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
