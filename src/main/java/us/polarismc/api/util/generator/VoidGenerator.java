package us.polarismc.api.util.generator;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.block.data.BlockData;
import us.polarismc.api.util.builder.WorldBuilder;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * A custom {@link ChunkGenerator} that creates an empty void world.
 * This generator creates a completely empty world with no terrain,
 * except for a single glass block at coordinates (0,64,0) in the spawn chunk.
 * <br><br>
 * This can be used as a starting point for custom worlds, lobbies, arenas, etc.
 * <br><br>
 * This generator can be easily integrated with {@link WorldBuilder} to streamline
 * the creation of custom void worlds within your plugin.
 */
public class VoidGenerator extends ChunkGenerator {
    @Override
    public void generateSurface(@NotNull WorldInfo info, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData data) {
        if (chunkX == 0 && chunkZ == 0) {
            BlockData glass = Material.GLASS.createBlockData();
            data.setBlock(0, 64, 0, glass);
        }
    }
}

