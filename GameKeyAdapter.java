package pacman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
This class implements the KeyListener interface and is used
in the Player class to change the position of the component.
 */
public class GameKeyAdapter implements KeyListener {

    boolean goUp, goDown, goLeft, goRight;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                goUp = true;
                break;
            case KeyEvent.VK_DOWN:
                goDown = true;
                break;
            case KeyEvent.VK_RIGHT:
                goRight = true;
                break;
            case KeyEvent.VK_LEFT:
                goLeft = true;
                break;
        }
    }
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
                goUp = false;
                break;
            case KeyEvent.VK_DOWN:
                goDown = false;
                break;
            case KeyEvent.VK_RIGHT:
                goRight = false;
                break;
            case KeyEvent.VK_LEFT:
                goLeft = false;
                break;
        }
    }
}
