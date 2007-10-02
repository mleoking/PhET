package edu.colorado.phet.statesofmatter.model.particle;

import edu.colorado.phet.common.phetcommon.patterns.PubliclyCloneable;

import java.awt.geom.Point2D;

public class StatesOfMatterParticle implements PubliclyCloneable {
    public static final StatesOfMatterParticle TEST = new StatesOfMatterParticle(0.0, 0.0, 1.0, 1.0);

    private final Point2D.Double position = new Point2D.Double();
    private volatile double radius, mass, vx, vy;
    private double inverseMass;

    public StatesOfMatterParticle(double x, double y, double radius, double mass) {
        this(x, y, radius, mass, 0.0, 0.0);
    }

    private StatesOfMatterParticle(double x, double y, double radius, double mass, double vx, double vy) {
        position.setLocation(x, y);
        
        this.mass   = mass;
        this.radius = radius;
        this.vy     = vy;
        this.vx     = vx;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public void setX(double x) {
        position.setLocation(x, getY());
    }

    public void setY(double y) {
        position.setLocation(getX(), y);
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
        if (Double.compare(that.getX(), getX()) != 0) {
            return false;
        }
        if (Double.compare(that.getY(), getY()) != 0) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        temp = getX() != +0.0d ? Double.doubleToLongBits(getX()) : 0L;
        result = (int)(temp ^ (temp >>> 32));
        temp = getY() != +0.0d ? Double.doubleToLongBits(getY()) : 0L;
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
        return getClass().getName() + "[x=" + getX() + ",y=" + getY() + ",radius=" + radius + ",mass" + mass + ",vx=" + vx + ",vy=" + vy + "]";
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

    public Point2D getPosition() {
        return position;
    }
}
