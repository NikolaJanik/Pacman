package pacman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
The class loads Player images, sets its position,
and handles functions that implement Improvement produced by Ghosts.
 */
public class Player extends GameComponents {

    GameViewPanel gameViewPanel;
    GameKeyAdapter gameKeyAdapter;
    BufferedImage up, down, left, right, neutral;
    Image upScaled, downScaled, leftScaled, rightScaled, neutralScaled;
    int score;
    int lives;
    boolean isDoublePoint = false;
    boolean isPacmanInvisible = false;
    boolean isKillGhost = false;
    boolean isAddLife = false;
    boolean isFreezeGhost = false;


    public Player(GameViewPanel gameViewPanel, GameKeyAdapter gameKeyAdapter){
        this.gameViewPanel = gameViewPanel;
        this.gameKeyAdapter = gameKeyAdapter;
        lives = 3;
        score = 0;
        
        setDefaultValues();
        getPlayerImage();
    }

    /*
    The setDefaultValues() method sets the Player to
    the default starting position and sets the direction to "neutral".
     */
    public void setDefaultValues(){
        col = 1;
        row = 1;
        speed = 1;
        direction = "neutral";
    }

    /*
    The getPlayerImage() method loads
    Player images for different directions.
     */
    public void getPlayerImage(){
        try {
            up = ImageIO.read(new File("images/up.png"));
            down = ImageIO.read(new File("images/down.png"));
            left = ImageIO.read(new File("images/left.png"));
            right = ImageIO.read(new File("images/right.png"));
            neutral = ImageIO.read(new File("images/default.png"));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Player image not found");
            JOptionPane.showMessageDialog(gameViewPanel, "Cannot load images" , "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        scaleImage();
    }

    /*
    The scaleImage() method scales Player images.
     */
    public void scaleImage(){
        upScaled = up.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        downScaled = down.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        leftScaled = left.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        rightScaled = right.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
        neutralScaled = neutral.getScaledInstance(gameViewPanel.cellSize, gameViewPanel.cellSize, Image.SCALE_SMOOTH);
    }

    /*
    The update() method sets the direction based on the values
    taken from the keyAdapter. It checks wallCollision and
    changes the Player position according to the direction.
    It checks checkWallCollision and checkImprovementCollision.
    After changing position, a collision with Ghost is checked
    because the Player can run into Ghost when it has not moved
    (Ghost will not check this condition then).
    Final conditionals will check
    if any Improvements have been collected.
     */
    public void update(){
        if(gameKeyAdapter.goDown){
            direction = "down";
        } else if(gameKeyAdapter.goUp){
            direction = "up";
        } else if(gameKeyAdapter.goLeft){
            direction = "left";
        } else if(gameKeyAdapter.goRight){
            direction = "right";
        }

        // CHECK COLLISION
        collisionOn = false;
        gameViewPanel.collision.checkWallCollision(this);

        if(!collisionOn) {
            switch (direction){
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

        gameViewPanel.collision.checkPointCollision(this);
        gameViewPanel.collision.checkImprovementCollision(this);

        for (Ghost ghost : gameViewPanel.ghosts) {
            gameViewPanel.collision.checkPacmanCollision(ghost);
        }

        if(isDoublePoint){
            doublePoints();
            isDoublePoint = false;
        }
        if(isPacmanInvisible){
            invisible();
            isPacmanInvisible = false;
        }
        if(isKillGhost){
            killGhost();
            isFreezeGhost = false;
        }
        if(isFreezeGhost){
            freezeGhost();
            isFreezeGhost = false;
        }
        if(isAddLife){
            addLife();
            isAddLife = false;
        }
    }

    /*
    The draw() method returns
    the appropriate image to the direction
     */
    public Image draw(){
        Image image = neutralScaled;
        switch(direction){
            case "up":
                image = upScaled;
                break;
            case "down":
                image = downScaled;
                break;
            case "left":
                image = leftScaled;
                break;
            case "right":
                image = rightScaled;
                break;
            default:
                image = neutralScaled;
                break;
            }

        direction = "neutral";

        return image;
    }

    /*
    The addLife() method called after
    collecting Improvement, adds one life.
     */
    public void addLife(){
        this.lives += 1;
    }

    /*
    The doublePoints() method called after
    collecting an Improvement, allows the Player
    to collect points with double their value for 5 seconds.
     */
    public synchronized void doublePoints(){
        isDoublePoint = true;
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (gameViewPanel) {
                isDoublePoint = false;
            }
        }).start();
    }

    /*
    The killGhost() method invoked after collecting an Improvement,
    allows the Player to kill the Ghost for 5 seconds.
    The isKillGhost variable is used in the Collision class.
    After the collision, Ghost is set to default values and
    Player's score increases by 50.
     */
    public synchronized void killGhost(){
        isKillGhost = true;
        for (Ghost ghost : gameViewPanel.ghosts) {
            ghost.isAbleToKillPacman = false;
        }
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (gameViewPanel) {
                for (Ghost ghost : gameViewPanel.ghosts) {
                    ghost.isAbleToKillPacman = true;
                }
                isKillGhost = false;
            }
        }).start();
    }

    /*
    The invisible() method called after
    collecting an Improvement, allows the Player to
    pass through Ghosts without collisions for 5 seconds.
     */
    public synchronized void invisible(){
        for (Ghost ghost : gameViewPanel.ghosts) {
            ghost.isAbleToKillPacman = false;
        }
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (gameViewPanel) {
                for (Ghost ghost : gameViewPanel.ghosts) {
                    ghost.isAbleToKillPacman = true;
                }
            }
        }).start();
    }

    /*
    The freezeGhost() method called after
    collecting Improvement, causes Ghosts
    to be unable to change position for 5 seconds.
     */
    public synchronized void freezeGhost(){
        for (Ghost ghost : gameViewPanel.ghosts) {
            ghost.isAbleToMove = false;
        }
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (gameViewPanel) {
                for (Ghost ghost : gameViewPanel.ghosts) {
                    ghost.isAbleToMove = true;
                }
            }
        }).start();
    }
}
