package com.betterLead;

import org.bukkit.plugin.java.JavaPlugin;

public final class BetterLead extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new LeashListener(this), this);
    }

    @Override
    public void onDisable() {
    }
}
