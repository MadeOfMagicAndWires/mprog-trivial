package online.madeofmagicandwires.trivial;

/**
 * Abstract Representation of a Trivia question
 */
abstract public class TriviaQuestion {


    private String question;
    private @TriviaGame.Type String questionType;
    private @TriviaGame.Difficulty String difficulty;

    /**
     * Default constructor
     * @param type
     * @param question
     */
    public TriviaQuestion(@TriviaGame.Type String type, String question) {
        this.question = question;
        this.questionType = type;
        this.difficulty = TriviaGame.TYPE_UNKNOWN;
    }

    public TriviaQuestion(
            @TriviaGame.Type String type,
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
    public @TriviaGame.Type String getQuestionType() {
        return questionType;
    }

    /**
     * Checks if the answer given is the right one
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    abstract public <T> boolean checkAnswer(T answer);
}
