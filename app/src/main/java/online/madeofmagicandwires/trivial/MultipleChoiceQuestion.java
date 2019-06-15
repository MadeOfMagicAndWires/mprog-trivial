package online.madeofmagicandwires.trivial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Concrete representation of a multiple choice trivia question
 */
public class MultipleChoiceQuestion extends TriviaQuestion {

    private String rightAnswer;
    private String[] wrongAnswers;


    /**
     * Most verbose constructor, sets all the class' elements to the given parameters
     *
     * @param aQuestion the question to be asked
     * @param theAnswer the answer to the question
     * @param theWrongAnswers a series of wrong answers to add in the mix
     * @param difficulty the difficulty level of the question
     * @param theCategory the category the question belongs to
     */
    public MultipleChoiceQuestion(
            String aQuestion,
            String theAnswer,
            String[] theWrongAnswers,
            @TriviaGame.Difficulty String difficulty,
            String theCategory) {
        super(aQuestion, difficulty, theCategory);
        this.rightAnswer = theAnswer;
        this.wrongAnswers = theWrongAnswers;
    }


    /**
     * Less verbose constructor, automatically sets the category to "Unknown"
     *
     * @param aQuestion the question to be asked
     * @param theAnswer the answer to the question
     * @param theWrongAnswers a series of wrong answers to add in the mix
     */
    public MultipleChoiceQuestion(
            String aQuestion, String theAnswer,
            String[] theWrongAnswers,
            @TriviaGame.Difficulty String level) {
        super(aQuestion, level);
        this.rightAnswer = theAnswer;
        this.wrongAnswers = theWrongAnswers;
    }


    /**
     * Standard constructor; automatically sets the difficulty and category to Unknown
     *
     * @param aQuestion the question to be asked
     * @param theAnswer the answer to the question
     * @param theWrongAnswers a series of wrong answers to add in the mix
     *
     */
    public MultipleChoiceQuestion(
            String aQuestion,
            String theAnswer,
            String[] theWrongAnswers) {
        super(aQuestion, TriviaGame.Difficulty.UNKNOWN);
        this.rightAnswer = theAnswer;
        this.wrongAnswers = theWrongAnswers;
    }

    /**
     * Returns all the choices to choose from, shuffled in a random order
     *
     * @return a list of human-readable possible answers to the question
     */
    @Override
    public List<String> getAnswers() {
        List<String> choices = new ArrayList<>();
        choices.add(rightAnswer);
        choices.addAll(Arrays.asList(wrongAnswers));
        Collections.shuffle(choices);
        return choices;
    }

    /**
     * Shows the correct answer
     *
     * @return String containing the right answer to the question
     */
    @Override
    public String getRightAnswer() {
        return this.rightAnswer;
    }

    /**
     * Checks if the answer given is the right one
     *
     * @param  answer the answer given by the user to check against the expected answer
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    @Override
    public <T extends Comparable> boolean checkAnswer(T answer) {
        return answer.equals(rightAnswer);
    }
}