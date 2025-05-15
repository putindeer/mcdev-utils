package us.polarismc.api.managers;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages multilingual translations and player language settings for a plugin.
 */
@SuppressWarnings({"unused", "ResultOfMethodCallIgnored"})
public class LangManager {
    //TODO - revisitar esto, quizás no funcione tan bien (?
    private final JavaPlugin plugin;
    private final Map<UUID, String> playerLanguages = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> translations = new HashMap<>();
    private final Pattern translationPattern = Pattern.compile("\\[lang](.*?)\\[/lang]");
    private final Pattern placeholderPattern = Pattern.compile("%([a-zA-Z0-9._-]+)%");

    /**
     * The default language code used when none is set or found.
     */
    @Getter
    private String defaultLanguage = "en-US";

    /**
     * Constructs a new LangManager instance.
     *
     * @param plugin The JavaPlugin instance.
     */
    public LangManager(JavaPlugin plugin) {
        this.plugin = plugin;

        // Create languages directory if it doesn't exist
        File langDir = new File(plugin.getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // Load any existing language files from the directory
        loadTranslationsFromDirectory();
    }

    /**
     * Loads language files from the "languages" directory.
     */
    private void loadTranslationsFromDirectory() {
        File langDir = new File(plugin.getDataFolder(), "languages");

        if (!langDir.exists() || !langDir.isDirectory()) {
            return;
        }

        File[] files = langDir.listFiles((d, n) -> n.endsWith(".yml"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            String code = file.getName().replace(".yml", "");
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            Map<String, String> map = new HashMap<>();
            for (String key : cfg.getKeys(true)) {
                if (cfg.isString(key)) map.put(key, cfg.getString(key));
            }
            translations.put(code, map);
            plugin.getLogger().info("Loaded language file: " + code);
        }
    }

    /**
     * Registers a language resource from the plugin's jar.
     * This method will copy the resource to the languages directory if it doesn't exist,
     * and then load it into the translations map.
     *
     * @param resourcePath The path to the language file in the plugin's resources.
     * @param langCode The language code for this resource.
     * @return True if the language was successfully registered.
     */
    public boolean registerLanguageResource(String resourcePath, String langCode) {
        // Check if the resource exists
        InputStream inputStream = plugin.getResource(resourcePath);
        if (inputStream == null) {
            plugin.getLogger().warning("Language resource not found: " + resourcePath);
            return false;
        }

        // Ensure the languages directory exists
        File outputFile = getFile(resourcePath);
        if (!outputFile.exists()) {
            try {
                Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("Copied language resource to: " + outputFile.getPath());
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to copy language resource: " + e.getMessage());
                return false;
            }
        }

        // Load the language file
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(outputFile);

        // Also load from resource to ensure defaults are included
        YamlConfiguration defaultCfg = YamlConfiguration.loadConfiguration(
                new InputStreamReader(Objects.requireNonNull(plugin.getResource(resourcePath)), StandardCharsets.UTF_8));
        cfg.setDefaults(defaultCfg);

        // Parse the translations
        Map<String, String> map = new HashMap<>();
        for (String key : cfg.getKeys(true)) {
            if (cfg.isString(key)) {
                map.put(key, cfg.getString(key));
            }
        }

        // Add to the translations map
        translations.put(langCode, map);
        plugin.getLogger().info("Registered language: " + langCode);

        return true;
    }

    private @NotNull File getFile(String resourcePath) {
        File langDir = new File(plugin.getDataFolder(), "languages");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // Extract the filename from the resource path
        String fileName = resourcePath.contains("/")
                ? resourcePath.substring(resourcePath.lastIndexOf('/') + 1)
                : resourcePath;

        // Copy the resource to the languages directory if it doesn't exist
        return new File(langDir, fileName);
    }

    /**
     * Registers multiple language resources at once.
     * Each entry in the map should have the resource path as the key and the language code as the value.
     *
     * @param resources Map of resource paths to language codes.
     * @return The number of languages successfully registered.
     */
    public int registerLanguageResources(Map<String, String> resources) {
        int count = 0;
        for (Map.Entry<String, String> entry : resources.entrySet()) {
            if (registerLanguageResource(entry.getKey(), entry.getValue())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Processes a text message, replacing translation keys with localized strings.
     *
     * @param text     The text containing [lang]key[/lang] tags.
     * @param langCode The language code to translate into.
     * @return The translated string.
     */
    private String process(String text, String langCode) {
        if (text == null) return "";
        Matcher m = translationPattern.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String key = m.group(1);
            String tr = lookup(key, langCode);
            m.appendReplacement(sb, Matcher.quoteReplacement(tr));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Looks up a translation key in a specific language, falling back to default if needed.
     *
     * @param key The translation key.
     * @param langCode The language code.
     * @return The translated string, or the key if not found.
     */
    private String lookup(String key, String langCode) {
        Map<String, String> map = translations.get(langCode);
        if (map != null && map.containsKey(key)) return map.get(key);
        map = translations.get(defaultLanguage);
        return map != null && map.containsKey(key) ? map.get(key) : key;
    }

    /**
     * Translates a message for the specified command sender.
     *
     * @param sender The command sender (usually a Player).
     * @param text The text containing translation keys.
     * @return The translated message.
     */
    public String translate(CommandSender sender, String text) {
        String lang = (sender instanceof Player p) ? playerLanguages.getOrDefault(p.getUniqueId(), defaultLanguage)
                : defaultLanguage;
        return process(text, lang);
    }

    /**
     * Translates a message using the default language.
     *
     * @param text The text containing translation keys.
     * @return The translated message.
     */
    public String translate(String text) {
        return process(text, defaultLanguage);
    }

    /**
     * Loads a player's language preference from file.
     *
     * @param player The player whose language should be loaded.
     */
    public void loadPlayerLanguage(Player player) {
        File dir = new File(plugin.getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();

        File f = new File(dir, player.getUniqueId() + ".yml");
        String lang = defaultLanguage;
        if (f.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            lang = cfg.getString("language", defaultLanguage);
        }
        playerLanguages.put(player.getUniqueId(), lang);
    }

    /**
     * Sets a player's preferred language and saves it.
     *
     * @param player The player.
     * @param code The language code.
     * @return True if the language was set successfully.
     */
    public boolean setPlayerLanguage(Player player, String code) {
        if (!translations.containsKey(code)) return false;
        playerLanguages.put(player.getUniqueId(), code);
        File dir = new File(plugin.getDataFolder(), "players");
        if (!dir.exists()) dir.mkdirs();
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("language", code);
        try {
            cfg.save(new File(dir, player.getUniqueId() + ".yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save player lang: " + e.getMessage());
        }
        return true;
    }

    /**
     * Sets the default server language.
     *
     * @param code The language code.
     * @return True if the language is valid and was set.
     */
    public boolean setDefaultLanguage(String code) {
        if (!translations.containsKey(code)) return false;
        defaultLanguage = code;
        return true;
    }

    /**
     * Gets the set of all loaded language codes.
     *
     * @return An unmodifiable set of available languages.
     */
    public Set<String> getAvailableLanguages() {
        return Collections.unmodifiableSet(translations.keySet());
    }

    /**
     * Gets the human-readable name of a language if available.
     *
     * @param code The language code.
     * @return The language name, or the code if not found.
     */
    public String getLanguageName(String code) {
        Map<String, String> map = translations.get(code);
        if (map != null && map.containsKey("language.name")) {
            return map.get("language.name");
        }
        return code;
    }

    /**
     * Gets a player's current language code.
     *
     * @param player The player.
     * @return The language code or the default language if not set.
     */
    public String getPlayerLanguage(Player player) {
        return playerLanguages.getOrDefault(player.getUniqueId(), defaultLanguage);
    }

    /**
     * Reloads all language files from disk.
     */
    public void reload() {
        translations.clear();
        loadTranslationsFromDirectory();
    }
}