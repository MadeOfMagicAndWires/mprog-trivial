package online.madeofmagicandwires.trivial;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Abstract Representation of a Trivia question
 */
abstract public class TriviaQuestion {
    /**
     * Android-efficient enum replacement representing the type of question asked.
     * Can be either 'MULTIPLE' or 'BOOLEAN'
     */
    public static final String MULTIPLE = "multiple choice";
    public static final String BOOLEAN = "true/false";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({MULTIPLE, BOOLEAN})
    @interface Type {}

    public static final String EASY = "Easy";
    public static final String MEDIUM = "Medium";
    public static final String HARD = "Hard";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EASY, MEDIUM, HARD})
    @interface Difficulty {}

    private String question;
    private @Type String questionType;
    private @Difficulty String difficlty;

    /**
     * Default constructor
     * @param type
     * @param question
     */
    public TriviaQuestion(@Type String type, String question) {
        this.question = question;
        this.questionType = type;
    }


    /**
     * Getter for the actual question
     *
     * @return a human-readable string of the actual question being asked
     */
    public String getQuestion() {
        return question;
    }

    /**
     * gets the question type
     *
     * @return the type of answer
     */
    public String getQuestionType() {
        return questionType;
    }

    /**
     * Checks if the answer given is the right one
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    abstract public <T extends Comparable<T>> boolean checkAnswer(T answer);
}
