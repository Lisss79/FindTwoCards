package com.lisss79.findtwocards;

import android.content.SharedPreferences;

public class Values {
    public static final String NUM_OF_CARDS_KEY = "NUM_OF_CARDS";
    public static final String[] BEST_RESULT_KEY =
            {"BEST_RESULT1", "BEST_RESULT2", "BEST_RESULT3", "BEST_RESULT4"};
    public static final int NONE = 100;

    public static final int CARD_VALUE = 0;
    public static final int CARD_STATE = 1;
    public static final int CARD_COL = 2;
    public static final int CARD_ROW = 3;
    public static final int CARD_OPEN = 2;
    public static final int CARD_CLOSE = 1;
    public static final int CARD_ABSENT = 0;
    public static final int CARD_OPEN_TO_BE_REMOVED = 3;
    public static final int CARD_CLOSE_TO_BE_REMOVED = 4;

    public static final double NO_ANIMATION = 0;
    public static final double SECOND_PART_OF_ANIMATION = 0.5;
    public static final double END_OF_ANIMATION = 1;
    public static final double ANIMATION_STEP = 1d / 60d;
    public static final double DISAPPEARANCE_STEP = 1d / 60d;
    public static final double ANIMATION_DURATION = 0.4;
    public static final double DISAPPEARANCE_DURATION = 0.5;

    public static SharedPreferences sp;

}
