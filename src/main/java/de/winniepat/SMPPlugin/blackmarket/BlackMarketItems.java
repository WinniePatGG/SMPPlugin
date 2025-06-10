package de.winniepat.SMPPlugin.blackmarket;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlackMarketItems {

    public static List<BlackMarketOffer> getRandomOffers(JavaPlugin plugin, int count) {
        List<BlackMarketOffer> all = new ArrayList<>();
        all.add(bloodBlade(plugin));
        all.add(xpHood(plugin));
        all.add(vampireAxe(plugin));
        all.add(swiftBoots(plugin));
        all.add(witherBow(plugin));
        all.add(unstableBlade(plugin));
        all.add(lightningAxe(plugin));
        all.add(bounceBow(plugin));
        all.add(levitateChest(plugin));
        Collections.shuffle(all);
        return all.subList(0, Math.min(count, all.size()));
    }

    private static BlackMarketOffer bloodBlade(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Blood Blade");
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        meta.setLore(List.of("§cDeals devastating damage.", "§7But harms the wielder on hit."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "self_damage");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.NETHERITE_INGOT, 3),
                new ItemStack(Material.ROTTEN_FLESH, 64)
        ), "self_damage");
    }

    private static BlackMarketOffer xpHood(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Hood of Hunger");
        meta.addEnchant(Enchantment.THORNS, 3, true);
        meta.setLore(List.of("§eBoosts XP gain.", "§7But constantly drains hunger."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "hunger_drain");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.EXPERIENCE_BOTTLE, 32)
        ), "hunger_drain");
    }

    private static BlackMarketOffer vampireAxe(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Vampire Axe");
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.setLore(List.of("§cHeals on hit.", "§7Burns in sunlight."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "burn_in_day");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.EMERALD, 20)
        ), "burn_in_day");
    }

    private static BlackMarketOffer swiftBoots(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Boots of Swiftness");
        meta.addEnchant(Enchantment.FEATHER_FALLING, 4, true);
        meta.setLore(List.of("§bIncreases speed.", "§7Causes knockback vulnerability."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "more_knockback");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.EMERALD, 15)
        ), "more_knockback");
    }

    private static BlackMarketOffer witherBow(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "Withering Bow");
        meta.addEnchant(Enchantment.POWER, 5, true);
        meta.setLore(List.of("§7Applies wither.", "§cHurts allies nearby."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "hurt_nearby_allies");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.EMERALD, 25)
        ), "hurt_nearby_allies");
    }

    private static BlackMarketOffer unstableBlade(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Blade of Instability");
        meta.addEnchant(Enchantment.SHARPNESS, 6, true);
        meta.setLore(List.of("§5Massive power.", "§cMight explode on hit."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "unstable");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.NETHERITE_INGOT, 2),
                new ItemStack(Material.TNT, 16)
        ), "unstable");
    }

    private static BlackMarketOffer lightningAxe(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Lightning Touch Axe");
        meta.addEnchant(Enchantment.SHARPNESS, 5, true);
        meta.setLore(List.of("§eElectrocutes enemies.", "§7Risk of chain lightning."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "lightning_touch");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.GOLD_BLOCK, 4)
        ), "lightning_touch");
    }

    private static BlackMarketOffer bounceBow(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.BOW);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + "Rebounding Bow");
        meta.addEnchant(Enchantment.POWER, 4, true);
        meta.addEnchant(Enchantment.PUNCH, 1, true);
        meta.setLore(List.of("§7Sometimes bounces arrows back... on YOU."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "arrow_bounceback");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.SPECTRAL_ARROW, 64)
        ), "arrow_bounceback");
    }

    private static BlackMarketOffer levitateChest(JavaPlugin plugin) {
        ItemStack item = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Wings of the Void");
        meta.addEnchant(Enchantment.PROTECTION, 4, true);
        meta.setLore(List.of("§3Grants levitation on crit.", "§8Leaves you floating."));
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "blackmarket_catch"),
                PersistentDataType.STRING, "levitate_on_crit");
        item.setItemMeta(meta);
        return new BlackMarketOffer(item, List.of(
                new ItemStack(Material.ENDER_PEARL, 16),
                new ItemStack(Material.ELYTRA, 1)
        ), "levitate_on_crit");
    }
}
