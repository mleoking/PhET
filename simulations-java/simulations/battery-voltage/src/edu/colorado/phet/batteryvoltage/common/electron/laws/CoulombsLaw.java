package edu.colorado.phet.batteryvoltage.common.electron.laws;

import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Particle;

public class CoulombsLaw implements ForceLaw {
    double range;
    double k;
    double minDist;

    public CoulombsLaw( double range, double k, double minDist ) {
        this.range = range;
        this.k = k;
        this.minDist = minDist;
    }

    public DoublePoint getForce( Particle a, Particle b ) {
        DoublePoint ax = a.getPosition();
        DoublePoint bx = b.getPosition();
        //util.Debug.traceln("ax="+ax+", bx="+bx);
        DoublePoint diff = ax.subtract( bx );
        double length = diff.getLength();
        if ( length < minDist ) {
            length = minDist;
        }
        if ( length > range ) {
            return new DoublePoint( 0, 0 );
        }
        //f=k q1 q2/r/r * rhat
        double rCubed = Math.pow( length, 3 );
        double force = -k * a.getCharge() * b.getCharge() / rCubed;
        if ( Double.isInfinite( force ) || Double.isNaN( force ) || Double.isNaN( diff.getX() ) || Double.isNaN( diff.getY() ) ) {
            return new DoublePoint();
        }
        try {
            DoublePoint scaled = diff.multiply( force );
            return scaled;
        }
        catch( RuntimeException e ) {
            e.printStackTrace();
//            util.Debug.traceln( "Using force: " + force + ", diff=" + diff );
            throw e;
        }
    }
}
