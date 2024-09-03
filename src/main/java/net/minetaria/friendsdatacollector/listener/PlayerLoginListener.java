package net.minetaria.friendsdatacollector.listener;

import de.gaunercools.languageapibungee.mysql.LanguageAPIBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.minetaria.friendsdatacollector.FriendsDataCollector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PlayerLoginListener implements Listener {
    @EventHandler
    public void onPlayerConnect(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        boolean exists = false;
        try {
            ResultSet rs = FriendsDataCollector.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `friendsAPI_playerData`.`uuid` = '" + player.getUniqueId().toString() + "';");
            exists = rs.next();
            rs.close();
            if (exists) {
                ArrayList<String> loadedFriends = FriendsDataCollector.getFriends(player.getUniqueId().toString());
                for (String s:loadedFriends) {
                    ProxiedPlayer target = null;
                    for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                        if (all.getUniqueId().toString().equals(s)) {
                            target = all;
                        }
                    }
                    if (target!=null) {
                        target.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(target,"friends_onlineMessage").replaceAll("%PLAYER%",player.getName()));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
