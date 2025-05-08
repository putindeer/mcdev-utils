package us.polarismc.api.util.builder;

import org.bukkit.*;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A builder class for creating and configuring {@link World} objects with a fluent interface.<p>
 * This class provides various methods to customize worlds including environment, world type, difficulty,
 * and specific settings such as spawn locations, game rules, and custom generators.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * World world = new WorldBuilder(plugin)
 *     .name("myWorld")
 *     .seed(12345L)
 *     .environment(Environment.NORMAL)
 *     .type(WorldType.AMPLIFIED)
 *     .generator(customGenerator)
 *     .settings("{}")
 *     .difficulty(Difficulty.HARD)
 *     .pvp(false)
 *     .autoSave(true)
 *     .spawnLocation(0, 64, 0)
 *     .gameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
 *     .build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class WorldBuilder {
    private final JavaPlugin plugin;
    private String name;
    private Long seed;
    private World.Environment environment;
    private ChunkGenerator generator;
    private WorldType type = WorldType.NORMAL;
    private String generatorSettings;

    private Difficulty difficulty;
    private Boolean pvp;
    private Boolean autoSave;
    private Location spawnLocation;
    private final Map<GameRule<?>, Object> gamerules = new HashMap<>();

    /**
     * Creates a new WorldBuilder instance.
     *
     * @param name The name of the world
     * @param plugin The JavaPlugin instance that is creating the world
     */
    public WorldBuilder(String name, JavaPlugin plugin) {
        this.name = name;
        this.plugin = plugin;
    }

    /**
     * Sets the seed for world generation.
     *
     * @param seed The seed value that determines the world's terrain generation
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder seed(long seed) {
        this.seed = seed;
        return this;
    }

    /**
     * Sets the environment type for the world.
     *
     * @param environment The environment (NORMAL, NETHER, THE_END)
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder environment(World.Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Sets the world generation type.
     *
     * @param type The world type (NORMAL, FLAT, AMPLIFIED, etc.)
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder type(WorldType type) {
        this.type = type;
        return this;
    }

    /**
     * Sets a custom chunk generator for the world.
     *
     * @param generator The custom chunk generator to use
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder generator(ChunkGenerator generator) {
        this.generator = generator;
        return this;
    }

    /**
     * Sets the generator settings JSON string.
     *
     * @param settings JSON string with generator settings
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder settings(String settings) {
        this.generatorSettings = settings;
        return this;
    }

    /**
     * Sets the difficulty level for the world.
     *
     * @param difficulty The difficulty setting (PEACEFUL, EASY, NORMAL, HARD)
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder difficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        return this;
    }

    /**
     * Sets whether PVP is enabled in this world.
     *
     * @param pvp True to enable player vs player combat, false to disable
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder pvp(boolean pvp) {
        this.pvp = pvp;
        return this;
    }

    /**
     * Sets whether the world should auto-save periodically.
     *
     * @param autoSave True to enable auto-saving, false to disable
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder autoSave(boolean autoSave) {
        this.autoSave = autoSave;
        return this;
    }

    /**
     * Sets the spawn location for the world.
     *
     * @param loc The location where players will spawn
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder spawnLocation(Location loc) {
        this.spawnLocation = loc;
        return this;
    }

    /**
     * Sets the spawn location for the world using coordinates.
     *
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param z The z-coordinate
     * @return This {@link WorldBuilder} instance
     */
    public WorldBuilder spawnLocation(double x, double y, double z) {
        this.spawnLocation = new Location(Bukkit.getWorld(name), x, y, z);
        return this;
    }

    /**
     * Sets a game rule for the world.
     *
     * @param rule The game rule to set
     * @param value The value for the game rule
     * @param <T> The type of value for the game rule
     * @return This {@link WorldBuilder} instance
     */
    public <T> WorldBuilder gamerule(GameRule<T> rule, T value) {
        gamerules.put(rule, value);
        return this;
    }

    /**
     * Checks if a world with the given name already exists.
     *
     * @param name The name of the world to check
     * @return True if the world exists, false otherwise
     */
    private boolean worldExists(String name) {
        if (Bukkit.getWorld(name) != null) {
            return true;
        }
        File worldDir = new File(plugin.getDataFolder().getParentFile().getParentFile(), name);
        return worldDir.exists() && worldDir.isDirectory();
    }

    /**
     * Creates a new world using the configured settings.
     *
     * @return The newly created {@link World} instance
     */
    private World createNewWorld() {
        WorldCreator creator = new WorldCreator(name)
                .type(type);

        if (seed != null) {
            creator.seed(seed);
        }
        if (environment != null) {
            creator.environment(environment);
        }
        if (generator != null) {
            creator.generator(generator);
        }
        if (generatorSettings != null) {
            creator.generatorSettings(generatorSettings);
        }

        return creator.createWorld();
    }

    /**
     * Creates or loads the world and applies all configured options.
     *
     * @return The created or loaded {@link World} instance
     * @throws IllegalStateException if the world name has not been specified
     */
    @SuppressWarnings("unchecked")
    public World build() {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("World name must be specified.");
        }

        World world = worldExists(name)
                ? Bukkit.getWorld(name)
                : createNewWorld();

        assert world != null;

        if (difficulty != null) {
            world.setDifficulty(difficulty);
        }
        if (pvp != null) {
            world.setPVP(pvp);
        }
        if (autoSave != null) {
            world.setAutoSave(autoSave);
        }
        if (spawnLocation != null) {
            world.setSpawnLocation(spawnLocation);
        }
        for (Map.Entry<GameRule<?>, Object> entry : gamerules.entrySet()) {
            GameRule<Object> rule = (GameRule<Object>) entry.getKey();
            world.setGameRule(rule, entry.getValue());
        }

        return world;
    }
}