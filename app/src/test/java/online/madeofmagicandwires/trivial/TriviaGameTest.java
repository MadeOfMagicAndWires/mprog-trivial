package online.madeofmagicandwires.trivial;

import org.junit.Test;

import static org.junit.Assert.*;

public class TriviaGameTest {

    TriviaGame testGame;



    @Test
    public void runTest() {
        TriviaGame testGame = new TriviaGame();
        assertNotNull(testGame);

    }
}