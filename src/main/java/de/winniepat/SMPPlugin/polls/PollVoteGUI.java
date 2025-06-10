package de.winniepat.SMPPlugin.polls;

import com.google.gson.annotations.Since;
import de.winniepat.SMPPlugin.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public class PollVoteGUI implements Listener {

    private final PollManager manager;
    private final SMPPlugin smpPlugin;

    public PollVoteGUI(PollManager manager, SMPPlugin smpPlugin) {
        this.smpPlugin = smpPlugin;
        this.manager = manager;
    }

    public void openVoteGUI(Player player) {
        if (!manager.hasActivePoll()) {
            player.sendMessage("§cNo active Poll.");
            return;
        }

        Poll poll = manager.getCurrentPoll();
        Inventory inv = Bukkit.createInventory(null, 9, "§6Vote: " + poll.getQuestion());

        int i = 0;
        for (String option : poll.getOptions()) {
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b" + option);
            meta.setLore(List.of("§7Remaining Time: §e" + poll.getRemainingTimeSeconds() + " Seconds"));
            item.setItemMeta(meta);
            inv.setItem(i++, item);
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTitle().startsWith("§6Vote:")) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            String vote = clicked.getItemMeta().getDisplayName().replace("§b", "");
            Poll poll = manager.getCurrentPoll();
            if (poll != null && !poll.hasVoted(player.getUniqueId())) {
                poll.vote(player.getUniqueId(), vote);
                player.sendMessage("§aYour vote for §e" + vote + " §awas registered.");
                player.closeInventory();
            } else {
                player.sendMessage(smpPlugin.getMessage("command.error.no_permission", Map.of("player", player.getName())));
            }
        }
    }
}