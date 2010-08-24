/**
 * Class: SolidSphere
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 22, 2004
 */
package edu.colorado.phet.common.collision;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

public class SolidSphere extends SphericalBody {
    public SolidSphere( double radius ) {
        super( radius );
    }

    protected SolidSphere( Point2D center, Vector2DInterface velocity, Vector2DInterface acceleration,
                           double mass, double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }
}
