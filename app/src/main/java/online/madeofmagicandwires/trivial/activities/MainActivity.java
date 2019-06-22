package online.madeofmagicandwires.trivial.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import online.madeofmagicandwires.trivial.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        //start GameActivity
        fab.setOnClickListener(MainActivity.this::startGame);
    }

    public void startGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void showScores(View v) {
        Intent intent = new Intent(this, HighscoresActivity.class);
        intent.putExtra("HIGHSCORE_MODE", 1);
        startActivity(intent);
    }



}
