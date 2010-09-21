/**
 * Class: MovementType Package: edu.colorado.phet.waves.model Author: Another
 * Guy Date: May 27, 2003
 */

package edu.colorado.phet.radiowaves.model.movement;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.radiowaves.model.Electron;

public interface MovementType {

    void stepInTime( Electron electron, double dt );

    Vector2D getVelocity( Electron electron );

    float getAcceleration( Electron electron );

    Point2D getNextPosition( Point2D position, double t );

    float getMaxAcceleration( Electron electron );
}
