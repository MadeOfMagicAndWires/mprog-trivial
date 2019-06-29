package online.madeofmagicandwires.trivial.models;

class Score {
    private int score;
    private int position;
    private String name;

    public Score(int score, int position, String name) {
        this.score = score;
        this.position = position;
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
