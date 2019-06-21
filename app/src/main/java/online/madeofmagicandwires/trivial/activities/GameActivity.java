package online.madeofmagicandwires.trivial.activities;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.List;

import online.madeofmagicandwires.trivial.fragments.GameFragment;
import online.madeofmagicandwires.trivial.R;
import online.madeofmagicandwires.trivial.helpers.TriviaRequestHelper;
import online.madeofmagicandwires.trivial.models.TriviaGame;
import online.madeofmagicandwires.trivial.models.TriviaQuestion;




public class GameActivity extends AppCompatActivity implements
        TriviaRequestHelper.SessionTokenResponseListener,
        TriviaRequestHelper.QuestionResponseListener {


    public interface GameView {
        void showNextQuestion(TriviaQuestion question);
    }

    private TriviaGame game;
    private TriviaRequestHelper request;
    private GameFragment currentFragment;
    private boolean startedGame;

    /**
     * Called when the activity is started
     * @param savedInstanceState Bundle of data containing objects saved from the previous instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        OnCreateGameFragment();
        initTriviaGame();
    }


    /**
     * Initiates the TriviaGame and TriviaRequestHelper instances
     */
    public void initTriviaGame(){
        game = new TriviaGame(10, TriviaGame.Difficulty.ANY, TriviaGame.QuestionType.ANY);
        request = TriviaRequestHelper.getInstance(getApplicationContext(), game);
        request.requestSessionToken(this);
    }


    /**
     * Creates a {@link GameFragment} and adds it to this activity in View {@link R.id#game_fragment}
     */
    public void OnCreateGameFragment() {
        currentFragment = GameFragment.newInstance();

        // replace fragment
        FragmentTransaction changes = getSupportFragmentManager().beginTransaction();
        changes.replace(R.id.game_fragment, currentFragment, GameFragment.GAME_FRAGMENT_TAG);
        changes.commit();

        // make sure changes are committed NOW, not "later".
        getSupportFragmentManager().executePendingTransactions();
    }

    /**
     * Actually start playing the game
     */
    public void startGame() {
        if(!startedGame) {
            // if no fragment is running, add it, then ask the first question
            Fragment gameFrag = getSupportFragmentManager().findFragmentById(R.id.game_fragment);
            if(!(gameFrag instanceof GameFragment)) {
                OnCreateGameFragment();
                if(currentFragment != null) {
                    startGame();
                }
            } else {
                ((GameFragment) gameFrag).showNextQuestion(game.getQuestion(0));
                Log.d(getClass().getSimpleName(), "Started game!");
                startedGame = true;
            }
        }
    }


    /**
     * Called when user has inputted an answer to a question
     * @param gotRightAnswer true when the user picked the correct answer to the question,
     *                       false if not
     */
    public void OnUserPickedAnswer(boolean gotRightAnswer) {
        if(gotRightAnswer) {
            // TODO: do stuff
        } else {
            // TODO: do stuff if user picked the wrong answer
        }
    }


    /**
     * called when a TriviaDB session request has been successfully requeste
     *
     * @param token the session token retrieved
     */
    @Override
    public void OnRequestTokenResponse(String token) {
        // token is saved in instance, just log it
        Log.i(getClass().getSimpleName(), "Successfully retrieved session token '" + token + "'");

        // once we've got a token, request questions
        request.requestQuestions(game.getQuestionAmount(), this);
    }

    @Override
    public void OnResetTokenResponse(String token) {
        Log.i(getClass().getSimpleName(), "Successfully reset session token '" + token + "'");

    }

    /**
     * Called when a TriviaDB question request successfully resolved
     *
     * @param questions a {@link List} of {@link TriviaQuestion} objects
     *                  representing the trivia questions that were retrieved from the TriviaDB
     * @see TriviaRequestHelper#requestQuestions(int, TriviaRequestHelper.QuestionResponseListener)
     */
    @Override
    public void OnQuestionsResponse(List<TriviaQuestion> questions) {
        Log.d(getClass().getSimpleName(), "Resolved question request");
        game.setQuestions(questions);

        // if the game hasn't started yet, start it
        startGame();
    }

    /**
     * called when an error occurs during a request to the OpenTrivia API
     *
     * @param lastRequest the endpoint of the request;
     *                    note that this might not be entirely accurate due to async requests
     * @param errorMsg    the error message included.
     */
    @Override
    public void OnErrorResponse(String lastRequest, @Nullable String errorMsg) {
        Log.e(getClass().getSimpleName(), "request to " + lastRequest + " failed!");
        if(errorMsg != null) {
            Log.e(getClass().getSimpleName(), errorMsg);
        }
    }
}
