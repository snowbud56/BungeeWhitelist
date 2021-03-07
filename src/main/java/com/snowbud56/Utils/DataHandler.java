package com.snowbud56.Utils;

import com.snowbud56.BungeeEssentials;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataHandler {

    private static File whitelistFile;

    public static void loadFiles() {
        whitelistFile = new File(BungeeEssentials.getInstance().getDataFolder() + "/config.yml");
        if (whitelistFile.exists()) return;
        if (!BungeeEssentials.getInstance().getDataFolder().exists()) BungeeEssentials.getInstance().getDataFolder().mkdir();
        try {
            java.util.ArrayList<String> blanklist = new ArrayList<>();
            Configuration configuration = new Configuration();
            configuration.set("config.prefix", "&9&lWhitelist &8>> &7");
            configuration.set("config.message-color", "&7");
            configuration.set("config.value-color", "&c");
            configuration.set("config.kick-message", "&fYou are not whitelisted!");
//            configuration.set("config.kick-fallback", "KickFallbackServerName");
            configuration.set("whitelist.bungeeNetwork.whitelisted", blanklist);
            configuration.set("whitelist.bungeeNetwork.enabled", false);
            for (String server : BungeeEssentials.getInstance().getProxy().getServers().keySet()) {
                configuration.set("whitelist." + server + ".enabled", false);
                configuration.set("whitelist." + server + ".whitelisted", blanklist);
            }
            YamlConfiguration.getProvider(YamlConfiguration.class).save(configuration, whitelistFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getKickFallbackServer() {
        Configuration config;
        try {
            config = YamlConfiguration.getProvider(YamlConfiguration.class).load(whitelistFile);
            return config.getString("config.kick-fallback");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveEssentials(java.util.Map<String, Boolean> enabled, java.util.Map<String, java.util.List<String>> whitelisted) {
        Configuration whitelistconfig = new Configuration();
        Configuration oldconfig;
        try {
            oldconfig = YamlConfiguration.getProvider(YamlConfiguration.class).load(whitelistFile);
        } catch (java.io.IOException e) {
            BungeeEssentials.getInstance().getLogger().severe("Failed to get config! Whitelist won't work without it!");
            return;
        }
        whitelistconfig.set("config.prefix", oldconfig.getString("config.prefix"));
        whitelistconfig.set("config.message-color", oldconfig.getString("config.message-color"));
        whitelistconfig.set("config.value-color", oldconfig.getString("config.value-color"));
        whitelistconfig.set("config.kick-message", oldconfig.getString("config.kick-message"));
//        whitelistconfig.set("config.kick-fallback", oldconfig.getString("config.kick-fallback"));
        whitelistconfig.set("whitelist.bungeeNetwork.enabled", enabled.get("bungeeNetwork"));
        whitelistconfig.set("whitelist.bungeeNetwork.whitelisted", whitelisted.get("bungeeNetwork"));
        for (String server : enabled.keySet()) whitelistconfig.set("whitelist." + server + ".enabled", enabled.get(server));
        for (String server : whitelisted.keySet()) whitelistconfig.set("whitelist." + server + ".whitelisted", whitelisted.get(server));
        try {
            YamlConfiguration.getProvider(YamlConfiguration.class).save(whitelistconfig, whitelistFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        whitelistFile = null;
    }
}
