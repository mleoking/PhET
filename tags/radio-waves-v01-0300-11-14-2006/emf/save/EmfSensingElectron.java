/**
 * Class: EmfSensingElectron
 * Package: edu.colorado.phet.emf.model
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.emf.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.emf.model.movement.ManualMovement;

import java.awt.geom.Point2D;
import java.awt.*;

public class EmfSensingElectron extends PositionConstrainedElectron {
    private Point2D location;
    RetardedFieldElement retardedFieldElement;
    private Electron sourceElectron;
    private Point2D prevPosition = new Point2D.Double();

    public EmfSensingElectron( EmfModel model, Point2D.Double location, Electron sourceElectron,
                               PositionConstraint positionConstraint ) {
        super( model, location, positionConstraint );
        this.location = location;
        this.sourceElectron = sourceElectron;
        retardedFieldElement = new RetardedFieldElement( location, sourceElectron );
        this.setMovementStrategy( new ManualMovement() );
    }

    private float velocityFactor = 100;
    private Vector2D aPrev = new Vector2D();
    public synchronized void stepInTime( double dt ) {
        super.stepInTime( dt );
        Vector2D v = this.getVelocity();
        if( sourceElectron.isFieldOff() ) {
            v.setX( 0 );
            v.setY( 0 );
        }
        else {

            // The field strength is a force on the electron, so we must compute an
            // acceleration
            Vector2D fieldStrength = sourceElectron.getFieldAtLocation( location );
            Vector2D a = fieldStrength;
            float vyPrev = v.getY();
//            v.setY( v.getY() + a.getY() * (float)dt / velocityFactor );

            double x = this.getCurrentPosition().getX();
            double y = this.getCurrentPosition().getY();

            location = this.getCurrentPosition();
            x += v.getX() * dt;
//            y += ( v.getY() + vyPrev ) * dt / 2; // rjl
            v.setY( v.getY() + (( a.getY() + aPrev.getY() ) / 2 ) * (float)dt / velocityFactor ); // Verlet


//            y += v.getY() * dt; // Euler
            location.setLocation( x, y );
            this.setCurrentPosition( location );

            aPrev.setX( a.getX() );
            aPrev.setY( a.getY() );
        }
        updateObservers();
    }

    public void recenter() {
        this.setCurrentPosition( this.getStartPosition() );
        this.setMovementStrategy( new ManualMovement() );
    }
}
