package pacman;

import java.util.Iterator;

/*
A class used to recognize and resolve collisions.
Types of collisions supported: moving component and wall,
Player and Point, Ghost and Player, Player and Improvement
 */

public class Collision {
    GameViewPanel gameViewPanel;

    public Collision(GameViewPanel gameViewPanel) {
        this.gameViewPanel = gameViewPanel;
    }

    /*
    The checkWallCollision() method checks
    the expected position of the component
    based on the retrieved direction value.
    If the predicted position will result in entry
    component to wall (mapCellNum[][] = 1),
    then collisionOn is set to true
    and the component cannot move in that direction from that position.
     */
    public void checkWallCollision(GameComponent component) {

        int componentRow = component.row;
        int componentCol = component.col;

        int componentLeftCol = component.col - 1;
        int componentRightCol = component.col + 1;
        int componentBottomRow = component.row + 1;
        int componentTopRow = component.row - 1;

        int expectedCell;

        switch(component.direction) {
            case "up":
                expectedCell = gameViewPanel.boardManager.mapCellNum[componentTopRow][componentCol];
                if (expectedCell == 1) {
                    component.collisionOn = true;
                }
                break;
            case "down":
                expectedCell = gameViewPanel.boardManager.mapCellNum[componentBottomRow][componentCol];
                if (expectedCell == 1) {
                    component.collisionOn = true;
                }
                break;
            case "left":
                expectedCell = gameViewPanel.boardManager.mapCellNum[componentRow][componentLeftCol];
                if (expectedCell == 1) {
                    component.collisionOn = true;
                }
                break;
            case "right":
                expectedCell = gameViewPanel.boardManager.mapCellNum[componentRow][componentRightCol];
                if (expectedCell == 1) {
                    component.collisionOn = true;
                }
        }
    }

    /*
    The checkPointCollision() method allows Player to collect
    points from the board. If the position of Player and point is the same,
    then the point is removed from the board (the cell is empty),
    and the Player score increases by 10 or 20 depending on
    whether it has Improvement.
    The collectPoint() method from the BoardManager class is called,
    which reduces the total number of points that can be collected by one.
     */
    public void checkPointCollision(Player player) {

        int playerCol = player.col;
        int playerRow = player.row;

        int cellNum = gameViewPanel.boardManager.mapCellNum[playerRow][playerCol];

        if (cellNum == 2) {
            gameViewPanel.boardManager.mapCellNum[playerRow][playerCol] = 0;
            gameViewPanel.boardManager.collectPoint();
            //If player has a "DoublePoint" improvement it will take 20 with one point
            player.score += player.isDoublePoint ? 20 :10;

        }
    }

    /*
    The checkPacmanCollision() method handles collisions between Player and Ghost.
    If Player and Ghost are in the same position, one of the possible actions may occur.
    If Player has collected Improvement killGhost, the Ghost is transferred to its
    starting position and Player's score increases by 50.
    If Player has collected Improvement pacmanInvisible, no interaction occurs.
    In the last case, when Ghost has the ability to kill Player,
    both components return to their starting positions and the Player loses one life.
     */
    public void checkPacmanCollision(Ghost ghost){

        int playerCol = gameViewPanel.player.col;
        int playerRow = gameViewPanel.player.row;

        int ghostCol = ghost.col;
        int ghostRow = ghost.row;

        if(playerRow == ghostRow && playerCol == ghostCol) {

            if(gameViewPanel.player.isKillGhost){
                gameViewPanel.player.score += 50;
                ghost.setDefaultValues();
            } else if(!gameViewPanel.player.isPacmanInvisible){
                if(ghost.isAbleToKillPacman){
                    gameViewPanel.player.lives -= 1;
                    gameViewPanel.player.setDefaultValues();
                    ghost.setDefaultValues();

                }
            }
        }
    }

    /*
    The checkImprovementCollision() method allows the Player to collect an Improvement.
    If the Improvement and Player are in the same position, it depends on the Improvement type
    one of the variables: isDoublePoint, isFreezeGhost, isAddLife, isKillGhost, isPacmanInvisible
    will be set to true, which will trigger the appropriate method in the Player class.
     */
    public void checkImprovementCollision(Player player) {
        int playerCol = player.col;
        int playerRow = player.row;

        Iterator<Improvement> iterator = gameViewPanel.improvements.iterator();
        while (iterator.hasNext()) {
            Improvement improvement = iterator.next();
            int improvementCol = improvement.col;
            int improvementRow = improvement.row;

            if(playerRow == improvementRow && playerCol == improvementCol) {
                switch (improvement.type) {
                    case "doublePoint":
                        player.isDoublePoint = true;
                        break;
                    case "freezeGhost":
                        player.isFreezeGhost = true;
                        break;
                    case "addLife":
                        player.isAddLife = true;
                        break;
                    case "killGhost":
                        player.isKillGhost = true;
                        break;
                    case "pacmanInvisible":
                        player.isPacmanInvisible = true;
                        break;
                }
                iterator.remove();
            }
        }
    }
}
