package net.nocturne.tools;//Java program to demonstrate keyPressed,
// keyReleased and keyTyped method
import java.awt.*;
import java.awt.event.*;

public class KeyListenerExample extends Frame implements KeyListener {

    private TextField textField;
    private Label displayLabel;

    static boolean shiftisdown;
    public static boolean iskeypressed(){
        boolean b = shiftisdown;
        return b;
    }
    // Constructor
    public boolean KeyListenerExample() {
        // Set frame properties
        if(shiftisdown) {
            return true;
        }
        else {
            return false;
        }
    }

    // Implement the keyPressed method
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if(KeyEvent.getKeyText(keyCode)=="Shift")
            shiftisdown=true;
        //System.out.println("Key Pressed: " + KeyEvent.getKeyText(keyCode));
    }

    // Implement the keyReleased method
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if(KeyEvent.getKeyText(keyCode)=="Shift")
            shiftisdown=false;
    }

    // Implement the keyTyped method
    @Override
    public void keyTyped(KeyEvent e) {
        char keyChar = e.getKeyChar();
        System.out.println("Key Typed: " + keyChar);
        displayLabel.setText("Typed Text: " + textField.getText() + keyChar);
    }

    public static void main(String[] args) {
        new KeyListenerExample();
    }
}
