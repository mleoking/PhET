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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StatesOfMatterParticle that = (StatesOfMatterParticle)o;

        if (Double.compare(that.radius, radius) != 0) {
            return false;
        }
        if (Double.compare(that.x, x) != 0) {
            return false;
        }
        if (Double.compare(that.y, y) != 0) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        temp = x != +0.0d ? Double.doubleToLongBits(x) : 0L;
        result = (int)(temp ^ (temp >>> 32));
        temp = y != +0.0d ? Double.doubleToLongBits(y) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = radius != +0.0d ? Double.doubleToLongBits(radius) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        return result;
    }
}
