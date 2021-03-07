package com.snowbud56;

import com.snowbud56.Utils.DataHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeEssentials extends Plugin {
    private static BungeeEssentials instance;

    @Override
    public void onEnable() {
        instance = this;
        DataHandler.loadFiles();
        WhitelistCommand.pluginEnable();
        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand());
        getProxy().getPluginManager().registerListener(this, new WhitelistCommand());
    }

    public static BungeeEssentials getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        instance = null;
        WhitelistCommand.pluginDisable();
    }
}
