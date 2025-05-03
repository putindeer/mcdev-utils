package us.polarismc.api;

import org.bukkit.plugin.java.JavaPlugin;

public final class PolarisAPI extends JavaPlugin {
    public static PolarisAPI instance;
    @Override
    public void onEnable() {
        instance = this;
    }
}