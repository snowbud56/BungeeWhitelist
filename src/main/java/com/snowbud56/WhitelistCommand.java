package com.snowbud56;

import com.snowbud56.Utils.ChatUtils;
import com.snowbud56.Utils.DataHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;

public class WhitelistCommand extends Command implements Listener {

    private static java.util.Map<String, Boolean> enabled;
    private static java.util.Map<String, java.util.List<String>> whitelisted;
    private static String prefix;
    private static String valuecolor;
    private static String messagecolor;
    private static String kickmessage;

    public WhitelistCommand() {
        super("bungeewhitelist", "bungeewhitelist.use","bwl","bwhitelist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Whitelist Commands"));
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "/bungeewhitelist <on | off> [server]: Enables/disables the whitelist. [optional server argument]"));
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "/bungeewhitelist status [server]: Views the status of the whitelist. [optional server argument]"));
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "/bungeewhitelist add <player> [server]: Adds a player to the whitelist. [optional server argument]"));
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "/bungeewhitelist remove <player> [server]: Removes a player from the whitelist. [optional server argument]"));
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "/bungeewhitelist save: save config data to disk"));
            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "/bungeewhitelist reload: reload config data from disk"));
        } else {
            if(args[0].equalsIgnoreCase("save") && sender.hasPermission("bungeewhitelist.save")){
                DataHandler.saveEssentials(enabled, whitelisted);
                DataHandler.loadFiles();
                sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Whitelist saved to disk"));
            }
            else if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("bungeewhitelist.reload")){
                DataHandler.loadFiles();
                pluginEnable();
                sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Whitelist loaded from disk"));
            }
            else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("enable")) {
                if (args.length == 1) {
                    enabled.put("bungeeNetwork", true);
                    sender.sendMessage(ChatUtils.format(prefix + messagecolor + "The global whitelist has been enabled!"));
                } else {
                    String server = BungeeEssentials.getInstance().getProxy().getConfig().getServers().get(args[1]).getName();
                    if (server == null) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That is not a server on the network!"));
                    else {
                        if (enabled.get(server)) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That server's whitelist is already enabled!"));
                        else {
                            enabled.put(server, true);
                            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "The whitelist for " + valuecolor + server + messagecolor + " has been enabled!"));
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("disable")) {
                if (args.length == 1) {
                    enabled.put("bungeeNetwork", false);
                    sender.sendMessage(ChatUtils.format(prefix + messagecolor + "The global whitelist has been disabled!"));
                } else {
                    String server = BungeeEssentials.getInstance().getProxy().getConfig().getServers().get(args[1]).getName();
                    if (server == null) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That is not a server on the network!"));
                    else {
                        if (!enabled.get(server)) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That server's whitelist is already disabled!"));
                        else {
                            enabled.put(server, false);
                            sender.sendMessage(ChatUtils.format(prefix + messagecolor + "The whitelist for " + valuecolor + server + messagecolor + " has been disabled!"));
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("status") || args[0].equalsIgnoreCase("list")) {
                if (args.length == 1) {
                    sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Whitelist Status:"));
                    sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Toggled: " + valuecolor + enabled.get("bungeeNetwork")));
                    sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Players whitelisted:"));
                    for (String player : whitelisted.get("bungeeNetwork")) sender.sendMessage(ChatUtils.format(prefix + valuecolor + "- " + player));
                } else {
                    String server = BungeeEssentials.getInstance().getProxy().getConfig().getServers().get(args[1]).getName();
                    if (server == null) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That is not a server on the network!"));
                    else {
                        sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Whitelist Status for " + valuecolor + server + messagecolor + ":"));
                        sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Toggled: " + valuecolor + enabled.get(server)));
                        sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Players whitelisted:"));
                        for (String player : whitelisted.get(server)) sender.sendMessage(ChatUtils.format(prefix + valuecolor + "- " + player));
                    }}
            } else if (args[0].equalsIgnoreCase("add")) {
                if (args.length == 1) sender.sendMessage(ChatUtils.format(prefix + "Usage: /bungeewhitelist add <player>"));
                else if (args.length == 2) {
                    whitelisted.get("bungeeNetwork").add(args[1]);
                    sender.sendMessage(ChatUtils.format(prefix + "Added " + valuecolor + args[1] + messagecolor + " to the global whitelist!"));
                } else {
                    String server = BungeeEssentials.getInstance().getProxy().getServerInfo(args[2]).getName();
                    whitelisted.get(server).add(args[1]);
                    sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Added " + valuecolor + args[1] + messagecolor + " to the " + valuecolor + server + messagecolor + " whitelist!"));
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length == 1) sender.sendMessage(ChatUtils.format(prefix + "Usage: /bungeewhitelist remove <player>"));
                else if (args.length == 2) {
                    if (!whitelisted.get("bungeeNetwork").contains(args[1])) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That player is not on the global whitelist!"));
                    else {
                        whitelisted.get("bungeeNetwork").remove(args[1]);
                        sender.sendMessage(ChatUtils.format(prefix + "Removed " + valuecolor + args[1] + messagecolor + " from the global whitelist!"));
                    }
                } else {
                    String server = BungeeEssentials.getInstance().getProxy().getServerInfo(args[2]).getName();
                    if (server == null) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That isn't a server on the network!"));
                    else {
                    if (!whitelisted.get("bungeeNetwork").contains(args[1])) sender.sendMessage(ChatUtils.format(prefix + messagecolor + "That player is not on the global whitelist!"));
                    else {
                        whitelisted.get(server).remove(args[1]);
                        sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Removed " + valuecolor + args[1] + messagecolor + " from the " + valuecolor + server + messagecolor + " whitelist!"));
                    }}
                }
            } else sender.sendMessage(ChatUtils.format(prefix + messagecolor + "Invalid usage! Type \"/bungeewhitelist\" for a list of commands"));
        }}

    public static void pluginEnable() {
        enabled = new java.util.HashMap<>();
        whitelisted = new java.util.HashMap<>();
        try {
            Configuration whitelistconfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(new File(BungeeEssentials.getInstance().getDataFolder() + "/config.yml"));
            for (String servername : BungeeEssentials.getInstance().getProxy().getConfig().getServers().keySet()) {
                //BungeeEssentials.getInstance().getLogger().info(servername);
                enabled.put(servername, whitelistconfig.getBoolean("whitelist." + servername + ".enabled"));
                whitelisted.put(servername, whitelistconfig.getStringList("whitelist." + servername + ".whitelisted"));
            }
            enabled.put("bungeeNetwork", whitelistconfig.getBoolean("whitelist.bungeeNetwork.enabled"));
            whitelisted.put("bungeeNetwork", whitelistconfig.getStringList("whitelist.bungeeNetwork.whitelisted"));
            prefix = whitelistconfig.getString("config.prefix");
            valuecolor = whitelistconfig.getString("config.value-color");
            messagecolor = whitelistconfig.getString("config.message-color");
            kickmessage = whitelistconfig.getString("config.kick-message");
        } catch (Exception e) {
            BungeeEssentials.getInstance().getLogger().severe("Failed to get config! Whitelist won't work without it!");
            e.printStackTrace();
        }
    }

    public static void pluginDisable() {
        DataHandler.saveEssentials(enabled, whitelisted);
        enabled = null;
        whitelisted = null;
        prefix = null;
        valuecolor = null;
        messagecolor = null;
        kickmessage = null;
    }

    @EventHandler
    public void onServerJoin(ServerConnectEvent e) {
        if (e.isCancelled())
            return;
        ProxiedPlayer p = e.getPlayer();
        String server = e.getTarget().getName();
        if (enabled.get("bungeeNetwork") && !whitelisted.get("bungeeNetwork").contains(p.getName()) && !p.hasPermission("bungeewhitelist.bypass.bungeenetwork")) {
            e.setCancelled(true);
            p.disconnect(ChatUtils.format("&cKicked whilst connecting to " + server + ": " + kickmessage));
            return;
        }
        else if (enabled.get(server)) {
            if (!(whitelisted.get(server).contains(p.getName())) && !p.hasPermission("bungeewhitelist.bypass." + server)) {
                e.setCancelled(true);
                p.sendMessage(ChatUtils.format("&cKicked whilst connecting to " + server + ": " + kickmessage));
            }
        }

    }

    @EventHandler
    public void onNetworkJoin(PreLoginEvent e) {
        if (e.isCancelled())
            return;
        PendingConnection p = e.getConnection();
        if ((enabled.get("bungeeNetwork") && !whitelisted.get("bungeeNetwork").contains(p.getName())) ||
            (enabled.get(p.getListener().getServerPriority().get(0)) &&
                !whitelisted.get(p.getListener().getServerPriority().get(0)).contains(p.getName()))) {
            p.disconnect(ChatUtils.format("&cKicked whilst connecting to " + p.getListener().getServerPriority().get(0) + ": " + kickmessage));
        }
    }

    @EventHandler
    public void onReload(ProxyReloadEvent e) {
        DataHandler.loadFiles();
        WhitelistCommand.pluginEnable();
    }
}
