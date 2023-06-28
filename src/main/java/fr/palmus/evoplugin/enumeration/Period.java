package fr.palmus.evoplugin.enumeration;

import org.jetbrains.annotations.NotNull;

public enum Period {
    PREHISTOIRE, ANTIQUITE, MOYENAGE, RENNAISSANCE, INDUSTRIEL, ACTUELLE, FUTUR;

    @NotNull
    public static Period getNextPeriod(Period period) {
        if (period == FUTUR) {
            return FUTUR;
        }
        boolean isPeriod = false;
        for (Period per : Period.values()) {
            if (isPeriod) {
                return per;
            }
            if (per == period) {
                isPeriod = true;
            }
        }
        return null;
    }

    @NotNull
    public static Period getBelowPeriod(Period period) {
        if (period == PREHISTOIRE) {
            return PREHISTOIRE;
        }
        Period currentPeriod = null;
        for (Period per : Period.values()) {
            if (per == period) {
                return currentPeriod;
            }
            currentPeriod = per;
        }
        return null;
    }
}
