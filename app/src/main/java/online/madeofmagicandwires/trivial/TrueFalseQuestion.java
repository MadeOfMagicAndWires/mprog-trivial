package online.madeofmagicandwires.trivial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrueFalseQuestion extends TriviaQuestion {

    private boolean correctAnswer;

    /**
     * Most verbose constructor, sets the difficulty and category of the question
     *
     * @param question   the question to be asked
     * @param difficulty the difficulty level of the question
     * @param category   the category the question belongs to
     */
    public TrueFalseQuestion(String question, boolean correctAnswer, String difficulty, String category) {
        super(question, difficulty, category);
        this.correctAnswer = correctAnswer;
    }

    /**
     * Less verbose constructor, automatically sets the category to "Unknown"
     *
     * @param question   the question to be asked
     * @param difficulty the difficulty of the question
     */
    public TrueFalseQuestion(String question, boolean correctAnswer, String difficulty) {
        super(question, difficulty);
        this.correctAnswer = correctAnswer;

    }

    /**
     * Most succinct constructor, automatically sets difficulty and category to "Unknown"
     *
     * @param question the question to be asked
     */
    public TrueFalseQuestion(String question, boolean correctAnswer) {
        super(question);
        this.correctAnswer = correctAnswer;
    }

    /**
     * Checks if the answer given is the right one
     *
     * @param  answer the answer given by the user to check against the expected answer
     * @return a boolean revealing whether the answer was correct; true if it was, false if it wasn't
     */
    @Override
    public <T extends Comparable> boolean checkAnswer(T answer) {
        return answer.equals(correctAnswer);
    }

    /**
     * Shows the correct answer in human-readable string format
     *
     * @return String containing the right answer to the question
     */
    @Override
    public String getRightAnswer() {
        return Boolean.toString(correctAnswer);
    }


    /**
     * Returns all the choices to choose from, shuffled in a random order
     *
     * @return a list of human-readable possible answers to the question
     */
    @Override
    public List<String> getAnswers() {
        List<String> choices = new ArrayList<>();
        choices.add(Boolean.toString(correctAnswer));
        choices.add(Boolean.toString(!correctAnswer));
        Collections.shuffle(choices);
        return choices;
    }
}
