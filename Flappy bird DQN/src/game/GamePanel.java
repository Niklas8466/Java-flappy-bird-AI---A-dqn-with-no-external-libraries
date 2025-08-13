package game;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//This class draws the game onto the screen
public class GamePanel extends JPanel {
    private final int scaleFactor;
    private final int pipeDistance;
    private final int width;
    private final int height;
    private final ImageIcon playerSprite;
    private final ImageIcon backgroundSprite;
    private final ImageIcon baseSprite;
    private final ImageIcon pipeBottomSprite;
    private final ImageIcon pipeTopSprite;
    private int playerHeight = 100;
    private ArrayList<Integer[]> pipePositions = new ArrayList<>();;

    public GamePanel(int width, int height, int pipeDistance, int scaleFactor){
        this.scaleFactor = scaleFactor;
        this.width = width;
        this.height = height;
        this.pipeDistance = pipeDistance;

        this.setBackground(Color.darkGray);
        this.setPreferredSize(new Dimension(width, height));
        this.setVisible(true);
        this.setLayout(null);

        try{
            playerSprite = new ImageIcon("Flappy bird DQN/sprites/yellowbird-midflap.png");

            backgroundSprite = new ImageIcon("Flappy bird DQN/sprites/background-day.png");

            baseSprite = new ImageIcon("Flappy bird DQN/sprites/base.png");
            pipeBottomSprite = new ImageIcon("Flappy bird DQN/sprites/pipe-green-bottom.png");
            pipeTopSprite = new ImageIcon("Flappy bird DQN/sprites/pipe-green-top.png");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setPipes(ArrayList<Integer[]> pipePositions){
        this.pipePositions = (ArrayList<Integer[]>) pipePositions.clone();
    }

    public void clearPipes(){
        pipePositions.clear();
    }

    public void setBirdHeight(int height){
        playerHeight = height;
    }

    public int getPipeWidth(){
        return pipeBottomSprite.getIconWidth()* scaleFactor;
    }


    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.cyan);
        g2d.fillRect(0,0, width, height);
        g2d.drawImage(backgroundSprite.getImage(), 0, 0, width, height, this);

        g2d.drawImage(playerSprite.getImage(), 100, playerHeight-playerSprite.getIconWidth()* scaleFactor /2, playerSprite.getIconWidth()* scaleFactor, playerSprite.getIconHeight()* scaleFactor, this);

        for (int i = 0; i < pipePositions.size(); i++) {
            Integer[] pos = pipePositions.get(i);
            g2d.drawImage(pipeTopSprite.getImage(), pos[0], pos[1] - pipeDistance/2 - pipeTopSprite.getIconHeight()* scaleFactor, pipeTopSprite.getIconWidth()* scaleFactor, pipeTopSprite.getIconHeight()* scaleFactor, this);
            g2d.drawImage(pipeBottomSprite.getImage(), pos[0], pos[1] + pipeDistance/2, pipeBottomSprite.getIconWidth()* scaleFactor, pipeBottomSprite.getIconHeight()* scaleFactor, this);
        }

        g2d.drawImage(baseSprite.getImage(), 0, height-baseSprite.getIconHeight(), width, baseSprite.getIconHeight(), this);
    }
}

