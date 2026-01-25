package me.putindeer.api.util.builder;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import me.putindeer.api.util.PluginUtils;

import java.util.*;

/**
 * A builder class for creating and modifying {@link ItemStack} objects with a fluent interface.
 * This class provides various methods to customize items including enchantments, lore, display name,
 * and specific metadata for different item types such as potions, banners, and more.
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * ItemStack sword = new ItemBuilder(Material.DIAMOND_SWORD, 1, player, pluginUtils)
 *     .name("&cMighty Sword")
 *     .enchant(Enchantment.DAMAGE_ALL, 5)
 *     .lore("A powerful sword", "Used by champions")
 *     .unbreakable()
 *     .build();
 * }
 * </pre>
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public class ItemBuilder {
    private final ItemStack item;
    private final PluginUtils utils;

    /**
     * Creates a new ItemBuilder instance.
     *
     * @param item   The base ItemStack to modify
     * @param utils  PluginUtils instance for chat formatting and other utilities
     */
    public ItemBuilder(ItemStack item, PluginUtils utils) {
        this.item = item;
        this.utils = utils;
    }

    /**
     * Creates a new ItemBuilder instance.
     *
     * @param item   The base ItemStack to modify
     * @param amount The amount of items in the stack
     * @param utils  PluginUtils instance for chat formatting and other utilities
     */
    public ItemBuilder(ItemStack item, int amount, PluginUtils utils) {
        this.item = item;
        this.item.setAmount(amount);
        this.utils = utils;
    }

    /**
     * Sets the display name of the item.
     *
     * @param name The name to set, supporting color codes with MiniMessage API
     * @return This builder instance for chaining
     */
    public ItemBuilder name(String name) {
        item.setData(DataComponentTypes.ITEM_NAME, format(name));
        return this;
    }

    /**
     * Sets the custom name of the item.
     *
     * @param name The name to set, supporting color codes with MiniMessage API
     * @return This builder instance for chaining
     */
    public ItemBuilder customName(String name) {
        item.setData(DataComponentTypes.CUSTOM_NAME, format(name));
        return this;
    }

    /**
     * Sets the lore of the item, replacing any existing lore.
     *
     * @param lines The lines of lore text, supporting color codes with '&' symbol
     * @return This builder instance for chaining
     */
    public ItemBuilder lore(String... lines) {
        List<Component> components = new ArrayList<>();
        Arrays.stream(lines).forEach(string -> components.add(format("<gray>" + string)));

        item.setData(DataComponentTypes.LORE, ItemLore.lore().lines(components).build());
        return this;
    }

    /**
     * Sets the lore of the item using a list of strings, replacing any existing lore.
     *
     * @param lines The list of lore lines, supporting color codes with '&' symbol
     * @return This builder instance for chaining
     */
    public ItemBuilder lore(List<String> lines) {
        return lore(lines.toArray(new String[0]));
    }

    /**
     * Adds lines to the existing lore of the item.
     *
     * @param lines The lines to add, supporting color codes with '&' symbol
     * @return This builder instance for chaining
     */
    public ItemBuilder addLore(String... lines) {
        List<Component> components = new ArrayList<>();
        ItemLore previousLines = item.getData(DataComponentTypes.LORE);
        if (previousLines != null) {
            components.addAll(previousLines.lines());
        }
        Arrays.stream(lines).forEach(string -> components.add(format("<gray>" + string)));

        item.setData(DataComponentTypes.LORE, ItemLore.lore(components));
        return this;
    }

    /**
     * Adds a list of lore lines to the existing lore of the item.
     *
     * @param lines The list of lore lines to add, supporting color codes with '&' symbol
     * @return This builder instance for chaining
     */
    public ItemBuilder addLore(List<String> lines) {
        return addLore(lines.toArray(new String[0]));
    }

    /**
     * Conditionally adds lore lines to the item if the specified condition is true.
     *
     * @param bool  The condition to check
     * @param lines The lore lines to add if the condition is true
     * @return This builder instance for chaining
     */
    public ItemBuilder addLoreIf(boolean bool, String... lines) {
        if (bool) return addLore(lines);
        return this;
    }

    /**
     * Conditionally adds a list of lore lines to the item if the specified condition is true.
     *
     * @param bool  The condition to check
     * @param lines The list of lore lines to add if the condition is true
     * @return This builder instance for chaining
     */
    public ItemBuilder addLoreIf(boolean bool, List<String> lines) {
        if (bool) return addLore(lines);
        return this;
    }

    private Component format(String input) {
        Component component = utils.chat(input);
        return component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    /**
     * Adds an enchantment to the item with the specified level.
     *
     * @param enchantment The enchantment to add
     * @param level       The level of the enchantment (ignored if negative)
     * @return This builder instance for chaining
     */
    public ItemBuilder enchant(Enchantment enchantment, int level) {
        if (level < 0) return this;
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        ItemEnchantments previousEnchantments = item.getData(DataComponentTypes.ENCHANTMENTS);
        if (previousEnchantments != null) {
            enchantments.putAll(previousEnchantments.enchantments());
        }
        enchantments.put(enchantment, level);
        item.setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(enchantments));
        return this;
    }

    /**
     * Adds an enchantment to the item with level 1.
     *
     * @param enchantment The enchantment to add
     * @return This builder instance for chaining
     */
    public ItemBuilder enchant(Enchantment enchantment) {
        return enchant(enchantment, 1);
    }

    /**
     * Adds enchantment glint to the item even if it has no enchantments.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder glint() {
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return this;
    }

    /**
     * Removes the enchantment glint from the item even if it has enchantments.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder removeGlint() {
        item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false);
        return this;
    }

    /**
     * Conditionally adds or removes enchantment glint based on the provided condition.
     *
     * @param bool If true, adds glint; if false, removes glint
     * @return This builder instance for chaining
     */
    public ItemBuilder glintIf(boolean bool) {
        if (bool) return glint();
        else return removeGlint();
    }

    /**
     * Sets the durability value for the item.
     *
     * @param durability the durability value to be set for the item
     * @return the current instance of {@code ItemBuilder} for method chaining
     */
    public ItemBuilder durability(int durability) {
        item.setData(DataComponentTypes.DAMAGE, durability);
        return this;
    }

    /**
     * Sets the item's durability as a percentage of its maximum durability.
     * The percentage value should be between 0 and 100 (inclusive).
     * If the percentage is invalid or the item's current durability is unavailable, the method will return the current ItemBuilder instance without making changes.
     *
     * @param percentage The durability percentage to set, ranging from 0 to 100.
     * @return The current ItemBuilder instance with the updated durability, or unchanged if the percentage is invalid or the item's durability data is unavailable.
     */
    public ItemBuilder durabilityPercentage(float percentage) {
        Integer currentDurability = item.getData(DataComponentTypes.DAMAGE);
        if (currentDurability == null || percentage < 0 || percentage > 100) return this;
        int newDurability = (int) (currentDurability * (percentage / 100));
        return durability(newDurability);
    }

    /**
     * Adds to the current durability damage value of the item.
     *
     * @param value The amount to add to the current durability damage
     * @return the modified {@code ItemBuilder} instance, allowing method chaining.
     */
    public ItemBuilder addDurability(int value) {
        Integer actualDurability = item.getData(DataComponentTypes.DAMAGE);
        if (actualDurability != null) return durability(actualDurability + value);
        return this;
    }

    /**
     * Subtracts from the current durability damage value of the item.
     *
     * @param value The amount to subtract from the current durability damage
     * @return the modified {@code ItemBuilder} instance, allowing method chaining.
     */
    public ItemBuilder substractDurability(int value) {
        Integer actualDurability = item.getData(DataComponentTypes.DAMAGE);
        if (actualDurability != null) return durability(actualDurability - value);
        return this;
    }

    /**
     * Sets the maximum durability damage value of the item.
     * This allows damaging items that are non-damageable by default
     * @param value The amount to set as the maximum durability damage value
     * @return the modified {@code ItemBuilder} instance, allowing method chaining.
     */
    public ItemBuilder maxDurability(int value) {
        item.setData(DataComponentTypes.MAX_DAMAGE, value);
        return this;
    }

    /**
     * Sets the maximum durability of the item to a specified percentage of its current maximum durability.
     *
     * @param percentage the percentage of the item's current maximum durability to set as the new maximum durability.
     *                   Values less than 0 are ignored.
     * @return the modified {@code ItemBuilder} instance, allowing method chaining.
     */
    public ItemBuilder maxDurabilityPercentage(float percentage) {
        Integer maxDurability = item.getData(DataComponentTypes.MAX_DAMAGE);
        if (maxDurability == null || percentage < 0) return this;
        int newMaxDurability = (int) (maxDurability * (percentage / 100));
        return maxDurability(newMaxDurability);
    }

    /**
     * Hides specific data component types from the item's tooltip.
     *
     * @param types The data component types to hide
     * @return This builder instance for chaining
     */
    public ItemBuilder hide(DataComponentType... types) {
        Set<DataComponentType> dataComponentTypes = new HashSet<>();
        TooltipDisplay actualDisplay = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
        if (actualDisplay != null) {
            dataComponentTypes.addAll(actualDisplay.hiddenComponents());
        }
        dataComponentTypes.addAll(Set.of(types));
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hiddenComponents(dataComponentTypes).build());
        return this;
    }

    /**
     * Hides enchantment information from the item's tooltip.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder hideEnchantments() {
        return hide(DataComponentTypes.ENCHANTMENTS);
    }

    /**
     * Hides the unbreakable flag from the item's tooltip.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder hideUnbreakable() {
        return hide(DataComponentTypes.UNBREAKABLE);
    }

    /**
     * Hides potion effects from the item's tooltip.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder hidePotionEffects() {
        return hide(DataComponentTypes.POTION_CONTENTS);
    }

    /**
     * Hides attribute modifiers from the item's tooltip.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder hideAttributeModifiers() {
        return hide(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    }

    /**
     * Hides everything that could be seen in a GUI.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder hideGUI() {
        return hide(DataComponentTypes.ENCHANTMENTS,
                DataComponentTypes.UNBREAKABLE,
                DataComponentTypes.ATTRIBUTE_MODIFIERS,
                DataComponentTypes.POTION_CONTENTS);
    }

    /**
     * Completely hides the tooltip of the item.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder hideTooltip() {
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
        return this;
    }

    /**
     * Makes the item unbreakable.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder unbreakable() {
        item.setData(DataComponentTypes.UNBREAKABLE);
        return this;
    }

    /**
     * Sets the owner of the item (mainly for player heads).
     *
     * @param player The player to set as the owner
     * @return This builder instance for chaining
     */
    public ItemBuilder profile(OfflinePlayer player) {
        item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile(player.getPlayerProfile()));
        return this;
    }

    public ItemBuilder profileTexture(String texture) {
        item.setData(DataComponentTypes.PROFILE, ResolvableProfile.resolvableProfile().addProperty(new ProfileProperty("textures", texture)));
        return this;
    }

    /**
     * Sets the color of a dyeable item (like leather armor).
     *
     * @param color The color to set
     * @return This builder instance for chaining
     */
    public ItemBuilder color(Color color) {
        item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(color));
        return this;
    }

    /**
     * Sets the color of a potion item.
     *
     * @param color The color to set for the potion
     * @return This builder instance for chaining
     */
    public ItemBuilder potionColor(Color color) {
        item.setData(DataComponentTypes.POTION_CONTENTS, getPotionBuilder().customColor(color).build());
        return this;
    }

    /**
     * Adds custom potion effects to a potion item.
     *
     * @param effect The potion effects to add
     * @return This builder instance for chaining
     */
    public ItemBuilder potionEffect(PotionEffect... effect) {
        item.setData(DataComponentTypes.POTION_CONTENTS, getPotionBuilder().addCustomEffects(List.of(effect)).build());
        return this;
    }

    /**
     * Sets the potion type of a potion item.
     *
     * @param type The potion type to set
     * @return This builder instance for chaining
     */
    public ItemBuilder potionType(PotionType type) {
        item.setData(DataComponentTypes.POTION_CONTENTS, getPotionBuilder().potion(type).build());
        return this;
    }

    /**
     * Helper method to get or create a potion contents builder with existing data.
     *
     * @return A potion contents builder with any existing potion data
     */
    private PotionContents.Builder getPotionBuilder() {
        PotionContents original = item.getData(DataComponentTypes.POTION_CONTENTS);
        PotionContents.Builder builder = PotionContents.potionContents();
        if (original == null) return builder;
        if (original.customColor() != null) builder.customColor(original.customColor());
        if (!original.customEffects().isEmpty()) builder.addCustomEffects(original.customEffects());
        if (original.potion() != null) builder.potion(original.potion());
        if (original.customName() != null) builder.customName(original.customName());
        return builder;
    }

    /**
     * Adds pattern layers to a banner item.
     *
     * @param patterns The patterns to add
     * @return This builder instance for chaining
     */
    public ItemBuilder pattern(Pattern... patterns) {
        List<Pattern> patternList = new ArrayList<>();
        BannerPatternLayers previousPatterns = item.getData(DataComponentTypes.BANNER_PATTERNS);
        if (previousPatterns != null) {
            patternList.addAll(previousPatterns.patterns());
        }
        patternList.addAll(List.of(patterns));
        item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(patternList));
        return this;
    }

    /**
     * Adds items to a container item (like shulker boxes).
     *
     * @param items The items to add to the container
     * @return This builder instance for chaining
     */
    public ItemBuilder addItem(ItemStack... items) {
        List<ItemStack> itemList = new ArrayList<>();
        ItemContainerContents previousItems = item.getData(DataComponentTypes.CONTAINER);
        if (previousItems != null) {
            itemList.addAll(previousItems.contents());
        }
        itemList.addAll(List.of(items));
        item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(itemList));
        return this;
    }

    /**
     * Sets the complete contents of a container item, replacing any existing contents.
     *
     * @param contents The list of items to set as the container contents
     * @return This builder instance for chaining
     */
    public ItemBuilder setContents(List<ItemStack> contents) {
        item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents));
        return this;
    }

    /**
     * Adds a firework effect to a firework item with specified amount of repetitions.
     *
     * @param type   The firework effect type
     * @param amount The number of times to add this effect
     * @param colors The colors for the firework effect
     * @return This builder instance for chaining
     */
    public ItemBuilder fireworkEffect(FireworkEffect.Type type, int amount, Color... colors) {
        List<FireworkEffect> effectList = new ArrayList<>();
        FireworkEffect effect = FireworkEffect.builder().with(type).withColor(colors).build();
        for (int i = 0; i < amount; i++) {
            effectList.add(effect);
        }
        item.setData(DataComponentTypes.FIREWORKS, getFireworksBuilder().addEffects(effectList));
        return this;
    }

    /**
     * Adds a single firework effect to a firework item.
     *
     * @param type   The firework effect type
     * @param colors The colors for the firework effect
     * @return This builder instance for chaining
     */
    public ItemBuilder fireworkEffect(FireworkEffect.Type type, Color... colors) {
        return fireworkEffect(type, 1, colors);
    }

    /**
     * Sets the flight duration (power) of a firework item.
     *
     * @param power The power level (1-3 for vanilla)
     * @return This builder instance for chaining
     */
    public ItemBuilder fireworkPower(int power) {
        item.setData(DataComponentTypes.FIREWORKS, getFireworksBuilder().flightDuration(power));
        return this;
    }

    /**
     * Helper method to get or create a fireworks builder with existing data.
     *
     * @return A fireworks builder with any existing firework data
     */
    private Fireworks.Builder getFireworksBuilder() {
        Fireworks original = item.getData(DataComponentTypes.FIREWORKS);
        Fireworks.Builder builder = Fireworks.fireworks();

        if (original != null) {
            if (!original.effects().isEmpty()) builder.addEffects(original.effects());
            builder.flightDuration(original.flightDuration());
        }

        return builder;
    }

    public ItemBuilder chargedProjectiles(ItemStack arrow) {
        item.setData(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectiles.chargedProjectiles(List.of(arrow)));
        return this;
    }

    /**
     * Sets the consume seconds for the consumable item.
     *
     * @param seconds The time in seconds to consume the item
     * @return This builder instance for chaining
     */
    public ItemBuilder consumeSeconds(float seconds) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().consumeSeconds(seconds).build());
        return this;
    }

    /**
     * Sets the animation type for the consumable item.
     *
     * @param animation The consume animation type
     * @return This builder instance for chaining
     */
    public ItemBuilder consumeAnimation(ItemUseAnimation animation) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().animation(animation).build());
        return this;
    }

    /**
     * Sets the sound to be played when consuming.
     *
     * @param sound The played sound
     * @return This builder instance for chaining
     */
    public ItemBuilder consumingSound(Key sound) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().sound(sound).build());
        return this;
    }

    /**
     * Sets whether the item has consume particles.
     *
     * @param hasParticles Whether to show consume particles
     * @return This builder instance for chaining
     */
    public ItemBuilder consumeParticles(boolean hasParticles) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().hasConsumeParticles(hasParticles).build());
        return this;
    }

    /**
     * Adds consume effects to the consumable item.
     *
     * @param effects The consume effects to add
     * @return This builder instance for chaining
     */
    public ItemBuilder addConsumeEffects(ConsumeEffect... effects) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().addEffects(List.of(effects)).build());
        return this;
    }

    public ItemBuilder consumeApplyEffects(PotionEffect... effects) {
        return consumeApplyEffects(1, effects);
    }

    public ItemBuilder consumeApplyEffects(int probability, PotionEffect... effects) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().addEffect(ConsumeEffect.applyStatusEffects(List.of(effects), probability)).build());
        return this;
    }

    /**
     * Adds specific effects to remove when consuming.
     *
     * @param effects The effect types to remove
     * @return This builder instance for chaining
     */
    public ItemBuilder consumeRemoveEffects(PotionEffectType... effects) {
        RegistryKeySet<@NotNull PotionEffectType> effectSet = RegistrySet.keySet(RegistryKey.MOB_EFFECT, Arrays.stream(effects)
                .map(effect -> TypedKey.create(RegistryKey.MOB_EFFECT, effect.getKey())).toList());
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().addEffect(ConsumeEffect.removeEffects(effectSet)).build());
        return this;
    }

    /**
     * Sets whether to clear all effects when consuming.
     *
     * @return This builder instance for chaining
     */
    public ItemBuilder consumeClearAllEffects() {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().addEffect(ConsumeEffect.clearAllStatusEffects()).build());
        return this;
    }

    /**
     * Sets the teleport randomly range for the consumable item.
     *
     * @param range The teleport range
     * @return This builder instance for chaining
     */
    public ItemBuilder consumeTeleportRandomly(float range) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().addEffect(ConsumeEffect.teleportRandomlyEffect(range)).build());
        return this;
    }

    public ItemBuilder consumePlaySound(Key key) {
        item.setData(DataComponentTypes.CONSUMABLE, getConsumableBuilder().addEffect(ConsumeEffect.playSoundConsumeEffect(key)).build());
        return this;
    }

    /**
     * Helper method to get or create a consumable builder with existing data.
     *
     * @return A consumable builder with any existing consumable data
     */
    private Consumable.Builder getConsumableBuilder() {
        Consumable original = item.getData(DataComponentTypes.CONSUMABLE);
        Consumable.Builder builder = Consumable.consumable();

        if (original == null) return builder;

        builder.consumeSeconds(original.consumeSeconds());
        builder.animation(original.animation());
        builder.sound(original.sound());
        builder.hasConsumeParticles(original.hasConsumeParticles());
        if (!original.consumeEffects().isEmpty()) builder.addEffects(original.consumeEffects());

        return builder;
    }

    public ItemBuilder useCooldown(int seconds) {
        item.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(seconds));
        return this;
    }

    public ItemBuilder useCooldown(int seconds, String key) {
        item.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(seconds).cooldownGroup(new NamespacedKey(utils.plugin.getName(), key)).build());
        return this;
    }
    public ItemBuilder useCooldown(int seconds, String identifier, String key) {
        item.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(seconds).cooldownGroup(new NamespacedKey(identifier, key)).build());
        return this;
    }

    public ItemBuilder rarity(ItemRarity rarity) {
        item.setData(DataComponentTypes.RARITY, rarity);
        return this;
    }

    public ItemBuilder food(int nutrition, float saturation, boolean canAlwaysEat) {
        item.setData(DataComponentTypes.FOOD, FoodProperties.food().nutrition(nutrition).saturation(saturation).canAlwaysEat(canAlwaysEat).build());
        return this;
    }

    public ItemBuilder food(int nutrition, float saturation) {
        return food(nutrition, saturation, false);
    }

    public ItemBuilder model(String namespace, String key) {
        item.setData(DataComponentTypes.ITEM_MODEL, new NamespacedKey(namespace, key));
        return this;
    }

    public ItemBuilder model(Material material) {
        item.setData(DataComponentTypes.ITEM_MODEL, Objects.requireNonNull(material.getKey()));
        return this;
    }

    public ItemBuilder model(Key key) {
        item.setData(DataComponentTypes.ITEM_MODEL, key);
        return this;
    }

    public ItemBuilder maxStackSize(int stackSize) {
        item.setData(DataComponentTypes.MAX_STACK_SIZE, stackSize);
        return this;
    }

    /**
     * Sets a non-valued data component for the item.
     *
     * @param type The data component type to set
     * @param <T>  The type parameter of the data component
     * @return This builder instance for chaining
     */
    public <T> ItemBuilder setDataComponent(DataComponentType.NonValued type) {
        item.setData(type);
        return this;
    }

    /**
     * Sets a valued data component for the item with the specified value.
     *
     * @param type  The data component type to set
     * @param value The value to set for the data component
     * @param <T>   The type parameter of the data component and value
     * @return This builder instance for chaining
     */
    public <T> ItemBuilder setDataComponent(DataComponentType.Valued<@NotNull T> type, T value) {
        item.setData(type, value);
        return this;
    }

    /**
     * Sets a valued data component for the item using a builder.
     *
     * @param type    The data component type to set
     * @param builder The builder to use for constructing the data component value
     * @param <T>     The type parameter of the data component and builder result
     * @return This builder instance for chaining
     */
    public <T> ItemBuilder setDataComponent(DataComponentType.Valued<@NotNull T> type, DataComponentBuilder<@NotNull T> builder) {
        item.setData(type, builder);
        return this;
    }

    /**
     * Builds and returns the final ItemStack with all applied modifications.
     *
     * @return The built ItemStack
     */
    public ItemStack build() {
        return item;
    }
}