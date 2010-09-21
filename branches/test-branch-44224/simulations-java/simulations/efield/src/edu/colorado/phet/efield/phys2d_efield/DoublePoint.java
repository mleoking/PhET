// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.phys2d_efield;

import java.util.Vector;

import edu.colorado.phet.efield.phys2d_efield.util.Averager;

public class DoublePoint {

    public DoublePoint() {
        this( 0.0D, 0.0D );
    }

    public String toString() {
        Vector vector = new Vector();
        vector.add( new Double( x ) );
        vector.add( new Double( y ) );
        return vector.toString();
    }

    public double getLength() {
        return Math.sqrt( x * x + y * y );
    }

    public DoublePoint( double d, double d1 ) {
        x = d;
        y = d1;
        if ( Double.isNaN( d ) ) {
            throw new RuntimeException( "x was NaN" );
        }
        if ( Double.isNaN( d1 ) ) {
            throw new RuntimeException( "Y was NaN" );
        }
        else {
            return;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public DoublePoint add( DoublePoint doublepoint ) {
        return new DoublePoint( x + doublepoint.x, y + doublepoint.y );
    }

    public DoublePoint multiply( double d ) {
        return new DoublePoint( x * d, y * d );
    }

    public DoublePoint subtract( DoublePoint doublepoint ) {
        return new DoublePoint( x - doublepoint.x, y - doublepoint.y );
    }

    public static DoublePoint average( DoublePoint adoublepoint[] ) {
        Averager averager = new Averager();
        Averager averager1 = new Averager();
        for ( int i = 0; i < adoublepoint.length; i++ ) {
            averager.update( adoublepoint[i].getX() );
            averager1.update( adoublepoint[i].getY() );
        }

        return new DoublePoint( averager.value(), averager1.value() );
    }

    double x;
    double y;
}
