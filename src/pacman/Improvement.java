package pacman;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
Objects of this class are used to represent Improvements produced by Ghosts.
The class has fields for the item position, the Improvement type
(killGhost, invisible, doublePoints, freezeGhost, addLife)
and the Improvement image.
An Improvement's position is determined
by the position of the Ghost that produced it.
 */
public class Improvement {

    int col, row;
    BufferedImage image;
    String type;

    public Improvement(int col, int row, BufferedImage image, String type) {
        this.col = col;
        this.row = row;
        this.image = image;
        this.type = type;

    }
}
