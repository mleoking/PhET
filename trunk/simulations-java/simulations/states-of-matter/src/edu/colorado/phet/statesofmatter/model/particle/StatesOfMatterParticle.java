package edu.colorado.phet.statesofmatter.model.particle;

public class StatesOfMatterParticle {
    public static final StatesOfMatterParticle TEST = new StatesOfMatterParticle(0.0, 0.0, 1.0);

    private volatile double x, y, radius;

    public StatesOfMatterParticle(double x, double y, double radius) {
        this.x      = x;
        this.y      = y;
        this.radius = radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
}
