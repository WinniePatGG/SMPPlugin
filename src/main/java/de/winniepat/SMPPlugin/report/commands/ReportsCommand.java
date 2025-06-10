package de.winniepat.SMPPlugin.report.commands;

import de.winniepat.SMPPlugin.SMPPlugin;
import de.winniepat.SMPPlugin.report.ReportActionGUI;
import de.winniepat.SMPPlugin.report.ReportDatabase;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ReportsCommand implements CommandExecutor, Listener {

    private final ReportDatabase db;
    private final SMPPlugin smpPlugin;
    private final JavaPlugin plugin;
    private final Map<UUID, Integer> pageMap = new HashMap<>();
    private final int REPORTS_PER_PAGE = 28;

    public ReportsCommand(JavaPlugin plugin, ReportDatabase db, SMPPlugin smpPlugin) {
        this.plugin = plugin;
        this.smpPlugin = smpPlugin;
        this.db = db;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.hasPermission("smputils.reports")) {
            sender.sendMessage(smpPlugin.getMessage("command.error.no_permission", Map.of("player", sender.getName())));
            return true;
        }

        pageMap.put(p.getUniqueId(), 0);
        openReportPage(p, 0);
        return true;
    }

    private void openReportPage(Player p, int page) {
        List<Map<String, String>> reports = db.getAllReports();
        int totalPages = (int) Math.ceil((double) reports.size() / REPORTS_PER_PAGE);
        if (page < 0 || page >= totalPages) page = 0;

        Inventory inv = Bukkit.createInventory(p, 54, "§cAll Reports - Page " + (page + 1));

        int start = page * REPORTS_PER_PAGE;
        int end = Math.min(start + REPORTS_PER_PAGE, reports.size());

        for (int i = 10, index = start; index < end; index++, i++) {
            if ((i + 1) % 9 == 0) i += 2;
            Map<String, String> report = reports.get(index);
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            meta.setDisplayName("§eVictim: §f" + report.get("reported"));
            meta.setLore(List.of(
                    "§7By: §a" + report.get("reporter"),
                    "§7Reason: §f" + report.get("reason"),
                    "§8ID: " + report.get("id")
            ));
            paper.setItemMeta(meta);
            inv.setItem(i, paper);
        }

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.setDisplayName("§a⬅ Back");
            prev.setItemMeta(meta);
            inv.setItem(45, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName("§a➡ Next Page");
            next.setItemMeta(meta);
            inv.setItem(53, next);
        }

        p.openInventory(inv);
        pageMap.put(p.getUniqueId(), page);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();
        if (!title.startsWith("§cAll Reports - Page")) return;

        e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta() || item.getType() == Material.AIR) return;

        UUID uuid = p.getUniqueId();
        int currentPage = pageMap.getOrDefault(uuid, 0);

        String name = item.getItemMeta().getDisplayName();
        if (name.equals("§a➡ Next Page")) {
            openReportPage(p, currentPage + 1);
            return;
        }
        if (name.equals("§a⬅ Back")) {
            openReportPage(p, currentPage - 1);
            return;
        }

        String idLine = item.getItemMeta().getLore().stream()
                .filter(l -> l.startsWith("§8ID: "))
                .findFirst().orElse(null);
        if (idLine == null) return;

        String reportId = idLine.replace("§8ID: ", "");
        Bukkit.getScheduler().runTask(plugin, () -> ReportActionGUI.open(plugin, db, p, reportId, smpPlugin));
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getView().getTitle().startsWith("§cAll Reports - Page")) {
            e.setCancelled(true);
        }
    }

}
