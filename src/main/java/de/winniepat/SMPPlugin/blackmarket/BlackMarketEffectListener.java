package de.winniepat.SMPPlugin.blackmarket;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BlackMarketEffectListener implements Listener {

    private final JavaPlugin plugin;
    private final Random random = new Random();
    private final NamespacedKey catchKey;

    public BlackMarketEffectListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.catchKey = new NamespacedKey(plugin, "blackmarket_catch");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    checkCatch(p, EquipmentSlot.HEAD, "hunger_drain", () -> {
                        if (p.getFoodLevel() > 1) p.setFoodLevel(p.getFoodLevel() - 1);
                    });
                    checkCatch(p, EquipmentSlot.HAND, "burn_in_day", () -> {
                        if (isDaylight(p)) p.setFireTicks(40);
                    });
                    checkCatch(p, EquipmentSlot.FEET, "random_blindness", () -> {
                        if (random.nextInt(100) < 5)
                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
                    });
                }
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player p)) return;
        ItemStack item = p.getInventory().getItemInMainHand();
        String catchId = getCatch(item);

        if (catchId == null) return;

        switch (catchId) {
            case "self_damage" -> {
                p.damage(2);
                p.sendMessage("§cThe weapon wounds you!");
            }
            case "unstable" -> {
                if (random.nextInt(100) < 10)
                    p.getWorld().createExplosion(p.getLocation(), 2f, false, false, p);
            }
            case "lightning_touch" -> {
                Entity target = event.getEntity();
                target.getWorld().strikeLightningEffect(target.getLocation());
            }
            case "hurt_nearby_allies" -> {
                for (Player nearby : p.getWorld().getPlayers()) {
                    if (!nearby.equals(p) && nearby.getLocation().distance(p.getLocation()) < 5)
                        nearby.damage(2);
                }
            }
            case "levitate_on_crit" -> {
                if (p.getAttackCooldown() > 0.9f)
                    p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 1));
            }
            case "arrow_bounceback" -> {
                if (p.getInventory().getItemInMainHand().getType().toString().endsWith("BOW") && random.nextInt(100) < 20) {
                    p.damage(2);
                    p.sendMessage(ChatColor.DARK_RED + "Your cursed arrow rebounds!");
                }
            }
        }
    }

    @EventHandler
    public void onHitTaken(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        checkCatch(p, EquipmentSlot.CHEST, "slowness_when_hit", () -> {
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60, 2));
        });
        checkCatch(p, EquipmentSlot.FEET, "more_knockback", () -> {
            p.setVelocity(p.getVelocity().multiply(2));
        });
    }

    @EventHandler
    public void onXpGain(PlayerExpChangeEvent event) {
        Player p = event.getPlayer();
        checkCatch(p, EquipmentSlot.HEAD, "xp_boost", () -> {
            event.setAmount(event.getAmount() * 2);
        });
    }

    private void checkCatch(Player p, EquipmentSlot slot, String catchId, Runnable action) {
        ItemStack item = p.getInventory().getItem(slot);
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.getPersistentDataContainer().has(catchKey, PersistentDataType.STRING)) {
                String tag = meta.getPersistentDataContainer().get(catchKey, PersistentDataType.STRING);
                if (tag != null && tag.equals(catchId)) {
                    action.run();
                }
            }
        }
    }

    private String getCatch(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(catchKey, PersistentDataType.STRING)) return null;
        return meta.getPersistentDataContainer().get(catchKey, PersistentDataType.STRING);
    }

    private boolean isDaylight(Player player) {
        long time = player.getWorld().getTime();
        return time < 12300 || time > 23850;
    }
}
