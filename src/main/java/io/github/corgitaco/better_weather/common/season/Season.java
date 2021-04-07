package io.github.corgitaco.better_weather.common.season;

import io.github.corgitaco.better_weather.common.misc.EnumCodec;
import net.minecraft.util.StringRepresentable;

public interface Season {

    int getLengthOfYear();

    int getCurrentYearTime();

    Key getKey();

    Phase getPhase();

    default int getStartTime() {
        return SeasonMaths.getSeasonStartTime(getKey(), getLengthOfYear());
    }

    default int getPhaseStartTime() {
        return SeasonMaths.getPhaseStartTime(getKey(), getPhase(), getLengthOfYear());
    }

    enum Key implements StringRepresentable {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER;

        public static final EnumCodec<Key> CODEC = new EnumCodec<>(values());

        @Override
        public String getSerializedName() {
            return name();
        }
    }

    enum Phase implements StringRepresentable {
        START,
        MID,
        END;

        public static final EnumCodec<Phase> CODEC = new EnumCodec<>(values());

        @Override
        public String getSerializedName() {
            return name();
        }
    }
}
