package fr.palmus.evoplugin.period;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.enumeration.Period;
import org.bukkit.ChatColor;

import java.util.Arrays;

public class PeriodCaster {

    private int FIRST_EXP_MILESTONE;
    private int SECOND_EXP_MILESTONE;
    private int THIRD_EXP_MILESTONE;

    public PeriodCaster() {
        EvoPlugin main = EvoPlugin.getInstance();
        try {
            FIRST_EXP_MILESTONE = Integer.parseInt(main.getConfig().getString("first_exp_milestone"));
            SECOND_EXP_MILESTONE = Integer.parseInt(main.getConfig().getString("second_exp_milestone"));
            THIRD_EXP_MILESTONE = Integer.parseInt(main.getConfig().getString("third_exp_milestone"));
        }catch (NumberFormatException e) {
            main.getCustomLogger().log(ChatColor.RED + "Failed to load data from config files. FATAL");
            main.safeInitialized = false;
        }
    }

    public String getRankToString(int i) {

        String rankSymbol = "I";

        return rankSymbol.repeat(i);
    }

    public String formatIntegerToReadableString(int exp) {
        if (exp <= 999) {
            return String.valueOf(exp);
        }
        if (exp <= 9999) {
            int expConvert = exp / 100;
            char[] digits1 = String.valueOf(expConvert).toCharArray();
            return digits1[0] + "." + digits1[1] + "k";
        }
        int expConvert = exp / 100;
        char[] digits1 = String.valueOf(expConvert).toCharArray();
        return digits1[0] + String.valueOf(digits1[1]) + "." + digits1[2] + "k";
    }

    public int getPeriodExpLimit(int i) {
        if (i == 1) {
            return FIRST_EXP_MILESTONE;
        }

        if (i == 2) {
            return SECOND_EXP_MILESTONE;
        }

        if (i == 3) {
            return THIRD_EXP_MILESTONE;
        }

        return 0;
    }

    public String getFormattedPeriodExpLimit(int i) {

        return formatIntegerToReadableString(getPeriodExpLimit(i));
    }

    public Period getEnumPeriodFromInt(int i) {

        return Arrays.stream(Period.values()).toList().get(i);
    }

    public Integer getPeriodIntFromEnum(Period desiredPeriod) {

        int i = 0;

        for (Period currentPeriod : Period.values()) {
            if (currentPeriod == desiredPeriod) {
                return i;
            }
            i++;
        }
        return null;
    }

    public String getPeriodToString(Period period) {

        return period.toString().toLowerCase().substring(0, 1).toUpperCase() + period.toString().substring(1);
    }
}
