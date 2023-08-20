package net.evolium.evoshop.event;

import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class PnjManager implements Listener {

    @EventHandler
    public void PnjManager(NPCRightClickEvent event) {
        System.out.println("Called");
        Player pl = event.getClicker();
        Inventory inventory = Bukkit.createInventory(null, 27, event.getNPC().getName());
        pl.openInventory(inventory);

    }
}
