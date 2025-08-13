package game;

import javax.swing.*;
import java.awt.*;

public class InformationPanel extends JPanel {
    private int highScore = 0;
    private int generation = 0;

    private JLabel title = new JLabel("AI plays flappy bird", SwingConstants.CENTER);
    private JLabel credits = new JLabel("by Niklas Hoti", SwingConstants.CENTER);
    private JLabel textfield = new JLabel("", SwingConstants.CENTER);


    public InformationPanel(int width, int height){
        this.setBackground(Color.darkGray);
        this.setPreferredSize(new Dimension(width, height));
        this.setVisible(true);
        this.setLayout(null);

        title.setLocation(0, 200);
        title.setSize(width, 100);
        title.setForeground(Color.white);
        title.setFont(new Font("font", Font.BOLD, 20));
        title.setVisible(true);

        credits.setLocation(0, 230);
        credits.setSize(width, 100);
        credits.setForeground(Color.white);
        credits.setFont(new Font("font", Font.BOLD, 15));
        credits.setVisible(true);

        textfield.setSize(width, height/2);
        textfield.setLocation(0,260);
        textfield.setForeground(Color.white);
        textfield.setFont(new Font("font", Font.BOLD, 20));
        textfield.setVisible(true);


        this.add(title);
        this.add(credits);
        this.add(textfield);

        refresh();
    }

    public void setGeneration(int generation) {
        this.generation = generation;
        refresh();
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
        refresh();
    }

    public int getGeneration() {
        return generation;
    }

    public int getHighScore() {
        return highScore;
    }

    public void refresh(){
        textfield.setText("<html><center>This was made with only java and without external libraries<br>except of course the default java library ;)<br><br>It uses a DQN, which slowly learns from its mistakes<br><br><br>Niklas Highscore: 12<br>optimisations, that didn't help: 8<br>actual errors found: 7<br>amount of coffee: yes<br><br>Aktuelle AI-Generation️: " + generation +"<br>AI Highscore: " + highScore + "<br><br><br>use a/d to slow down/speed up the simulation or space to pause/unpause it</html>");
        textfield.repaint();
    }
}
