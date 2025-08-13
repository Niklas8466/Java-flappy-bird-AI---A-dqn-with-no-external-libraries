package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Inputs implements KeyListener {
    private final HashMap<Character, Boolean> pressedButtons = new HashMap<>();


    @Override
    public void keyTyped(KeyEvent e) {
        //not needed
    }


    @Override
    public void keyPressed(KeyEvent e) {
        //sets the lowercase version of the char in the hashmap to true
        String s = "" + e.getKeyChar();
        s = s.toLowerCase();
        pressedButtons.put(s.charAt(0), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //sets the lowercase version of the char in the hashmap to false
        String s = "" + e.getKeyChar();
        s = s.toLowerCase();
        pressedButtons.put(s.charAt(0), false);
    }

    public boolean isKeyPressed(char c){
        if(pressedButtons.get(c) == null)
            return false;
        return pressedButtons.get(c);
    }

    public void removePressedButton(char c){
        pressedButtons.put(c, false);
    }
}
