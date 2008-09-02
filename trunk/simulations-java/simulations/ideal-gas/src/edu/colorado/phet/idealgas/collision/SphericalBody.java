/**
 * Class: SphericalBody
 * Package: edu.colorado.phet.model.body
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * NOTE: This class is not thread-safe!!!!!
 */
public class SphericalBody extends CollidableBody {

    private double radius;

    private static Point2D.Double tempVector = new Point2D.Double();

    public SphericalBody( double radius ) {
        this.radius = radius;
    }

    protected SphericalBody( Point2D center,
                             Vector2D velocity,
                             Vector2D acceleration,
                             double mass,
                             double radius ) {
        super( center, velocity, acceleration, mass, 0 );
        this.radius = radius;
    }

    public Point2D getCM() {
        return this.getPosition();
    }

    public double getMomentOfInertia() {
        return getMass() * radius * radius * 2 / 5;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius( double radius ) {
        this.radius = radius;
    }

    public Point2D getCenter() {
        return this.getPosition();
    }

    public double getContactOffset( Body body ) {
        return this.getRadius();
    }
}
