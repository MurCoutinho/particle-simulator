import javax.swing.*;
import java.awt.*;

public class LifeFrame extends JFrame {
    private static final int WIDTH = 1000,HEIGHT = 700;
    public LifeFrame(){
        //Create and set up the window.
        JFrame frame = new JFrame("SliderDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainPanel panel = new MainPanel(WIDTH, HEIGHT);

        //Add content to the window.
        frame.add(panel, BorderLayout.CENTER);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);

        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LifeFrame();
            }
        });
    }
}