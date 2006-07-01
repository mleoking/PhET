package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.CompositePropagator;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;
import edu.colorado.phet.common.phys2d.laws.ForceLawPropagator;
import edu.colorado.phet.common.phys2d.propagators.PositionUpdate;
import edu.colorado.phet.common.phys2d.propagators.ResetAcceleration;
import edu.colorado.phet.common.phys2d.propagators.VelocityUpdate;
import edu.colorado.phet.travoltage.rotate.RotatingImage;

import java.awt.*;

public class GoToElbow implements Propagator {
    RotatingImage a;
    CompositePropagator cp;
    GoToFinger gtf;

    public GoToElbow( RotatingImage a, TravoltaImageBounce tib, ForceLawPropagator coulomb, double maxVel, GoToFinger gtf ) {
        this.a = a;
        this.cp = new CompositePropagator();
        cp.add( new ResetAcceleration() );
        //add wall bouncing.
        //cp.add(tib);
        cp.add( new ElbowPropagator() );
        //cp.add(coulomb);
        //add force laws.
        cp.add( new VelocityUpdate( maxVel ) );
        cp.add( new PositionUpdate() );
        this.gtf = gtf;
    }

    public void propagate( double dt, Particle p ) {
        cp.propagate( dt, p );
    }

    public class ElbowPropagator implements Propagator {
        public void propagate( double dt, Particle p ) {
            //edu.colorado.phet.common.util.Debug.traceln();
            DoublePoint dp = p.getPosition();
            Point pivot = a.getPivot();
            int DX = 0;
            int DY = 55;
            DoublePoint diff = new DoublePoint( pivot.x + DX, pivot.y + DY ).subtract( dp );
            double threshold = 20;
            if( diff.getLength() < threshold ) {
                ShockElectron sh = (ShockElectron)p;
                sh.setPropagator( gtf );
            }
            diff = diff.normalize();
            //double angle=a.getAngle();
            p.setAcceleration( diff.multiply( 1000 ) );
        }
    }
}
