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

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({Type.MULTIPLE, Type.BOOLEAN, Type.UNKNOWN})
    @interface Type {
        String MULTIPLE = "multiple choice";
        String BOOLEAN = "true/false";
        String UNKNOWN = "unknown";
    }


    private String question;
    private @TriviaQuestion.Type String questionType;
    private @TriviaGame.Difficulty String difficulty;

    /**
     * Default constructor
     * @param type
     * @param question
     */
    public TriviaQuestion(@TriviaQuestion.Type String type, String question) {
        this.question = question;
        this.questionType = type;
        this.difficulty = TriviaQuestion.Type.UNKNOWN;
    }

    public TriviaQuestion(
            @TriviaQuestion.Type String type,
            String question,
            @TriviaGame.Difficulty String difficulty) {
        this.questionType = type;
        this.question = question;
        this.difficulty = difficulty;
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
    public @TriviaQuestion.Type String getQuestionType() {
        return questionType;
    }

    /**
     * Gets the difficulty of the question
     * @return the difficulty of the question rated
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     * Checks if the answer given is the right one
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    abstract public <T> boolean checkAnswer(T answer);

    /**
     * Shows the correct answer
     * @return String containing the right answer to the question
     */
    abstract public String getRightAnswer();
}
