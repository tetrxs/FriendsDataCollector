package net.minetaria.friendsdatacollector.commands;

import de.gaunercools.languageapibungee.mysql.LanguageAPIBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.minetaria.friendsdatacollector.FriendsDataCollector;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            if (strings.length > 0) {
                ArrayList<String> loadedFriends;
                ArrayList<String> loadedRequests;
                UUID uuid;
                switch (strings[0]) {
                    case "list":
                        try {
                            int site = Integer.parseInt(strings[1])-1;
                            if (site>-1) {
                                showFriends(player, site);
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_noValidSite"));
                            }
                        } catch (Exception e) {
                            showFriends(player, 0);
                            e.printStackTrace();
                        }
                        break;
                    case "add":
                        if (strings.length>=2 && strings[1] != null) {
                            uuid = FriendsDataCollector.getUUIDfromName(strings[1]);
                            if (uuid != null) {
                                if (!uuid.equals(player.getUniqueId())) {
                                    if (!FriendsDataCollector.getFriends(player.getUniqueId().toString()).contains(uuid.toString())) {
                                        if (!FriendsDataCollector.getFriendRequests(uuid.toString()).contains(player.getUniqueId().toString())) {
                                            if (!FriendsDataCollector.getFriendRequests(player.getUniqueId().toString()).contains(uuid.toString())) {
                                                FriendsDataCollector.sendFriendRequest(player.getUniqueId().toString(),uuid.toString());
                                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_send").replaceAll("%PLAYER%",strings[1]));
                                                ProxiedPlayer target = null;
                                                for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                                                    if (all.getUniqueId().equals(uuid)) {
                                                        target = all;
                                                    }
                                                }
                                                if (target!=null) {
                                                    target.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(target,"friends_command_request_sendNotify").replaceAll("%PLAYER%",player.getName()));
                                                }
                                            } else {
                                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_alreadySend").replaceAll("%PLAYER%", strings[1]).replaceAll("%PLAYER%", strings[1]));
                                            }
                                        } else {
                                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_alreadySend").replaceAll("%PLAYER%", strings[1]));
                                        }
                                    } else {
                                        player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_alreadyFriends").replaceAll("%PLAYER%", strings[1]));
                                    }
                                } else {
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                                }
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", "-"));
                        }
                        break;
                    case "remove":
                        if (strings.length>=2 && strings[1] != null) {
                            uuid = FriendsDataCollector.getUUIDfromName(strings[1]);
                            if (uuid != null) {
                                if (FriendsDataCollector.getFriends(player.getUniqueId().toString()).contains(uuid.toString())) {
                                    FriendsDataCollector.removeFriends(player.getUniqueId().toString(),uuid.toString());
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_remove").replaceAll("%PLAYER%",strings[1]));
                                } else {
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_friendsMenu_addFriendMenu_playerAlreadyFriend"));
                                }
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", "-"));
                        }
                        break;
                    case "requests":
                        try {
                            int site = Integer.parseInt(strings[1])-1;
                            if (site>-1) {
                                showRequets(player, site);
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_noValidSite"));
                            }
                        } catch (Exception e) {
                            showRequets(player, 0);
                            e.printStackTrace();
                        }
                        break;
                    case "accept":
                        if (strings.length>=2 && strings[1] != null) {
                            loadedRequests = FriendsDataCollector.getFriendRequests(player.getUniqueId().toString());
                            uuid = FriendsDataCollector.getUUIDfromName(strings[1]);
                            if (uuid != null) {
                                if (loadedRequests.contains(uuid.toString())) {
                                    FriendsDataCollector.removeFriendRequest(uuid.toString(),player.getUniqueId().toString());
                                    FriendsDataCollector.removeFriendRequest(player.getUniqueId().toString(),uuid.toString());
                                    FriendsDataCollector.pairFriends(uuid.toString(),player.getUniqueId().toString());
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_accept").replaceAll("%PLAYER%",strings[1]));
                                    ProxiedPlayer target = null;
                                    for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                                        if (all.getUniqueId().equals(uuid)) {
                                            target = all;
                                        }
                                    }
                                    if (target!=null) {
                                        target.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(target,"friends_command_request_accept_notify").replaceAll("%PLAYER%",player.getName()));
                                    }
                                } else {
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                                }
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", "-"));
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", "-"));
                        }
                        break;
                    case "decline":
                        if (strings.length>=2 && strings[1] != null) {
                            loadedRequests = FriendsDataCollector.getFriendRequests(player.getUniqueId().toString());
                            uuid = FriendsDataCollector.getUUIDfromName(strings[1]);
                            if (uuid != null) {
                                if (loadedRequests.contains(uuid.toString())) {
                                    FriendsDataCollector.removeFriendRequest(uuid.toString(),player.getUniqueId().toString());
                                    FriendsDataCollector.removeFriendRequest(player.getUniqueId().toString(),uuid.toString());
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_decline").replaceAll("%PLAYER%",strings[1]));
                                    ProxiedPlayer target = null;
                                    for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                                        if (all.getUniqueId().equals(uuid)) {
                                            target = all;
                                        }
                                    }
                                    if (target!=null) {
                                        target.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(target,"friends_command_request_decline_notify").replaceAll("%PLAYER%",player.getName()));
                                    }
                                } else {
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                                }
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", "-"));
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", "-"));
                        }
                        break;
                    case "acceptAll":
                        loadedRequests = FriendsDataCollector.getFriendRequests(player.getUniqueId().toString());
                        if (!loadedRequests.isEmpty()) {
                            for (String s:loadedRequests) {
                                FriendsDataCollector.removeFriendRequest(s,player.getUniqueId().toString());
                                FriendsDataCollector.removeFriendRequest(player.getUniqueId().toString(),s);
                                FriendsDataCollector.pairFriends(s,player.getUniqueId().toString());
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_acceptAll"));
                                ProxiedPlayer target = null;
                                for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                                    if (all.getUniqueId().toString().equals(s)) {
                                        target = all;
                                    }
                                }
                                if (target!=null) {
                                    target.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(target,"friends_command_request_accept_notify").replaceAll("%PLAYER%",player.getName()));
                                }
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_noFound"));
                        }
                        break;
                    case "declinetAll":
                        loadedRequests = FriendsDataCollector.getFriendRequests(player.getUniqueId().toString());
                        if (!loadedRequests.isEmpty()) {
                            for (String s:loadedRequests) {
                                FriendsDataCollector.removeFriendRequest(s,player.getUniqueId().toString());
                                FriendsDataCollector.removeFriendRequest(player.getUniqueId().toString(),s);
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_declineAll"));
                                ProxiedPlayer target = null;
                                for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                                    if (all.getUniqueId().toString().equals(s)) {
                                        target = all;
                                    }
                                }
                                if (target!=null) {
                                    target.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(target,"friends_command_request_decline_notify").replaceAll("%PLAYER%",player.getName()));
                                }
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_request_noFound"));
                        }
                        break;
                    case "jump":
                        if (strings.length>=2 && strings[1] != null) {
                            loadedFriends = FriendsDataCollector.getFriends(player.getUniqueId().toString());
                            uuid = FriendsDataCollector.getUUIDfromName(strings[1]);
                            if (uuid != null) {
                                if (loadedFriends.contains(uuid.toString())) {
                                    ProxiedPlayer target = null;
                                    for (ProxiedPlayer all:FriendsDataCollector.getInstance().getProxy().getPlayers()) {
                                        if (all.getName().equals(strings[1])) {
                                            target = all;
                                        }
                                    }
                                    if (target!=null) {
                                        player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_jump").replaceAll("%PLAYER%", strings[1]));
                                        player.connect(target.getServer().getInfo());
                                    } else {
                                        player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                                    }
                                } else {
                                    player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                                }
                            } else {
                                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%", strings[1]));
                            }
                        } else {
                            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_playerNotFound").replaceAll("%PLAYER%","-"));
                        }
                        break;

                }
            } else {
                player.sendMessage("§1");
                player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_help"));
                player.sendMessage("§8• §5/friend");
                player.sendMessage("§8• §5/friend list");
                player.sendMessage("§8• §5/friend requests");
                player.sendMessage("§8• §5/friend add");
                player.sendMessage("§8• §5/friend remove");
                player.sendMessage("§8• §5/friend accept");
                player.sendMessage("§8• §5/friend decline");
                player.sendMessage("§8• §5/friend acceptAll");
                player.sendMessage("§8• §5/friend declineAll");
                player.sendMessage("§8• §5/friend jump");
                player.sendMessage("§2");
            }
        }
    }

    private void showFriends(ProxiedPlayer player, int site) {
        ArrayList<String> loadedFriends = FriendsDataCollector.getFriends(player.getUniqueId().toString());
        int max = 1;
        if (loadedFriends.size() > 36) {
            max = loadedFriends.size()/36;
        }
        if (!((site+1) > max)) {
            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_friend").replaceAll("%SITE%", String.valueOf(site+1)).replaceAll("%MAX%", String.valueOf(max)));
            for (int i = (site*36);i<((site+1)*36);i++) {
                if (i<loadedFriends.size()) {
                    if (loadedFriends.get(i) != null) {
                        UUID friendUUI = UUID.fromString(loadedFriends.get(i));
                        if (FriendsDataCollector.isOnline(friendUUI)) {
                            TextComponent message = new TextComponent("§8• §a" + FriendsDataCollector.getNamefromUUID(friendUUI) + " §8(§aOnline§8) §8• ");
                            TextComponent jump = new TextComponent("§a" + FriendsDataCollector.getInstance().getProxy().getPlayer(friendUUI).getServer().getInfo().getName());
                            jump.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend jump " + FriendsDataCollector.getNamefromUUID(friendUUI)));
                            message.addExtra(jump);
                            player.sendMessage((BaseComponent)message);
                        }
                    }
                }
            }
            for (int i = (site*36);i<((site+1)*36);i++) {
                if (i<loadedFriends.size()) {
                    if (loadedFriends.get(i) != null) {
                        UUID friendUUI = UUID.fromString(loadedFriends.get(i));
                        if (!FriendsDataCollector.isOnline(friendUUI)) {
                            long yourmilliseconds = FriendsDataCollector.getLastLogin(friendUUI);
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                            Date resultdate = new Date(yourmilliseconds);
                            player.sendMessage("§8• §7" + FriendsDataCollector.getNamefromUUID(friendUUI) + " §8(§cOffline§8) §8• §e" + sdf.format(resultdate));
                        }
                    }
                }
            }
        } else {
            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player, "friends_command_notEnoughSites"));
        }
    }

    private void showRequets(ProxiedPlayer player, int site) {
        ArrayList<String> loadedRequests = FriendsDataCollector.getFriendRequests(player.getUniqueId().toString());
        int max = 1;
        if (loadedRequests.size() > 36) {
            max = loadedRequests.size()/36;
        }
        if (!((site+1) > max)) {
            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player,"friends_command_requets").replaceAll("%SITE%", String.valueOf(site+1)).replaceAll("%MAX%", String.valueOf(max)));
            for (int i = (site*36);i<((site+1)*36);i++) {
                if (i<loadedRequests.size()) {
                    if (loadedRequests.get(i) != null) {
                        UUID friendUUI = UUID.fromString(loadedRequests.get(i));
                        TextComponent message = new TextComponent("§8• §a" + FriendsDataCollector.getNamefromUUID(friendUUI) + " §8• ");
                        TextComponent splitter = new TextComponent(" §8• ");
                        TextComponent accept = new TextComponent(LanguageAPIBungee.getTranslatedMessage(player,"friends_friendsMenu_friendRequestsAccept"));
                        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + FriendsDataCollector.getNamefromUUID(friendUUI)));
                        TextComponent decline = new TextComponent(LanguageAPIBungee.getTranslatedMessage(player,"friends_friendsMenu_friendRequestsDecline"));
                        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend decline " + FriendsDataCollector.getNamefromUUID(friendUUI)));
                        message.addExtra(accept);
                        message.addExtra(splitter);
                        message.addExtra(decline);
                        player.sendMessage((BaseComponent)message);
                    }
                }
            }
        } else {
            player.sendMessage(FriendsDataCollector.prefix + LanguageAPIBungee.getTranslatedMessage(player, "friends_command_notEnoughSites"));
        }
    }
}
