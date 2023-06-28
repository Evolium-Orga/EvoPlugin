package fr.palmus.evoplugin.economy;

import fr.palmus.evoplugin.listeners.custom.PlayerMoneyChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerEconomy {

    Player player;

    Economy econ;

    int money;

    int bank;

    public PlayerEconomy(Player pl, Economy economy) {
        this.player = pl;
        this.econ = economy;
        this.bank = econ.main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".bank");
        this.money = econ.main.getPeriodConfigurationFile().getInt(player.getUniqueId() + ".money");
    }

    public void addMoney(int money){
        this.money += money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.MONEY, econ.main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void subtractMoney(int money){
        this.money -= money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.MONEY, econ.main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void setMoney(int money){
        this.money = money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.MONEY, econ.main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void addBank(int money){
        this.money += money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.BANK, econ.main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void subtractBank(int money){
        this.money -= money;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.BANK, econ.main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void setBank(int bank) {
        this.bank = bank;
        PlayerMoneyChangeEvent event = new PlayerMoneyChangeEvent(player, money, bank, TransferType.BANK, econ.main);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void saveMoney(){
        econ.main.getPeriodConfigurationFile().set(player.getDisplayName() + ".money", money);
    }

    public Player getPlayer() {
        return player;
    }

    public int getMoney() {
        return money;
    }

    public int getBank() {
        return bank;
    }

    public enum TransferType{
        BANK, MONEY
    }
}
