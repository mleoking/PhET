package phys2d.propagators;

import phys2d.DoublePoint;
import phys2d.Particle;
import phys2d.Propagator;

public abstract class BoundsBounce implements Propagator {
    public void propagate( double time, Particle p ) {
        DoublePoint v = p.getVelocity();
        DoublePoint x = p.getPosition();

        if( isOutOfBounds( x ) ) {
            //util.Debug.traceln("out of bounds.");
            DoublePoint newVel = ( getNewVelocity( v ) );
            //util.Debug.traceln("V0="+p.getVelocity());
            p.setVelocity( newVel );
            //util.Debug.traceln("VF="+p.getVelocity());
            p.setPosition( getPointAtBounds( x ) );
            p.setAcceleration( new DoublePoint() );
        }
    }

    /**
     * Returns the corresponding point just outside the boundary.
     */
    public abstract DoublePoint getPointAtBounds( DoublePoint x );

    public abstract boolean isOutOfBounds( DoublePoint x );

    public abstract DoublePoint getNewVelocity( DoublePoint v );
}
