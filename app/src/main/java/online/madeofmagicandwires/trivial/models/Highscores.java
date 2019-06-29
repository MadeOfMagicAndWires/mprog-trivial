package online.madeofmagicandwires.trivial.models;

import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;


import java.util.Set;

public class Highscores {

    public static class Score extends online.madeofmagicandwires.trivial.models.Score {
        public Score(int score, int position, String name) {
            super(score, position, name);
        }
    }

    private SparseArrayCompat<String> scores;

    public Highscores(SparseArrayCompat<String> highscores) {
        this.scores = highscores;
    }

    public Highscores() {
        this.scores = new SparseArrayCompat<>();
    }

    public void setScores(SparseArrayCompat<String> scores) {
        this.scores = scores;
    }

    public SparseArrayCompat<String> getScores() {
        return scores;
    }

    public void addScore(int score, String name) {
        scores.append(score, name);
    }

    public void setScore(int score, String name) {
        scores.put(score, name);
    }

    // TODO: more single score manipulation methods

    public Score getScoreAt(int index) {
        return new Score(scores.keyAt(index), index, scores.valueAt(index));
    }
}
