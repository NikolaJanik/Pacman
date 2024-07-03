package pacman;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/*
The class creates a start panel. The class constructor defines
the window size and three panels: imagePanel, textPanel and buttonPanel.
The first one contains an animation consisting of six pictures,
the second one contains text, and the third one contains
three buttons (newGame, highScore, exit).
 */
public class StartFrame extends JFrame implements Runnable{

    private final int DEFAULT_WIDTH = 400;
    private final int DEFAULT_HEIGHT = 400;
    private final int IMAGE_WIDTH = DEFAULT_WIDTH/10;
    private final int IMAGE_HEIGHT = IMAGE_WIDTH;

    Thread animationThread;
    Image[] imageLabels = new Image[6];
    JPanel imagePanel = new JPanel();
    int startCol;  //which column should draw pictures in this frame of thread start from

    public StartFrame(){

        loadImages();
        startCol = 0;
        imagePanel.setLayout(new GridLayout(1, 10));
        imagePanel.setBackground(Color.BLACK);
        createImagePanel();

        JPanel textPanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        // SET VALUES
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setBackground(Color.BLACK);
        setTitle("Pacman");
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new GridLayout(0, 1));

        // TEXT PANEL
        textPanel.setLayout(new GridLayout(0, 1));
        JLabel textLabel = new JLabel("Pacman", JLabel.CENTER);
        JLabel textLabel2 = new JLabel("by Nikola Janik", JLabel.CENTER);
        textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel2.setFont(new Font("Arial", Font.BOLD, 20));
        textLabel.setForeground(Color.YELLOW);
        textLabel2.setForeground(Color.YELLOW);
        textPanel.add(textLabel);
        textPanel.add(textLabel2);
        textPanel.setBackground(Color.BLACK);

        // BUTTON PANEL
        buttonPanel.setLayout(new GridLayout(0, 1));
        JButton newGameButton = new JButton("New game");
        JButton rankButton = new JButton("High scores");
        JButton exitButton = new JButton("Exit");

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseMap();
            }
        });

        rankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRanking();
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(newGameButton);
        buttonPanel.add(rankButton);
        buttonPanel.add(exitButton);

        //add panels to frame
        add(textPanel);
        add(imagePanel);
        add(buttonPanel);
        setLocationRelativeTo(null);
        revalidate();
        repaint();

        //start a thread
        startAnimation();
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
    The chooseMap() method creates a panel with a group of
    JRadioButtons. Choosing one of possible option causes
    opening a new frame with game panel.
     */
    private void chooseMap(){
        JPanel buttonPanel = new JPanel();
        ButtonGroup group = new ButtonGroup();
        JRadioButton b1 = new JRadioButton("Mapa 15x16");
        JRadioButton b2 = new JRadioButton("Mapa 19x20");
        JRadioButton b3 = new JRadioButton("Mapa 21x22");
        JRadioButton b4 = new JRadioButton("Mapa 17x18");
        JRadioButton b5 = new JRadioButton("Mapa 17x20");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationThread = null;
                SwingUtilities.invokeLater(()->new GameFrame("maps/map1.csv"));
                setVisible(false);
            }
        });
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationThread = null;
                SwingUtilities.invokeLater(()->new GameFrame("maps/map2.csv"));
                setVisible(false);
            }
        });
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationThread = null;
                SwingUtilities.invokeLater(()->new GameFrame("maps/map3.csv"));
                setVisible(false);
            }
        });
        b4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationThread = null;
                SwingUtilities.invokeLater(()->new GameFrame("maps/map4.csv"));
                setVisible(false);
            }
        });
        b5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animationThread = null;
                SwingUtilities.invokeLater(()->new GameFrame("maps/map5.csv"));
                setVisible(false);
            }
        });
        group.add(b1);
        group.add(b2);
        group.add(b3);
        group.add(b4);
        group.add(b5);
        buttonPanel.add(b1);
        buttonPanel.add(b2);
        buttonPanel.add(b3);
        buttonPanel.add(b4);
        buttonPanel.add(b5);
        JOptionPane.showMessageDialog(this, buttonPanel, "Ranking", JOptionPane.PLAIN_MESSAGE);

    }

    /*
    The startAnimation() method creates and
    starts new thread.
     */
    public void startAnimation() {
        animationThread = new Thread(this);
        animationThread.start();
    }

    /*
    The run() method calls methods move() and crateImagePanel()
    and is executed in thread.
     */
    @Override
    public void run(){
        double interval = 1000000000 / 5;
        double delta = 0;
        long lastTime = System.nanoTime();

        while(animationThread != null) {
            long now = System.nanoTime();
            delta += (now - lastTime) / interval;
            lastTime = now;

            if(delta >= 1) {
                move();
                createImagePanel();
                delta--;
            }
        }
    }

    /*
    The move() method changes the field startCol.
     */
    private void move(){
        if(startCol < 9) startCol += 1;
        else startCol = 0;
    }

    /*
    The createImagePanel() method creates an image panel.
    The method sets Icon on JLabels in a specific order
    depending on startCol.
     */
    private void createImagePanel(){
        imagePanel.removeAll();
        int cols = 10;
        JLabel cellLabel;

        switch(startCol){
            case 0:
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[5]));
                imagePanel.add(cellLabel);
                for(int i = 1; i < imageLabels.length-1; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                for(int i = 1; i < cols - imageLabels.length; i++){
                    imagePanel.add(new JLabel());
                }
                break;
            case 1:
                imagePanel.add(new JLabel());
                for(int i = 0; i < imageLabels.length-1; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                for(int i = 1; i < cols - (imageLabels.length+startCol); i++){
                    imagePanel.add(new JLabel());
                }
                break;
            case 2:
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[5]));
                imagePanel.add(cellLabel);
                for(int i = 1; i < imageLabels.length-1; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                for(int i = 1; i < cols - (imageLabels.length+startCol); i++){
                    imagePanel.add(new JLabel());
                }
                break;
            case 3:
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                for(int i = 0; i < imageLabels.length-1; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                for(int i = 1; i < cols - (imageLabels.length+startCol); i++){
                    imagePanel.add(new JLabel());
                }
                break;
            case 4:
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[5]));
                imagePanel.add(cellLabel);
                for(int i = 1; i < imageLabels.length-1; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                for(int i = 1; i < cols - (imageLabels.length+startCol); i++){
                    imagePanel.add(new JLabel());
                }
                break;
            case 5:
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                for(int i = 0; i < imageLabels.length-1; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                break;
            case 6:
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[4]));
                imagePanel.add(cellLabel);
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[5]));
                imagePanel.add(cellLabel);
                for(int i = 0; i < imageLabels.length - 2; i++){
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                break;
            case 7:
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[3]));
                imagePanel.add(cellLabel);
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[4]));
                imagePanel.add(cellLabel);
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                for(int i = 0; i < imageLabels.length - 3; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                break;
            case 8:
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[2]));
                imagePanel.add(cellLabel);
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[3]));
                imagePanel.add(cellLabel);
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[4]));
                imagePanel.add(cellLabel);
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[5]));
                imagePanel.add(cellLabel);
                for(int i = 0; i < imageLabels.length - 4; i++){
                    cellLabel = new JLabel();
                    cellLabel.setIcon(new ImageIcon(imageLabels[i]));
                    imagePanel.add(cellLabel);
                }
                break;
            case 9:
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[1]));
                imagePanel.add(cellLabel);
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[2]));
                imagePanel.add(cellLabel);
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[3]));
                imagePanel.add(cellLabel);
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[4]));
                imagePanel.add(cellLabel);
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                imagePanel.add(new JLabel());
                cellLabel = new JLabel();
                cellLabel.setIcon(new ImageIcon(imageLabels[0]));
                imagePanel.add(cellLabel);

                break;

        }
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    public void loadImages(){
        try {
            BufferedImage img1 = ImageIO.read(new File("images/right.png"));
            BufferedImage img2 = ImageIO.read(new File("images/ghosts/redGhost.png"));
            BufferedImage img3 = ImageIO.read(new File("images/ghosts/blueGhost.png"));
            BufferedImage img4 = ImageIO.read(new File("images/ghosts/yellowGhost.png"));
            BufferedImage img5 = ImageIO.read(new File("images/ghosts/greenGhost.png"));
            BufferedImage img6 = ImageIO.read(new File("images/default.png"));

            //Scale images
            Image scaledImg1 = img1.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledImg2 = img2.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledImg3 = img3.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledImg4 = img4.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledImg5 = img5.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            Image scaledImg6 = img6.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);

            // Add labels to image panel
            imageLabels[0] = scaledImg1;
            imageLabels[1] = scaledImg2;
            imageLabels[2] = scaledImg3;
            imageLabels[3] = scaledImg4;
            imageLabels[4] = scaledImg5;
            imageLabels[5] = scaledImg6;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot load images");
            JOptionPane.showMessageDialog(this, "Cannot load images" , "ERROR", JOptionPane.ERROR_MESSAGE);

        }
    }
}
