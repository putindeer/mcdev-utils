package us.polarismc.api;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class MainAPI extends JavaPlugin {
    @Getter
    private static MainAPI api;
    @Override
    public void onEnable() {
        api = this;
    }

    @Override
    public void onDisable() {

    }
}