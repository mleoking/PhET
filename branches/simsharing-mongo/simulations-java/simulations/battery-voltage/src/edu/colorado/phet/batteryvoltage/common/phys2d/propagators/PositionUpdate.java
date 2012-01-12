// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.phys2d.propagators;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;

public class PositionUpdate implements Propagator {
    public void propagate( double time, Particle p ) {
        DoublePoint v = p.getVelocity();
        DoublePoint x = p.getPosition();
        //util.Debug.traceln("Position="+x+"\n");
        DoublePoint vDt = v.multiply( time );
        //util.Debug.traceln("v*dt="+vDt+"\n");
        DoublePoint xNew = x.add( vDt );
        p.setPosition( xNew );
        //util.Debug.traceln("New Position="+xNew+"\n");
    }

}
