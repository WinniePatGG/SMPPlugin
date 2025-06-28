package de.winniepat.SMPPlugin.bloodmoon;

import de.winniepat.SMPPlugin.blackmarket.BlackMarketManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BloodmoonManager {
    private final JavaPlugin plugin;
    private boolean active = false;
    private BossBar bloodmoonBar;
    private int durationTicks = 20 * 60 * 8;
    private int ticksElapsed = 0;
    private BukkitRunnable task;

    public BloodmoonManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startBloodmoon() {
        if (active) return;
        active = true;
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "☠ The Bloodmoon has risen... Be wary!");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.RED + "Bloodmoon", ChatColor.DARK_RED + "Mobs are stronger tonight!", 10, 100, 20);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60 * 8, 1, false, false, false));
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);

            player.spawnParticle(Particle.DUST, player.getLocation().add(0, 10, 0), 100,5, 5, 5, 0, new Particle.DustOptions(Color.RED, 2));
        }

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (active) {
                    player.spawnParticle(Particle.DUST, player.getLocation().add(
                                    Math.random() * 10 - 5, Math.random() * 5, Math.random() * 10 - 5),
                            5, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 1));
                }
            }
        }, 0L, 1L);

        bloodmoonBar = Bukkit.createBossBar("§4🌕 Bloodmoon Night", BarColor.RED, BarStyle.SEGMENTED_10);
        bloodmoonBar.setProgress(1.0);

        for (Player player : Bukkit.getOnlinePlayers()) {
            bloodmoonBar.addPlayer(player);
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                ticksElapsed += 20;
                double progress = Math.max(0, 1.0 - (double) ticksElapsed / durationTicks);
                bloodmoonBar.setProgress(progress);

                if (ticksElapsed >= durationTicks) {
                    endBloodmoon();
                }
            }
        };
        task.runTaskTimer(plugin, 20L, 20L);

    }

    public void endBloodmoon() {
        if (!active) return;
        active = false;

        Bukkit.broadcastMessage(ChatColor.GRAY + "☀ The Bloodmoon has ended. It's safe again.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.YELLOW + "☀", ChatColor.GREEN + "Bloodmoon ended!", 10, 100, 20);
        }
        NamespacedKey key = new NamespacedKey(plugin, "bloodmoon_boss");

        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (entity.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
                    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 0.5f);

                    entity.remove();
                }
            }
        }
        if (new Random().nextInt(100) < 5) {
            new BlackMarketManager(plugin).spawnMarket();
        }
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (bloodmoonBar != null) {
            bloodmoonBar.removeAll();
            bloodmoonBar = null;
        }

        ticksElapsed = 0;
    }

    public boolean isActive() {
        return active;
    }
}
