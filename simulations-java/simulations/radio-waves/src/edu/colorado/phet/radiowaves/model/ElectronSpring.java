/**
 * Class: ElectronSpring Package: edu.colorado.phet.emf.model Author: Another
 * Guy Date: Jun 4, 2003
 */

package edu.colorado.phet.radiowaves.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common_1200.math.Vector2D;

public class ElectronSpring implements ModelElement {

    private Point2D origin;
    private Electron electron;
    private float k;

    public ElectronSpring( Point2D origin, Electron electron, float k ) {
        this.origin = origin;
        this.electron = electron;
        this.k = k;
    }

    public void setOrigin( Point2D origin ) {
        this.origin = origin;
    }

    private Vector2D a = new Vector2D.Float();
    private Point2D.Float newLoc = new Point2D.Float();

    public void stepInTime( double dt ) {
        Point2D electronLoc = electron.getCurrentPosition();
        float springLength = (float) origin.distance( electronLoc );
        float fMag = springLength * k;
        double m = electron.getMass();
        double aMag = fMag / m;
        a.setX( (float) ( origin.getX() - electronLoc.getX() ) );
        a.setY( (float) ( origin.getY() - electronLoc.getY() ) );
        a.normalize();
        a.scale( (float) aMag );
        newLoc.setLocation( electronLoc );
        edu.colorado.phet.common.phetcommon.math.Vector2D electronVelocity = electron.getVelocity();
        float vx0 = (float) electronVelocity.getX();
        float vx1 = vx0 + (float) a.getX() * (float) dt;
        float vxAve = 0;
        //        float vxAve = ( vx0 + vx1 ) / 2;

        float vy0 = (float) electronVelocity.getY();
        float vy1 = vy0 + (float) a.getY() * (float) dt;
        float vyAve = ( vy0 + vy1 ) / 2;

        if ( newLoc.x < origin.getX() ) {
            newLoc.x = (float) Math.max( origin.getX(), newLoc.x + vxAve * (float) dt );
        }
        else {
            newLoc.x = (float) Math.min( origin.getX(), newLoc.x + vxAve * (float) dt );
        }
        if ( newLoc.y < origin.getY() ) {
            newLoc.y = (float) Math.max( origin.getY(), newLoc.y + vyAve * (float) dt );
        }
        else {
            newLoc.y = (float) Math.min( origin.getY(), newLoc.y + vyAve * (float) dt );
        }

        //        newLoc.x += vxAve * (float)dt;
        //        newLoc.y += vyAve * (float)dt;

        electron.setCurrentPosition( newLoc );
        electron.notifyObservers();
    }
}
