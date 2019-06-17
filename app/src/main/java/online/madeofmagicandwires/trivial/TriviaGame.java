package online.madeofmagicandwires.trivial;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.IndexOutOfBoundsException;
import java.util.List;


public class TriviaGame {

    /**
     * Android-efficient enum representing the difficulty of the questions to be asked this game
     */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.UNKNOWN})
    @interface Difficulty {
        String EASY = "Easy";
        String MEDIUM = "Medium";
        String HARD  = "Hard";
        String UNKNOWN = "Unknown";
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({})
    @interface QuestionType {
        String MULTIPLE = "multiple";
        String BOOLEAN  = "boolean";
        String ANY      = null;
    }

    /**
     * Interface implementing the generating or retrieving of new questions for a Trivia game
     */
    public interface QuestionsHandler {
        /**
         * Generates or Retrieves a set amount of TriviaQuestion objects for a TriviaGame
         * @param amount the amount of questions to be generated or retrieved
         * @return the list of questions generated or retrieved
         */
        List<TriviaQuestion> retrieveQuestions(int amount);
    }

    /**
     * Exception thrown when trying to call a QuestionHandler where none is provided
     */
    public static class NoQuestionHandlerProvidedException extends Exception {

        /**
         * Constructs a new exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A <tt>null</tt> value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public NoQuestionHandlerProvidedException(String message, Throwable cause) {
            super(message, cause);
        }


        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public NoQuestionHandlerProvidedException(String message) {
            super(message);
        }

    }

    private boolean gameOver;
    private int questionAmount;
    private int questionIndex;
    private List<TriviaQuestion> questions;
    private int score;
    private @Difficulty String gameDifficulty;
    private Integer gameCategory;
    private  @QuestionType String gameQuestionType;
    private QuestionsHandler questionsHandler;

    /**
     * Most precise constructor; sets the amount of questions to be asked as well as their difficulty
     *
     * @param qAmount the amount of questions to be asked before this game ends;
     *                when 0 is passed the game will go on forever
     * @param qDifficulty the difficulty of the questions asked; must be one defined in {@link Difficulty}.
     *                   When {@link Difficulty#UNKNOWN} is passed it will add questions of all
     *                   difficulties
     * @param qType  the type of questions to retrieve, must be one of {@link QuestionType};
     *               set to {@link QuestionType#ANY} for any category
     * @param qCategory the category to retrieve questions from; set to null for any category
     *
     *
     */
    public TriviaGame(
            int qAmount,
            @Difficulty String qDifficulty,
            @QuestionType String qType,
            Integer qCategory) {
        this.questionAmount = qAmount;
        this.gameDifficulty = qDifficulty;
        this.gameCategory = qCategory;
        this.gameQuestionType = qType;

        // these are not be set by humans
        this.score = 0;
        this.questionIndex = 0;
        this.gameOver = false;
    }

    /**
     * Alternative constructor
     * Categories will automatically be set "any", meaning questions of all categories will be asked
     *
     * @param qAmount the amount of questions to be asked before this game ends;
     *                when 0 is passed the game will go on forever
     * @param qDifficulty the difficulty of the questions asked; must be one defined in {@link Difficulty}.
     *                   When {@link Difficulty#UNKNOWN} is passed it will add questions of all
     *                   difficulties
     * @param qType  the type of questions to retrieve, must be one of {@link QuestionType};
     *               set to {@link QuestionType#ANY} for any category
     */
    public TriviaGame(int qAmount, @Difficulty String qDifficulty, @QuestionType String qType) {
        this(qAmount, qDifficulty, qType, null);
    }


    /**
     * Standard constructor
     * Difficulty Category and QuestionType will  will be automatically set to "any"
     * meaning questions of all difficulties, categories, and types will be asked.
     *
     * @param qAmount the amount of questions to be asked before the game ends
     *                when 0 is passed the game will go on forever
     */
    public TriviaGame(int qAmount) {
        this(qAmount, Difficulty.UNKNOWN, QuestionType.ANY, null);
    }





    /**
     * Alternative constructor
     * The amount of questions to be asked will be set to 0, meaning the game will go on forever
     *
     * @param qDifficulty the difficulty of the questions asked; must be one defined in {@link Difficulty}.
     *                   When {@link Difficulty#UNKNOWN} is passed it will add questions of all
     *                   difficulties
     * @param qType  the type of questions to retrieve, must be one of {@link QuestionType};
     *               set to {@link QuestionType#ANY} for any category
     * @param qCategory the category to retrieve questions from; set to null for any category
     */
    public TriviaGame(@Difficulty String qDifficulty, @QuestionType String qType, Integer qCategory) {
        this(0, qDifficulty, qType, qCategory);
    }

    /**
     * Most concise constructor
     * This game will go on forever, and ask questions of all difficulties. Use is not recommended.
     *
     */
    public TriviaGame() {
        this(0, Difficulty.UNKNOWN, QuestionType.ANY, null);
    }

    /**
     * Retrieves the current question of the game
     * @return TriviaQuestion object representing the question to be answered next
     *
     * @throws IndexOutOfBoundsException if the question index is higher
     * than the total amount of questions to be asked before the game ends
     */
    public TriviaQuestion getCurrentQuestion() throws ArrayIndexOutOfBoundsException {
        return questions.get(questionIndex);
    }

    /**
     * Retrieves the category questions of this game belong to
     *
     * @return the category any question of this game will belong to;
     *  if set to null questions of all categories will be asked
     */
    public int getGameCategoryId() {
        return gameCategory;
    }

    /**
     * Sets the category all questions of this game should belong to
     *
     * @param gameCategory the category all questions asked during this game should belong to
     *                     if set to null questions of all categories can be asked
     */
    public void setGameCategoryId(int gameCategory) {
        this.gameCategory = gameCategory;
    }

    /**
     * Gets the difficulty set for this game
     *
     * @return the difficulty of the questions to be asked this game
     *                        if set to {@link Difficulty#UNKNOWN} it means questions of all
     *                        difficulties can be asked
     */
    public String getGameDifficulty() {
        return gameDifficulty;
    }

    /**
     * Sets the difficulty of this game
     *
     * @param gameDifficulty  he difficulty of the questions to be asked this game;
     *                        must be one of {@link TriviaGame.Difficulty} and
     *                        when set to {@link Difficulty#UNKNOWN} it means questions of all
     *                        difficulties can be asked
     */
    public void setGameDifficulty(@Difficulty String gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    /**
     * Gets the amount of questions to be asked this game before it ends
     * If set to 0 it means the game will go on forever
     * @return the total amount of questions to be asked before the game ends
     */
    public int getQuestionAmount() {
        return questionAmount;
    }

    /**
     * The index keeping track of where we are in the game
     * @return the amount of questions already asked, including the current one,
     *         if it has not been answered already
     */
    public int getQuestionIndex() {
        return questionIndex;
    }

    /**
     * Gets the players score this game
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets or updates the players score
     * @param score the new score to be set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Increments the score by an amount
     * @param score the amount to be added to the score
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * Returns the current question handler, used for getting the questions of this game
     * @return the QuestionHandler object linked to the current game.
     * @throws NoQuestionHandlerProvidedException if {@link TriviaGame#questionsHandler} was not set
     *
     */
    public QuestionsHandler getQuestionsHandler() throws NoQuestionHandlerProvidedException {
        if(this.questionsHandler != null) {
            return questionsHandler;
        } else {
            throw new NoQuestionHandlerProvidedException(
                    "Could not find a QuestionHandler object linked to this instance. " +
                    "Make sure a call to TriviaGame.setHandler(QuestionHandler) was made!");
        }

    }

    /**
     * Set or update the question handler for this game
     * @param questionsHandler object implementing the QuestionHandler interface.
     */
    public void setQuestionsHandler(QuestionsHandler questionsHandler) {
        if(questionsHandler != this.questionsHandler) {
            this.questionsHandler = questionsHandler;
        }
    }

    /**
     * Updates the questions to be asked before the game ends
     * @throws NoQuestionHandlerProvidedException if {@link TriviaGame#questionsHandler}
     * was not set before calling this method
     */
    public void updateQuestions(int questionAmount) throws NoQuestionHandlerProvidedException {
        if(questionsHandler != null) {
            if(questionAmount != 0 || questions == null) {
                questions = questionsHandler.retrieveQuestions(questionAmount);
            } else {
                questions.addAll(questionsHandler.retrieveQuestions(questionAmount));
            }
        } else {
            throw new NoQuestionHandlerProvidedException(
                    "Could not find a QuestionHandler object linked to this instance. " +
                            "Make sure a call to TriviaGame.setHandler(QuestionHandler) was made" +
                            "before attempting this");
        }

    }

    /**
     * Moves the game on to the next question, or if there isn't any, ends the game.
     */
    public void nextQuestion() {
        this.questionIndex += 1;
        if(questionAmount != 0 && questionIndex >= questionAmount) {
            this.gameOver = true;
        }
    }

    /**
     * Checks if the game is currently still in progress or ended.
     * @return a boolean representing the state of the game;
     *         true means the game is over, false that the game is currently still in progress
     */
    public boolean isGameOver() {
        return gameOver;
    }
}
