package io.github.corgitaco.better_weather.common.season;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SeasonMaths {

    public int getLengthOfSeason(int yearLength) {
        return yearLength / Season.Key.values().length;
    }

    public int getSeasonStartTime(Season.Key key, int yearLength) {
        int seasonLength = SeasonMaths.getLengthOfSeason(yearLength);;
        return seasonLength * key.ordinal();
    }

    public int getLengthOfPhase(int seasonLength) {
        return seasonLength / Season.Phase.values().length;
    }

    public int getPhaseStartTime(Season.Key key, Season.Phase phase, int yearLength) {
        int startTime = SeasonMaths.getSeasonStartTime(key, yearLength);

        int phaseLength = SeasonMaths.getLengthOfSeason(yearLength) / Season.Phase.values().length;

        return startTime + (phaseLength * phase.ordinal());
    }
}
