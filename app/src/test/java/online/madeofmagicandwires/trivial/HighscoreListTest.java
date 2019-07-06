package online.madeofmagicandwires.trivial;

import android.support.v4.util.SparseArrayCompat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.String;
import java.util.List;

import online.madeofmagicandwires.trivial.models.HighscoreList;

import static org.junit.Assert.*;

public class HighscoreListTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private HighscoreList testScores;


    /**
     * Run before every {@link Test} method, creates a mock testScores instance.
     * TODO: use actual mock data functionality to replace this
     */
    @Before
    public void setUp() {
        SparseArrayCompat<String> scores = new SparseArrayCompat<>();
        scores.append(100, "foo");
        scores.append(5, "bar");
        scores.append(-1, "baz");
        scores.append(20, "qux");
        this.testScores = new HighscoreList(scores);
    }


    /**
     * Tests updating the scores list with {@link HighscoreList#setScoreList(SparseArrayCompat)} and
     * {@link HighscoreList#getScoreList()}
     */
    @Test
    public void setScores() {
        SparseArrayCompat<String> scores = new SparseArrayCompat<>();
        scores.append(400, "foo");
        testScores.setScoreList(scores);
        assertNotNull("scores was not set!", testScores.getScoreList());
        assertEquals(
                "scores was not overwritten",
                1,
                testScores.getScoreList().size()
        );
    }

    @Test
    public void getScores() {
        assertNotNull(
                "scores was not retrieved properly!",
                testScores.getScoreList());
        assertEquals(
                "retrieved scores were unexpected",
                "foo",
                testScores.getScoreList().get(100)
        );
    }


    /**
     * Tests adding a new score to the HighscoreList using
     * {@link HighscoreList#addScore(int, String)}
     */
    @Test
    public void addScore() {
        testScores.addScore(30, "piyo");
        HighscoreList.Score newScore = testScores.getScore(30);
        assertNotNull("new score of '30' was not found!", newScore);
        System.out.println(newScore.toString());
        assertEquals(
                "new score was not retrieved correctly",
                30,
                newScore.getScore()
        );
        assertNotNull(
                "new score by 'piyo' was not found!",
                testScores.getScoresByUser("piyo")
        );
    }

    /**
     * Tests replacing a score using {@link HighscoreList#setScore(int, String)}
     */
    @Test
    public void setScore() {
        testScores.setScore(100, "hoge");
        HighscoreList.Score newScore = testScores.getScore(100);
        assertNotNull("new score was not retrieved correctly", newScore);
        assertEquals("Score was note updated!", "hoge", newScore.getName());

    }

    /**
     * Tests {@link HighscoreList#hasScore(int)} and {@link HighscoreList#hasScore(String)}
     */
    @Test
    public void hasScore() {
        assertTrue("could not find available score of '100'", testScores.hasScore(100));
        assertTrue(
                "could not find available score by user 'foo'",
                testScores.hasScore("foo")
        );
    }

    /**
     * Tests {@link HighscoreList#getScoreAt(int)}, including passing an incorrect index
     */
    @Test
    public void getScoreAt() {
        HighscoreList.Score scoreAt = testScores.getScoreAt(0);
        assertNotNull("Could not retrieve score at index 0!", scoreAt);
        System.out.println(scoreAt.toString());

        thrown.expect(ArrayIndexOutOfBoundsException.class);
        testScores.getScoreAt(-1);
    }

    /**
     * Tests {@link HighscoreList#getScoresByUser(String)},
     * including retrieving scores by a non-existing user
     */
    @Test
    public void scoresByUser() {
        testScores.addScore(1, "fun");
        testScores.addScore(2, "fun");
        testScores.addScore(3, "fun");

        List<HighscoreList.Score> scoresByUser = testScores.getScoresByUser("fun");
        assertNotNull("No scores by user 'fun' retrieved!", scoresByUser);
        assertEquals(
                "Not all scores by user 'fun' retrieved!",
                3,
                scoresByUser.size()
        );
        scoresByUser.forEach(score -> System.out.println(score.toString()));

        assertNull(
                "Could somehow retrieve scores by fake user?!",
                testScores.getScoresByUser("fake news")
        );
    }

    /**
     * Tests {@link HighscoreList#getLastAddedIndex()}, {@link HighscoreList#getLastAddedScore()},
     * and {@link HighscoreList#isNew(HighscoreList.Score)}
     */
    @Test
    public void getLastAdded() {
        testScores.addScore(4, "test");
        HighscoreList.Score newScore = testScores.getScore(4);
        assertNotNull("Score not set correctly", newScore);
        assertEquals("Last added score not saved correctly!",
                newScore.getPosition(),
                testScores.getLastAddedIndex());
        assertEquals(
                "last added score was not the same!",
                newScore,
                testScores.getLastAddedScore()
        );
        assertTrue(
                "last added score was not the same according to highscorelist! (it is)",
                testScores.isNew(newScore)
        );
    }
}