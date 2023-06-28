package fr.palmus.evoplugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryManager implements Listener {

    // TODO: Open specific inventory for example mining inv = what blocks player can mine
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase("§eInformations")) {
            e.setCancelled(true);
            Player pl = (Player) e.getWhoClicked();

            if (e.getCurrentItem() == null) {
                return;
            }

            if (e.getCurrentItem().getType() == Material.BARRIER) {
                e.setCancelled(true);
                e.getWhoClicked().closeInventory();
            }

            if (e.getCurrentItem().getType() == Material.EXPERIENCE_BOTTLE) {
                pl.sendMessage("§aSOON");
                return;
            }

            if (e.getCurrentItem().getType() == Material.IRON_PICKAXE) {
                pl.sendMessage("§aSOON");
                return;
            }

            if (e.getCurrentItem().getType() == Material.CRAFTING_TABLE) {
                pl.sendMessage("§aSOON");
                return;
            }

            if (e.getCurrentItem().getType() == Material.WOODEN_SWORD) {
                pl.sendMessage("§aSOON");
            }
        }
    }
}
