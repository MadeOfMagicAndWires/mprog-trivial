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
     * Standard constructor
     * @param aQuestion the question to be asked
     * @param theAnswer the answer to the question
     * @param theWrongAnswers a series of wrong answers to add in the mix
     */
    public MultipleChoiceQuestion(String aQuestion, String theAnswer, String[] theWrongAnswers) {
        super(TriviaQuestion.Type.MULTIPLE, aQuestion, TriviaGame.Difficulty.UNKNOWN);
        this.rightAnswer = theAnswer;
        this.wrongAnswers = theWrongAnswers;
    }

    /**
     * Standard constructor
     * @param aQuestion the question to be asked
     * @param theAnswer the answer to the question
     * @param theWrongAnswers a series of wrong answers to add in the mix
     */
    public MultipleChoiceQuestion(
                                  String aQuestion, String theAnswer,
                                  String[] theWrongAnswers,
                                  @TriviaGame.Difficulty String level) {
        super(TriviaQuestion.Type.MULTIPLE, aQuestion, level);
        this.rightAnswer = theAnswer;
        this.wrongAnswers = theWrongAnswers;
    }

    /**
     * Returns all the choices to choose from, shuffled in a random order.
     * @return a list of human-readable possible answers to the question
     */
    public List<String> getAnswers() {
        List<String> choices = new ArrayList<>();
        choices.add(rightAnswer);
        choices.addAll(Arrays.asList(wrongAnswers));
        Collections.shuffle(choices);
        return choices;
    }

    /**
     * Shows the correct answer
     * @return String containing the right answer to the question
     */
    @Override
    public String getRightAnswer() {
        return this.rightAnswer;
    }

    /**
     * Checks if the answer given is the right one
     *
     * @param answer
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    @Override
    public <T> boolean checkAnswer(T answer) {
        return answer.equals(rightAnswer);
    }
}