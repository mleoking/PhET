// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModeList;

import static java.lang.Math.exp;

/**
 * Converts between cartoon and real coordinates for Body instances.
 *
 * @author Sam Reid
 */
public class CartoonPositionMap {
    private double cartoonOffsetScale;

    public CartoonPositionMap( double cartoonOffsetScale ) {
        this.cartoonOffsetScale = cartoonOffsetScale;
    }

    final double DEFAULT_DIST = 3.9137E8;
    final int K = 10;

    /*
     * Convert real coordinates to cartoon by using interpolating between the real coordinate and a scaled offset from the parent.
     */
    public ImmutableVector2D toCartoon( String name, ImmutableVector2D xR, ImmutableVector2D parentPosition ) {

        double distance = xR.getDistance( parentPosition );

        double alpha = 1 - Math.exp( -distance / DEFAULT_DIST / K );

        //When nearby to the parent position, use this linear function: cartoonx = parent.x+(x - parent.x) * scale
        final ImmutableVector2D xC = parentPosition.plus( xR.minus( parentPosition ).times( cartoonOffsetScale ) );

//        if ( name.equals( "Moon" ) ) {
//            System.out.println( "child = " + name + ", distance = " + distance + ", alpha = " + alpha );
//        }

        //alpha = 0:cartoon, low distance
        //alpha = 1:real, high distance

        //x = xC*(1-alpha) + xR*(alpha);
        final ImmutableVector2D x = xC.getScaledInstance( 1 - alpha ).getAddedInstance( xR.getScaledInstance( alpha ) );
        return x;//cartoonx = parent.x+(x - parent.x) * scale
    }

    /*
     * To convert from cartoon to real coordinates, have to invert the above function.  This is done by converting it to a 1-Dimensional problem, then
     * by using a root finding algorithm (bisection) to find the root.  Then the solution is projected back into real space.
     */
    public ImmutableVector2D toReal( ImmutableVector2D cartoonPosition, ImmutableVector2D parentPosition ) {
        // |(xC-xR)| = (scale-1) * dist
        // |(x-xR)| = (1-alpha)(scale-1)*dist = exp(-dist/D/K)*(scale-1)*dist

        // |x-P|

        // y = bxe^(ax) => x = W(ay/b)/a

        // Coff,P =(R-P) * scale
        // xoff,P = (R-P) * scale * (1-alpha) + alpha * (R-P)      axe^bx + cxe^dx
        // xoff = dist * scale * exp(-dist/D/K) + dist *(1 - exp(-dist/D/K))

        // xoff,P = dist + dist * (scale - 1) * exp(-dist/D/K)

        double cpDist = cartoonPosition.getDistance( parentPosition );
        double s = new Bisection( cpDist, cartoonOffsetScale, DEFAULT_DIST, K, GravityAndOrbitsModeList.EARTH_PERIHELION * 10 ).bisect();

        //R = P + Xoff * (C-P) / |C-P|
//        return parentPosition.getAddedInstance( cartoonPosition.getScaledInstance( s ) )/cartoonPosition.getMagnitude();
        ImmutableVector2D cMinusP = cartoonPosition.getSubtractedInstance( parentPosition );
        final ImmutableVector2D real = parentPosition.getAddedInstance( cMinusP.getScaledInstance( s / cMinusP.getMagnitude() ) );

        //output should be similar to cartoonPosition
//        System.out.println( "metric  =  " + toCartoon( "tester", real, parentPosition ).getDistance( cartoonPosition ));

        return real;
    }

    static class Bisection {
        double cpDist;
        double scale;
        double D;
        double K;
        private final double MAX;

        Bisection( double cpDist, double scale, double d, double k, double MAX ) {
            this.cpDist = cpDist;
            this.scale = scale;
            D = d;
            K = k;
            this.MAX = MAX;
        }

        //http://www.torkian.info/Site/Research/Entries/2008/2/28_Root-finding_algorithm_files/NO5A.java
        public double bisect() {
            double x = 0, del = 1e-2, a = 0;
            double b = MAX;
            double dx = b - a;
            int k = 0;
            final int MAX_ITERATIONS = 1000;
            while ( Math.abs( dx ) > del && k < MAX_ITERATIONS && f( x ) != 0 ) {
                x = ( ( a + b ) / 2 );
                if ( ( f( a ) * f( x ) ) < 0 ) {
                    b = x;
                    dx = b - a;
                }
                else {
                    a = x;
                    dx = b - a;
                }
                k++;
            }
            return x;
        }

        public double f( double dist ) {
            return dist + dist * ( scale - 1 ) * exp( -dist / D / K ) - cpDist;
        }

    }
}
