package pacman;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
The class allows you to present scores from different users.
The class has a static SCORE_FILE field that stores the file path,
which stores the game results.
 */
public class Ranking {
    private static final String SCORE_FILE = "scores.ser";

    /*
    The saveScore() method accepts an argument of type ScoreEntry.
    The method adds the object given as an argument to the results list,
    sorts the list and saves the whole thing to a file.
     */
    public static void saveScore(ScoreEntry scoreEntry) {
        List<ScoreEntry> scores = loadScores();
        scores.add(scoreEntry);
        Collections.sort(scores);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new StartFrame(), "Cannot save score in ranking" , "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    /*
    The loadScores() method returns a list storing
    objects of the ScoreEntry type. The list contains
    objects loaded from the file.
     */
    public static List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORE_FILE))) {
            scores = (List<ScoreEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File not found, no scores to load
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new StartFrame(), "Cannot load ranking" , "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return scores;
    }
}
