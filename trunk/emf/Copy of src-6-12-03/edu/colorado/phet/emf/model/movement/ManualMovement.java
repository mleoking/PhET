/**
 * Class: ManualMovement
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.model.movement;

import edu.colorado.phet.emf.model.movement.MovementType;
import edu.colorado.phet.emf.model.Electron;

import java.awt.geom.Point2D;

/**
 * This movement strategy does nothing automatically. It is
 * intended for use when the object in the model to which it
 * applies is to be moved with the mouse.
 */
public class ManualMovement implements MovementType {

    private Point2D position;

    /**
     * TODO: The conditional here is a hack
     * @param position
     * @param runningTime
     * @param dt
     * @return
     */
    public Point2D getNextPosition( Point2D position, double t ) {
        return this.position != null ? this.position : position;
    }

    public void setPosition( Point2D position ) {
        this.position = position;
    }

    public void stepInTime( Electron electron, double dt ) {
    }

    public float getVelocity( Electron electron ) {
        return 0;
    }

    public float getAcceleration( Electron electron ) {
        return 0;
    }
}
