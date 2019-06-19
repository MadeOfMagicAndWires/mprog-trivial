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
        String ANY = null;

    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({QuestionType.BOOLEAN, QuestionType.MULTIPLE})
    @interface QuestionType {
        String MULTIPLE = "multiple";
        String BOOLEAN  = "boolean";
        String ANY = null;
    }


    private boolean gameOver;
    private int questionAmount;
    private int questionIndex;
    private List<TriviaQuestion> questions;
    private int score;
    private @Difficulty String gameDifficulty;
    private int gameCategory;
    private  @QuestionType String gameQuestionType;

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
            int qCategory) {
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
        this(qAmount, qDifficulty, qType, -1);
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
        this(qAmount, Difficulty.ANY, QuestionType.ANY, -1);
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
        this(0, Difficulty.ANY, QuestionType.ANY, -1);
    }

    /**
     * Retrieves the nth question of this game
     *
     * @param index the index of the question to be retrieved
     *
     * @return the TriviaQuestion object saved under the index
     * @throws IndexOutOfBoundsException when there is no question saved under that index
     * @throws NullPointerException when the question list has not been generated yet
     * @see TriviaGame#setQuestions(List) to set the question list for this game
     *
     */
    public TriviaQuestion getQuestion(int index) throws IndexOutOfBoundsException, NullPointerException {
            return questions.get(index);
    }

    /**
     * Retrieves the current question of the game
     *
     * @return TriviaQuestion object representing the question to be answered next
     * @throws IndexOutOfBoundsException when there is no question saved under that index
     * @throws NullPointerException when the question list has not been generated yet
     * @see TriviaGame#setQuestions(List) to set the question list for this game
     */
    public TriviaQuestion getCurrentQuestion() throws IndexOutOfBoundsException, NullPointerException {
        return getQuestion(questionIndex);
    }

    /**
     * Sets the questions to be asked; if a question list already exists the new questions will
     * be added to the current list, rather than the list being completely replaced
     *
     * @param newQuestions the list of new questions that has been generated or retrieved
     */
    public void setQuestions(List<TriviaQuestion> newQuestions) {
        if(this.questionIndex != 0 || this.questions == null) {
            this.questions = newQuestions;
        } else {
            this.questions.addAll(newQuestions);
        }
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
