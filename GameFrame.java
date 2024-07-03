package pacman;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
This class creates a window with the game panel.
In the constructor, it takes the path to the map file selected by the user.
 */

public class GameFrame extends JFrame {

    String mapPath;
    int[] panelSize;

    public GameFrame(String mapPath) {
        this.mapPath = mapPath;
        panelSize = getColsRows();
        pack();
        setTitle("Pacman");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        GameViewPanel gamePanel = new GameViewPanel(this.mapPath, panelSize);
        add(gamePanel);
        gamePanel.requestFocusInWindow();
        pack();
        setLocationRelativeTo(null);
        validate();
        repaint();
    }

    /*
    The getColsRows() method counts how many columns and rows a map has
    to use this data to create the game panel.
     */
    public int[] getColsRows() {
        int row = 1;
        int col = 0;
        String cvsSplitBy = ";";
        try {
            BufferedReader br = new BufferedReader(new FileReader(mapPath));

            String line = br.readLine();
            String[] tokens = line.split(cvsSplitBy);
            col = tokens.length;

            while ((line = br.readLine()) != null) {
                row++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cannot load map" , "ERROR", JOptionPane.ERROR_MESSAGE);

        }
        return new int[]{row, col};
    }
}


