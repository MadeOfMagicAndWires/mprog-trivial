package online.madeofmagicandwires.trivial;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


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

    }



}
