package edu.colorado.phet.statesofmatter.model;

public class StatesOfMatterParticle {
    public static final StatesOfMatterParticle TEST = new StatesOfMatterParticle(0.0, 0.0);

    private volatile double x, y;

    public StatesOfMatterParticle(double x, double y) {
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
