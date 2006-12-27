package edu.colorado.phet.batteryvoltage.man.dir;

import edu.colorado.phet.batteryvoltage.man.Directional;
import edu.colorado.phet.batteryvoltage.man.Release;
import phys2d.Propagator;

public class PropagatorDir implements Directional {
    Release drop;
    Propagator left;
    Propagator right;

    public PropagatorDir( Release drop, Propagator left, Propagator right ) {
        this.drop = drop;
        this.left = left;
        this.right = right;
    }

    public void setCarryRight( boolean b ) {
        if( b ) {
            drop.setPropagator( right );
        }
        else {
            drop.setPropagator( left );
        }
    }
}
