/**
 * Class: SolidSphere
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 22, 2004
 */
package edu.colorado.phet.idealgas.collision;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

import java.awt.geom.Point2D;

public class SolidSphere extends SphericalBody {
    public SolidSphere( double radius ) {
        super( radius );
    }

    protected SolidSphere( Point2D center, //    protected SphericalBody( Vector2D center,
                           Vector2D velocity, Vector2D acceleration, double mass, double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }
}
