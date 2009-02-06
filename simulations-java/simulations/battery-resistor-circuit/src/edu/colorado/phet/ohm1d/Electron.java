package edu.colorado.phet.ohm1d;

import edu.colorado.phet.ohm1d.collisions.DefaultCollisionEvent;
import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;
import edu.colorado.phet.ohm1d.common.wire1d.WirePatch;
import edu.colorado.phet.ohm1d.common.wire1d.propagators.CompositePropagator1d;
import edu.colorado.phet.ohm1d.oscillator2d.Core;

public class Electron extends WireParticle {
    boolean collided = false;
    Core last;
    double time = Double.NaN;
    DefaultCollisionEvent dce;

    public Electron( CompositePropagator1d cp, WirePatch wp, DefaultCollisionEvent dce ) {
        super( cp, wp );
        this.dce = dce;
    }

    public void forgetCollision() {
        this.last = null;
        this.time = Double.NaN;
        this.collided = false;
        //this.dce=null;
    }

    public Core getLastCollision() {
        if ( dce == null ) {
            return null;
        }
        if ( dce.currentTime() - time > 20 ) {
            setCollided( false );
        }
        return last;
    }

    public void setVelocity( double v ) {
        super.setVelocity( v );
        /**My hack to make sure the electron sits still for a moment.*/
//	double timeSinceCollision=dce.currentTime()-time;
//	double stallTime=2;
//	double linearTime=15;
//	if (timeSinceCollision<stallTime)
//	    {
//		super.setVelocity(0);
//	    }
//	else if (timeSinceCollision<linearTime)
//	    {
//		double dt=timeSinceCollision-stallTime;
//		super.setVelocity(v*dt/(linearTime-stallTime));
//	    }
//	else
//	    super.setVelocity(v);
    }

    public void setLastCollision( Core last, double time ) {
        this.last = last;
        this.time = time;//throw new RuntimeException();
    }

    public void setCollided( boolean collided ) {
        this.collided = collided;
    }

    public boolean isCollided() {
        return collided;
    }
}
