package online.madeofmagicandwires.trivial.models;


import java.util.List;

/**
 * Abstract Representation of a Trivia question
 */
abstract public class TriviaQuestion {


    private String question;
    private @TriviaGame.Difficulty String difficulty;
    private String category;
    private boolean answered;

    /**
     * Most verbose constructor, sets the difficulty and category of the question
     *
     * @param question the question to be asked
     * @param difficulty the difficulty level of the question
     * @param category the category the question belongs to
     */
    public TriviaQuestion(
            String question,
            @TriviaGame.Difficulty String difficulty,
            String category) {
        this.question = question;
        this.difficulty = difficulty;
        this.category = category;

        // set this automatically
        this.answered = false;
    }

    /**
     * Less verbose constructor, automatically sets the category to "Unknown"
     *
     * @param question the question to be asked
     * @param difficulty the difficulty of the question
     */
    public TriviaQuestion(String question, @TriviaGame.Difficulty String difficulty) {
        this(question, difficulty, "Unknown");
    }



    /**
     * Most succinct constructor, automatically sets difficulty and category to "Unknown"
     *
     * @param question the question to be asked
     */
    public TriviaQuestion(String question) {
        this(question, TriviaGame.Difficulty.UNKNOWN);
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
    public Class getQuestionType() {
        return this.getClass();
    }

    /**
     * Gets the difficulty of the question
     * @return the difficulty of the question rated
     */
    public @TriviaGame.Difficulty String getDifficulty() {
        return difficulty;
    }

    /**
     * Gets the category this question belongs to.
     * @return human readable representation of which category this question belongs to
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Checks if the answer given is the right one
     *
     * @param  answer the answer given by the user to check against the expected answer
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    public abstract  <T extends Comparable> boolean checkAnswer(T answer);

    public <T extends Comparable> void pickAnswer(T answer) {

    }

    /**
     * Shows the correct answer
     * @return String containing the right answer to the question
     */
    abstract public String getRightAnswer();

    /**
     * Returns all the choices to choose from, shuffled in a random order
     *
     * @return a list of human-readable possible answers to the question
     */
    abstract public List<String> getAnswers();

    /**
     * returns whether this question has already been answered once
     * @return true if the question has already been answered, false if not
     */
    abstract public boolean isAnswered();
    /**
     * Saves the value of a picked answer to a question
     * @param answer the picked answer to remember
     */
    abstract public  <T extends Comparable> void setPickedAnswer(T answer);
}
