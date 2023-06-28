package fr.palmus.evoplugin.item;

import fr.palmus.evoplugin.api.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Deprecated
public class CustomItem {

    public static ItemStack fiber = new ItemBuilder(Material.STRING).setName("§aFIBRE").setLore(Arrays.asList("§7-------------", "§eRareté: ⭐", "§eType: Utils", "§7-------------", "§aPériode: Préhistoire I", "§aUtilité: Craft Ficelle", "§7-------------")).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack();
    public static ItemStack massue = new ItemBuilder(Material.STONE_AXE).setName("§eMASSUE").setLore(Arrays.asList("§7-------------", "§eRareté: ⭐⭐", "§eType: Armes", "§cDamage: +20", "§7-------------", "§aPériode: Préhistoire I", "§aUtilité: Dégats-Boost", "§7-------------")).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).toItemStack();
    public static ItemStack evo_hoe = new ItemBuilder(Material.WOODEN_HOE, 1).setName("§a§lEvoHoue").setLore(Arrays.asList("§7-------------", "§eRareté: ⭐⭐⭐⭐", "§eType: §a§lEvoItems", "§7-------------", "§aPériode: Préhistoire", "§aUtilité: Tools", "§7-------------")).addItemFlag(ItemFlag.HIDE_ATTRIBUTES).addItemFlag(ItemFlag.HIDE_UNBREAKABLE).setDurability((short) 1).setUnbreakable().toItemStack();

}
