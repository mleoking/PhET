/**
 * Class: RelativisticManualMovement Package:
 * edu.colorado.phet.emf.model.movement Author: Another Guy Date: Jun 13, 2003
 */

package edu.colorado.phet.radiowaves.model.movement;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.radiowaves.RadioWavesApplication;
import edu.colorado.phet.radiowaves.model.Electron;

public class RelativisticManualMovement extends ManualMovement {

    private Point2D position;
    private Point2D driverPosition;
    private Spring mouseSpring;
    private Spring originSpring;
    private static float s_k = 1f;
    private Vector2D.Float f = new Vector2D.Float();
    private float vy;

    //    private Vector2D.Float v = new Vector2D.Float();

    public RelativisticManualMovement( Point2D drivenBodyPosition ) {
        mouseSpring = new Spring( s_k );
        originSpring = new Spring( s_k / 100 );
        this.position = drivenBodyPosition;
        this.driverPosition = drivenBodyPosition;
        super.setPosition( drivenBodyPosition );
        mouseSpring.setEndpoint1( this.position );
        originSpring.setEndpoint2( new Point2D.Double( 100, 300 ) );
    }

    public synchronized void setPosition( Point2D driverPosition ) {
        this.driverPosition = driverPosition;
    }

    public synchronized void stepInTime( Electron electron, double dt ) {
        if ( this.position != null ) {
            mouseSpring.setEndpoint2( driverPosition );
            originSpring.setEndpoint1( this.position );
            f = mouseSpring.getForceOnEndpoint1();
            Vector2D.Float fOriginSpring = originSpring.getForceOnEndpoint1();
            f.add( originSpring.getForceOnEndpoint1() );
            //            vy = super.getVelocity( electron ).getY();
            double m = electron.getMass();

            // The relativistic mass can go wacko because we move through
            // time in quatized steps. So we need this check
            if ( Double.isNaN( m ) ) {
                vy = (float) RadioWavesApplication.s_speedOfLight * 0.9f;
            }
            else {
                double a = f.getY() / m;
                vy = (float) ( vy + a * dt );
            }
            position.setLocation( position.getX(), position.getY() + vy * dt );
            super.setPosition( position );
        }
        super.stepInTime( electron, dt );
    }
}
