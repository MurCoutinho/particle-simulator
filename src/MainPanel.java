import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MainPanel extends JPanel implements ChangeListener {

    JSlider[][] forceSliders = new JSlider[4][4];
    LifePanel lifePanel;

    public MainPanel(int width, int height) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        forceSliders[0][0] = addSliders(c,0,"T1 -> T1");
        forceSliders[1][1] = addSliders(c,1,"T2 -> T2");
        forceSliders[1][0] = addSliders(c,2,"T1 -> T2");
        forceSliders[0][1] = addSliders(c,3,"T2 -> T1");
        forceSliders[2][2] = addSliders(c,4,"T3 -> T3");
        forceSliders[0][2] = addSliders(c,5,"T3 -> T1");
        forceSliders[2][0] = addSliders(c,6,"T1 -> T3");
        forceSliders[1][2] = addSliders(c,7,"T3 -> T2");
        forceSliders[2][1] = addSliders(c,8,"T2 -> T3");
        forceSliders[3][3] = addSliders(c,9,"T4 -> T4");
        forceSliders[0][3] = addSliders(c,10,"T4 -> T1");
        forceSliders[3][0] = addSliders(c,11,"T1 -> T4");
        forceSliders[1][3] = addSliders(c,12,"T4 -> T2");
        forceSliders[3][1] = addSliders(c,13,"T2 -> T4");
        forceSliders[2][3] = addSliders(c,14,"T4 -> T3");
        forceSliders[3][2] = addSliders(c,15,"T3 -> T4");
        addLifePanel(c, width, height);
    }

    private void addLifePanel(GridBagConstraints c, int width, int height){
        lifePanel = new LifePanel(width,height);
        lifePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lifePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(height,width,-10,-10)));

        c.gridwidth = 5;
        c.gridheight = 16;
        c.gridx = 2;
        c.gridy = 0;
        add(lifePanel,c);
    }

    private JSlider addSliders(GridBagConstraints c, int position, String text){
        Font font = new Font("Serif", Font.ITALIC, 8);

        //Create the label.
        JLabel sliderLabel = new JLabel(text, JLabel.CENTER);
        sliderLabel.setFont(font);
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Create the slider.
        JSlider slider = new JSlider(JSlider.HORIZONTAL,
                -100,100,0);

        slider.addChangeListener(this);

        //Turn on labels at major tick marks.
        slider.setMajorTickSpacing(20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setFont(font);

        //Put everything together.
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 0;      //make this component tall
        c.weightx = 0.0;
        c.gridx = 0;
        c.gridy = position;
        add(sliderLabel,c);

        c.gridx = 1;
        c.gridy = position;
        add(slider,c);

        return slider;
    }

    /** Listen to the slider. */
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) {
            double[][] forceMatrix = {
                    {0,0,0,0},
                    {0,0,0,0},
                    {0,0,0,0},
                    {0,0,0,0}
            };
            for(int i=0;i<4;i++){
                for(int j=0;j<4;j++){
                    forceMatrix[i][j] = (double) forceSliders[i][j].getValue()/200;
                }
            }
            lifePanel.setForceMatrix(forceMatrix);
        }
    }
}


