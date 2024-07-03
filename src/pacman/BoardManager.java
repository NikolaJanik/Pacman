package pacman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/*
A class used to manage the board.
Takes images of board elements and saves them in the array board.
Loads a map file and saves it in a two-dimensional array mapCellNum.
Draws a map using the setIcon method and JLabel components,
which are placed on the board thanks to loops and conditional expressions.
 */
public class BoardManager {
    GameViewPanel gameViewPanel;
    String mapPath;
    GameBoard[] board;
    int[][] mapCellNum;
    private int remainingPoints;


    public BoardManager(GameViewPanel gameViewPanel, String mapPath) {
        this.gameViewPanel = gameViewPanel;
        this.mapPath = mapPath;
        board = new GameBoard[8];
        mapCellNum = new int[gameViewPanel.ROWS][gameViewPanel.COLS];

        getBoardImage();
        getMap(mapPath);

        this.remainingPoints = calculateInitialPoints();
    }

    /*
    The getBoardImage() method loads images,
    saves them in the array, which accepts GameBoard objects.
    GameBoard objects contain image, icon, isCollision, isImprovement fields.
    Images are read using ImageIO.read(),
    then they are scaled and converted to the Icon type.
     */
    public void getBoardImage(){

        try{
            board[0] = new GameBoard();
            board[0].image = ImageIO.read(new File("images/board/space.png"));
            Image scaledImg0 = board[0].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[0].icon = new ImageIcon(scaledImg0);
            board[0].isCollision = false;

            board[1] = new GameBoard();
            board[1].image = ImageIO.read(new File("images/board/wall.png"));
            Image scaledImg1 = board[1].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[1].icon = new ImageIcon(scaledImg1);
            board[1].isCollision = true;

            board[2] = new GameBoard();
            board[2].image = ImageIO.read(new File("images/board/point.png"));
            Image scaledImg2 = board[2].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[2].icon = new ImageIcon(scaledImg2);
            board[2].isCollision = true;

            board[3] = new GameBoard();
            board[3].image = ImageIO.read(new File("images/board/freeze.png"));
            Image scaledImg3 = board[3].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[3].icon = new ImageIcon(scaledImg3);
            board[3].isImprovement = true;

            board[4] = new GameBoard();
            board[4].image = ImageIO.read(new File("images/board/heart.png"));
            Image scaledImg4 = board[4].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[4].icon = new ImageIcon(scaledImg4);
            board[4].isImprovement = true;

            board[5] = new GameBoard();
            board[5].image = ImageIO.read(new File("images/board/kill.png"));
            Image scaledImg5 = board[5].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[5].icon = new ImageIcon(scaledImg5);
            board[5].isImprovement = true;

            board[6] = new GameBoard();
            board[6].image = ImageIO.read(new File("images/board/invisible.png"));
            Image scaledImg6 = board[6].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[6].icon = new ImageIcon(scaledImg6);
            board[6].isImprovement = true;

            board[7] = new GameBoard();
            board[7].image = ImageIO.read(new File("images/board/double.png"));
            Image scaledImg7 = board[7].image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
            board[7].icon = new ImageIcon(scaledImg7);
            board[7].isImprovement = true;


        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Cannot load images");
            JOptionPane.showMessageDialog(gameViewPanel, "Cannot load images" , "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    /*
    The getMap() method loads a CSV file from the maps folder.
    Writes the values (0-empty cell, 1-wall, 2-point)
    to a two-dimensional array mapCellNum.
     */
    public void getMap(String fileName) {

        String cvsSplitBy = ";";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            int row = 0;
            String line;

            while ((line = br.readLine()) != null) {
                line = line.replace("\uFEFF", "");  //remove Byte Order Mark
                String[] records = line.split(cvsSplitBy);
                for(int col = 0; col < gameViewPanel.COLS && col < records.length; col++){
                    int record = Integer.parseInt(records[col]);
                    mapCellNum[row][col] = record;
                }
                row++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(gameViewPanel, "Cannot load map" , "ERROR", JOptionPane.ERROR_MESSAGE);

        }
    }

    //DRAWING MAP
    public void draw() {

        //Player image and position
        Image playerImage = gameViewPanel.player.draw();
        int playerRow = gameViewPanel.player.row;
        int playerCol = gameViewPanel.player.col;

        //Drawing wall and points
        for (int row = 0; row < gameViewPanel.ROWS - 1; row++) {
            for (int col = 0; col < gameViewPanel.COLS; col++) {
                int cellNum = mapCellNum[row][col];
                JLabel cellLabel = new JLabel();
                if (cellNum > 0) {
                    cellLabel.setIcon(board[cellNum].icon);
                }
                //Drawing player
                if(row == playerRow && col == playerCol){
                    cellLabel.setIcon(new ImageIcon(playerImage));
                }
                //Drawing ghosts
                for(Ghost ghost : gameViewPanel.ghosts){
                    if(ghost.row == row && ghost.col == col && ghost.image != null){
                        cellLabel.setIcon(new ImageIcon(ghost.image));
                    }
                }
                //Drawing improvements
                for(Improvement improvement : gameViewPanel.improvements){
                    if(improvement.row == row && improvement.col == col && improvement.image != null){
                        Image scaledImage = improvement.image.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
                        cellLabel.setIcon(new ImageIcon(scaledImage));
                    }
                }
                gameViewPanel.add(cellLabel);
            }
        }
        //Drawing the last one row: menuButton, scores, time, lives (on specific positions)
        for(int col = 0; col < gameViewPanel.COLS; col++) {
            if(col == 0) {
                addButton();
            } else if(col == 1) {
                JLabel labelScore = new JLabel("Score");
                labelScore.setFont(gameViewPanel.font);
                labelScore.setForeground(Color.WHITE);
                gameViewPanel.add(labelScore);
            } else if( col == 2) {
                gameViewPanel.add(gameViewPanel.printScore());
            } else if( col == gameViewPanel.COLS /2 - 1) {
                JLabel labelTime = new JLabel("Time");
                labelTime.setFont(gameViewPanel.font);
                labelTime.setForeground(Color.WHITE);
                gameViewPanel.add(labelTime);
            } else if(col == gameViewPanel.COLS /2 ) {
                gameViewPanel.add(gameViewPanel.printTime());
            } else if(col == gameViewPanel.COLS - 2) {
                JLabel labelLives = new JLabel("Lives:");
                labelLives.setFont(gameViewPanel.font);
                labelLives.setForeground(Color.WHITE);
                gameViewPanel.add(labelLives);
            }
            else if(col == gameViewPanel.COLS - 1) {
                gameViewPanel.add(gameViewPanel.printLives());
            } else {
                gameViewPanel.add(new JLabel());
            }
        }
    }

    /*
    The calculateInitialPoints() method
    calculates the initial number of points on the board.
     */
    private int calculateInitialPoints() {
        int points = 0;
        for (int row = 0; row < gameViewPanel.ROWS - 1; row++) {
            for (int col = 0; col < gameViewPanel.COLS; col++) {
                if (mapCellNum[row][col] == 2) {
                    points += 1;
                }
            }
        }
        return points;
    }

    /*
    The getRemainingPoints() method returns
    the current number of points to collect from the board.
     */
    public int getRemainingPoints() {

        return remainingPoints;
    }

    /*
    The collectPoint() method is called every time
    when the player collects a point from the board
    and reduces the number of remainingPoints by one.
     */
    public void collectPoint() {

        remainingPoints--;
    }

    /*
    The addButton() method creates a menuButton of the JButton type,
    which allows user to end the game at any time
    and return to the startPanel. menuButton is placed
    at the bottom of the board on the left side.
     */
    private void addButton(){
        JButton menuButton = new JButton();
        menuButton.setText("Menu");
        menuButton.setFont(gameViewPanel.font);

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                gameViewPanel.returnToStartFrame();
            }
        });
        gameViewPanel.add(menuButton);
    }
}

