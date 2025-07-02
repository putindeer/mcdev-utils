package us.polarismc.api.util;

import io.papermc.paper.registry.TypedKey;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import us.polarismc.api.managers.LangManager;
import us.polarismc.api.managers.StartupManager;
import us.polarismc.api.util.builder.ItemBuilder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class PluginUtils {
    public final JavaPlugin plugin;
    public final LangManager lang;

    public PluginUtils(JavaPlugin plugin, String prefix) {
        this.plugin = plugin;
        this.prefix = chat(prefix);
        if (plugin != null) {
            this.lang = null;
            //this.lang = new LangManager(plugin);
            StartupManager.registerPlugin(plugin, this);
        } else lang = null;
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
     * Converts and translates a text with HEX codes to a {@link Component}
     * @param string The input as a {@link String}
     * @param player The player that is getting the text translated
     * @return The converted text
     */
    public Component chat(String string, CommandSender player) {
        if (lang == null) return chat(string);
        return MiniMessage.miniMessage().deserialize(convert(lang.translate(player, string)));
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
    public void broadcast(TypedKey<Sound> soundKey, String... messages) {
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

    public void message(CommandSender receiver, TypedKey<Sound> soundKey, String... messages) {
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

    public void message(Collection<? extends CommandSender> receivers, boolean usePrefix, Sound sound, String... messages) {
        receivers.forEach(r -> message(r, usePrefix, sound, messages));
    }

    public void message(CommandSender receiver, boolean usePrefix, Sound sound, String... messages) {
        message(receiver, usePrefix, sound, Arrays.stream(messages).map(msg -> chat(msg, receiver)).toArray(Component[]::new));
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

    public void title(Player player, String title, String subtitle) {
        title(player, title, subtitle, null);
    }

    public void title(Player player, String title, String subtitle, Sound sound) {
        player.showTitle(Title.title(chat(title), chat(subtitle)));
        if (sound != null) {
            player.playSound(sound);
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

    public boolean isInWorld(Location loc, World world) {
        return loc.getWorld().getName().equalsIgnoreCase(world.getName());
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
        return ib(new ItemStack(mat), 1, null);
    }

    /**
     * Constructs an ItemBuilder with the specified material and amount.
     *
     * @param mat    The {@link Material} to use for the item
     * @param amount The amount of items in the stack
     */
    public ItemBuilder ib(Material mat, int amount) {
        return ib(new ItemStack(mat), amount, null);
    }

    /**
     * Constructs an ItemBuilder with the specified material and amount.
     *
     * @param mat    The {@link Material} to use for the item
     * @param player The player, used to translate using the translation keys of each player.
     */
    public ItemBuilder ib(Material mat, Player player) {
        return ib(new ItemStack(mat), 1, player);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and amount of 1.
     *
     * @param item The base {@link ItemStack} to modify
     */
    public ItemBuilder ib(ItemStack item) {
        return ib(item, 1, null);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and specified amount.
     *
     * @param item   The base {@link ItemStack} to modify
     * @param amount The amount of items in the stack
     */
    public ItemBuilder ib(ItemStack item, int amount) {
        return ib(item, amount, null);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and amount of 1.
     *
     * @param item The base {@link ItemStack} to modify
     * @param player The player, used to translate using the translation keys of each player.
     */
    public ItemBuilder ib(ItemStack item, Player player) {
        return ib(item, 1, player);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and specified amount.
     *
     * @param item   The base {@link ItemStack} to modify
     * @param amount The amount of items in the stack
     * @param player The player, used to translate using the translation keys of each player.
     */
    public ItemBuilder ib(ItemStack item, int amount, Player player) {
        return new ItemBuilder(item, amount, player, this);
    }
    //endregion
}