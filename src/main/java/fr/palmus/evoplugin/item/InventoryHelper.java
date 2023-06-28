package fr.palmus.evoplugin.item;

import fr.palmus.evoplugin.api.ItemBuilder;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Deprecated
public class InventoryHelper {

    @Deprecated
    public void setDefaultMaterial(Inventory inv, ItemStack defaultItem) {

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) inv.setItem(i, defaultItem);
            else if (inv.getItem(i).getType() == Material.AIR) inv.setItem(i, defaultItem);
        }
    }

    @Deprecated
    public ItemStack getHead(Player player) {
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

    @Deprecated
    public void initInfoInventory(Inventory inv, Player pl) {

        setDefaultMaterial(inv, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack());

        for (int i = 0; i < 9; i++) {
            inv.setItem(i, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).toItemStack());
        }

        for (int i = 45 - 9; i < 45; i++) {
            inv.setItem(i, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).toItemStack());
        }

        EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

        inv.setItem(4, getHead(pl));
        inv.setItem(inv.getSize() - 5, new ItemBuilder(Material.BARRIER).setName("§cFermer").addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack());
        inv.setItem(22, new ItemBuilder(Material.EXPERIENCE_BOTTLE, 1).addEnchant(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addItemFlag(ItemFlag.HIDE_ENCHANTS).setName("§6Comment exp ?").setLore(new ArrayList<>(Arrays.asList("", "§eCliquez pour voir vos différents", "§eMoyens de gagner de l'éxperience", "", "§6Avancement actuel:", "", evoPlayer.getProgressBar(), "§6" + evoPlayer.getProgressPercent() + "§e%", ""))).toItemStack());
    }
}
