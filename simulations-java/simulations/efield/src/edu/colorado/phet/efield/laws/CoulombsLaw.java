// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.laws;

import edu.colorado.phet.efield.phys2d_efield.DoublePoint;
import edu.colorado.phet.efield.phys2d_efield.Particle;

// Referenced classes of package edu.colorado.phet.efield.laws:
//            ForceLaw

public class CoulombsLaw
        implements MyForceLaw {

    public CoulombsLaw( double d ) {
        this( 1.7976931348623157E+308D, d );
    }

    public CoulombsLaw( double d, double d1 ) {
        this( d, d1, 0.0D );
    }

    public CoulombsLaw( double d, double d1, double d2 ) {
        range = d;
        k = d1;
        minDist = d2;
    }

    public DoublePoint getForce( Particle particle, Particle particle1 ) {
        DoublePoint doublepoint2;
        double d2;
        DoublePoint doublepoint = particle.getPosition();
        DoublePoint doublepoint1 = particle1.getPosition();
        doublepoint2 = doublepoint.subtract( doublepoint1 );
        double d = doublepoint2.getLength();
        if ( d < minDist ) {
            d = minDist;
        }
        if ( d > range ) {
            return new DoublePoint( 0.0D, 0.0D );
        }
        double d1 = Math.pow( d, 3D );
        d2 = ( -k * particle.getCharge() * particle1.getCharge() ) / d1;
        if ( Double.isInfinite( d2 ) || Double.isNaN( d2 ) || Double.isNaN( doublepoint2.getX() ) || Double.isNaN( doublepoint2.getY() ) ) {
            return new DoublePoint();
        }
        DoublePoint doublepoint3 = doublepoint2.multiply( d2 );
        return doublepoint3;
//
//        runtimeexception.printStackTrace();
//        Debug.traceln("Using force: " + d2 + ", diff=" + doublepoint2);
//        throw runtimeexception;
    }

    double range;
    double k;
    double minDist;
}
