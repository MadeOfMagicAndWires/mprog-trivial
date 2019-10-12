package online.madeofmagicandwires.trivial.models;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Container class of a single item from a HighscoreList.
 * contains the score, id, and associated name of a score
 */
class Score {
    private int score;
    private final int id;
    private String name;

    /**
     * Constructor of this object
     * @param score the numerical value of this score
     * @param id the id of this score in the highscore list
     * @param name the name of the user who obtained this score
     */
    public Score(int score, int id, @NonNull String name) {
        this.score = score;
        this.id = id;
        this.name = name;
    }

    /**
     * Returns the id of this score
     * @return the id of this score in the highscore list
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the score of this item
     * @return the score as a numerical value
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of this item
     * @param score a numerical value representing how well a user did
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the name of the user who obtained this score
     * @return the name associated with this score
     */
    public @NonNull String getName() {
        return name;
    }

    /**
     * Sets the name of the user who obtained this score
     * @param name the name associated with this score
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks whether this instance's state equals that of a different one
     * @param o the different object instance to compare state with
     * @return true if the states are exactly the same, false if they differ
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Score score1 = (Score) o;
        return getScore() == score1.getScore() &&
                getId() == score1.getId() &&
                getName().equals(score1.getName());
    }

    /**
     * Creates a hashcode of this object instance
     * @return a hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(getScore(), getId(), getName());
    }

    /**
     * Returns this score as string
     * @return a human readable string representation of this instance
     */
    @Override
    @NonNull
    public String toString() {
      return "(" + getId() + ") " + getScore() + ": " + getName();
    }
}
