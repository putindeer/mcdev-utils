package me.putindeer.api.util;

import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.keys.SoundEventKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import me.putindeer.api.util.builder.ItemBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class PluginUtils {
    public final JavaPlugin plugin;

    public PluginUtils(JavaPlugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = chat(prefix);
    }

    /**
     * Plugin prefix
     */
    public final Component prefix;

    /**
     * Converts a text with HEX codes to a {@link Component}
     * @param string The input as a {@link String}
     * @return The converted text
     */
    public Component chat(String string) {
        return MiniMessage.miniMessage().deserialize(convert(string));
    }

    /**
     * Converts HEX codes to MiniMessage tags in order to deserialize it.
     * @param s The {@link String} with HEX codes
     * @return The {@link String} with MiniMessage tags
     */
    private String convert(String s) {
        s = s.replaceAll("&#([A-Fa-f0-9]{6})", "<#$1>");
        return s.replace("&0", "<black>").replace("&1", "<dark_blue>").replace("&2", "<dark_green>").replace("&3", "<dark_aqua>").replace("&4", "<dark_red>").replace("&5", "<dark_purple>").replace("&6", "<gold>").replace("&7", "<gray>").replace("&8", "<dark_gray>").replace("&9", "<blue>").replace("&a", "<green>").replace("&b", "<aqua>").replace("&c", "<red>").replace("&d", "<light_purple>").replace("&e", "<yellow>").replace("&f", "<white>").replace("&n", "<underlined>").replace("&m", "<strikethrough>").replace("&k", "<obfuscated>").replace("&o", "<italic>").replace("&l", "<bold>").replace("&r", "<reset>");
    }

    /**
     * Sends a versatile message to a recipient with multiple configuration options.<br><br>
     * This method allows sending messages with:<br><br>
     * - Optional prefix<br>
     * - Multiple message types ({@link String} or {@link Component})<br>
     * - Optional sound playback<br><br>
     *
     * There are several simpler sub-methods in case there are unnecessary arguments or you need to send to multiple players:<br><br>
     * - {@code message(receivers, messages)}: Sends message with default prefix<br>
     * - {@code message(receivers, sound, messages)}: Sends message with sound<br>
     * - {@code message(receivers, prefix, messages)}: Sends message with prefix<br><br>
     * There is also the {@code broadcast(prefix, sound, messages)} method that sends a message to all players, including the console.
     * This method also has the previous sub-methods.
     *
     * @param receiver Message recipient (can be a {@link Player} or a {@link CommandSender})
     * @param usePrefix Determines if the plugin prefix is used in the message
     * @param sound Optional sound to play when sending the message (only applies to {@link Player})
     * @param messages The messages ({@link String} or {@link Component}) to be sent
     */
    public void message(CommandSender receiver, boolean usePrefix, Sound sound, Component... messages) {
        for (Component component : messages) {
            Component prefixComponent = usePrefix ? prefix : Component.text("");
            receiver.sendMessage(prefixComponent.append(component));
        }

        if (sound != null && receiver instanceof Player player) {
            player.playSound(sound);
        }
    }

    //region [Sub-methods of 'message']
    //region [String methods]
    public void broadcast(String... messages) {
        broadcast(true, null, messages);
    }

    public void broadcast(boolean prefix, String... messages) {
        broadcast(prefix, null, messages);
    }

    public void broadcast(Sound sound, String... messages) {
        broadcast(true, sound, messages);
    }
    public void broadcast(TypedKey<@NotNull Sound> soundKey, String... messages) {
        broadcast(true, Sound.sound(soundKey, Sound.Source.MASTER, 10f, 1f), messages);
    }

    public void broadcast(boolean prefix, Sound sound, String... messages) {
        message(Stream.concat(Bukkit.getOnlinePlayers().stream().map(p -> (CommandSender) p), Stream.of(Bukkit.getConsoleSender())).collect(Collectors.toList()), prefix, sound, messages);
    }

    public void message(CommandSender receiver, String... messages) {
        message(receiver, true, null, messages);
    }

    public void message(CommandSender receiver, Sound sound, String... messages) {
        message(receiver, true, sound, messages);
    }

    public void message(CommandSender receiver, Key soundKey, String... messages) {
        message(receiver, true, Sound.sound(soundKey, Sound.Source.MASTER, 10f, 1f), messages);
    }

    public void message(CommandSender receiver, boolean prefix, String... messages) {
        message(receiver, prefix, null, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, String... messages) {
        message(receivers, true, null, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, boolean prefix, String... messages) {
        message(receivers, prefix, null, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, Sound sound, String... messages) {
        message(receivers, true, sound, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, Key soundKey, String... messages) {
        message(receivers, true, Sound.sound(soundKey, Sound.Source.MASTER, 10f, 1f), messages);
    }

    public void message(Collection<? extends CommandSender> receivers, boolean usePrefix, Sound sound, String... messages) {
        receivers.forEach(r -> message(r, usePrefix, sound, messages));
    }

    public void message(CommandSender receiver, boolean usePrefix, Sound sound, String... messages) {
        message(receiver, usePrefix, sound, Arrays.stream(messages).map(this::chat).toArray(Component[]::new));
    }
    //endregion
    //region [Component methods]
    public void broadcast(Component... messages) {
        broadcast(true, null, messages);
    }

    public void broadcast(boolean prefix, Component... messages) {
        broadcast(prefix, null, messages);
    }

    public void broadcast(Sound sound, Component... messages) {
        broadcast(true, sound, messages);
    }

    public void broadcast(boolean prefix, Sound sound, Component... messages) {
        message(Stream.concat(Bukkit.getOnlinePlayers().stream().map(p -> (CommandSender) p), Stream.of(Bukkit.getConsoleSender())).collect(Collectors.toList()), prefix, sound, messages);
    }

    public void message(CommandSender receiver, Component... messages) {
        message(receiver, true, null, messages);
    }

    public void message(CommandSender receiver, Sound sound, Component... messages) {
        message(receiver, true, sound, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, Component... messages) {
        message(receivers, true, null, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, boolean prefix, Component... messages) {
        message(receivers, prefix, null, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, Sound sound, Component... messages) {
        message(receivers, true, sound, messages);
    }

    public void message(Collection<? extends CommandSender> receivers, boolean prefix, Sound sound, Component... messages) {
        receivers.forEach(receiver -> message(receiver, prefix, sound, messages));
    }

    public void message(CommandSender receiver, boolean prefix, Component... messages) {
        message(receiver, prefix, null, messages);
    }
    //endregion
    //endregion

    /**
     * Fully restores a player's state between rounds.
     * Resets inventory, health, food level, potion effects, and other combat-related states.
     *
     * @param player The player to restore
     */
    public void restorePlayer(Player player) {
        setMaxHealth(player);
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setLevel(0);
        player.setExp(0.0f);
        player.setFireTicks(0);
        player.setArrowsInBody(0);
        player.setItemOnCursor(new ItemStack(Material.AIR));
        player.setInvulnerable(false);
    }

    public void title(List<Player> players, String title) {
        players.forEach(player -> title(player, title));
    }

    public void title(List<Player> players, String title, String subtitle) {
        players.forEach(player -> title(player, title, subtitle));
    }

    public void title(List<Player> players, String title, String subtitle, Sound sound) {
        players.forEach(player -> title(player, title, subtitle, sound));
    }

    public void title(Player player, String title) {
        title(player, title, "", (Sound) null);
    }

    public void title(Player player, String title, String subtitle) {
        title(player, title, subtitle, (Sound) null);
    }

    public void title(Player player, String title, String subtitle, Sound sound) {
        player.showTitle(Title.title(chat(title), chat(subtitle)));
        if (sound != null) {
            player.playSound(sound);
        }
    }

    public void title(List<Player> players, String title, Title.Times times) {
        players.forEach(player -> title(player, title, "", null, times));
    }

    public void title(List<Player> players, String title, String subtitle, Title.Times times) {
        players.forEach(player -> title(player, title, subtitle, null, times));
    }

    public void title(List<Player> players, String title, String subtitle, Sound sound, Title.Times times) {
        players.forEach(player -> title(player, title, subtitle, sound, times));
    }

    public void title(Player player, String title, Title.Times times) {
        title(player, title, "", null, times);
    }

    public void title(Player player, String title, String subtitle, Title.Times times) {
        title(player, title, subtitle, null, times);
    }

    public void title(Player player, String title, String subtitle, Sound sound, Title.Times times) {
        Title advTitle = Title.title(chat(title), chat(subtitle), times);

        player.showTitle(advTitle);

        if (sound != null) {
            player.playSound(sound);
        }
    }

    public void broadcastTitle(String title) {
        broadcastTitle(title, "", (Sound) null);
    }

    public void broadcastTitle(String title, String subtitle) {
        broadcastTitle(title, subtitle, (Sound) null);
    }

    public void broadcastTitle(String title, String subtitle, Sound sound) {
        Title advTitle = Title.title(chat(title), chat(subtitle));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(advTitle);
            if (sound != null) player.playSound(sound);
        }
    }

    public void broadcastTitle(String title, Title.Times times) {
        broadcastTitle(title, "", null, times);
    }

    public void broadcastTitle(String title, String subtitle, Title.Times times) {
        broadcastTitle(title, subtitle, null, times);
    }

    public void broadcastTitle(String title, String subtitle, Sound sound, Title.Times times) {
        Title advTitle = Title.title(chat(title), chat(subtitle), times);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(advTitle);
            if (sound != null) player.playSound(sound);
        }
    }

    public Title.Times timesFromTicks(long fadeInTicks, long stayTicks, long fadeOutTicks) {
        return Title.Times.times(
                Duration.ofMillis(fadeInTicks * 50),
                Duration.ofMillis(stayTicks * 50),
                Duration.ofMillis(fadeOutTicks * 50)
        );
    }

    public String clickableCommand(String command) {
        return "<click:run_command:" + command + ">" + command + "</click>";
    }

    public ItemBuilder goldenHeadTexture() {
        return goldenHeadTexture(Material.PLAYER_HEAD, 1);
    }

    public ItemBuilder goldenHeadTexture(int amount) {
        return goldenHeadTexture(Material.PLAYER_HEAD, amount);
    }

    public ItemBuilder goldenHeadTexture(Material material) {
        return goldenHeadTexture(material, 1);
    }

    public ItemBuilder goldenHeadTexture(Material material, int amount) {
        return ib(material, amount).profileTexture("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU1YjMwOGExZWI1Y2FhOTdlNWZiMjU3YjJkOWUxODYxZmRlZjE1MTYxZDUwYTFmNDZmMjIzMTVmNDkyOSJ9fX0=")
                .model(Material.PLAYER_HEAD);
    }

    public ItemStack marlowGHead() {
        return marlowGHead(1);
    }

    public ItemStack marlowGHead(int amount) {
        return goldenHeadTexture(Material.PLAYER_HEAD, amount)
                .name("Golden Head")
                .lore("<blue><lang:effect.minecraft.absorption> (02:00)", "<blue><lang:effect.minecraft.regeneration> III (00:05)", "<gray>Cooldown:<yellow> 10 seconds")
                .rarity(ItemRarity.RARE)
                .food(4, 9.6f, true)
                .useCooldown(10, "ghead")
                .consumeSeconds(1)
                .consumeAnimation(ItemUseAnimation.EAT)
                .consumingSound(SoundEventKeys.ENTITY_GENERIC_EAT)
                .consumeParticles(false)
                .consumeApplyEffects(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0), new PotionEffect(PotionEffectType.REGENERATION, 100, 2))
                .build();
    }

    public String getTimeAgo(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days == 1 ? "1 day ago" : days + " days ago";
        } else if (hours > 0) {
            return hours == 1 ? "1 hour ago" : hours + " hours ago";
        } else if (minutes > 0) {
            return minutes == 1 ? "1 minute ago" : minutes + " minutes ago";
        } else {
            return seconds == 1 ? "1 second ago" : seconds + " seconds ago";
        }
    }

    //region [Methods of 'log']
    /**
     * Sends an informative message to the server console
     * @param messages The messages to be sent
     */
    public void log(String... messages) {
        for (String message : messages) {
            plugin.getServer().getConsoleSender().sendMessage(chat("<gray>[<aqua>" + plugin.getName() + "<gray>] <reset>" + message));
        }
    }

    /**
     * Sends a warning message to the server console
     * @param messages The warning messages to be sent
     */
    public void warning(String... messages) {
        for (String message : messages) {
            plugin.getLogger().warning(message);
        }
    }

    /**
     * Sends a severe error message to the server console
     * @param messages The error messages to be sent
     */
    public void severe(String... messages) {
        for (String message : messages) {
            plugin.getLogger().severe(message);
        }
    }

    /**
     * Sends an informative message to the server console
     * @param messages The messages to be sent
     */
    public void log(StackTraceElement... messages) {
        log(Arrays.stream(messages).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
    }

    /**
     * Sends a warning message to the server console
     * @param messages The warning messages to be sent
     */
    public void warning(StackTraceElement... messages) {
        warning(Arrays.stream(messages).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
    }

    /**
     * Sends a severe error message to the server console
     * @param messages The error messages to be sent
     */
    public void severe(StackTraceElement... messages) {
        severe(Arrays.stream(messages).map(StackTraceElement::toString).collect(Collectors.joining("\n")));
    }

    /**
     * Sends an informative message to the server console
     * @param messages The messages to be sent
     */
    public void log(Component... messages) {
        for (Component message : messages) {
            plugin.getServer().getConsoleSender().sendMessage(chat("<gray>[<aqua>" + plugin.getName() + "<gray>] <reset>").append(message));
        }
    }

    /**
     * Sends a warning message to the server console
     * @param messages The warning messages to be sent
     */
    public void warning(Component... messages) {
        for (Component message : messages) {
            plugin.getLogger().warning(PlainTextComponentSerializer.plainText().serialize(message));
        }
    }

    /**
     * Sends a severe error message to the server console
     * @param messages The error messages to be sent
     */
    public void severe(Component... messages) {
        for (Component message : messages) {
            plugin.getLogger().severe(PlainTextComponentSerializer.plainText().serialize(message));
        }
    }
    //endregion

    public void actionBar(Player player, String message) {
        player.sendActionBar(chat(message));
    }

    /**
     * Restores the player's health to maximum
     * @param p The player whose health will be restored to maximum
     */
    public void setMaxHealth(Player p) {
        p.setHealth(Objects.requireNonNull(p.getAttribute(Attribute.MAX_HEALTH)).getDefaultValue());
    }

    /**
     * Determines if a location is inside a specified area
     * @param loc Location to check
     * @param cornerOne First corner of the specified area
     * @param cornerTwo Second corner of the specified area
     * @return If the location is inside returns 'true', if outside, returns 'false'
     */
    public boolean isInside(Location loc, Location cornerOne, Location cornerTwo) {
        if (cornerOne == null || cornerTwo == null) return false;

        double minX = Math.min(cornerOne.getX(), cornerTwo.getX());
        double maxX = Math.max(cornerOne.getX(), cornerTwo.getX());
        double minY = Math.min(cornerOne.getY(), cornerTwo.getY());
        double maxY = Math.max(cornerOne.getY(), cornerTwo.getY());
        double minZ = Math.min(cornerOne.getZ(), cornerTwo.getZ());
        double maxZ = Math.max(cornerOne.getZ(), cornerTwo.getZ());

        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        return x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    /**
     * Determines if a location is inside a specified area (only X and Z axes)
     * @param loc Location to check
     * @param cornerOne First corner of the specified area
     * @param cornerTwo Second corner of the specified area
     * @return True if the location is within the area in X and Z, false otherwise
     */
    public boolean isInsideIgnoreY(Location loc, Location cornerOne, Location cornerTwo) {
        if (cornerOne == null || cornerTwo == null) return false;

        double minX = Math.min(cornerOne.getX(), cornerTwo.getX());
        double maxX = Math.max(cornerOne.getX(), cornerTwo.getX());
        double minZ = Math.min(cornerOne.getZ(), cornerTwo.getZ());
        double maxZ = Math.max(cornerOne.getZ(), cornerTwo.getZ());

        double x = loc.getX();
        double z = loc.getZ();

        return x >= minX && x <= maxX &&
                z >= minZ && z <= maxZ;
    }


    public boolean isInWorld(Location loc, World world) {
        return loc.getWorld().getName().equalsIgnoreCase(world.getName());
    }

    public double calculateSafeRadius(Location corner1, Location corner2) {
        double minX = Math.min(corner1.getX(), corner2.getX());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());

        return Math.min(maxX - minX, maxZ - minZ) * 0.4;
    }

    public Location getRandomLocationNear(Location center, double maxOffset) {
        Random random = new Random();
        double offsetX = (random.nextDouble() - 0.5) * 2 * maxOffset;
        double offsetZ = (random.nextDouble() - 0.5) * 2 * maxOffset;

        return center.clone().add(offsetX, 0, offsetZ);
    }

    public List<Location> getPositionsAroundCenter(int positionCount, Location center, double radius) {
        List<Location> positions = new ArrayList<>();
        World world = center.getWorld();

        for (int i = 0; i < positionCount; i++) {
            double angle = 2 * Math.PI * i / positionCount + Math.PI / 2;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location safeLoc = findSafeGroundLocation(world, x, z, world.getMinHeight(), world.getMaxHeight());
            positions.add(Objects.requireNonNullElseGet(safeLoc, () -> new Location(world, x, center.getY(), z)));
        }

        return positions;
    }

    public Location getRandomLocationAroundCenter(Location center, double maxRadius, int attempts) {
        World world = center.getWorld();
        Random random = new Random();

        for (int i = 0; i < attempts; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * maxRadius;

            double x = center.getX() + distance * Math.cos(angle);
            double z = center.getZ() + distance * Math.sin(angle);

            Location safeLoc = findSafeGroundLocation(world, x, z, world.getMinHeight(), world.getMaxHeight());
            if (safeLoc != null) {
                return safeLoc;
            }
        }

        return center.clone();
    }

    public Location getRandomLocationAroundCenter(Location center, double maxRadius) {
        return getRandomLocationAroundCenter(center, maxRadius, 30);
    }

    public Location findSafeGroundLocation(World world, double x, double z, int minY, int maxY) {
        for (int y = minY; y <= maxY - 2; y++) {
            Block block = world.getBlockAt((int) x, y, (int) z);
            Block above = block.getRelative(BlockFace.UP);
            Block above2 = above.getRelative(BlockFace.UP);

            if (block.getType().isSolid() && above.getType() == Material.AIR && above2.getType() == Material.AIR) {
                return new Location(world, x + 0.5, y + 1, z + 0.5);
            }
        }

        return null;
    }


    /**
     * Executes a task ({@link Runnable}) after a specified time.
     * @param delay The waiting time in ticks before execution.
     * @param run The task to execute, implemented as a {@code Runnable}.
     */
    public void delay(int delay, Runnable run) {
        Bukkit.getScheduler().runTaskLater(plugin, run,delay);
    }

    public void delay(Runnable run) {
        delay(1, run);
    }

    /**
     * Checks if an inventory has enough space for a specific ItemStack,
     * considering the possibility of stacking it with existing stacks in the inventory.
     * <br>
     * It's useful to determine if, when adding an item, it will be stored correctly
     * or if it will end up in the player's cursor or removed due to lack of space.
     *
     * @param inv  The inventory to which the item will be added.
     * @param item The item to be added.
     * @return {@code true} if the item can be completely stored, {@code false} if there isn't enough space.
     */
    public boolean canCompletelyStore(Inventory inv, ItemStack item) {
        int toStore = item.getAmount();

        for (ItemStack stack : inv.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                toStore -= item.getMaxStackSize();
            }
            else if (stack.isSimilar(item)) {
                int space = stack.getMaxStackSize() - stack.getAmount();
                toStore -= space;
            }

            if (toStore <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Formats a time in seconds into a {@link String}.
     *
     * @param time         The total time in seconds.
     * @param showSeconds  Whether to show seconds.
     * @param showMinutes  Whether to show minutes.
     * @param showHours    Whether to show hours.
     * @return String of text with formatted time.
     */
    public String formatTime(int time, boolean showSeconds, boolean showMinutes, boolean showHours) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;

        List<String> parts = new ArrayList<>();

        if (showHours) {
            parts.add(String.format("%02d", hours));
        }
        if (showMinutes) {
            if (showHours) {
                parts.add(String.format("%02d", minutes));
            } else {
                parts.add(String.valueOf(hours * 60 + minutes));
            }
        }
        if (showSeconds) {
            if (showHours || showMinutes) {
                parts.add(String.format("%02d", seconds));
            } else {
                parts.add(String.valueOf(time));
            }
        }

        return String.join(":", parts);
    }

    //region [Sub-methods of formatTime]
    public String formatTime(int time) {
        return formatTime(time, true, true, false);
    }

    public String formatSec(int time) {
        return formatTime(time, true, false, false);
    }

    public String formatMin(int time) {
        return formatTime(time, false, true, false);
    }

    public String formatHour(int time) {
        return formatTime(time, false, false, true);
    }
    //endregion

    /**
     * Formats a time in seconds into a {@link Component}.
     *
     * @param time         The total time in seconds.
     * @param showSeconds  Whether to show seconds.
     * @param showMinutes  Whether to show minutes.
     * @param showHours    Whether to show hours.
     * @return Component of text with formatted time.
     */
    public Component formatComponentTime(int time, boolean showSeconds, boolean showMinutes, boolean showHours) {
        int hours = time / 3600;
        int minutes = (time % 3600) / 60;
        int seconds = time % 60;

        Component comp = Component.empty();

        if (showHours) {
            comp = comp.append(Component.text(String.format("%02d", hours)));
        }

        if (showMinutes) {
            if (showHours) {
                comp = comp.append(Component.text(":"));
                comp = comp.append(Component.text(String.format("%02d", minutes)));
            } else {
                comp = comp.append(Component.text(hours * 60 + minutes));
            }
        }

        if (showSeconds) {
            if (showHours || showMinutes) {
                comp = comp.append(Component.text(":"));
                comp = comp.append(Component.text(String.format("%02d", seconds)));
            } else {
                comp = comp.append(Component.text(time));
            }
        }

        return comp;
    }

    //region [Sub-methods of formatComponentTime]
    public Component formatComponentTime(int time) {
        return formatComponentTime(time, true, true, false);
    }

    public Component formatSecComponent(int time) {
        return formatComponentTime(time, true, false, false);
    }

    public Component formatMinComponent(int time) {
        return formatComponentTime(time, false, true, false);
    }

    public Component formatHourComponent(int time) {
        return formatComponentTime(time, false, false, true);
    }
    //endregion

    //region [ItemBuilder methods]
    /**
     * Constructs an ItemBuilder with the specified material and amount of 1.
     *
     * @param mat The {@link Material} to use for the item
     */
    public ItemBuilder ib(Material mat) {
        return ib(new ItemStack(mat), 1);
    }

    /**
     * Constructs an ItemBuilder with the specified material and amount.
     *
     * @param mat    The {@link Material} to use for the item
     * @param amount The amount of items in the stack
     */
    public ItemBuilder ib(Material mat, int amount) {
        return ib(new ItemStack(mat), amount);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and amount of 1.
     *
     * @param item The base {@link ItemStack} to modify
     */
    public ItemBuilder ib(ItemStack item) {
        return new ItemBuilder(item,this);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and specified amount.
     *
     * @param item   The base {@link ItemStack} to modify
     * @param amount The amount of items in the stack
     */
    public ItemBuilder ib(ItemStack item, int amount) {
        return new ItemBuilder(item, amount, this);
    }
    //endregion

    public boolean isTool(ItemStack item) {
        String name = item.getType().name();
        return name.endsWith("_AXE") || name.endsWith("_PICKAXE") || name.endsWith("_HOE") || name.endsWith("_SHOVEL")
                || item.getType() == Material.SHEARS || item.getType() == Material.FISHING_ROD || item.getType() == Material.FLINT_AND_STEEL;
    }

    public void giveOrDrop(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1) {
            World world = player.getWorld();
            world.dropItemNaturally(player.getLocation(), item);
        } else {
            player.playSound(Sound.sound(SoundEventKeys.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 0.15f, 2.0f));
            player.getInventory().addItem(item);
        }
    }

    public void giveOrDrop(Player player, Collection<ItemStack> items) {
        items.forEach(item -> giveOrDrop(player, item));
    }

    public @Nullable Player getDamager(EntityDamageByEntityEvent event) {
        return switch (event.getDamager()) {
            case Player player -> player;
            case Projectile projectile when projectile.getShooter() instanceof Player player -> player;
            case AreaEffectCloud cloud when cloud.getSource() instanceof Player player -> player;
            default -> null;
        };
    }

    public int getMaterialAmount(Player player, Material material) {
        int amount = 0;
        for (ItemStack item : player.getInventory().getStorageContents()) {
            if (item == null || item.getType() != material) continue;
            amount += item.getAmount();
        }
        return amount;
    }
}