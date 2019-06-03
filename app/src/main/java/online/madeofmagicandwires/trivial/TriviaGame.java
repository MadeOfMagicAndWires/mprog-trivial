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
    private String gameDifficulty;
    private QuestionsHandler questionsHandler;

    /**
     * Most precise constructor; sets the amount of questions to be asked as well as their difficulty
     * @param qAmount the amount of questions to be asked before this game ends;
     *                when 0 is passed the game will go on forever
     * @param difficulty the difficulty of the questions asked; must be one defined in {@link Difficulty}.
     *                   When {@link Difficulty#UNKNOWN} is passed it will add questions of all
     *                   difficulties
     *
     *
     */
    public TriviaGame(int qAmount, @Difficulty String difficulty) {
        this.questionAmount = qAmount;
        this.gameDifficulty = difficulty;
        this.score = 0;
        this.questionIndex = 0;
        this.gameOver = false;
    }

    /**
     * Standard constructor
     * Difficulty will be automatically set to {@link Difficulty#UNKNOWN}
     * meaning questions of all difficulties will be asked.
     *
     * @param qAmount the amount of questions to be asked before the game ends
     *                when 0 is passed the game will go on forever
     */
    public TriviaGame(int qAmount) {
        this(qAmount, Difficulty.UNKNOWN);
    }

    /**
     * Alternative constructor
     * The amount of questions to be asked will be set to 0, meaning the game will go on forever
     *
     * @param difficulty the difficulty of the questions asked; must be one defined in {@link Difficulty}.
     *                   When {@link Difficulty#UNKNOWN} is passed it will add questions of all
     *                   difficulties
     */
    public TriviaGame(@Difficulty String difficulty) {
        this(0, difficulty);
    }

    /**
     * Most concise constructor
     * This game will go on forever, and ask questions of all difficulties. Use is not recommended.
     *
     */
    public TriviaGame() {
        this(0, Difficulty.UNKNOWN);
    }

    /**
     * Gets the difficulty set for this game
     * @return the difficulty of the questions to be asked this game.
     * If set to {@link Difficulty#UNKNOWN} it means questions of all difficulties will be set
     */
    public String getGameDifficulty() {
        return gameDifficulty;
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
     * Retrieves the current question of the game
     * @return TriviaQuestion object representing the question to be answered next
     *
     * @throws IndexOutOfBoundsException if the question index is higher
     * than the total amount of questions to be asked before the game ends
     *
     * @throws NoQuestionHandlerProvidedException if {@link TriviaGame#questionsHandler} was not set
     */
    public TriviaQuestion getCurrentQuestion() throws ArrayIndexOutOfBoundsException,
            NoQuestionHandlerProvidedException {
         if(questions == null || questionIndex >= (questions.size()-1) && questionAmount == 0) {
             updateQuestions(10);
         }
         return questions.get(questionIndex);
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
        if(questionsHandler == null || !questionsHandler.equals(this.questionsHandler)) {
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
