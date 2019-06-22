package online.madeofmagicandwires.trivial.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import online.madeofmagicandwires.trivial.fragments.HighscoresFragment;
import online.madeofmagicandwires.trivial.R;


public class HighscoresActivity extends AppCompatActivity {

    public static final String HIGHSCORE_INTENT_SCORE_TAG = "score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscores_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, HighscoresFragment.newInstance())
                    .commitNow();
        }
    }
}
