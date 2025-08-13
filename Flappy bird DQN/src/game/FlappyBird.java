package game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBird extends JFrame{
    private GamePanel gamePanel;
    private InformationPanel informationPanel;
    private Inputs inputs = new Inputs();

    private int birdHeight;
    private int score = 0;

    private final int pipeDistance = 250;
    private final int scaleFactor = 2;
    private final double yAcceleration = 0.6;
    private final int pipeSpeed = 2;
    private final double jumpHeight = 8;
    private final int pipeWidth = 52 * scaleFactor;
    private final int birdXPos = 100;
    private final int birdVerticalSize = 24 * scaleFactor;
    private final int birdHorizontalSize = 34 * scaleFactor;
    private final int baseHeight = 112;
    private final int maxVelocity = 12;

    private double yVelocity;
    private ArrayList<Integer[]> pipePositions = new ArrayList<>();

    private final int height;
    private final int width;

    public FlappyBird(int width, int height){
        this.height = height;
        this.width = width;
        gamePanel = new GamePanel(width/2, height, pipeDistance, scaleFactor);
        informationPanel = new InformationPanel(width/2, height);

        this.add(gamePanel, BorderLayout.WEST);
        this.add(informationPanel, BorderLayout.EAST);
        this.addKeyListener(inputs);
        this.pack();
        this.setTitle("AI plays Flappy Bird");
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        reset();
    }

    public boolean isPressed(char c){
        return inputs.isKeyPressed(c);
    }

    public void removePressedButton(char c){
        inputs.removePressedButton(c);
    }

    //state = {bird height, bird velocity, distance to next pipe, height of next top pipe, height of next bottom pipe}
    //everything gets normed to a value between 0 and 1
    public float[] getGameState(){
        float[] state = new float[5];
        state[0] = (float) birdHeight/height;//[0,1]
        state[1] = (float) ((yVelocity/maxVelocity) + 1)/2;//[0,1]
        int[] closestPipe = getClosestPipeToPlayer();
        state[2] = (float) ((closestPipe[0] - (birdXPos + birdHorizontalSize))/(width-(birdXPos + birdHorizontalSize)));//[0,1]
        state[3] = (float) ((closestPipe[1] - pipeDistance/2)/height);//[0,1]
        state[4] = (float) ((closestPipe[1] + pipeDistance/2)/height);//[0,1]
        return state;
    }

    public boolean isTerminalState(){
        return isBirdDead();
    }

    public void update(boolean jumpIsPressed){
        generatePipe();
        deletePipe();
        if(jumpIsPressed)
            jump();
        else
            moveBirdDown();
        movePipes();
        updateScore();
    }

    public void reset(){
        deleteAllPipes();
        birdHeight = 200;
        yVelocity = 0;
        score = 0;
        generatePipe();
    }

    public int getScore(){
        return score;
    }

    private int[][] getPipePositions(){
        int[][] pipes = new int[pipePositions.size()][2];
        for (int i = 0; i < pipePositions.size(); i++) {
            pipes[i][0] = pipePositions.get(i)[0];
            pipes[i][1] = pipePositions.get(i)[1];
        }
        return pipes;
    }

    //calculates if a given pipe is behind the bird
    private boolean pipeIsBehindBird(int pipeXPos){
        return birdXPos > pipeXPos + pipeWidth;
    }

    private void updateScore(){
        int closestPipeX = getClosestPipeToPlayer()[0];

        //the first frame, in which the bird is between the pipe and not dead, the score goes up
        //calculates how much of the bird is horizontally between the pipes
        //if it is the first frame between the pipe, than:
        //distance by how much the bird is between the pipes is <= game speed (pipe speed)
        if((birdXPos + birdHorizontalSize) - closestPipeX <= pipeSpeed && (birdXPos + birdHorizontalSize) - closestPipeX > 0 && !isBirdDead())
            score++;
    }

    private int[] getClosestPipeToPlayer(){
        int[][] pipes = getPipePositions();
        for (int i = 0; i < pipes.length; i++) {
            if(!pipeIsBehindBird(pipes[i][0]))
                return pipes[i];
        }
        //there is always a pipe
        return null;
    }

    private boolean isBirdDead(){
        //bird is too high
        if(birdHeight < 0)
            return true;

        //bird is too low
        if(birdHeight + birdVerticalSize > height-baseHeight)
            return true;

        //collision with pipe
        //loop through all pipes
        for (int i = 0; i < Math.min(pipePositions.size(), 2); i++) {
            //check if the x position of the pipe can collide with the bird, to safe time calculating
            if(birdXPos + birdHorizontalSize > pipePositions.get(i)[0] && birdXPos < pipePositions.get(i)[0] + pipeWidth){

                //if yes check, if the bird is colliding
                if(birdHeight + birdVerticalSize /2  > pipePositions.get(i)[1] + pipeDistance/2){
                    return true;
                }
                if(birdHeight - birdVerticalSize /2 < pipePositions.get(i)[1] - pipeDistance/2){
                    return true;
                }
            }
        }
        return false;
    }


    private void generatePipe(){
        if(!pipePositions.isEmpty() && pipePositions.getLast()[0] > width/4)
            return;

        //buffer of 100 px up and down
        //112 is the height of the ground
        int maxHeight = height - 112 - 100 - pipeDistance/2;
        int minHeight = 100 + pipeDistance/2;

        Random random = new Random();
        int height = random.nextInt(minHeight, maxHeight);
        int xPos = width/2;
        Integer[] pos = {xPos, height};
        pipePositions.add(pos);

        gamePanel.setPipes(pipePositions);

    }

    private void movePipes(){
        gamePanel.clearPipes();
        for (int i = 0; i < pipePositions.size(); i++) {
            pipePositions.get(i)[0] -= pipeSpeed;
        }

        gamePanel.setPipes(pipePositions);
        gamePanel.repaint();
    }


    private void moveBirdDown(){
        yVelocity -= yAcceleration;
        yVelocity = Math.max(yVelocity, -maxVelocity);
        birdHeight -= (int) yVelocity;
        gamePanel.setBirdHeight(birdHeight);
    }

    private void jump(){
        yVelocity = jumpHeight;
        birdHeight -= (int) yVelocity;
        gamePanel.setBirdHeight(birdHeight);
    }

    //deletes Pipes if they are off-screen
    private void deletePipe(){
        if(pipePositions.getFirst()[0] <= -gamePanel.getPipeWidth()){
            pipePositions.removeFirst();
        }
    }

    public void setHighScore(int newScore){
        informationPanel.setHighScore(newScore);
        informationPanel.refresh();
    }

    public int getHighScore(){
        return informationPanel.getHighScore();
    }

    public void setGeneration(int generation){
        informationPanel.setGeneration(generation);
        informationPanel.refresh();
    }

    public int getGeneration(){
        return informationPanel.getGeneration();
    }

    private void deleteAllPipes(){
        pipePositions.clear();
    }

}
