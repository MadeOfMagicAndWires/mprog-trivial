package online.madeofmagicandwires.trivial;

import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import online.madeofmagicandwires.trivial.models.MultipleChoiceQuestion;
import online.madeofmagicandwires.trivial.models.TriviaGame;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TriviaQuestionTest {

    final String QUESTION = "Is this working?";
    final String ANSWER = "yes";
    final String[] WRONG_ANSWERS = {"no", "not really", "maybe"};

    MultipleChoiceQuestion testQuestion;


    @Before
    public void setUp(){
        testQuestion = new MultipleChoiceQuestion(
                QUESTION,
                ANSWER,
                WRONG_ANSWERS,
                TriviaGame.Difficulty.EASY);
    }

    @Test
    public void getQuestion() {
        assertEquals(testQuestion.getQuestion(), QUESTION);
    }

    @Test
    public void getQuestionType() {

        String[] possibleTypes = {
                TriviaGame.QuestionType.ANY,
                TriviaGame.QuestionType.BOOLEAN,
                TriviaGame.QuestionType.MULTIPLE};

        assertThat(
                "Returned question type was not a valid one!",
                Arrays.asList(possibleTypes),
                hasItem(testQuestion.getQuestionType()));
        assertNotEquals(
                "question type was set to any! Should be Multiple choice",
                testQuestion.getQuestionType(),
                TriviaGame.QuestionType.ANY);


    }

    @Test
    public void checkAnswer() {
        assertTrue("", testQuestion.checkAnswer(ANSWER));
        assertFalse(testQuestion.checkAnswer(WRONG_ANSWERS[2]));
    }

    @Test
    public void getAnswers() {
        List<String> answers = testQuestion.getAnswers();
        assertThat(answers.size(), is(WRONG_ANSWERS.length + 1));
        assertThat(answers, hasItems(WRONG_ANSWERS));
        assertThat(answers, hasItems(ANSWER));
    }
}