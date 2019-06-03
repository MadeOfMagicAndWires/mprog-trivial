package online.madeofmagicandwires.trivial;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TriviaGameTest implements TriviaGame.QuestionsHandler {

    public TriviaGame testGame;
    List<TriviaQuestion> testQs;

    @Before
    public void setUp() {
        testGame = new TriviaGame(1, TriviaGame.Difficulty.EASY);
    }


    @Test
    public void createTriviaGame() {
        testGame = new TriviaGame(0, TriviaGame.Difficulty.UNKNOWN);
        assertNotNull(testGame);
        testGame = new TriviaGame(0);
        assertNotNull(testGame);
        testGame = new TriviaGame(TriviaGame.Difficulty.EASY);
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

    @Test(expected = TriviaGame.NoQuestionHandlerProvidedException.class)
    public void getQuestionsHandler() throws TriviaGame.NoQuestionHandlerProvidedException {
        testGame.setQuestionsHandler(this);

        assertEquals(
                "QuestionHandler was not set or retrieved correctly!",
                this,
                testGame.getQuestionsHandler()
        );

        testGame.setQuestionsHandler(null);
        testGame.getQuestionsHandler();
    }

    @Test
    public void getCurrentQuestion() throws TriviaGame.NoQuestionHandlerProvidedException {
        testGame.setQuestionsHandler(this);
        TriviaQuestion testQ = testGame.getCurrentQuestion();
        System.out.println(testQ.getQuestion());
        System.out.println(testQ.getRightAnswer());
        assertEquals(testQ, testQs.get(0));
    }

    @Test(expected = TriviaGame.NoQuestionHandlerProvidedException.class)
    public void getCurrentQuestionExceptionThrowing() throws TriviaGame.NoQuestionHandlerProvidedException {
        testGame.getCurrentQuestion();
    }

    @Test(expected = TriviaGame.NoQuestionHandlerProvidedException.class)
    public void updateQuestions() throws TriviaGame.NoQuestionHandlerProvidedException {
        testGame.setQuestionsHandler(this);
        testGame.updateQuestions(1);
        TriviaQuestion testQ = testGame.getCurrentQuestion();
        testGame.updateQuestions(1);
        // assertEquals(testQ.getQuestion(), testGame.getCurrentQuestion().getQuestion());
        assertNotSame(
                "Questions have not been updated!",
                testQ,
                testGame.getCurrentQuestion()
        );

        testGame.setQuestionsHandler(null);
        testGame.getQuestionsHandler();
    }


    @Test
    public void isGameOver() {
        assertFalse(testGame.isGameOver());
        testGame.nextQuestion();
        assertTrue(testGame.isGameOver());
    }


    /**
     * Generates or Retrieves a set amount of TriviaQuestion objects for a TriviaGame
     *
     * @param amount the amount of questions to be generated or retrieved
     * @return the list of questions generated or retrieved
     */
    @Override
    public List<TriviaQuestion> retrieveQuestions(int amount) {
        testQs = new ArrayList<>();

        for(int i=0;i<amount;i++) {
            MultipleChoiceQuestion q = new MultipleChoiceQuestion(
                    "How many questions?",
                    (i+1) + " so far",
                    new String[] {"wrong", "answers", "will", "fail"});
            testQs.add(q);
        }

        return testQs;
    }

    @Test
    public void setScore() {
        testGame.setScore(10);
        assertThat(testGame.getScore(), is(10));
        testGame.addScore(10);
        assertThat(testGame.getScore(), is(20));
    }
}