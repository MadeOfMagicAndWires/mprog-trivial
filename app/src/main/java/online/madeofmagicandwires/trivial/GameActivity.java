package online.madeofmagicandwires.trivial;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;


public class GameActivity extends AppCompatActivity implements
        TriviaRequestHelper.SessionTokenResponseListener,
        TriviaRequestHelper.QuestionResponseListener {


    public interface GameView {
        void showNextQuestion(TriviaQuestion question);
    }
    public static String GAME_FRAGMENT_TAG = "GAME_FRAGMENT";


    private TriviaGame game;

    private TriviaRequestHelper request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FragmentTransaction changes = getSupportFragmentManager().beginTransaction();
        changes.replace(R.id.game_fragment, GameFragment.newInstance(), GAME_FRAGMENT_TAG);

        changes.commit();
        getSupportFragmentManager().executePendingTransactions();

        initTriviaGame();


    }


    /**
     * Initiates the TriviaGame and TriviaRequestHelper
     */
    public void initTriviaGame(){
        game = new TriviaGame(10, TriviaGame.Difficulty.ANY, TriviaGame.QuestionType.ANY);
        request = TriviaRequestHelper.getInstance(getApplicationContext(), game);
        request.requestSessionToken(this);
    }

    /**
     * Initiates the game
     */
    public void startGame() {
        Log.d(getClass().getSimpleName(), "Started game!");

        Fragment gameFrag = getSupportFragmentManager().findFragmentById(R.id.game_fragment);
        if(gameFrag instanceof GameFragment) {
            ((GameFragment) gameFrag).showNextQuestion(game.getCurrentQuestion());
        }

    }


    /**
     * called when a TriviaDB session request has been successfully requeste
     *
     * @param token the session token retrieved
     */
    @Override
    public void OnRequestTokenResponse(String token) {
        Log.i(getClass().getName(), "Successfully retrieved session token '" + token + "'");
        Log.d(getClass().getSimpleName(), "Requesting questions");
        request.requestQuestions(game.getQuestionAmount(), this);
    }

    @Override
    public void OnResetTokenResponse(String token) {
        Log.i(getClass().getName(), "Successfully reset session token '" + token + "'");

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
