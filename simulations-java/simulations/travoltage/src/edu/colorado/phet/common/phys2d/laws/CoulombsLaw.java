package edu.colorado.phet.common.phys2d.laws;

import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;

public class CoulombsLaw implements ForceLaw {
    double range;
    double k;
    double minDist;

    public CoulombsLaw( double k ) {
        this( Double.MAX_VALUE, k );
    }

    public CoulombsLaw( double range, double k ) {
        this( range, k, 0 );
    }

    public CoulombsLaw( double range, double k, double minDist ) {
        this.range = range;
        this.k = k;
        this.minDist = minDist;
    }

    public DoublePoint getForce( Particle a, Particle b ) {
        DoublePoint ax = a.getPosition();
        DoublePoint bx = b.getPosition();
        //edu.colorado.phet.common.util.Debug.traceln("ax="+ax+", bx="+bx);
        DoublePoint diff = ax.subtract( bx );
        double length = diff.getLength();
        if( length < minDist ) {
            length = minDist;
        }
        if( length > range ) {
            return new DoublePoint( 0, 0 );
        }
        //f=k q1 q2/r/r * rhat
        double rCubed = Math.pow( length, 3 );
        double force = -k * a.getCharge() * b.getCharge() / rCubed;
        //edu.colorado.phet.common.util.Debug.traceln("Force="+force);
        if( Double.isInfinite( force ) || Double.isNaN( force ) || Double.isNaN( diff.getX() ) || Double.isNaN( diff.getY() ) ) {
            return new DoublePoint();
        }
        try {
            DoublePoint scaled = diff.multiply( force );
            return scaled;
        }
        catch( RuntimeException e ) {
            e.printStackTrace();
            edu.colorado.phet.common.util.Debug.traceln( "Using force: " + force + ", diff=" + diff );
            throw e;
        }
    }
}
