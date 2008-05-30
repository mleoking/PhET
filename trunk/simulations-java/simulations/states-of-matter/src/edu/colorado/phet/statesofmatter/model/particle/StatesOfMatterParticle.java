package edu.colorado.phet.statesofmatter.model.particle;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.patterns.PubliclyCloneable;

public final class StatesOfMatterParticle implements PubliclyCloneable {
    public static final StatesOfMatterParticle TEST = new StatesOfMatterParticle(0.0, 0.0, 1.0, 1.0);

    private Point2D.Double  position = new Point2D.Double();
    private Vector2D.Double velocity = new Vector2D.Double();
    private Vector2D.Double accel    = new Vector2D.Double();
    private volatile double radius, mass;
    private double inverseMass;

    public StatesOfMatterParticle(double x, double y, double radius, double mass) {
        this(x, y, radius, mass, 0.0, 0.0, 0.0, 0.0);

        if (mass   <= 0.0) throw new IllegalArgumentException("Mass is out of range");
        if (radius <= 0.0) throw new IllegalArgumentException("Radius is out of range");
    }

    private StatesOfMatterParticle(double x, double y, double radius, double mass, double vx, double vy, double ax, double ay) {
        position.setLocation(x, y);
        velocity.setComponents(vx, vy);
        accel.setComponents(ax, ay);
        
        this.mass   = mass;
        this.radius = radius;
    }

    public double getX() {
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    public void setX(double x) {
        position.x = x;
    }

    public void setY(double y) {
        position.y = y;
    }

    public double getVy() {
        return velocity.getY();
    }

    public void setVy(double vy) {
        velocity.setY(vy);
    }

    public double getVx() {
        return velocity.getX();
    }

    public void setVx(double vx) {
        velocity.setX(vx);
    }

    public double getAx() {
        return accel.getX();
    }

    public double getAy() {
        return accel.getY();
    }

    public void setAx(double ax) {
        accel.setX(ax);
    }

    public void setAy(double ay) {
        accel.setY(ay);
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
        if (Double.compare(that.getVx(), getVx()) != 0) {
            return false;
        }
        if (Double.compare(that.getVy(), getVy()) != 0) {
            return false;
        }
        if (Double.compare(that.getX(), getX()) != 0) {
            return false;
        }
        if (Double.compare(that.getY(), getY()) != 0) {
            return false;
        }
        if (Double.compare(that.getAx(), getAx()) != 0) {
            return false;
        }
        if (Double.compare(that.getAy(), getAy()) != 0) {
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
        temp = getVx() != +0.0d ? Double.doubleToLongBits(getVx()) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = getAx() != +0.0d ? Double.doubleToLongBits(getAx()) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = getAy() != +0.0d ? Double.doubleToLongBits(getAy()) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = getVy() != +0.0d ? Double.doubleToLongBits(getVy()) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        temp = inverseMass != +0.0d ? Double.doubleToLongBits(inverseMass) : 0L;
        result = 31 * result + (int)(temp ^ (temp >>> 32));
        return result;
    }

    public Object clone() {
        try {
            StatesOfMatterParticle p = (StatesOfMatterParticle)super.clone();

            p.position = new Point2D.Double(getX(), getY());
            p.velocity = new Vector2D.Double(getVx(), getVy());
            p.accel    = new Vector2D.Double(getAx(), getAy());

            return p;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public String toString() {
        return getClass().getName() + "[x=" + getX() + ",y=" + getY() + ",radius=" + radius + ",mass=" + mass + ",vx=" + getVx() + ",vy=" + getVy() + ",ax=" + getAx() + ",ay=" + getAy() + "]";
    }

    public double getInverseMass() {
        if (inverseMass == 0.0) {
            inverseMass = mass == 0.0 ? Double.MAX_VALUE : 1.0 / mass;
        }

        return inverseMass;
    }

    public double getKineticEnergy() {
        return 0.5 * mass * (getVx() * getVx() + getVy() * getVy());
    }

    public void setKineticEnergy(double energy) {
        // KE = 0.5 * m * v^2 => v = sqrt(2 KE / m)
        double mag = Math.sqrt(2.0 * energy / mass);

        double curMag = velocity.getMagnitude();

        if (curMag == 0.0) {
            double rad = Math.random() * Math.PI * 2.0;

            velocity.setComponents(Math.cos(rad), Math.sin(rad));

            curMag = 1.0;
        }
        
        double scale = mag / curMag;

        velocity.scale(scale);
    }

    public Point2D getPosition() {
        return position;
    }
    
    public Vector2D getVelocity() {
        return velocity;
    }

    public Vector2D getAccel() {
        return accel;
    }
}
