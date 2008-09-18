/**
 * Class: ManualMovement Package: edu.colorado.phet.waves.model Author: Another
 * Guy Date: May 27, 2003
 */

package edu.colorado.phet.radiowaves.model.movement;

import java.awt.geom.Point2D;

import edu.colorado.phet.common_1200.math.MedianFilter;
import edu.colorado.phet.common_1200.math.Vector2D;
import edu.colorado.phet.radiowaves.model.Electron;

/**
 * This movement strategy does nothing automatically. It is
 * intended for use when the object in the model to which it
 * applies is to be moved with the mouse.
 */
public class ManualMovement implements MovementType {

    private Point2D position;
    private static int s_posHistoryLength = 10;
    private int numHistoryEntries;
    private float[] yPosHistory = new float[s_posHistoryLength];
    private double[] yVHistory = new double[s_posHistoryLength - 1];
    private double[] yAHistory = new double[s_posHistoryLength - 2];
    private float vAve;
    private float aAve;
    private Vector2D.Float velocity = new Vector2D.Float();
    private MedianFilter dataFilter = new MedianFilter( yVHistory );

    /**
     * TODO: The conditional here is a hack
     */
    public Point2D getNextPosition( Point2D position, double dt ) {
        return this.position != null ? this.position : position;
    }

    public void setPosition( Point2D position ) {
        this.position = position;
    }

    public void stepInTime( Electron electron, double dt ) {
        if ( this.position != null ) {
            numHistoryEntries = Math.min( numHistoryEntries + 1, s_posHistoryLength );
            electron.setCurrentPosition( this.position );
            for ( int i = yPosHistory.length - 1; i > 0; i-- ) {
                yPosHistory[i] = yPosHistory[i - 1];
            }
            yPosHistory[0] = (float) electron.getCurrentPosition().getY();
            computeKinetics();
        }
    }

    private void computeKinetics() {
        // Compute velocities
        vAve = 0;
        for ( int i = 0; i < numHistoryEntries - 1; i++ ) {
            float v = yPosHistory[i + 1] - yPosHistory[i];
            yVHistory[i] = v;
            vAve += v;
        }
        vAve /= yVHistory.length;
        velocity.setX( vAve );

        // Compute accelerations
        dataFilter.filter( 3 );
        aAve = 0;
        for ( int i = 0; i < numHistoryEntries - 2; i++ ) {
            double a = yVHistory[i + 1] - yVHistory[i];
            yAHistory[i] = a;
            aAve += a;
        }
        aAve /= yAHistory.length;
        //        aAve = (float)MedianFilter.getMedian( yAHistory );
    }

    public Vector2D getVelocity( Electron electron ) {
        return velocity;
    }

    public float getAcceleration( Electron electron ) {
        return (float) aAve;
    }

    public float getMaxAcceleration( Electron electron ) {
        return 0.1f;
    }
}
