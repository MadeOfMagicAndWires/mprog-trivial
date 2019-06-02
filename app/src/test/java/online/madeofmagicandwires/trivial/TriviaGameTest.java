package online.madeofmagicandwires.trivial;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TriviaGameTest implements TriviaGame.QuestionsHandler {

    // TODO: broaden tests to cover all the functionaities of the TriviaGame class.

    TriviaGame testGame;

    @Test
    public void createTriviaGame() {
        testGame = new TriviaGame(0, TriviaGame.Difficulty.UNKNOWN);
        assertNotNull(testGame);
        testGame = new TriviaGame(0);
        assertNotNull(testGame);
        testGame = new TriviaGame(TriviaGame.Difficulty.EASY);
        assertNotNull(testGame);
    }


    /**
     * Generates or Retrieves a set amount of TriviaQuestion objects for a TriviaGame
     *
     * @param amount the amount of questions to be generated or retrieved
     * @return the list of questions generated or retrieved
     */
    @Override
    public List<TriviaQuestion> retrieveQuestions(int amount) {
        List<TriviaQuestion> testQs = new ArrayList<>();

        for(int i=0;i<amount;i++) {
            MultipleChoiceQuestion q = new MultipleChoiceQuestion(
                    "How many questions?",
                    Integer.toString(i) + " so far",
                    new String[] {"wrong", "answers", "will", "fail"});
            testQs.add(q);
        }

        return testQs;
    }
}