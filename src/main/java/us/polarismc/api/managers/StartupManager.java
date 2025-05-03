package us.polarismc.api.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import us.polarismc.api.util.PluginUtils;

import java.util.*;

/**
 * Manager for plugin startup process and formatted console messages.
 * <br><br>
 * This utility class handles the registration of plugins with the Polaris API
 * and displays formatted startup information in the server console when
 * the server finishes loading. It includes an ASCII art banner and details
 * for each plugin, such as version, authors, website and load messages (registerMessage).
 * <br><br>
 * The {@link StartupManager} uses a {@link JavaPlugin} as a host to register the {@link ServerLoadEvent} listener
 * which triggers the display of all registered plugin information.
 */
@SuppressWarnings("unused")
public class StartupManager {
    private static final Map<JavaPlugin, PluginUtils> plugins = new LinkedHashMap<>();
    private static final Map<JavaPlugin, List<String>> pluginMessages = new HashMap<>();
    private static boolean messagesDisplayed = false;

    private static JavaPlugin hostPlugin = null;
    private static PluginUtils hostUtils = null;

    /**
     * Registers a plugin with the StartupManager to display its information during startup.
     * <p>
     * The first plugin registered will be used as the host plugin for registering
     * the server load event listener.
     *
     * @param plugin The plugin to register
     * @param utils The plugin's utility instance for message formatting
     */
    public static synchronized void registerPlugin(JavaPlugin plugin, PluginUtils utils) {
        if (plugin == null || utils == null) return;
        if (plugins.containsKey(plugin)) return;

        plugins.put(plugin, utils);

        if (hostPlugin == null) {
            hostPlugin = plugin;
            hostUtils = utils;
            registerServerLoadListener();
        }
    }

    /**
     * Adds a custom message to display during the plugin startup.
     * <br>
     * These messages will be shown after the plugin's standard information.
     *
     * @param plugin The plugin to add the message for
     * @param message The message to display
     */
    public static synchronized void registerMessage(JavaPlugin plugin, String message) {
        if (plugin == null || !plugins.containsKey(plugin)) return;

        List<String> messages = pluginMessages.getOrDefault(plugin, new ArrayList<>());
        messages.add(message);
        pluginMessages.put(plugin, messages);
    }

    /**
     * Registers a server load event listener using the host plugin.
     * <br>
     * This {@link Listener} will trigger the display of all startup messages
     * when the server finishes loading.
     */
    private static void registerServerLoadListener() {
        hostPlugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR)
            public void onServerLoad(ServerLoadEvent event) {
                if (event.getType() == ServerLoadEvent.LoadType.STARTUP && !messagesDisplayed) {
                    messagesDisplayed = true;
                    displayAllStartMessages();
                }
            }
        }, hostPlugin);
    }

    /**
     * Displays all startup messages for registered plugins.
     * <br>
     * First, it shows the ASCII banner, then displays information for each registered plugin.
     */
    private static void displayAllStartMessages() {
        showAsciiBanner();

        for (Map.Entry<JavaPlugin, PluginUtils> entry : plugins.entrySet()) {
            JavaPlugin plugin = entry.getKey();
            PluginUtils utils = entry.getValue();

            showPluginInfo(plugin, utils);
        }
    }

    /**
     * Displays the ASCII art banner for the Polaris API.
     */
    private static void showAsciiBanner() {
        log("<aqua>      ___         ___                         ___           ___                       ___     ",
                "<aqua>     /  /\\       /  /\\                       /  /\\         /  /\\        ___          /  /\\    ",
                "<aqua>    /  /::\\     /  /::\\                     /  /::\\       /  /::\\      /  /\\        /  /:/_   ",
                "<aqua>   /  /:/\\:\\   /  /:/\\:\\    ___     ___    /  /:/\\:\\     /  /:/\\:\\    /  /:/       /  /:/ /\\  ",
                "<aqua>  /  /:/~/:/  /  /:/  \\:\\  /__/\\   /  /\\  /  /:/~/::\\   /  /:/~/:/   /__/::\\      /  /:/ /::\\ ",
                "<aqua> /__/:/ /:/  /__/:/ \\__\\:\\ \\  \\:\\ /  /:/ /__/:/ /:/\\:\\ /__/:/ /:/___ \\__\\/\\:\\__  /__/:/ /:/\\:\\",
                "<aqua> \\  \\:\\/:/   \\  \\:\\ /  /:/  \\  \\:\\  /:/  \\  \\:\\/:/__\\/ \\  \\:\\/:::::/    \\  \\:\\/\\ \\  \\:\\/:/~/:/",
                "<aqua>  \\  \\::/     \\  \\:\\  /:/    \\  \\:\\/:/    \\  \\::/       \\  \\::/~~~~      \\__\\::/  \\  \\::/ /:/ ",
                "<aqua>   \\  \\:\\      \\  \\:\\/:/      \\  \\::/      \\  \\:\\        \\  \\:\\          /__/:/    \\__\\/ /:/  ",
                "<aqua>    \\  \\:\\      \\  \\::/        \\__\\/        \\  \\:\\        \\  \\:\\         \\__\\/       /__/:/   ",
                "<aqua>     \\__\\/       \\__\\/                       \\__\\/         \\__\\/                     \\__\\/   ",
                "",
                "<green>polaris-api has been initialized!");
    }

    /**
     * Displays information about a specific plugin.
     * <br>
     * Shows the plugin name, version, authors, website and any custom messages
     * registered for this plugin.
     *
     * @param plugin The plugin to display information for
     * @param utils The plugin's utility instance for message formatting
     */
    private static void showPluginInfo(JavaPlugin plugin, PluginUtils utils) {
        plugin.getServer().getConsoleSender().sendMessage("");
        utils.log("<green>" + plugin.getName() + " has been initialized!");
        utils.log("<yellow>Version: <white>" + plugin.getPluginMeta().getVersion());
        utils.log("<yellow>Authors: <white>" + plugin.getPluginMeta().getAuthors());
        utils.log("<yellow>Website: <white>" + plugin.getPluginMeta().getWebsite());
        utils.log(pluginMessages.getOrDefault(plugin, new ArrayList<>()).toArray(new String[0]));
    }

    /**
     * Logs formatted messages to the server console with the polaris-api prefix.
     *
     * @param messages The messages to log
     */
    private static void log(String... messages) {
        Component prefix = hostUtils.chat("<gray>[<aqua>polaris-api<gray>] <reset>");
        for (String message : messages) {
            hostPlugin.getServer().getConsoleSender().sendMessage(prefix.append(hostUtils.chat(message)));
        }
    }
}