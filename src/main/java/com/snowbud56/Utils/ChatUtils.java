package com.snowbud56.Utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ChatUtils {

    public static BaseComponent[] format(String text) {
        return new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', text)).create();
    }

}
