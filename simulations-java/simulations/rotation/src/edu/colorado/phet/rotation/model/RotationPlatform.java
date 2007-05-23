package edu.colorado.phet.rotation.model;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.motion.model.MotionBody;

import java.awt.geom.Point2D;

/**
 * Author: Sam Reid
 * May 11, 2007, 3:41:07 AM
 */
public class RotationPlatform extends MotionBody {
    private SerializablePoint2D center = new SerializablePoint2D( 200, 200 );
    private double radius = 200.0;

    public void setState( RotationPlatform rotationPlatform ) {
        super.setState( rotationPlatform );
        center = new SerializablePoint2D( rotationPlatform.center );
        radius = rotationPlatform.radius;
    }

    public boolean containsPosition( Point2D loc ) {
        return loc.distance( center ) < radius;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

}
