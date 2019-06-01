package online.madeofmagicandwires.trivial;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TriviaGame {

    /**
     * Android-efficient enum replacement representing the type of question asked.
     * Can be either 'MULTIPLE' or 'BOOLEAN'
     */
    public static final String MULTIPLE = "multiple choice";
    public static final String BOOLEAN = "true/false";
    public static final String TYPE_UNKNOWN = "unknown";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MULTIPLE, BOOLEAN, TYPE_UNKNOWN})
    @interface Type {}

    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";
    public static final String DIFFICULTY_UNKNOWN = "Unknown";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EASY, MEDIUM, HARD, DIFFICULTY_UNKNOWN})
    @interface Difficulty {}



    private int questionAmount;
    private int currentQuestion;
    private int score;

}
