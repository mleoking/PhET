package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.CompositePropagator;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;
import edu.colorado.phet.common.phys2d.laws.ForceLawPropagator;
import edu.colorado.phet.common.phys2d.propagators.PositionUpdate;
import edu.colorado.phet.common.phys2d.propagators.ResetAcceleration;
import edu.colorado.phet.common.phys2d.propagators.VelocityUpdate;
import edu.colorado.phet.travoltage.rotate.Finger;
import edu.colorado.phet.travoltage.rotate.RotatingImage;

import java.awt.*;

public class GoToFinger implements Propagator {
    RotatingImage a;
    CompositePropagator cp;
    RemovalPropagator next;
    static final int THRESHOLD = 15;

    public GoToFinger( RotatingImage a, TravoltaImageBounce tib, ForceLawPropagator coulomb, double maxVel, RemovalPropagator next ) {
        this.next = next;
        this.a = a;
        this.cp = new CompositePropagator();
        cp.add( new ResetAcceleration() );
        //add wall bouncing.
        //cp.add(tib);
        cp.add( new FingerPropagator() );
        //cp.add(coulomb);
        //add force laws.
        cp.add( new VelocityUpdate( maxVel ) );
        cp.add( new PositionUpdate() );
    }

    public void propagate( double dt, Particle p ) {
        cp.propagate( dt, p );
    }

    public class FingerPropagator implements Propagator {
        public void propagate( double dt, Particle p ) {
            //edu.colorado.phet.common.util.Debug.traceln();
            Point target = Finger.getFingerLocation( a, 115, 6 );
            DoublePoint dp = new DoublePoint( target.x, target.y );
            DoublePoint position = p.getPosition();
            //Point pivot=a.getPivot();
            DoublePoint diff = new DoublePoint( dp.getX(), dp.getY() ).subtract( position );
            if( diff.getLength() < THRESHOLD ) {
                ( (ShockElectron)p ).setPropagator( next );
            }
            diff = diff.normalize();
            //double angle=a.getAngle();
            p.setAcceleration( diff.multiply( 1000 ) );
        }
    }
}
