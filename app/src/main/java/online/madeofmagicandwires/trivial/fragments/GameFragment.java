package online.madeofmagicandwires.trivial.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.activities.GameActivity;
import online.madeofmagicandwires.trivial.adapters.AnswerViewHolder;
import online.madeofmagicandwires.trivial.adapters.AnswersAdapter;
import online.madeofmagicandwires.trivial.models.MultipleChoiceQuestion;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements GameActivity.GameView {

    public static final String GAME_FRAGMENT_TAG = "GAME_FRAGMENT";

    private TriviaQuestion question;

    public GameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameFragment.
     */

    public static GameFragment newInstance() {
        return new GameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("OnCreateView", "onCreateView has been called");
         View root =  inflater.inflate(R.layout.fragment_game, container, false);

         //set fullscreen manually, getView() is not available yet
        root.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        return root;
    }

    /**
     * Toggles the visibility of placeholder/loading screen views
     * @param visibilityState the visibility state to be
     */
    @SuppressWarnings("ConstantConditions")
    public void togglePlaceholderView(int visibilityState) {
        {
            try{
                // remove progressbar view
                getView().findViewById(R.id.game_fragment_placeholder).setVisibility(visibilityState);

                if(visibilityState == View.GONE) {
                    // remove placeloader elements for question and category
                    getView().findViewById(R.id.question_text).setForeground(null);
                    Chip category = getView().findViewById(R.id.categoryChip);
                    category.setChipBackgroundColor(getActivity().getColorStateList(R.color.colorAccent));
                } else {
                    // show placeholder elements for question and category
                    getView().findViewById(R.id.question_text).setForeground(
                            getActivity().getDrawable(R.drawable.rounded_corner_background));
                    Chip category = getView().findViewById(R.id.categoryChip);
                    category.setChipBackgroundColor(
                            getActivity().getColorStateList(R.color.lighter_grey));
                    category.setText(null);
                }
            } catch (NullPointerException e) {
                Log.e(getClass().getSimpleName(), "Could not find root fragment view");
                e.printStackTrace();
            }
        }
    }

    /**
     * Binds question data to the relevant views
     * @param question the question whose data to bind
     */
    @SuppressWarnings("ConstantConditions")
    private void bindData(TriviaQuestion question) {
        if(isAdded()) {
            Chip categoryView = getView().findViewById(R.id.categoryChip);
            TextView questionView = getView().findViewById(R.id.question_text);
            RecyclerView answersContainer = getView().findViewById(R.id.answers_container);

            categoryView.setText(question.getCategory());
            questionView.setText(question.getQuestion());


            if(answersContainer != null) {
                // use grid layout for multiple choice, or linear for 'true or false'
                RecyclerView.LayoutManager manager;

                if(question instanceof MultipleChoiceQuestion) {
                    manager = new GridLayoutManager(getContext(), 2);
                } else {
                    manager = new LinearLayoutManager(getContext());
                }
                answersContainer.setLayoutManager(manager);
                // create adapter if needed and add answers
                if(answersContainer.getAdapter() == null) {
                    AnswersAdapter adapter = new AnswersAdapter(
                            this,
                            R.layout.question_answer_tile,
                            question.getAnswers());
                    answersContainer.setAdapter(adapter);
                } else {
                    if(answersContainer.getAdapter() instanceof AnswersAdapter) {
                        ((AnswersAdapter) answersContainer.getAdapter()).setAnswers(question.getAnswers());
                    }
                }
            }
        } else {
            Log.e(getClass().getSimpleName(), "fragment not added to activity");
        }

    }

    /**
     * Implementation of the {@link GameActivity.GameView} interface
     * binding a question's data to the fragment
     *
     * @param question the TriviaQuestion instance containing the data to represent
     */
    // isAdded makes sure getActivity and getView() are always available
    @SuppressWarnings("ConstantConditions")
    @Override
    public void setQuestion(TriviaQuestion question) {
        if(isAdded()) {
            getActivity().runOnUiThread(() -> {
                bindData(question);
                togglePlaceholderView(View.GONE);
            });
            this.question = question;
        }

    }


    /**
     * Returns the TriviaQuestion of this fragment
     * @return the triviaquestion instance whose data is being represented by this fragment
     */
    @Override
    public TriviaQuestion getQuestion() {
        return this.question;
    }

    /**
     * Called when the user has picked an answer to the question,
     * which needs to be checked against the correct one
     *
     * @param answer the answer picked by the user
     */
    @SuppressWarnings("ConstantConditions")
    public <T extends  Comparable> void userPickedAnswer(T answer) {
        // check against correct answer
        if(question != null) {
            boolean pickedCorrectAnswer;
            if(question instanceof MultipleChoiceQuestion) {
                pickedCorrectAnswer = question.checkAnswer(answer);
            } else {
                pickedCorrectAnswer = question.checkAnswer(Boolean.valueOf((String) answer));
            }


            // save the correct answer value before notifying GameActivity,
            // as it might move on to the next question
            String correctAnswer = question.getRightAnswer();

            // then pass on the result to GameActivity, which will handle the actual game actions
            if(isAdded() && getActivity() instanceof GameActivity) {
                ((GameActivity) getActivity()).OnUserPickedAnswer(pickedCorrectAnswer);
            }

            // either way, show Snackbar
            String msg;
            if(pickedCorrectAnswer) {
                msg = "Good job, you got the correct answer!";
            } else {
                msg = "Almost! The correct answer was " + correctAnswer;
            }
            if(isAdded()) {
                Snackbar sb = Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG);
                sb.show();
            }
        }
    }
}
