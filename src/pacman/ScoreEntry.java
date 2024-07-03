package pacman;

import java.io.Serializable;

/*
Objects of this class store the user's name, score and game time.
 */
public class ScoreEntry implements Serializable, Comparable<ScoreEntry> {

    private String userName;
    private int score;
    private long time;

    public ScoreEntry(String userName, int score, long time) {
        this.userName = userName;
        this.score = score;
        this.time = time;
    }

    /*
    The compareTo() method is necessary to using
    a sort() method on score in Ranking class.
     */
    @Override
    public int compareTo(ScoreEntry other) {
        int scoreComparison = Integer.compare(other.score, this.score);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        return Long.compare(this.time, other.time);
    }

    /*
    The formatTime() method set a time format
    which will be used to write it in a JList.
     */
    private String formatTime(long time) {
        long minutes = (time / 1000) / 60;
        long seconds = (time / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return String.format("User: %s, Score: %d, Time: %s", userName, score, formatTime(time));
    }
}
