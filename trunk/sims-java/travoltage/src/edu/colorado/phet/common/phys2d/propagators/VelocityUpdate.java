package edu.colorado.phet.common.phys2d.propagators;

import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;

public class VelocityUpdate implements Propagator {
    double max;

    public VelocityUpdate( double maxSpeed ) {
        this.max = maxSpeed;
    }

    public void propagate( double time, Particle p ) {
        //Acceleration a=a.getAcceleration();
        DoublePoint a = p.getAcceleration();
        DoublePoint v = p.getVelocity();
        DoublePoint aDt = a.multiply( time );
        DoublePoint vNew = v.add( aDt );

        double speed = vNew.getLength();
        if( speed > max ) {
            vNew = vNew.multiply( max / speed );
        }
        p.setVelocity( vNew );
        //edu.colorado.phet.common.util.Debug.traceln("V="+vNew);
    }
}
