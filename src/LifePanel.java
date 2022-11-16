import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

public class LifePanel extends JPanel implements ActionListener {
    private static final int SQUARE_SIZE = 3;
    private static final int CORRECTION = SQUARE_SIZE/2;
    private static final int NUMBER_OF_PARTICLES = 5000;
    private static final int NUMBER_OF_COLORS = 4;
    private static final int SLOW_MOTION = 3;
    private static final double REDUCE_VELOCITY = 0.3;
    private static final int LIMIT_DISTANCE_PULL_BACK = 120000;
    private static final int LIMIT_DISTANCE_FOR_INTERACTION = 80;

    //Negative values = attraction
    //positive values = repulsion
    //Let x_{i,j} be the element of the matrix. x_{i,j} represent how i is affected by j.
    private double[][] forceMatrix = {
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0}
    };
    private static final double FORCE_CENTER = -0.000001;

    private double[][] forces;
    private int numberOfThreads = 2;
    private int count = 0;
    Timer timer;
    ArrayList<Particle> particles = new ArrayList<Particle>();
    Particle[] particlesForUi = new Particle[NUMBER_OF_PARTICLES];
    public LifePanel(int width, int height){
        setSize(width,height);

        for(int i=0;i<NUMBER_OF_PARTICLES;i++){
            particles.add(new Particle(width,height, NUMBER_OF_COLORS));
            particles.get(i).setReduceVelocityFactor(REDUCE_VELOCITY);
            particles.get(i).setForces(forceMatrix,FORCE_CENTER);
            particles.get(i).setLimitDistanceForInteraction(LIMIT_DISTANCE_FOR_INTERACTION);
            particles.get(i).setLimitDistancePullBack(LIMIT_DISTANCE_PULL_BACK);
            particlesForUi[i] = (Particle) particles.get(i).clone();
        }

        timer = new Timer(50, this);
        timer.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        setBackground(Color.BLACK);
        display(g);
    }

    private void display(Graphics g){
        Particle p;

        for(int i=0;i<NUMBER_OF_PARTICLES;i++){
            p = particlesForUi[i];
            g.setColor(p.getColor());
            g.fillRect((int)p.getPosition()[0]-CORRECTION, (int)p.getPosition()[1]-CORRECTION, SQUARE_SIZE, SQUARE_SIZE);
        }
    }

    public void setForceMatrix(double[][] forceMatrix) {
        this.forceMatrix = forceMatrix;
        for(int i=0;i<NUMBER_OF_PARTICLES;i++){
            particles.get(i).setForces(forceMatrix,FORCE_CENTER);
            particlesForUi[i] = (Particle) particles.get(i).clone();
        }
    }

    class ForcesThread extends Thread {
        @Override
        public void run() {
            var threadId = Thread.currentThread().getId()%numberOfThreads;

            for(int i=(int)threadId;i<NUMBER_OF_PARTICLES;i+=numberOfThreads){
                double[] f = particles.get(i).computeResultantForce(particles, NUMBER_OF_PARTICLES);
                forces[i][0] = f[0];
                forces[i][1] = f[1];
            }
        }
    }

    public void computeForces() {
        forces = new double[NUMBER_OF_PARTICLES][2];

        ForcesThread[] threads = new ForcesThread[numberOfThreads];
        for(int t=0;t<numberOfThreads;t++){
            threads[t] = new ForcesThread();
            threads[t].start();
        }

        for(int t=0;t<numberOfThreads;t++){
            try {
                threads[t].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class UpdateThread extends Thread {
        @Override
        public void run() {
            for(int i=0;i<NUMBER_OF_PARTICLES;i++){
                particles.get(i).updatePosition(1);
            }
            Collections.sort(particles, new ParticleComparator());
            computeForces();
            for(int i=0;i<NUMBER_OF_PARTICLES;i++){
                particles.get(i).updateVelocity(forces[i]);
            }
        }
    }

    public void actionPerformed(ActionEvent e){
        if(count%SLOW_MOTION == 0){
            //copy phase
            for(int i=0;i<NUMBER_OF_PARTICLES;i++){
                particlesForUi[i] = (Particle) particles.get(i).clone();
            }

            new UpdateThread().start();
        }
        count++;

        for(int i=0;i<NUMBER_OF_PARTICLES;i++){
            particlesForUi[i].updatePosition(SLOW_MOTION);
        }

        repaint();
    }
}