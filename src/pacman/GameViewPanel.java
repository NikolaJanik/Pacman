package pacman;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
The class creates a game panel where all board elements and components are drawn
and where the board is updated using the gameThread.
 */

public class GameViewPanel extends JPanel implements  Runnable {

    // SCREEN SETTINGS
    int cellSize;
    final int COLS;
    final int ROWS;
    int screenWidth;
    int screenHeight;
    String mapPath;
    Font font;

    // THREAD SETTINGS
    long startTime;
    Thread gameThread;

    // GAME COMPONENTS
    GameKeyAdapter gameKeyAdapter;
    Player player;
    BoardManager boardManager;
    Collision collision;
    Ghost ghost1;
    Ghost ghost2;
    Ghost ghost3;
    Ghost ghost4;
    Ghost[] ghosts;
    List<Improvement> improvements;


    public GameViewPanel(String mapPath, int[] size){
        this.mapPath = mapPath;
        this.ROWS = size[0] + 1;
        this.COLS = size[1];
        this.cellSize = 40;
        this.screenHeight = ROWS * cellSize;
        this.screenWidth = COLS * cellSize;
        font = new Font("Arial", Font.PLAIN, 11);
        setLayout(new GridLayout(ROWS, COLS));

        gameKeyAdapter = new GameKeyAdapter();
        player = new Player(this, gameKeyAdapter);
        boardManager = new BoardManager(this, mapPath);
        collision = new Collision(this);

        ghost1 = new Ghost(this, "red");
        ghost2 = new Ghost(this, "blue");
        ghost3 = new Ghost(this, "yellow");
        ghost4 = new Ghost(this, "green");
        ghosts = new Ghost[]{ghost1, ghost2, ghost3, ghost4};
        improvements = new ArrayList<>();

        setPreferredSize(new Dimension(screenWidth, screenHeight));
        addKeyListener(gameKeyAdapter);
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setFocusable(true);

        startGameThread();
    }

    /*
    The resizeComponents() method allows you to scale the elements of the board
    and components when the window size changes.
     */
    private void resizeComponents() {
        Dimension newSize = getSize();
        screenWidth = newSize.width;
        screenHeight = newSize.height;
        cellSize = Math.min(screenWidth / COLS, screenHeight / ROWS);
        boardManager.getBoardImage();
        player.scaleImage();
        for(Ghost ghost : ghosts){
            ghost.scaleImage();
            ghost.image = ghost.draw(ghost.color);
        }
        font = new Font("Arial", Font.PLAIN, (int)(cellSize / 3));

    }

    /*
    The startGameThread() method creates a new thread and starts it.
    This method is called in the panel constructor.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        startTime = System.currentTimeMillis();
        gameThread.start();
    }

    @Override
    public  void run() {
        /*
         To have the same FSP all the time.
         The game is updated in the thread without lags.
        */
        double interval = 1000000000 / 6;
        double delta = 0;
        long lastTime = System.nanoTime();

        while(gameThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / interval;
            lastTime = now;

            if(delta >= 1) {
                update();
                draw();
                delta--;
            }
        }
    }

    /*
    The addImprovement() method add new improvement produced by Ghost
    to the list of this type objects.
     */
    public void addImprovement(Improvement improvement) {
        improvements.add(improvement);
    }

    /*
    The update() method calls update() methods from Player and Ghost classes
    and checks the conditions for ending the game.
     */
    public void update(){
        player.update();
        for(Ghost ghost : ghosts){
            if(ghost.isAbleToMove) {
                ghost.update();
            }
        }
        checkWinCondition();
        checkGameOverCondition();
    }

    /*
    The draw() method calls draw() method from BoardManager,
    so it draw all the board and components.
    If the screen size changed it calls resizeComponents() method.
     */
    public void draw() {
        if(screenHeight != getHeight() || screenWidth != getWidth()) {
            resizeComponents();
        }
        removeAll();
        boardManager.draw();
        revalidate();
        repaint();
    }

    /*
    The printLives() method returns a JLabel with
    information about Player's lives.
    It is called in BoardManager class in draw() method.
     */
    public JLabel printLives(){
        JLabel livesLabel = new JLabel("" + player.lives);
        livesLabel.setFont(font);
        livesLabel.setForeground(Color.WHITE);

        return livesLabel;
    }

    /*
    The printTime() method returns a JLabel with
    information about current game time.
    It is called in BoardManager class in draw() method.
     */
    public JLabel printTime(){

        long elapsedTime = System.currentTimeMillis() - startTime;
        int seconds = (int) (elapsedTime / 1000) % 60;
        int minutes = (int) (elapsedTime / 1000 / 60) % 60;
        String time = String.format("%02d:%02d", minutes, seconds);

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(font);
        timeLabel.setForeground(Color.WHITE);

        return timeLabel;
    }

    /*
    The printScore() method returns a JLabel with
    information about score.
    It is called in BoardManager class in draw() method.
    */
    public JLabel printScore(){

        JLabel scoreLabel = new JLabel("" + player.score);
        scoreLabel.setFont(font);
        scoreLabel.setForeground(Color.WHITE);

        return scoreLabel;
    }

    /*
    The checkGameOverCondition() method checks the number of Player's lives.
    If Player has 0 lives then method collects a username, writes down score,
    time and username in Ranking. It calls showRanking() method and returnToStartFrame() method.
     */
    private void checkGameOverCondition(){
        if(player.lives <= 0) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            String userName = JOptionPane.showInputDialog(this, "Enter your name for the ranking:", "Game over!", JOptionPane.INFORMATION_MESSAGE);
            if (userName != null && !userName.trim().isEmpty()) {
                ScoreEntry scoreEntry = new ScoreEntry(userName, player.score, elapsedTime);
                Ranking.saveScore(scoreEntry);
            }
            showRanking();
            returnToStartFrame();
        }
    }

    /*
    The checkWinCondition() method checks the number of remaining points.
    If there is  0 points then method collects a username, writes down score,
    time and username in Ranking. It calls showRanking() method and returnToStartFrame() method.
     */
    private void checkWinCondition() {
        if (boardManager.getRemainingPoints() == 0) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            String userName = JOptionPane.showInputDialog(this, "Enter your name for the ranking:", "Winner!", JOptionPane.INFORMATION_MESSAGE);
            if (userName != null && !userName.trim().isEmpty()) {
                ScoreEntry scoreEntry = new ScoreEntry(userName, player.score, elapsedTime);
                Ranking.saveScore(scoreEntry);
            }
            showRanking();
            returnToStartFrame();
        }
    }

    /*
    The showRanking() method loads sores and display them in
    JOptionPane.showMessageDialog() as a JList object.
     */
    private void showRanking() {
        List<ScoreEntry> scores = Ranking.loadScores();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (ScoreEntry entry : scores) {
            listModel.addElement(entry.toString());
        }
        JList<String> scoreList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(scoreList);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        JOptionPane.showMessageDialog(this, scrollPane, "Ranking", JOptionPane.PLAIN_MESSAGE);
    }

    /*
    The returnToStartFrame() method closes the gameThread and opens StartFrame.
     */
    public void returnToStartFrame() {
        gameThread = null;
        SwingUtilities.invokeLater(() -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            topFrame.dispose();
            new StartFrame();
        });
    }
}
