package de.winniepat.SMPPlugin.polls;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PollResultGUI {

    private final PollManager manager;

    public PollResultGUI(PollManager manager) {
        this.manager = manager;
    }

    public void openResultsGUI(Player player) {
        int size = 9 * 3;
        Inventory inv = Bukkit.createInventory(null, size, "§6Poll-Results");

        int slot = 0;

        Poll current = manager.getCurrentPoll();
        if (current != null) {
            ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§aActive: " + current.getQuestion());
            meta.setLore(current.getResults().entrySet().stream()
                    .map(e -> "§7" + e.getKey() + ": §e" + e.getValue())
                    .toList());
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        List<Poll> pastPolls = manager.getExpiredPolls();
        for (Poll past : pastPolls) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§7Ended: " + past.getQuestion());
            meta.setLore(past.getResults().entrySet().stream()
                    .map(e -> "§7" + e.getKey() + ": §e" + e.getValue())
                    .toList());
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
            if (slot >= size) break;
        }

        player.openInventory(inv);
    }
}
