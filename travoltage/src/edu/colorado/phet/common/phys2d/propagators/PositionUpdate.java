package edu.colorado.phet.common.phys2d.propagators;

import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;

public class PositionUpdate implements Propagator {
    public void propagate( double time, Particle p ) {
        DoublePoint v = p.getVelocity();
        DoublePoint x = p.getPosition();
        //edu.colorado.phet.common.util.Debug.traceln("Position="+x+"\n");
        DoublePoint vDt = v.multiply( time );
        //edu.colorado.phet.common.util.Debug.traceln("v*dt="+vDt+"\n");
        DoublePoint xNew = x.add( vDt );
        p.setPosition( xNew );
        //edu.colorado.phet.common.util.Debug.traceln("New Position="+xNew+"\n");
    }

    public static void changePosition( double time, Particle p ) {
        DoublePoint v = p.getVelocity();
        DoublePoint x = p.getPosition();
        //edu.colorado.phet.common.util.Debug.traceln("Position="+x+"\n");
        DoublePoint vDt = v.multiply( time );
        //edu.colorado.phet.common.util.Debug.traceln("v*dt="+vDt+"\n");
        DoublePoint xNew = x.add( vDt );
        p.setPosition( xNew );
        //edu.colorado.phet.common.util.Debug.traceln("New Position="+xNew+"\n");
    }
}
