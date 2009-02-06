package edu.colorado.phet.ohm1d.common.phys2d;

public class DoublePoint {
    double x;
    double y;

    public DoublePoint() //immutable
    {
        this( 0, 0 );
    }

    public String toString() {
        java.util.Vector v = new java.util.Vector();
        v.add( new Double( x ) );
        v.add( new Double( y ) );
        return v.toString();
    }

    public double length() {
        return getLength();
    }

    public double getLength() {
        return Math.sqrt( x * x + y * y );
    }

    public DoublePoint( double x, double y ) {
        this.x = x;
        this.y = y;
        if ( Double.isNaN( x ) ) {
            throw new RuntimeException( "x was NaN" );
        }
        if ( Double.isNaN( y ) ) {
            throw new RuntimeException( "Y was NaN" );
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public DoublePoint add( DoublePoint b ) {
        return new DoublePoint( this.x + b.x, this.y + b.y );
    }

    public DoublePoint normalize() {
        double length = getLength();
        return multiply( 1.0 / length );
    }

    public DoublePoint multiply( double scale ) {
        return new DoublePoint( this.x * scale, this.y * scale );
    }

    public DoublePoint subtract( DoublePoint p ) {
        return new DoublePoint( this.x - p.x, this.y - p.y );
    }

//    public static DoublePoint average( DoublePoint[] dp ) {
//        phet.phys2d.util.Averager x = new phet.phys2d.util.Averager();
//        phet.phys2d.util.Averager y = new phet.phys2d.util.Averager();
//        for( int i = 0; i < dp.length; i++ ) {
//            x.update( dp[i].getX() );
//            y.update( dp[i].getY() );
//        }
//        return new DoublePoint( x.value(), y.value() );
//    }
}
