package edu.colorado.phet.statesofmatter.model;

public class Particle {
    public static final Particle TEST = new Particle(0.0, 0.0);
    
    private volatile double x, y;

    Particle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
