package de.winniepat.SMPPlugin.secrets;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class SecretButtonHandler implements Listener {
    private static final String WORLD = "world";
    private static final int A_X = -652, A_Y = 63, A_Z = -837;
    private static final int B_X = 0, B_Y = 0, B_Z = 0;


    @EventHandler
    public void onButtonPress(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (block.getType() != Material.STONE_BUTTON) return;

        String world = block.getWorld().getName();
        int x = block.getX(), y = block.getY(), z = block.getZ();

        if (!world.equals(WORLD)) return;

        if (x == A_X && y == A_Y && z == A_Z) {
            if (SecretsSQLite.hasClaimedSecret(uuid, "secretA")) {
                player.sendMessage("§cYou've already claimed this Secret!");
                return;
            }

            ItemStack secret1 = new ItemStack(Material.SPORE_BLOSSOM);
            ItemMeta secret1meta = secret1.getItemMeta();
            secret1meta.setItemName("Prinzessinnen-Enzian");
            secret1meta.addEnchant(Enchantment.SMITE, 5, false);
            secret1meta.addEnchant(Enchantment.LOOTING, 3, false);
            secret1meta.addEnchant(Enchantment.UNBREAKING, 3, false);
            secret1meta.addEnchant(Enchantment.MENDING, 1, false);
            secret1meta.setRarity(ItemRarity.EPIC);
            secret1.setItemMeta(secret1meta);

            player.getInventory().addItem(new ItemStack(secret1));
            SecretsSQLite.markSecretClaimed(uuid, "secretA");
            player.sendMessage("§aYou claimed Secret myfcknmelody's secret!");
            return;
        }

        if (x == B_X && y == B_Y && z == B_Z) {
            if (SecretsSQLite.hasClaimedSecret(uuid, "secretB")) {
                player.sendMessage("§cYou've already claimed Secret B!");
                return;
            }
            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            SecretsSQLite.markSecretClaimed(uuid, "secretB");
            player.sendMessage("§bYou claimed Secret B!");
        }
    }
}
