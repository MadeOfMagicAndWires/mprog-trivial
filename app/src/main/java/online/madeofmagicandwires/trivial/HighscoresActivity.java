package online.madeofmagicandwires.trivial;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import online.madeofmagicandwires.trivial.HighscoresFragment;

public class HighscoresActivity extends AppCompatActivity {

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
