package us.polarismc.api.util.builder;

import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import us.polarismc.api.PolarisAPI;
import us.polarismc.api.util.PluginUtils;

import java.util.function.Consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A builder class for creating and modifying {@link ItemStack} objects with a fluent interface.<br>
 * This class provides various methods to customize items including enchantments, lore, display name,
 * and specific metadata for different item types such as potions, banners, and more.
 * <br><br>
 * Usage example:
 * <br><br>
 * <pre>
 * {@code
 * ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD)
 *     .name("&cMighty Sword")
 *     .enchant(Enchantment.DAMAGE_ALL, 5)
 *     .lore("A powerful sword", "Used by champions")
 *     .unbreakable()
 *     .build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final PluginUtils utils;

    /**
     * Constructs an ItemBuilder with the specified material and amount of 1.
     *
     * @param mat The {@link Material} to use for the item
     */
    public ItemBuilder(Material mat) {
        this(new ItemStack(mat), 1);
    }

    /**
     * Constructs an ItemBuilder with the specified material and amount.
     *
     * @param mat    The {@link Material} to use for the item
     * @param amount The amount of items in the stack
     */
    public ItemBuilder(Material mat, int amount) {
        this(new ItemStack(mat), amount);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and amount of 1.
     *
     * @param item The base {@link ItemStack} to modify
     */
    public ItemBuilder(ItemStack item) {
        this(item, 1);
    }

    /**
     * Constructs an ItemBuilder with an existing ItemStack and specified amount.
     *
     * @param item   The base {@link ItemStack} to modify
     * @param amount The amount of items in the stack
     */
    public ItemBuilder(ItemStack item, int amount) {
        this.utils = new PluginUtils(PolarisAPI.instance, "");
        this.item = item;
        this.item.setAmount(amount);
        this.meta = item.getItemMeta();
    }

    /**
     * Adds an enchantment to the item and hides enchantment info in the tooltip.<br>
     * This can be used to add an enchant glint to an item without adding an enchantment.
     *
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder enchant() {
        this.meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * Adds a level 1 enchantment of the specified type to the item.
     *
     * @param ench The {@link Enchantment} to add
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder enchant(Enchantment ench) {
        this.meta.addEnchant(ench, 1, true);
        return this;
    }

    /**
     * Adds a specific enchantment with the specified level to the item.
     *
     * @param ench The {@link Enchantment} to add
     * @param lvl  The level of the enchantment
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder enchant(Enchantment ench, int lvl) {
        if (lvl > 0) {
            this.meta.addEnchant(ench, lvl, true);
            return this;
        }
        return this;
    }

    /**
     * Conditionally adds an Unbreaking I enchantment with hidden enchant flag,
     * or removes all enchantments if the condition is false.
     *
     * @param condition Whether to add the enchantment
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder enchantIf(boolean condition) {
        if (condition) {
            this.meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            return this;
        }

        this.meta.getEnchants().keySet().forEach(this.meta::removeEnchant);
        return this;
    }

    /**
     * Sets the durability of the item.
     *
     * @param i The durability value (higher value means less damage)
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder durability(int i) {
        Damageable dmg = (Damageable)this.meta;
        dmg.setDamage(this.item.getType().getMaxDurability() - i);
        return this;
    }

    /**
     * Hides all attributes and information in the item tooltip.
     *
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder hideAll() {
        this.meta.setHideTooltip(true);
        return this;
    }

    /**
     * Adds a specific {@link ItemFlag} to the item.
     *
     * @param itemflag The ItemFlag to add
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder addFlag(ItemFlag itemflag) {
        this.meta.addItemFlags(itemflag);
        return this;
    }

    /**
     * Sets the lore of the item with multiple messages.<br>
     * Each message is automatically prefixed with gray color code.
     *
     * @param msgs Array of strings to set as lore
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder lore(String... msgs) {
        List<Component> lore = new ArrayList<>();
        Arrays.stream(msgs).forEach(s -> lore.add(utils.chat("<gray>" + s)));

        this.meta.lore(lore);
        return this;
    }

    /**
     * Sets the lore of the item with a list of messages.<br>
     * Each message is automatically prefixed with gray color code.
     *
     * @param msgs List of strings to set as lore
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder lore(List<String> msgs) {
        List<Component> lore = new ArrayList<>();
        msgs.forEach(s -> lore.add(utils.chat("<gray>" + s)));
        this.meta.lore(lore);
        return this;
    }

    /**
     * Sets the lore of the item with a single message.<br>
     * The message is automatically prefixed with gray color code.
     *
     * @param msg String to set as lore
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder lore(String msg) {
        List<Component> lore = new ArrayList<>();
        lore.add(utils.chat("<gray>" + msg));
        this.meta.lore(lore);
        return this;
    }

    /**
     * Adds additional lore messages to the existing lore.<br>
     * Each message is automatically prefixed with gray color code.
     *
     * @param msg Array of strings to add to the lore
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder loreAdd(String... msg) {
        List<Component> lore = this.meta.lore() == null ? new ArrayList<>() : new ArrayList<>(Objects.requireNonNull(this.meta.lore()));
        for (String s : msg)
            lore.add(utils.chat("<gray>" + s));

        this.meta.lore(lore);
        return this;
    }

    /**
     * Conditionally adds lore messages to the existing lore.<br>
     * Each message is automatically prefixed with gray color code.
     *
     * @param bool Whether to add the lore messages
     * @param msgs Array of strings to conditionally add to the lore
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder loreAddIf(boolean bool, String... msgs) {
        List<Component> lore = this.meta.lore() == null ? new ArrayList<>() : new ArrayList<>(Objects.requireNonNull(this.meta.lore()));

        if (bool) {
            for (String s : msgs)
                lore.add(utils.chat("<gray>" + s));
        }

        this.meta.lore(lore);
        return this;
    }

    /**
     * Sets the display name of the item.<br>
     * Automatically applies color codes using the MiniMessage API or the '&' character.
     *
     * @param str The name to set
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder name(String str) {
        this.meta.displayName(utils.chat(str));
        return this;
    }

    /**
     * Makes the item unbreakable and hides the unbreakable flag in the tooltip.
     *
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder unbreakable() {
        this.meta.setUnbreakable(true);
        this.meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        return this;
    }

    /**
     * Applies a specific operation to the item's metadata if it's of the specified type.
     * Useful for applying operations to specialized metadata types.
     *
     * @param <T>          The type of ItemMeta
     * @param metaClass    The class of the ItemMeta type
     * @param metaConsumer A consumer function that applies changes to the metadata
     * @return This {@link ItemBuilder} instance for chaining
     */
    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        if (metaClass.isInstance(this.meta)) {
            metaConsumer.accept(metaClass.cast(this.meta));
        }
        return this;
    }

    /**
     * Sets the owner of a skull item.
     * Only works with skull items that can have owners.
     *
     * @param p The {@link OfflinePlayer} to set as the skull owner
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder owner(OfflinePlayer p) {
        return this.meta(SkullMeta.class, m -> m.setOwningPlayer(p));
    }

    /**
     * Sets the color of leather armor.
     * Only works with leather armor items.
     * Also hides dye and attribute flags in the tooltip.
     *
     * @param color The {@link Color} to apply to the armor
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder color(Color color) {
        this.meta.addItemFlags(ItemFlag.HIDE_DYE);
        this.meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this.meta(LeatherArmorMeta.class, m -> m.setColor(color));
    }

    /**
     * Sets the color of a potion.
     * Only works with potion items.
     *
     * @param color The {@link Color} to apply to the potion
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder potColor(Color color) {
        return this.meta(PotionMeta.class, m -> m.setColor(color));
    }

    /**
     * Adds a custom potion effect to a potion.
     * Only works with potion items.
     *
     * @param pe The {@link PotionEffect} to add to the potion
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder potEffect(PotionEffect pe) {
        return this.meta(PotionMeta.class, m -> m.addCustomEffect(pe, true));
    }

    /**
     * Sets the base potion type of a potion.
     * Only works with potion items.
     *
     * @param type The {@link PotionType} to set
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder potionType(PotionType type) {
        return this.meta(PotionMeta.class, m -> m.setBasePotionType(type));
    }

    /**
     * Sets all the patterns of a banner at once from another banner metadata.
     * Only works with banner items.
     *
     * @param meta The {@link BannerMeta} to copy patterns from
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder setBannerMeta(BannerMeta meta) {
        ((BannerMeta) this.meta).setPatterns(meta.getPatterns());
        this.item.setItemMeta(this.meta);
        return this;
    }

    /**
     * Adds a pattern to a banner.
     * Only works with banner items.
     *
     * @param pattern The {@link Pattern} to add to the banner
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder addPattern(Pattern pattern) {
        List<Pattern> list = new ArrayList<>(((BannerMeta) this.meta).getPatterns());
        list.add(pattern);

        ((BannerMeta) this.meta).setPatterns(list);
        this.item.setItemMeta(this.meta);
        return this;
    }

    /**
     * Adds an item to a chest item's inventory.
     * Only works with chest items.
     *
     * @param i The {@link ItemStack} to add to the chest
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder addItem(ItemStack i) {
        Chest chest = (Chest) ((BlockStateMeta) this.meta).getBlockState();
        chest.getInventory().addItem(i);
        ((BlockStateMeta)this.meta).setBlockState(chest);
        this.item.setItemMeta(this.meta);
        return this;
    }

    /**
     * Adds a firework effect to a firework item.
     * Only works with firework items.
     *
     * @param t The {@link FireworkEffect.Type} to use
     * @param c The {@link Color} to use for the effect
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder addFireworkEffect(FireworkEffect.Type t, Color c) {
        FireworkEffect e = FireworkEffect.builder().with(t).withColor(c).build();
        ((FireworkMeta) this.meta).addEffect(e);
        return this;
    }

    /**
     * Adds multiple instances of the same firework effect to a firework item.
     * Only works with firework items.
     *
     * @param t      The {@link FireworkEffect.Type} to use
     * @param c      A list of {@link Color} objects to use for the effect
     * @param amount The number of times to add the effect
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder addFireworkEffect(FireworkEffect.Type t, List<Color> c, int amount) {
        FireworkEffect e = FireworkEffect.builder().with(t).withColor(c).build();
        for (int i = 0; i < amount; i++)
            ((FireworkMeta) this.meta).addEffect(e);
        return this;
    }

    /**
     * Adds multiple instances of the same firework effect to a firework item.
     * Only works with firework items.
     *
     * @param t      The {@link FireworkEffect.Type} to use
     * @param c      The {@link Color} to use for the effect
     * @param amount The number of times to add the effect
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder addFireworkEffect(FireworkEffect.Type t, Color c, int amount) {
        FireworkEffect e = FireworkEffect.builder().with(t).withColor(c).build();
        for (int i = 0; i < amount; i++)
            ((FireworkMeta) this.meta).addEffect(e);
        return this;
    }

    /**
     * Sets the power (flight duration) of a firework item.
     * Only works with firework items.
     *
     * @param i The power level (0-127)
     * @return This {@link ItemBuilder} instance for chaining
     */
    public ItemBuilder setFireworkPower(int i) {
        ((FireworkMeta) this.meta).setPower(i);
        return this;
    }

    /**
     * Builds and returns the final {@link ItemStack} with all the applied modifications.
     * This method should be called after all modifications are complete.
     *
     * @return The finalized {@link ItemStack}
     */
    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item;
    }
}