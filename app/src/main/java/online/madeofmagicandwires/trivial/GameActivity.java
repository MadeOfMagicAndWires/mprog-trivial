package online.madeofmagicandwires.trivial;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class GameActivity extends AppCompatActivity implements
        TriviaRequestHelper.SessionTokenRequestListener {

    public interface GameView {
        void showNextQuestion();
    }

    public static String GAME_FRAGMENT_TAG = "GAME_FRAGMENT";


    private TriviaRequestHelper request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FragmentTransaction changes = getSupportFragmentManager().beginTransaction();
        changes.replace(R.id.game_fragment, GameFragment.newInstance(), GAME_FRAGMENT_TAG);
        changes.addToBackStack(GAME_FRAGMENT_TAG);
        changes.commit();
        getSupportFragmentManager().executePendingTransactions();

        initTriviaGame();


    }


    /**
     * Initiates the TriviaGame and TriviaRequestHelper
     */
    public void initTriviaGame(){
        request = TriviaRequestHelper.getInstance(getApplicationContext());
        request.requestSessionToken(this);

    }

    /**
     * Tests showing the placeholder before the content is loaded.
     */
    public void testShowNextQuestion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Fragment frag =  getSupportFragmentManager().findFragmentById(R.id.game_fragment);
                    if(frag instanceof GameFragment) {
                        ((GameFragment) frag).showNextQuestion();
                        Log.d("testShowNext", "showNextQuestion succesfully called");
                    } else {
                        Log.e("testShowNext", "GameFragment not Added!");
                    }
                }

            }
        }).start();
    }


    /**
     * called when an error occurs during a request to the OpenTrivia API
     *
     * @param lastRequest the endpoint of the request;
     *                    note that this might not be entirely accurate due to async requests
     * @param errorMsg    the error message included.
     */
    @Override
    public void OnResponseError(String lastRequest, @Nullable String errorMsg) {
        Log.e("GameActivity", "request to " + lastRequest + " failed!");
        if(errorMsg != null) {
            Log.e("GameActivity", errorMsg);
        }
    }

    /**
     * called when a TriviaDB session request has been successfully requeste
     *
     * @param token the session token retrieved
     */
    @Override
    public void OnTokenRequestSuccess(String token) {
        Log.i("TriviaRequestHelper", "Succesfully retrieved session token '" + token + "'");
    }
}
