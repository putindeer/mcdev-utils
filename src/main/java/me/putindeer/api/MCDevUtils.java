package me.putindeer.api;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCDevUtils extends JavaPlugin {
    public static MCDevUtils instance;
    @Override
    public void onEnable() {
        instance = this;
    }
}