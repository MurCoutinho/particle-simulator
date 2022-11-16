import java.awt.*;
import java.util.ArrayList;
import java.util.*;

public class Particle implements Cloneable{
    private double [] maxPosition = {0,0}, velocity = {0,0}, position = {0,0};
    private int type;

    private double reduceVelocityFactor = 1;

    public static final int TYPE_RED = 0;
    public static final int TYPE_BLUE = 1;
    public static final int TYPE_GREEN = 2;
    public static final int TYPE_WHITE = 3;

    private double forceCenter = 0;
    private double[][] forceMatrix;
    private double limitDistanceForInteraction = 0;
    private double limitDistancePullBack = 0;
    private double distanceToOrigin = 0;

    public Particle(int xMax, int yMax, int numberOfColors) {
        maxPosition[0] = xMax;
        maxPosition[1] = yMax;
        randomizePosition(xMax, yMax);
        randomizeType(numberOfColors);
    }

    @Override
    public Object clone() {
        try {
            Particle p = (Particle) super.clone();
            p.position = new double[2];
            p.velocity = new double[2];
            p.position[0] = position[0];
            p.position[1] = position[1];
            p.velocity[0] = velocity[0];
            p.velocity[1] = velocity[1];
            return p;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public double[] getPosition() {
        return position;
    }

    public int getType() {
        return type;
    }

    public Color getColor() {
        Color color;
        switch (type) {
            case TYPE_RED -> color = Color.RED;
            case TYPE_BLUE -> color = Color.BLUE;
            case TYPE_GREEN -> color = Color.GREEN;
            default -> color = Color.WHITE;
        }
        return color;
    }

    public double getDistanceToOrigin() {
        return distanceToOrigin;
    }

    public void setReduceVelocityFactor(double reduceVelocityFactor) {
        this.reduceVelocityFactor = reduceVelocityFactor;
    }

    public void setForces(double[][] forceMatrix, double forceCenter){
        this.forceMatrix = forceMatrix;
        this.forceCenter = forceCenter;
    }

    public void setLimitDistanceForInteraction(double limitDistanceForInteraction) {
        this.limitDistanceForInteraction = limitDistanceForInteraction;
    }

    public void setLimitDistancePullBack(double limitDistancePullBack) {
        this.limitDistancePullBack = limitDistancePullBack;
    }

    public void updatePosition(int slowMotionFactor) {
        for (int i=0;i<2;i++){
            position[i] += velocity[i]/slowMotionFactor;
        }
        distanceToOrigin = Math.sqrt(position[0]*position[0]+position[1]*position[1]);
    }

    public void updateVelocity(double[] shift){
        for (int i=0;i<2;i++) {
            velocity[i] += shift[i];
            velocity[i] *= reduceVelocityFactor;
        }
    }

    public void randomizePosition(int xMax, int yMax){
        position[0] = (int) (Math.random()*xMax/2+xMax/4);
        position[1] = (int) (Math.random()*yMax/2+yMax/4);
    }

    public void randomizeType(int numberOfColors){
        type = (int) (Math.random()*numberOfColors);
    }

    public double[] computeForceFromCenter(){
        double[] f = {0,0};
        double[] delta = {this.getPosition()[0] - maxPosition[0]/2,
                this.getPosition()[1] - maxPosition[1]/2};
        double d = (delta[0] * delta[0] + delta[1] * delta[1]);

        if (d>limitDistancePullBack) {
            double F = forceCenter * d;
            f[0] += (F * delta[0]);
            f[1] += (F * delta[1]);
        }

        return f;
    }

    public double[] computeResultantForce(ArrayList<Particle> particles, int numberOfParticles){
        double[] f = {0,0};
        double dMin = this.distanceToOrigin-limitDistanceForInteraction;
        double dMax = this.distanceToOrigin+limitDistanceForInteraction;

        for(int p2=0;p2<numberOfParticles;p2++) {
            if(particles.get(p2).getDistanceToOrigin() > dMax)
                break;
            if(particles.get(p2).getDistanceToOrigin() < dMin)
                continue;

            double[] delta = {this.getPosition()[0] - particles.get(p2).getPosition()[0],
                    this.getPosition()[1] - particles.get(p2).getPosition()[1]};

            double d = Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
            if (d > 0 && d<limitDistanceForInteraction) {
                double F = forceMatrix[this.type][particles.get(p2).getType()] * (1 / d);
                f[0] += (F * delta[0]);
                f[1] += (F * delta[1]);
            }
        }

        double[] fc = this.computeForceFromCenter();
        f[0] += fc[0];
        f[1] += fc[1];

        return f;
    }

}

class ParticleComparator implements Comparator<Particle> {
    @Override
    public int compare(Particle o1, Particle o2) {
        if (o1.getDistanceToOrigin() == o2.getDistanceToOrigin())
            return 0;
        else if (o1.getDistanceToOrigin() > o2.getDistanceToOrigin())
            return 1;
        else
            return -1;
    }
}
