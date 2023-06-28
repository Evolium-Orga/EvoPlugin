package fr.palmus.evoplugin.fastboard;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.player.EvoPlayer;
import net.luckperms.api.model.user.User;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * see <a href="https://github.com/MrMicky-FR/FastBoard">...</a>
 */
public class EvoScoreboard {

    private static final Map<UUID, FastBoard> playerToScoreboardHashmap = new HashMap<>();

    private static final EvoPlugin main = EvoPlugin.getInstance();

    /**
     * Used to update the scoreboard of a Player
     *
     * @param pl Player of which the scoreboard should be updated
     */
    public static void updateScoreboard(Player pl) {
        FastBoard board = playerToScoreboardHashmap.get(pl.getUniqueId());
        EvoPlayer evoPlayer = EvoPlayer.getInstanceOf(pl);

        int rank = evoPlayer.getRank();
        User user = EvoPlugin.getInstance().LPapi.getPlayerAdapter(Player.class).getUser(pl);
        String prefix;
        try {
            prefix = user.getCachedData().getMetaData().getPrefix().replace("&", "§").replace('"', ' ');
        } catch (NullPointerException e) {
            prefix = "null";
        }
        String period = main.getPeriodCaster().getPeriodToString(evoPlayer.getPlayerPeriod());
        int money = main.getEconomyModule().getPlayerEcon(pl).getMoney();
        int bank = main.getEconomyModule().getPlayerEcon(pl).getBank();
        board.updateTitle(ChatColor.WHITE + "§lPLAY.EVOLIUM.FR ☀");
        board.updateLines(
                "§7",
                "§7| §r§lMa Période",
                "  §7Période actuelle: §2" + period + " " + main.getPeriodCaster().getRankToString(rank),
                "  §7Points d'Expérience: §2" + main.getPeriodCaster().formatIntegerToReadableString(evoPlayer.getExp()) + "/" + main.getPeriodCaster().getFormattedPeriodExpLimit(rank),
                "  §7Objectifs: §2",
                " §6",
                "§7| §r§lMon Profil",
                "  §7Pseudo: §6" + pl.getDisplayName(),
                "  §7Grade: §6" + prefix,
                "  §7Argent: §6" + money + "§6 ⛀",
                "  §7Banque: §6" + bank + "§6 ⛃",
                " ",
                "§aVoter Pour le serveur = soutiens"
        );
    }

    /**
     * Make the Player linked to the plugin's scoreboard system, if not done the player's scoreboard can't be updated
     *
     * @param pl         Player to register
     * @param scoreboard Instance of the player's scoreboard
     */
    public static void registerPlayer(Player pl, FastBoard scoreboard) {
        playerToScoreboardHashmap.put(pl.getUniqueId(), scoreboard);
    }

    public static Map<UUID, FastBoard> getPlayerToScoreboardHashmap() {
        return playerToScoreboardHashmap;
    }
}
