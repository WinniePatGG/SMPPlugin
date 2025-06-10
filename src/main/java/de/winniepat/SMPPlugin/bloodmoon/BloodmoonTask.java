package de.winniepat.SMPPlugin.bloodmoon;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class BloodmoonTask {

    private final JavaPlugin plugin;
    private final BloodmoonManager manager;

    private boolean checkedThisNight = false;

    public BloodmoonTask(JavaPlugin plugin, BloodmoonManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorlds().get(0);
                long time = world.getTime();
                if (time >= 13000 && time <= 13100) {
                    if (!checkedThisNight) {
                        checkedThisNight = true;

                        if (new Random().nextInt(100) < 5) {
                            manager.startBloodmoon();
                        }
                    }
                }

                if (time >= 0 && time < 1000) {
                    if (manager.isActive()) {
                        manager.endBloodmoon();
                    }
                    checkedThisNight = false;
                }
            }
        }.runTaskTimer(plugin, 0L, 100L);
    }
}
