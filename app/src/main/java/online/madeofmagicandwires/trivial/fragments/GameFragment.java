package online.madeofmagicandwires.trivial.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.activities.GameActivity;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameFragment extends Fragment implements GameActivity.GameView {

    // TODO: move from OnWIndowFocus to VIew.ONFOCUSListener()?

    public static final String GAME_FRAGMENT_TAG = "GAME_FRAGMENT";

    // TODO what to do about GameFragment and GameActivity??

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
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
        return root;
    }

    /**
     * Toggles the root view of this Fragment into fullscreen mode
     *
     */
    @SuppressWarnings("ConstantConditions")
    public void toggleFullscreen(boolean hasFocus) {
        Log.d(getClass().getSimpleName(), "toggleFullscreen ran!");
        if(isAdded()) {
            if(hasFocus) {
                // focus came back, go fullscreen
                getView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );
            } else {
                // otherwise stop going fullscreen
                getView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );
            }
        }
    }

    /**
     * Toggles the visibility of placeholder/loading screen views
     * @param visibilityState the visibility state to be
     */
    @SuppressWarnings("ConstantConditions")
    private void togglePlaceholderView(int visibilityState) {
        {
            try{
                getView().findViewById(R.id.game_fragment_placeholder).setVisibility(View.GONE);
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
            categoryView.setChipBackgroundColor(getActivity().getColorStateList(R.color.colorAccent));
            questionView.setText(question.getQuestion());
            questionView.setBackground(null);

        } else {
            Log.e(getClass().getSimpleName(), "fragment not added to activity");
        }

    }




    // TODO: Bind new question
    // isAdded makes sure getActivity and getView() are always available
    @SuppressWarnings("ConstantConditions")
    @Override
    public void showNextQuestion(TriviaQuestion question) {
        if(isAdded()) {
            getActivity().runOnUiThread(() -> {
                bindData(question);
                togglePlaceholderView(View.GONE);
            });
        }

    }
}
