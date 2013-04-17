// Copyright 2002-2012, University of Colorado

/**
 * Class: EmfSensingElectron Package: edu.colorado.phet.emf.model Author:
 * Another Guy Date: May 29, 2003
 */

package edu.colorado.phet.radiowaves.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.radiowaves.model.movement.ManualMovement;
import edu.colorado.phet.radiowaves.model.movement.SinusoidalMovement;

public class EmfSensingElectron extends PositionConstrainedElectron {

    private Point2D location;
    private Electron sourceElectron;

    public EmfSensingElectron( EmfModel model, Point2D.Double location, Electron sourceElectron, PositionConstraint positionConstraint ) {
        super( model, location, positionConstraint );
        this.location = location;
        this.sourceElectron = sourceElectron;
        this.setMovementStrategy( new ManualMovement() );

        super.setRecordHistory( false );
    }

    private MutableVector2D aPrev = new MutableVector2D();

    public synchronized void stepInTime( double dt ) {
        super.stepInTime( dt );
        MutableVector2D v = this.getVelocity();

        // If there is no field, then move the electron back to its original location
        if ( sourceElectron.isFieldOff( this.getCurrentPosition().getX() ) ) {
            v.setX( 0 );
            v.setY( (float) ( getStartPosition().getY() - getCurrentPosition().getY() ) / 30 );
            location.setLocation( this.getCurrentPosition().getX(), this.getCurrentPosition().getY() + v.getY() * dt );
        }
        else {
            // For sinusoidal movement, we will just use the incremental displacement of the source
            // electron, multiplied by -1, because the second derivative of a sine or cosine is
            // also a sine or cosine
            if ( sourceElectron.getMovementTypeAt( location ) instanceof SinusoidalMovement ) {
                double d = sourceElectron.getPositionAt( location );
                double dy = ( sourceElectron.getPositionAt( location ) - this.getStartPosition().getY() ) * 0.4;
                location.setLocation( location.getX(), this.getStartPosition().getY() + dy );
            }

            // If the movement is not sinusoidal, then we will use the acceleration of the source
            // electron to determine the field
            else {
                // The field strength is a force on the electron, so we must compute an
                // acceleration
                MutableVector2D fieldStrength = sourceElectron.getDynamicFieldAt( location );
                MutableVector2D a = fieldStrength;
                double x = this.getCurrentPosition().getX();
                double y = this.getCurrentPosition().getY();
                location = this.getCurrentPosition();
                x += v.getX() * dt;

                // The 30 here is a complete fudge factor
                dt /= 10;
                y = y + v.getY() * dt + a.getY() * dt * dt / 2;
                v.setY( v.getY() + ( ( a.getY() + aPrev.getY() ) / 2 ) * (float) dt ); // Verlet
                location.setLocation( x, y );
                aPrev.setX( a.getX() );
                aPrev.setY( a.getY() );
            }

            this.setCurrentPosition( location );

        }
        // Don't need to update observers because that was done in the call to super.stepInTime()
    }

    public void recenter() {
        this.setCurrentPosition( this.getStartPosition() );
        this.setMovementStrategy( new ManualMovement() );
    }
}
