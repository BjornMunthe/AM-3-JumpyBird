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
    private final Rectangle birb;
    private boolean gameOver;
    private int points = 0;
    private final int SCREEN_WIDTH;
    private final int SCREEN_HEIGHT;
    private final int PIPE_WIDTH;
    private final int PIPE_HEIGHT;
    private final int PIPE_GAP;
    private Image birbDown;
    private Image background;
    private Image topPipe;
    private Image bottomPipe;
    private int backgroundCounter;
    private int fallingCounter;


    public GameSurface(final int width, final int height) throws IOException {
        this.timer = new Timer(20, this);
        this.timer.start();
        this.gameOver = false;
        this.SCREEN_WIDTH = width;
        this.SCREEN_HEIGHT = height;
        this.PIPE_WIDTH = SCREEN_WIDTH/8;
        this.PIPE_HEIGHT = height;
        this.PIPE_GAP = height/6;

       addPipes();
       this.birb = new Rectangle(SCREEN_WIDTH / 2, height / 4, 30, 20);
       setImage();
    }

    public void setImage() {
        try {
            BufferedImage image = null;
            image = ImageIO.read(new File("img/background.png"));
            background = image;
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(new File("src/main/java/se/yrgo/am3/gameobjects/birbner.png"));
            birbDown = image;
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            BufferedImage image = null;
            image = ImageIO.read(new File("img/topPipe.png"));
            topPipe = image;
        } catch (Exception e) {
            // TODO: handle exception
        }

        try {
            BufferedImage image = null;
            image = ImageIO.read(new File("img/bottomPipe.png"));
            bottomPipe = image;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    private int calculateBottomY() {
        int temp = 0;
        while(temp <= PIPE_GAP+50 || temp >= SCREEN_HEIGHT-PIPE_GAP) {
            temp = ThreadLocalRandom.current().nextInt(SCREEN_HEIGHT - PIPE_GAP);
        }
        return temp;
    }

    public void addPipes() {
        pipes = new ArrayList<>();
        pipes.add(firstPipe = new Pipe(PIPE_WIDTH, PIPE_HEIGHT, "top"));
        firstPipe.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
        firstPipe.setyLoc(calculateBottomY());
        pipes.add(secondPipe = new Pipe(PIPE_WIDTH, PIPE_HEIGHT, "bot"));
        secondPipe.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
        secondPipe.setyLoc(firstPipe.getyLoc() - PIPE_GAP - PIPE_HEIGHT);
        pipes.add(thirdPipe = new Pipe(0,0, "top"));
        thirdPipe.setxLoc(0);
        thirdPipe.setyLoc(0);
        pipes.add(fourthPipe = new Pipe(0,0 , "bot"));
        fourthPipe.setxLoc(0);
        fourthPipe.setyLoc(0);

    }

    @Override
    protected void paintComponent(Graphics g) {
        repaint(g);
    }

    //    Fill the background
    private void repaint(Graphics g) {
        final Dimension d = this.getSize();

        g.drawImage(background, -backgroundCounter/2, 0, SCREEN_WIDTH*2, SCREEN_HEIGHT, this);
        g.drawImage(birbDown, birb.x, birb.y, birb.width, birb.height, this);

        for (Pipe pipe : pipes) {
            if (pipe.getPos().equals("top")) {
                g.drawImage(topPipe, pipe.getxLoc(), pipe.getyLoc(), pipe.getWidth(), pipe.getHeight(), this);
            } else if (pipe.getPos().equals("bot")) {
                g.drawImage(bottomPipe, pipe.getxLoc(), pipe.getyLoc(), pipe.getWidth(), pipe.getHeight(), this);
            }
        }

        if (!(gameOver)) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString(String.valueOf(points), d.height / 2, d.width / 4);
        }

        if (gameOver) {
            g.setColor(Color.black);
            g.setFont(new Font("Arial", Font.BOLD, d.height / 15));
            g.drawString("Game over!", d.width / 5, d.height / 2 );
            g.drawString("Points" + points, d.width / 5, d.height / 2 + PIPE_WIDTH);
            return;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this will trigger on the timer event
        // this is where we update positions of different elements in the game
        // this is where we set it to game over


        for (Pipe pipe: pipes) {
            pipe.setxLoc(pipe.getxLoc() - 2);
            if(pipe.getRectangle().intersects(birb)) {
                gameOver = true;
            }
        }
        if(birb.y > SCREEN_HEIGHT - SCREEN_HEIGHT / 9) {
            gameOver = true;
        }

        if (birb.y < (this.getSize().height - birb.height - 10)) {
            if (fallingCounter>0 && fallingCounter<5){
                birb.translate(0, -(2*fallingCounter*fallingCounter - 3*fallingCounter-2));
            }
            if (fallingCounter > 12 && fallingCounter < 26) {
                birb.translate(0, 1);
            }
            else if (fallingCounter >= 26 && fallingCounter < 40) {
                birb.translate(0, 3);
            }
            else if (fallingCounter >= 40) {
                birb.translate(0, 5);
            }
        }

        if (firstPipe.getxLoc() == SCREEN_WIDTH / 2 - PIPE_WIDTH) {
            thirdPipe.setItAll(SCREEN_WIDTH, calculateBottomY(), PIPE_HEIGHT , PIPE_WIDTH);
            fourthPipe.setItAll(SCREEN_WIDTH, thirdPipe.getyLoc() - PIPE_GAP - PIPE_HEIGHT, PIPE_HEIGHT, PIPE_WIDTH );
        }

        if (firstPipe.getxLoc() == -PIPE_WIDTH) {
            firstPipe.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
            secondPipe.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
            firstPipe.setyLoc(calculateBottomY());
            secondPipe.setyLoc(firstPipe.getyLoc() - PIPE_GAP - PIPE_HEIGHT);
        }

        if (thirdPipe.getxLoc() == -PIPE_WIDTH) {
            thirdPipe.setxLoc(SCREEN_WIDTH + PIPE_WIDTH);
            fourthPipe.setxLoc(SCREEN_WIDTH + PIPE_WIDTH );
            thirdPipe.setyLoc(calculateBottomY());
            fourthPipe.setyLoc(thirdPipe.getyLoc() - PIPE_GAP - PIPE_HEIGHT);
        }


        if (firstPipe.getxLoc() == SCREEN_WIDTH / 2 -PIPE_WIDTH || thirdPipe.getxLoc() == SCREEN_WIDTH / 2 -PIPE_WIDTH) {
            points++;
        }

        if (backgroundCounter >= 2*SCREEN_WIDTH) {
            backgroundCounter = 0;
        }

        fallingCounter++;
        backgroundCounter++;
        this.repaint();
        if (gameOver) {
            timer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // this event triggers when we release a key and then
        // we will move the space ship if the game is not over yet

        if (gameOver) {
            return;
        }

        final int minHeight = 10;
        final int maxHeight = this.getSize().height - birb.height - 10;
        final int kc = e.getKeyCode();

        if (kc == KeyEvent.VK_SPACE && birb.y > minHeight) {
            fallingCounter = 0;
            }

        if (kc == KeyEvent.VK_SPACE && gameOver == true) {
            gameOver = false;
            this.repaint();
            timer.start();
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
}




