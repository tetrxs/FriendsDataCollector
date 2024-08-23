package net.minetaria.friendsdatacollector.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minetaria.friendsdatacollector.FriendsDataCollector;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerSwitchServerListener implements Listener {
    @EventHandler
    public void onPlayerSwitchServer(ServerSwitchEvent event) {

        ProxiedPlayer player = event.getPlayer();

        boolean exists = false;
        try {
            ResultSet rs = FriendsDataCollector.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `friendsAPI_playerData`.`uuid` = '" + player.getUniqueId().toString() + "';");
            exists = rs.next();
            rs.close();
            if (exists) {
                FriendsDataCollector.getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_playerData` SET `server` = '" + player.getServer().getInfo().getName() + "' WHERE `uuid` = '" + player.getUniqueId().toString() + "';");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
