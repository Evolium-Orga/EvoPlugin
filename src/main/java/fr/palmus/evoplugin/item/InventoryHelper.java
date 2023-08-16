package fr.palmus.evoplugin.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;

@Deprecated
public class InventoryHelper {

    @Deprecated
    public static void setDefaultMaterial(Inventory inv, ItemStack defaultItem) {

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, defaultItem);
            else if (inv.getItem(i).getType() == Material.AIR) inv.setItem(i, defaultItem);
        }
    }

    @Deprecated
    public static ItemStack getHead(Player player) {
        boolean isNewVersion = Arrays.stream(Material.values()).map(Material::name).toList().contains("PLAYER_HEAD");
        Material type = Material.matchMaterial(isNewVersion ? "PLAYER_HEAD" : "SKULL_ITEM");
        ItemStack item = new ItemStack(type, 1);
        if (!isNewVersion) {
            item.setDurability((short) 3);
        }
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(player);
        skull.setDisplayName("§2§nInformations");
        ArrayList<String> lore = new ArrayList<>(Arrays.asList("", "§aBienvenue §2" + player.getDisplayName() + " §asur", "§ala page d'informations d'evolium", "§avous pouvez récupérer des informations", "§aà propos de votre période", ""));
        skull.setLore(lore);
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);

        return item;
    }
}
