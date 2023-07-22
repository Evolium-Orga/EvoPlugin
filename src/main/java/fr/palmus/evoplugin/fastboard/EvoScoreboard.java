package fr.palmus.evoplugin.fastboard;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.api.messages.Formator;
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

        String period = evoPlayer.getEntirePeriodStyle();

        int money = EvoPlayer.getInstanceOf(pl).getEconomy().getMoney();
        int bank = EvoPlayer.getInstanceOf(pl).getEconomy().getBank();

        board.updateTitle(Formator.hex("#e3f2c1PLAY.EVOLIUM.FR ☀"));
        board.updateLines(Formator.hex(
                "",
                "",
                "§7| §r#c9dbb2Mon Profil",
                "  §7Pseudo: #E5F9DB" + pl.getDisplayName(),
                "  §7Grade: #E5F9DB" + prefix,
                "",
                "§7| §r#c4d7b2Ma Période",
                "  §7Période actuelle: #E5F9DB" + period,
                "  §7Points d'Expérience: #E5F9DB" + main.getPeriodCaster().formatIntegerToReadableString(evoPlayer.getExp()) + "/" + main.getPeriodCaster().getFormattedPeriodExpLimit(rank),
                "",
                "§7| §r#c9dbb2Mon Argent",
                "  §7Argent: #E5F9DB" + money + "§6 ⛀",
                "  §7Banque: #E5F9DB" + bank + "§6 ⛃",
                "",
                ""
        ));
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
