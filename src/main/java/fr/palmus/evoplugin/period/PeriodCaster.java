package fr.palmus.evoplugin.period;

import fr.palmus.evoplugin.enumeration.Period;

import java.text.DecimalFormat;
import java.util.Arrays;

public class PeriodCaster {

    public static String formatIntegerToReadableString(float value) {
        String[] arr = {"", "K", "M", "B", "T"};
        int index = 0;
        while ((value / 1000) >= 1) {
            value = value / 1000;
            index++;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return String.format("%s %s", decimalFormat.format(value), arr[index]).replace(" ", "").replace(",", ".");
    }

    public static Period getEnumPeriodFromInt(int i) {

        return Arrays.stream(Period.values()).toList().get(i);
    }

    public static Integer getPeriodIntFromEnum(Period desiredPeriod) {

        int i = 0;

        for (Period currentPeriod : Period.values()) {
            if (currentPeriod == desiredPeriod) {
                return i;
            }
            i++;
        }
        return null;
    }

    public static String getPeriodToString(Period period) {

        return period.toString().toLowerCase().substring(0, 1).toUpperCase() + period.toString().substring(1);
    }
}
