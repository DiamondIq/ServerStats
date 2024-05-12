package me.diamond.serverstats.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        Inventory inventory = p.getInventory();
        p.getInventory().clear();
        ItemStack map = new ItemStack(Material.MAP);
        ItemStack returnItem = new ItemStack(Material.BARRIER);
        p.getInventory().setItem(p.getInventory().getSize() - 9, map);
        p.getInventory().setItem(p.getInventory().getSize(), returnItem);
        for (int i = 9; i < 45; i++) {
            if (p.getInventory().getItem(i) == null) {
                ItemStack fillerglass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
                ItemMeta fillerglass_meta = fillerglass.getItemMeta();
                fillerglass_meta.setDisplayName(" ");
                fillerglass.setItemMeta(fillerglass_meta);
                p.getInventory().setItem(i, fillerglass);
            }
        }
        return false;
    }
}
