package online.madeofmagicandwires.trivial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class GameActivity extends AppCompatActivity {

    public static String GAME_FRAGMENT_TAG = "GAME_FRAGMENT";

    public interface GameView {
        void showNextQuestion();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FragmentTransaction changes = getSupportFragmentManager().beginTransaction();
        changes.replace(R.id.game_fragment, GameFragment.newInstance(), GAME_FRAGMENT_TAG);
        changes.addToBackStack(GAME_FRAGMENT_TAG);
        changes.commit();
        getSupportFragmentManager().executePendingTransactions();

        testShowNextQuestion();

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


}
