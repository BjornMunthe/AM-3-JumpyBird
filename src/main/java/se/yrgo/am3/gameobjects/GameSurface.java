package se.yrgo.am3.gameobjects;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GameSurface extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 6260582674762246325L;
    private Timer timer;
    private Pipe firstPipe;
    private Pipe secondPipe;
    private Pipe thirdPipe;
    private Pipe fourthPipe;
    private List<Pipe> pipes;
    private Rectangle birb;
    private boolean gameOver;
    private int points = 0;
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    private final int PIPE_WIDTH;
    private final int PIPE_HEIGHT;
    private final int PIPE_GAP;
    //Flytta ut till birb
    private Image birbDown;
    private Image birbUp;
    private Image birbDead;
    private Image background;
    private int backgroundCounter;
    private int framesAfterJumpCounter;
    private Highscore highscore;
    private boolean firstRound;

    public GameSurface(final int width, final int height) {
        this.timer = new Timer(12, this);
        this.firstRound = true;
        this.gameOver = false;
        // Kanske flytta till egen metod "setconstants()"
        this.SCREEN_WIDTH = width;
        this.SCREEN_HEIGHT = height;
        this.PIPE_WIDTH = SCREEN_WIDTH / 8;
        this.PIPE_HEIGHT = height;
        this.PIPE_GAP = height / 6;
        // Ut i klasserna evt
        this.background = setImage("src/main/resources/background.png");
        this.birbDown = setImage("src/main/resources/birbner.png");
        this.birbUp = setImage("src/main/resources/birbupp.png");
        this.birbDead = setImage("src/main/resources/Dead.png");
        this.highscore = new Highscore();
        this.repaint();
    }


    public BufferedImage setImage(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return image;
    }

    /**
     * Method to get a fitting starting position for the pipe
     *
     * @return Random number fitted to the screen
     */
    private int calculateBottomY() {
        return ThreadLocalRandom.current().nextInt(PIPE_GAP + SCREEN_HEIGHT / 10, SCREEN_HEIGHT - PIPE_GAP);
    }

    /**
     * Initiates the pipes at set start positions
     *
     * @throws IOException
     */
    public void addPipes() throws IOException {
        pipes = new ArrayList<>();
        pipes.add(firstPipe = new Pipe(PIPE_WIDTH, PIPE_HEIGHT, SCREEN_WIDTH + PIPE_WIDTH, calculateBottomY(), "top"));
        pipes.add(secondPipe = new Pipe(PIPE_WIDTH, PIPE_HEIGHT, SCREEN_WIDTH + PIPE_WIDTH, firstPipe.getyLoc() - PIPE_GAP - PIPE_HEIGHT, "bot"));
        pipes.add(thirdPipe = new Pipe(0, 0, 0, 0, "top"));
        pipes.add(fourthPipe = new Pipe(0, 0, 0, 0, "bot"));
    }

    @Override
    protected void paintComponent(Graphics g) {
        repaint(g);
    }

    private void repaint(Graphics g) {
        manageBackground(g);
        if (firstRound) {
            setStartingScreen(g);
        } else if (gameOver) {
            paintHighScore(g);
            setGameOverScreen(g);
        } else {
            drawPipes(g);
            drawPoints(g);
            paintBirb(g);
        }
    }

    private void manageBackground(Graphics g) {
        g.drawImage(background, -backgroundCounter / 2, 0, SCREEN_WIDTH * 2, SCREEN_HEIGHT, this);
        backgroundCounter++;
        if (backgroundCounter >= 2 * SCREEN_WIDTH) {
            backgroundCounter = 0;
        }
    }

    /**
     * Draws the points
     *
     * @param g the graphics to be used
     */
    private void drawPoints(Graphics g) {
        g.setColor(Color.yellow);
        g.setFont(new Font("Candara", Font.BOLD, SCREEN_HEIGHT / 15));
        g.drawString(String.valueOf("Points: " + points), SCREEN_WIDTH / 10, SCREEN_HEIGHT / 9);
    }

    /**
     * Animates the pipes
     *
     * @param g the graphics to be used
     */
    private void drawPipes(Graphics g) {
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.getPipeImage(), pipe.getxLoc(), pipe.getyLoc(), pipe.getWidth(), pipe.getHeight(), this);
        }
    }

    /**
     * Paints the starting screen with instructions
     *
     * @param g the graphics to be used
     */
    private void setStartingScreen(Graphics g) {
//        g.setColor(Color.green);
//        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Candara", Font.BOLD, SCREEN_HEIGHT / 15));
        g.drawString("Welcome to Jumpybirb!", SCREEN_WIDTH / 10, SCREEN_HEIGHT / 5);
        g.drawString("Press SPACE to jump.", SCREEN_WIDTH / 10, 2 * SCREEN_HEIGHT / 5);
        g.drawString("Choose difficulty to start", SCREEN_WIDTH / 10, 3 * SCREEN_HEIGHT / 5);
        g.drawString("1: EASY   2:NORMAL    3:HARD", SCREEN_WIDTH / 10, 4 * SCREEN_HEIGHT / 5);
    }

    /**
     * Sets the style and text for the game over notification
     */
    private void setGameOverScreen(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Candara", Font.BOLD, SCREEN_HEIGHT / 15));
        g.drawString("GAME OVER!",  5*SCREEN_WIDTH/ 15,  SCREEN_HEIGHT / 10);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Candara", Font.BOLD, SCREEN_HEIGHT / 25));
        g.drawString("Press SPACE to play again", 5*SCREEN_WIDTH/ 15 , 2 * SCREEN_HEIGHT / 10);
        g.drawString("To change difficulty press:",  5*SCREEN_WIDTH/ 15, 3 * SCREEN_HEIGHT / 10);
        g.drawString("1: EASY  2:NORMAL  3:HARD",  5*SCREEN_WIDTH/ 15 , 4 * SCREEN_HEIGHT / 10);
    }

    /**
     * animation for the birb
     *
     * @param g the graphics to be used
     */
    private void paintBirb(Graphics g) {
        if (framesAfterJumpCounter < 20 && framesAfterJumpCounter > 3) {
            g.drawImage(birbDown, birb.x, birb.y, birb.width, birb.height, this);
        } else {
            g.drawImage(birbUp, birb.x, birb.y, birb.width, birb.height, this);
        }
    }

    private void paintHighScore(Graphics g) {
        g.drawImage(birbDead, birb.x, birb.y, birb.width, birb.height, this);
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Candara", Font.BOLD, SCREEN_HEIGHT / 15));
        if (highscore.fileRead()) {
            String[] highscores = highscore.printHighscore();
            int yPos = 11 * SCREEN_HEIGHT/20;
            int xPos = SCREEN_WIDTH/ 20;
            int xOffset = 0;
            g.drawString("HIGHSCORES", SCREEN_WIDTH / 2 - 120, yPos);
            g.setFont(new Font("Candara", Font.BOLD, SCREEN_HEIGHT / 20));
            g.setColor(Color.ORANGE);
            for (int i = 0; i < highscore.getPoints().length; i++) {
                 if (i == highscore.getPoints().length/2) {
                     xOffset+=SCREEN_WIDTH/2;
                     yPos=11*SCREEN_HEIGHT/20;
                 }
                yPos += 40;
                g.drawString(highscores[i * 2], xPos + xOffset, yPos);
                g.drawString(highscores[i * 2 + 1], xPos *9 + xOffset, yPos);
            }
        } else {
            g.drawString(String.format("Although you scored %d fabulous points,", points), 10, SCREEN_HEIGHT / 8);
            g.drawString("the highscores could unfortunately not", 10, (SCREEN_HEIGHT / 8) * 2);
            g.drawString("be retrieved!", 10, (SCREEN_HEIGHT / 8) * 3);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // moves the pipes and checks if it intersects with the bird.
        handlePipePosition();

        setBirbYPosition(framesAfterJumpCounter);
        framesAfterJumpCounter++;
        this.repaint();

        //Increase points if pipes move past a point.
        if (firstPipe.getxLoc() == SCREEN_WIDTH / 2 - PIPE_WIDTH || thirdPipe.getxLoc() == SCREEN_WIDTH / 2 - PIPE_WIDTH) {
            points++;
        }

        // Game over if you hit the ground
        if (birb.y > SCREEN_HEIGHT - SCREEN_HEIGHT / 6) {
            gameOver = true;
        }

        if (gameOver) {
            if (points > highscore.getLowscore() && highscore.fileRead()) {
                highscoreInput();
            }
            points = 0;
            timer.stop();
        }

    }

    private void setBirbYPosition(int framesAfterJumpCounter) {
        if (birb.y < (this.getSize().height - birb.height - 10)) {
            if (framesAfterJumpCounter > 0 && framesAfterJumpCounter < 5) {
                birb.translate(0, -(2 * framesAfterJumpCounter * framesAfterJumpCounter - 3 * framesAfterJumpCounter - 2));
            }
            if (framesAfterJumpCounter > 12 && framesAfterJumpCounter < 26) {
                birb.translate(0, 1);
            } else if (framesAfterJumpCounter >= 26 && framesAfterJumpCounter < 40) {
                birb.translate(0, 3);
            } else if (framesAfterJumpCounter >= 40) {
                birb.translate(0, 5);
            }
        }
    }

    /**
     * Method for displaying a popup to retrieve the players name and passing it
     * and the points made to the highscore class
     */
    private void highscoreInput() {
        String s = (String) JOptionPane.showInputDialog(
                this,
                "Concratulations!\n"
                        + "You've set a highscore\n"
                        + points + " points\n"
                        + "Please enter your name below:",
                "Highscore",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                highscore.getLatestEntry());
        highscore.newEntry(points, s);
        this.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        final int minHeight = 10;
        final int keyCode = e.getKeyCode();

        if (firstRound || gameOver) {
            startGame(keyCode);
        }

        if (keyCode == KeyEvent.VK_SPACE && birb.y > minHeight && !gameOver) {
            framesAfterJumpCounter = 0;
        }
    }

    /**
     * Resets positions for pipes and birb. Changes the difficulty and initializes
     * the game by starting the timer when user presses correct key(s).
     *
     * @param keyCode the key code for the key registered in keyEvent()
     */
    public void startGame(int keyCode) {
        try {
            addPipes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.birb = new Rectangle(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 4, 60, 40);
        if (keyCode == KeyEvent.VK_SPACE
                || keyCode == KeyEvent.VK_1
                || keyCode == KeyEvent.VK_2
                || keyCode == KeyEvent.VK_3) {
            setDifficulty(keyCode);
            gameOver = false;
            firstRound = false;
            timer.start();
        }
    }

    /**
     * Sets the difficulty of the game by changing the timer delay which in controls the frame rate.
     *
     * @param keyCode key code for chosen difficulty. 1=easy, 2=normal, 3=hard.
     */
    public void setDifficulty(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1:
                timer.setDelay(16);
                break;
            case KeyEvent.VK_2:
                timer.setDelay(13);
                break;
            case KeyEvent.VK_3:
                timer.setDelay(10);
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // do nothing
    }

    /**
     * Method that handle the pipes movement and when they shoud be reset
     * and intersection with birb
     */
    private void handlePipePosition() {
        for (Pipe pipe : pipes) {
            pipe.setxLoc(pipe.getxLoc() - 2);
            if (pipe.getRectangle().intersects(birb)) {
                gameOver = true;
            }
        }
        if (firstPipe.getxLoc() == SCREEN_WIDTH / 2 - PIPE_WIDTH) {
            thirdPipe.setItAll(SCREEN_WIDTH, calculateBottomY(), PIPE_HEIGHT, PIPE_WIDTH);
            fourthPipe.setItAll(SCREEN_WIDTH, thirdPipe.getyLoc() - PIPE_GAP - PIPE_HEIGHT, PIPE_HEIGHT, PIPE_WIDTH);
        }
        resetPipePosition(firstPipe, secondPipe);
        resetPipePosition(thirdPipe, fourthPipe);

    }

    private void resetPipePosition(Pipe one, Pipe two) {
        if (one.getxLoc() == -PIPE_WIDTH) {
            one.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
            two.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
            one.setyLoc(calculateBottomY());
            two.setyLoc(one.getyLoc() - PIPE_GAP - PIPE_HEIGHT);
        }
    }
}




