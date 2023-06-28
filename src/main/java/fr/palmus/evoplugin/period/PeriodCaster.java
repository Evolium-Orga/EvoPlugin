package fr.palmus.evoplugin.period;

import fr.palmus.evoplugin.EvoPlugin;
import fr.palmus.evoplugin.enumeration.Period;

import java.util.Arrays;

public class PeriodCaster {

    private final EvoPlugin main = EvoPlugin.getInstance();

    private final int FIRST_EXP_MILESTONE = main.getConfig().getInt("period.first-exp-milestone");
    private final int SECOND_EXP_MILESTONE = main.getConfig().getInt("period.second-exp-milestone");
    private final int THIRD_EXP_MILESTONE = main.getConfig().getInt("period.third-exp-milestone");

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

    public Integer getIntPeriodFromEnum(Period desiredPeriod) {

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
