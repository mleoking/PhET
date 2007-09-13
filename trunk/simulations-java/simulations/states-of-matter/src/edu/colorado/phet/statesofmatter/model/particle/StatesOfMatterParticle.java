package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.common.phetcommon.patterns.PubliclyCloneable;

public class StatesOfMatterParticle implements PubliclyCloneable {
    public static final StatesOfMatterParticle TEST = new StatesOfMatterParticle(0.0, 0.0, 1.0, 1.0);

    private volatile double x, y, radius, mass, vx, vy;
    private double inverseMass;

    public StatesOfMatterParticle(double x, double y, double radius, double mass) {
        this(x, y, radius, mass, 0.0, 0.0);
    }

    private StatesOfMatterParticle(double x, double y, double radius, double mass, double vx, double vy) {
        this.x      = x;
        this.y      = y;
        this.mass   = mass;
        this.radius = radius;
        this.vy     = vy;
        this.vx     = vx;
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

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getMass() {
        return mass;
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

        if (Double.compare(that.inverseMass, inverseMass) != 0) {
            return false;
        }
        if (Double.compare(that.mass, mass) != 0) {
            return false;
        }
        if (Double.compare(that.radius, radius) != 0) {
            return false;
        }
        if (Double.compare(that.vx, vx) != 0) {
            return false;
        }
        if (Double.compare(that.vy, vy) != 0) {
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
        temp = mass != +0.0d ? Double.doubleToLongBits(mass) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = vx != +0.0d ? Double.doubleToLongBits(vx) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = vy != +0.0d ? Double.doubleToLongBits(vy) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = inverseMass != +0.0d ? Double.doubleToLongBits(inverseMass) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        return result;
    }

    public Object clone() {
        try {
            // Shallow clone is sufficient
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",radius=" + radius + ",mass" + mass + ",vx=" + vx + ",vy=" + vy + "]";
    }

    public double getInverseMass() {
        if (inverseMass == 0.0) {
            inverseMass = mass == 0.0 ? Double.MAX_VALUE : 1.0 / mass;
        }

        return inverseMass;
    }

    public double getKineticEnergy() {
        return 0.5 * mass * (vx * vx + vy * vy);
    }
}
