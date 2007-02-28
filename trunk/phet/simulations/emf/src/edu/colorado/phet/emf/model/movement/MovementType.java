/**
 * Class: MovementType
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.model.movement;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.emf.model.Electron;

import java.awt.geom.Point2D;

public interface MovementType {

    void stepInTime( Electron electron, double dt );
    Vector2D getVelocity( Electron electron );
    float getAcceleration( Electron electron );
    Point2D getNextPosition( Point2D position, double t );
    float getMaxAcceleration( Electron electron );
}
