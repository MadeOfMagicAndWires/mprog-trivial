package online.madeofmagicandwires.trivial.models;

import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import java.util.ArrayList;
import java.util.List;

public class HighscoreList {

    public static class Score extends online.madeofmagicandwires.trivial.models.Score {
        public Score(int id, int score, String name) {
            super(score, id, name);
        }
    }

    private SparseArrayCompat<String> scores;
    private int lastAdded;

    public HighscoreList(SparseArrayCompat<String> highscores) {
        this.scores = highscores;
        this.lastAdded = -1;
    }

    public HighscoreList() {
        this.scores = new SparseArrayCompat<>();
    }

    public void setScoreList(SparseArrayCompat<String> scores) {
        this.scores = scores;
    }

    public SparseArrayCompat<String> getScoreList() {
        return scores;
    }

    public void addScore(int score, String name) {
        scores.append(score, name);
        lastAdded = scores.indexOfKey(score);
    }

    public void setScore(int score, String name) {
        scores.put(score, name);
        lastAdded = scores.indexOfKey(score);
    }

    public boolean hasScore(int score) {
        return scores.containsKey(score);
    }

    public boolean hasScore(String name) {
        return scores.containsValue(name);
    }

    public @Nullable Score getScore(int score) {
        if(scores.containsKey(score)) {
            return new Score(score, scores.indexOfKey(score),scores.get(score));
        } else {
            return null;
        }
    }

    public @Nullable Score getScoreAt(int index) throws ArrayIndexOutOfBoundsException {
        return new Score(scores.keyAt(index), index, scores.valueAt(index));
    }

    public List<Score> getScoresByUser(String name) {
        List<Score> result = new ArrayList<>();
        for(int i=0;i<scores.size();i++){
            if(scores.valueAt(i).equals(name)) {
                result.add(new Score(scores.keyAt(i), i, scores.valueAt(i)));
            }
        }
        if(!result.isEmpty()) {
            return result;
        } else {
            return null;
        }
    }

    public int getLastAddedIndex() {
        return lastAdded;
    }

    public Score getLastAddedScore() {
        return getScoreAt(lastAdded);
    }

    public boolean isNew(Score score) {
        return score.equals(getScoreAt(lastAdded));
    }
}
