package de.winniepat.SMPPlugin.blackmarket;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BlackMarketManager {

    private final JavaPlugin plugin;

    public BlackMarketManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnMarket() {
        Location spawnLoc = getRandomSpawnLocation();
        Villager trader = spawnBlackMarketVillager(spawnLoc);

        Bukkit.broadcastMessage(ChatColor.GRAY + "☠ A mysterious trader has appeared nearby...");

        String coords = String.format("X: %d, Y: %d, Z: %d",
                spawnLoc.getBlockX(), spawnLoc.getBlockY(), spawnLoc.getBlockZ());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(spawnLoc) <= 200) {
                player.sendMessage(ChatColor.GRAY + "§7The trader lurks around §f" + coords);
            }
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                if (trader == null || trader.isDead() || !trader.isValid()) {
                    cancel();
                    return;
                }

                trader.getWorld().spawnParticle(
                        Particle.ENCHANT,
                        trader.getLocation().add(0, 2, 0),
                        20,
                        0.5, 0.5, 0.5,
                        0.1
                );
            }
        }.runTaskTimer(plugin, 0L, 20L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (trader != null && trader.isValid()) {
                    trader.remove();
                    Bukkit.broadcastMessage(ChatColor.GRAY + "The Black Market fades into the shadows...");
                }
            }
        }.runTaskLater(plugin, 20 * 60 * 5);
    }

    private Villager spawnBlackMarketVillager(Location loc) {
        Villager v = loc.getWorld().spawn(loc, Villager.class);
        v.setAI(false);
        v.setInvulnerable(true);
        v.setCustomName(ChatColor.DARK_PURPLE + "Black Market");
        v.setCustomNameVisible(true);
        v.setProfession(Villager.Profession.NITWIT);
        v.setVillagerLevel(5);
        v.setVillagerExperience(1);
        v.setCanPickupItems(false);

        BlackMarketTradeHandler.applyTrades(v, plugin);

        return v;
    }

    private Location getRandomSpawnLocation() {
        World world = Bukkit.getWorlds().get(0);
        Random random = new Random();

        int baseX = -635;
        int baseZ = -837;
        int radius = 100;

        int x = baseX + random.nextInt(radius * 2 + 1) - radius;
        int z = baseZ + random.nextInt(radius * 2 + 1) - radius;
        int y = world.getHighestBlockYAt(x, z) + 1;

        return new Location(world, x + 0.5, y, z + 0.5);
    }
}
