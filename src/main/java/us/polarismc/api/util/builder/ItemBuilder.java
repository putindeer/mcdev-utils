package us.polarismc.api.util.builder;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Chest;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import us.polarismc.api.util.PluginUtils;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Builder para crear ItemStack usando DataComponent (Paper 1.21.5) + ItemMeta.
 * Mantiene todos los métodos originales, pero acumula cambios en builders
 * y los aplica en build(), dejando meta para casos especiales.
 */
@SuppressWarnings("unused")
public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;
    private final Player player;
    private final PluginUtils utils;

    // Builders internos de DataComponent
    private final ItemLore.Builder loreBuilder = ItemLore.lore();
    private final ItemEnchantments.Builder enchantBuilder = ItemEnchantments.itemEnchantments();
    private final PotionContents.Builder potionBuilder = PotionContents.potionContents();
    private final ItemAttributeModifiers.Builder attrBuilder = ItemAttributeModifiers.itemAttributes();
    private final TooltipDisplay.Builder tooltipBuilder = TooltipDisplay.tooltipDisplay();
    private final Fireworks.Builder fireworkBuilder = Fireworks.fireworks();
    private final FoodProperties.Builder foodBuilder = FoodProperties.food();

    public ItemBuilder(ItemStack item, int amount, Player player, PluginUtils utils) {
        this.item = item;
        this.item.setAmount(amount);
        this.meta = item.getItemMeta();
        this.player = player;
        this.utils = utils;
    }

    public ItemBuilder enchant() {
        this.item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return this;
    }

    public ItemBuilder enchant(Enchantment ench) {
        return enchant(ench, 1);
    }

    public ItemBuilder enchant(Enchantment ench, int lvl) {
        if (lvl > 0) {
            enchantBuilder.add(ench, lvl);
        }
        return this;
    }

    public ItemBuilder enchantIf(boolean condition) {
        if (condition) enchant();
        return this;
    }

    public ItemBuilder durability(int i) {
        item.setData(DataComponentTypes.DAMAGE, i);
        return this;
    }

    public ItemBuilder hideAll() {
        tooltipBuilder.hideTooltip(true);
        return this;
    }

    public ItemBuilder addHiddenDataComponent(DataComponentType flag) {
        tooltipBuilder.addHiddenComponents(flag);
        return this;
    }

    public ItemBuilder hideTooltip() {
        tooltipBuilder.hideTooltip(true);
        return this;
    }

    public ItemBuilder lore(String... msgs) {
        for (String s : msgs) {
            String t = player != null ? utils.lang.translate(player, s) : s;
            loreBuilder.addLine(utils.chat(t, player));
        }
        return this;
    }

    public ItemBuilder lore(List<String> msgs) {
        msgs.forEach(s -> {
            String t = player != null ? utils.lang.translate(player, s) : s;
            loreBuilder.addLine(utils.chat(t, player));
        });
        return this;
    }

    public ItemBuilder lore(String msg) {
        return lore(new String[]{msg});
    }

    public ItemBuilder loreAdd(String... msg) {
        return lore(msg);
    }

    public ItemBuilder loreAddIf(boolean bool, String... msgs) {
        if (bool) lore(msgs);
        return this;
    }

    public ItemBuilder name(String str) {
        String t = player != null ? utils.lang.translate(player, str) : str;
        Component comp = utils.chat(t, player);
        item.setData(DataComponentTypes.CUSTOM_NAME, comp);
        return this;
    }

    public ItemBuilder unbreakable() {
        item.setData(DataComponentTypes.UNBREAKABLE);
        return this;
    }

    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> metaConsumer) {
        if (metaClass.isInstance(this.meta)) {
            metaConsumer.accept(metaClass.cast(this.meta));
        }
        return this;
    }

    public ItemBuilder owner(OfflinePlayer p) {
        return meta(SkullMeta.class, m -> m.setOwningPlayer(p));
    }

    public ItemBuilder color(Color color) {
        return meta(LeatherArmorMeta.class, m -> m.setColor(color));
    }

    public ItemBuilder potColor(Color color) {
        return meta(PotionMeta.class, m -> m.setColor(color));
    }

    public ItemBuilder potEffect(PotionEffect pe) {
        potionBuilder.addCustomEffect(pe);
        return this;
    }

    public ItemBuilder potionType(PotionType type) {
        potionBuilder.potion(type);
        return this;
    }

    public ItemBuilder setBannerMeta(BannerMeta bannerMeta) {
        return meta(BannerMeta.class, m -> m.getPatterns().clear());
    }

    public ItemBuilder addPattern(Pattern pattern) {
        return meta(BannerMeta.class, m -> m.addPattern(pattern));
    }

    public ItemBuilder addItem(ItemStack i) {
        meta(BlockStateMeta.class, m -> {
            Chest chest = (Chest) m.getBlockState();
            chest.getInventory().addItem(i);
            m.setBlockState(chest);
        });
        return this;
    }

    public ItemBuilder addFireworkEffect(FireworkEffect.Type t, Color c) {
        fireworkBuilder.addEffect(FireworkEffect.builder().with(t).withColor(c).build());
        return this;
    }

    public ItemBuilder addFireworkEffect(FireworkEffect.Type t, List<Color> c, int amount) {
        FireworkEffect.Builder b = FireworkEffect.builder().with(t).withColor(c);
        for (int i = 0; i < amount; i++) b.withFade(c);
        fireworkBuilder.addEffect(b.build());
        return this;
    }

    public ItemBuilder addFireworkEffect(FireworkEffect.Type t, Color c, int amount) {
        FireworkEffect.Builder b = FireworkEffect.builder().with(t).withColor(c);
        for (int i = 0; i < amount; i++) b.withFade(c);
        fireworkBuilder.addEffect(b.build());
        return this;
    }

    public ItemBuilder setFireworkPower(int i) {
        fireworkBuilder.flightDuration(i);
        return this;
    }

    /**
     * Método helper: Define el item como comestible con propiedades custom.
     */
    public ItemBuilder edible(int hunger, float saturation, boolean always) {
        foodBuilder.nutrition(hunger).saturation(saturation);
        foodBuilder.canAlwaysEat(always);
        return this;
    }

    /**
     * Método helper: Ajusta velocidad de minería/ataque.
     */
    public ItemBuilder breakSpeed(double speed) {
        // Crea un NamespacedKey único para este modificador
        NamespacedKey key = new NamespacedKey(utils.plugin, "break_speed");
        AttributeModifier mod = new AttributeModifier(key, speed, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.ANY);
        attrBuilder.addModifier(Attribute.ATTACK_SPEED, mod);
        return this;
    }

    /**
     * Método helper: Marca el ítem para GUI (por ejemplo, no intercambiable).
     */
    public ItemBuilder guiItem() {
        enchant(); // simple marcador visual
        return this;
    }

    /**
     * Método helper: Bloquea el slot (solo lógica interna de plugin).
     */
    public ItemBuilder slotLock() {
        // Podrías agregar un PDC flag aquí para tu plugin
        return this;
    }

    /**
     * Aplica todos los builders de DataComponent y guarda meta residual.
     */
    public ItemStack build() {
        item.setData(DataComponentTypes.LORE, loreBuilder.build());
        item.setData(DataComponentTypes.ENCHANTMENTS, enchantBuilder.build());
        item.setData(DataComponentTypes.ATTRIBUTE_MODIFIERS, attrBuilder.build());
        item.setData(DataComponentTypes.POTION_CONTENTS, potionBuilder.build());
        item.setData(DataComponentTypes.FIREWORKS, fireworkBuilder.build());
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipBuilder.build());
        item.setData(DataComponentTypes.FOOD, foodBuilder.build());
        item.setItemMeta(this.meta);
        return this.item;
    }
}
