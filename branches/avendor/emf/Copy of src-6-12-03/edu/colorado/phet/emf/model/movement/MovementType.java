/**
 * Class: MovementType
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.model.movement;

import edu.colorado.phet.emf.model.Electron;

import java.awt.geom.Point2D;

public interface MovementType {

    public void stepInTime( Electron electron, double dt );
    public float getVelocity( Electron electron );
    public float getAcceleration( Electron electron );
    public Point2D getNextPosition( Point2D position, double t );
}
