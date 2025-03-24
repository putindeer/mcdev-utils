package us.polarismc.api;

import org.bukkit.plugin.java.JavaPlugin;

public final class MainAPI extends JavaPlugin {
    public static MainAPI api;
    @Override
    public void onEnable() {
        api = this;
    }

    @Override
    public void onDisable() {

    }
}