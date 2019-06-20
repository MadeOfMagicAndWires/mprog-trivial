package online.madeofmagicandwires.trivial;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import online.madeofmagicandwires.trivial.models.MultipleChoiceQuestion;
import online.madeofmagicandwires.trivial.models.TriviaGame;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TriviaGameTest {

    public TriviaGame testGame;
    List<TriviaQuestion> testQs;

    @Before
    public void setUp() {
        testGame = new TriviaGame(1, TriviaGame.Difficulty.EASY, TriviaGame.QuestionType.ANY);
    }


    @Test
    public void createTriviaGame() {
        testGame = new TriviaGame(0, TriviaGame.Difficulty.UNKNOWN, TriviaGame.QuestionType.ANY);
        assertNotNull(testGame);
        testGame = new TriviaGame(0);
        assertNotNull(testGame);
        testGame = new TriviaGame(TriviaGame.Difficulty.ANY, TriviaGame.QuestionType.ANY, -1);
        assertNotNull(testGame);
        testGame = new TriviaGame();
        assertNotNull(testGame);
    }

    @Test
    public void getGameDifficulty() {
        assertEquals(
                "Difficulty level was not set or retrieved correctly!",
                TriviaGame.Difficulty.EASY,
                testGame.getGameDifficulty()
        );
    }

    @Test
    public void getQuestionAmount() {
        assertEquals(
                "Question amount was not set or retrieved correctly!",
                1,
                testGame.getQuestionAmount()
        );
    }

    @Test
    public void getQuestionIndex() {
        int qIndex = testGame.getQuestionIndex();
        assertThat("question Index did not start at 0", qIndex, is(0));
        testGame.nextQuestion();
        assertTrue(
                "questionIndex has not incremented properly!",
                qIndex < testGame.getQuestionIndex());
    }

    @Test
    public void isGameOver() {
        assertFalse(testGame.isGameOver());
        testGame.nextQuestion();
        assertTrue(testGame.isGameOver());
    }

    @Test
    public void setScore() {
        testGame.setScore(10);
        assertThat(testGame.getScore(), is(10));
        testGame.addScore(10);
        assertThat(testGame.getScore(), is(20));
    }
}