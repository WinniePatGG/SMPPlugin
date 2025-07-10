package de.winniepat.SMPPlugin.bloodmoon;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BloodmoonListener implements Listener {

    private final BloodmoonManager manager;
    private final JavaPlugin plugin;
    private final Random random = new Random();

    public BloodmoonListener(BloodmoonManager manager, JavaPlugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBedEnter(PlayerBedEnterEvent event) {
        if (manager.isActive()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot sleep during a Bloodmoon!");
        }
    }

    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        if (!manager.isActive()) return;

        LivingEntity entity = event.getEntity();

        if (entity instanceof Zombie z && z.isBaby()) return;

        if (random.nextInt(100) < 20) {
            spawnBoss(entity);
            return;
        }

        if (entity instanceof Creeper creeper) {
            creeper.setExplosionRadius(6);
            if (random.nextInt(100) < 30) {
                creeper.setPowered(true);
            }
        }

        if (entity.getAttribute(Attribute.MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                    entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue() * 1.5
            );
            entity.setHealth(entity.getAttribute(Attribute.MAX_HEALTH).getBaseValue());
        }

        if (entity.getAttribute(Attribute.ATTACK_DAMAGE) != null) {
            entity.getAttribute(Attribute.ATTACK_DAMAGE).setBaseValue(
                    entity.getAttribute(Attribute.ATTACK_DAMAGE).getBaseValue() + 2
            );
        }

        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(
                    entity.getAttribute(Attribute.MOVEMENT_SPEED).getBaseValue() * 1.2
            );
        }
    }

    private void spawnBoss(LivingEntity entity) {
        if (!(entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Husk || entity instanceof Drowned))
            return;

        entity.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "bloodmoon_boss"),
                PersistentDataType.BYTE,
                (byte) 1
        );

        entity.setCustomName(ChatColor.DARK_RED + "Blood Knight");
        entity.setCustomNameVisible(true);

        if (entity instanceof Zombie || entity instanceof Husk || entity instanceof Drowned) {
            entity.getEquipment().setHelmet(createItem(Material.DIAMOND_HELMET, "Blood Helmet"));
            entity.getEquipment().setChestplate(createItem(Material.DIAMOND_CHESTPLATE, "Blood Chestplate"));
            entity.getEquipment().setLeggings(createItem(Material.DIAMOND_LEGGINGS, "Blood Leggings"));
            entity.getEquipment().setBoots(createItem(Material.DIAMOND_BOOTS, "Blood Boots"));
            entity.getEquipment().setItemInMainHand(createItem(Material.DIAMOND_SWORD, "Blood Blade"));
        } else if (entity instanceof Skeleton) {
            entity.getEquipment().setHelmet(createItem(Material.DIAMOND_HELMET, "Blood Helmet"));
            entity.getEquipment().setChestplate(createItem(Material.NETHERITE_CHESTPLATE, "Blood Chestplate"));
            entity.getEquipment().setItemInMainHand(createItem(Material.BOW, "Blood Bow"));
        }

        entity.getEquipment().setHelmetDropChance(0.001f);
        entity.getEquipment().setChestplateDropChance(0.001f);
        entity.getEquipment().setLeggingsDropChance(0.001f);
        entity.getEquipment().setBootsDropChance(0.001f);
        entity.getEquipment().setItemInMainHandDropChance(0.001f);

        if (entity.getAttribute(Attribute.MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(80);
            entity.setHealth(80);
        }
        if (entity.getAttribute(Attribute.MAX_HEALTH) != null) {
            entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(12);
        }
        if (entity.getAttribute(Attribute.MOVEMENT_SPEED) != null) {
            entity.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.35);
        }

        startParticleEffect(entity);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + name);
            meta.addEnchant(Enchantment.PROTECTION, 2, true);
            meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            meta.addEnchant(Enchantment.SHARPNESS, 2, true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private void startParticleEffect(LivingEntity entity) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (entity == null || entity.isDead() || !entity.isValid()) {
                    cancel();
                    return;
                }

                entity.getWorld().spawnParticle(
                        Particle.DUST,
                        entity.getLocation().add(0, 1, 0),
                        8,
                        0.3, 0.5, 0.3,
                        0,
                        new Particle.DustOptions(Color.RED, 1.5f)
                );

                if ((ticks += 5) > 20 * 300) cancel();
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }
}
