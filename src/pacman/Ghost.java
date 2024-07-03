package pacman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/*
The class extends the Game Components class. The class loads ghost images,
allows objects of this class to move and create Improvements.
 */
public class Ghost extends GameComponent{

    GameViewPanel gameViewPanel;
    BufferedImage red, green, blue, yellow;
    Image redScaled, greenScaled, blueScaled, yellowScaled, image;
    String color;
    int randomDirection;
    int framesToChangeDirection = 0;
    boolean isAbleToKillPacman = true;
    boolean isAbleToMove = true;
    Random rand = new Random();
    long time = System.currentTimeMillis();

    public Ghost(GameViewPanel gameViewPanel, String color){
        this.gameViewPanel = gameViewPanel;
        this.color = color;

        setDefaultValues();
        getGhostImage();
        image = draw(this.color);
    }

    /*
    The setDefaultValues() method sets the object to the appropriate starting position
    and sets the direction and randomDirection variables.
     */
    public void setDefaultValues() {
        row = gameViewPanel.ROWS /2 - 1;
        col = gameViewPanel.COLS /2;
        speed = 1;
        direction = "up";
        randomDirection = (int) (Math.random() * 4);
    }

    /*
    The getGhostImage() method loads images of Ghosts in different colors.
     */
    public void getGhostImage(){
        try {
            red = ImageIO.read(new File("images/ghosts/redGhost.png"));
            green = ImageIO.read(new File("images/ghosts/greenGhost.png"));
            blue = ImageIO.read(new File("images/ghosts/blueGhost.png"));
            yellow = ImageIO.read(new File("images/ghosts/yellowGhost.png"));

        } catch (IOException e) {
            System.out.println("Ghost image not found");
            JOptionPane.showMessageDialog(gameViewPanel, "Cannot load images" , "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        scaleImage();
    }

    /*
    The scaleImage() method scales images of Ghosts.
     */
    public void scaleImage(){
        redScaled = red.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        greenScaled = green.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        blueScaled = blue.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        yellowScaled = yellow.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
    }

    /*
    The draw() method returns scaled images
    of Ghosts depending on their color.
     */
    public Image draw(String color){
        switch (color) {
            case "red":
                return redScaled;
            case "blue":
                return blueScaled;
            case "green":
                return greenScaled;
            case "yellow":
                return yellowScaled;
            default:
                return null;
        }
    }

    /*
    The update() method checks the checkPacmanCollision and checkWallCollision.
    In this method there are called throwImprovement(), chooseDirection()
    and moveInCurrentDirection() methods.
     */
    public void update(){

        gameViewPanel.collision.checkPacmanCollision(this);

        if(isAbleToMove){
            throwImprovement();

            chooseDirection();
            collisionOn = false;
            gameViewPanel.collision.checkWallCollision(this);

            if(!collisionOn){
                moveInCurrentDirection();
            }
        }
        gameViewPanel.collision.checkPacmanCollision(this);
    }

    /*
    The chooseDirection() method randomly choose one of four
    possible directions: up, down, left, right.
     */
    private void chooseDirection() {
        if (framesToChangeDirection == 0 || collisionOn) {
            randomDirection = (int) (Math.random() * 4);
            framesToChangeDirection = 5; // TO CHANGE THE DIRECTION EVEN IF THERE IS NO COLLISION
        } else {
            framesToChangeDirection--;
        }

        switch (randomDirection) {
            case 0:
                direction = "up";
                break;
            case 1:
                direction = "down";
                break;
            case 2:
                direction = "left";
                break;
            case 3:
                direction = "right";
                break;
        }
    }

    /*
    The moveInCurrentDirection() method changes object's position
    depending on direction.
     */
    private void moveInCurrentDirection() {
        switch (direction) {
            case "up":
                row -= speed;
                break;
            case "down":
                row += speed;
                break;
            case "left":
                col -= speed;
                break;
            case "right":
                col += speed;
                break;
        }
    }

    /*
    The throwImprovement() method allows ghosts to produce in
    every 5 seconds with probability 0.25% one of five possible improvement.
     */
    private void throwImprovement(){

        long elapsedTime = System.currentTimeMillis() - time;
        int seconds = (int) (elapsedTime / 1000) % 60;

        if(seconds == 5){
            int chance = rand.nextInt(4);
            if(chance == 0){
                int improvement = rand.nextInt(5);
                BufferedImage improvementImage = null;
                String type = null;
                switch (improvement) {
                    case 0:
                        improvementImage = gameViewPanel.boardManager.board[3].image;
                        type = "freezeGhost";
                        break;
                    case 1:
                        improvementImage = gameViewPanel.boardManager.board[4].image;
                        type = "addLife";
                        break;
                    case 2:
                        improvementImage = gameViewPanel.boardManager.board[5].image;
                        type = "killGhost";
                        break;
                    case 3:
                        improvementImage = gameViewPanel.boardManager.board[6].image;
                        type = "pacmanInvisible";
                        break;
                    case 4:
                        improvementImage = gameViewPanel.boardManager.board[7].image;
                        type = "doublePoint";
                        break;
                }
                if (improvementImage != null) {
                    gameViewPanel.addImprovement(new Improvement(col, row, improvementImage, type));
                }
            }
            time = System.currentTimeMillis();
        }
    }
}
