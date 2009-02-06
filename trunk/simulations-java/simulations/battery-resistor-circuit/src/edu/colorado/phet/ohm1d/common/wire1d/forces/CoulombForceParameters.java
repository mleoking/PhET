package edu.colorado.phet.ohm1d.common.wire1d.forces;

import edu.colorado.phet.ohm1d.common.wire1d.WireParticle;

public class CoulombForceParameters {
    double k;
    double power;
    double minDist;
    double maxDist;

    public CoulombForceParameters( double k, double power ) {
        this.k = k;
        this.power = power;
        maxDist = Double.POSITIVE_INFINITY;
    }

    public double getForce( WireParticle source, WireParticle test ) {
        return getForce( source.getPosition(), source.getCharge(), test.getPosition(), test.getCharge() );
    }

    public double getForce( double sourceX, double sourceQ, double testX, double testQ ) {
        //System.err.println("Different reference.");
        double dx = sourceX - testX;
        double r = Math.abs( dx );

        if ( r < minDist ) {
            r = minDist;
        }
        else if ( r > maxDist ) {
            r = maxDist;
        }
        //System.err.println("P="+i);
        double term = k * Math.pow( r, power ) * sourceQ * testQ;
        if ( dx > 0 ) {
            term *= -1;
        }

        //System.err.println("a="+p.getPosition()+", b="+wp.getPosition()+", sum="+sum);

        return term;
    }

    public void setMinDistance( double d ) {
        this.minDist = d;
    }

}
