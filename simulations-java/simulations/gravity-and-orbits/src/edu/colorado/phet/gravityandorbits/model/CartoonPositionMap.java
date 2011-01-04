// Copyright 2010-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModeList;

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

    public ImmutableVector2D toCartoon( String name, ImmutableVector2D xR, ImmutableVector2D parentPosition ) {
        final double DEFAULT_DIST = 3.9137E8;
        double distance = xR.getDistance( parentPosition );
        final int K = 10;
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

    //solve for x given cartoonx:
    //cartoonx = parent.x+(x - parent.x) * scale
    //cartoonx = parent.x+x*scale - parent.x*scale
    //(cartoonx -parent.x+parent.x*scale)/scale = x
    //Quick attempt to invert the above function numerically
    public ImmutableVector2D toReal( ImmutableVector2D cartoonPosition, ImmutableVector2D parentPosition ) {
        double dAngle = 0.01;
        double dScale = 1.01;
        ArrayList<Double> angles = getAngles( 0, Math.PI * 2, dAngle );
        ArrayList<Double> distances = getDistances( 1, GravityAndOrbitsModeList.EARTH_PERIHELION, dScale );

        double angle = getAngle( cartoonPosition, parentPosition, 1, angles );
        double dist = getBestDist( cartoonPosition, parentPosition, angle, distances );
        for ( int i = 0; i < 5; i++ ) {
            angle = getAngle( cartoonPosition, parentPosition, dist, getAngles( angle - dAngle, angle + dAngle, dAngle / 2 ) );
            dist = getBestDist( cartoonPosition, parentPosition, angle, getDistances( dist / 2, dist * 2, dScale ) );

            dAngle = dAngle / 2;
            dScale = ( dScale - 1 ) / 2 + 1;
        }
        return parentPosition.getAddedInstance( ImmutableVector2D.parseAngleAndMagnitude( dist, angle ) );
    }

    private ArrayList<Double> getAngles( double minAngle, double maxAngle, double deltaAngle ) {
        ArrayList<Double> angles = new ArrayList<Double>();
        for ( double alpha = minAngle; alpha <= maxAngle; alpha += deltaAngle ) {
            angles.add( alpha );
        }
        return angles;
    }

    private ArrayList<Double> getDistances( double minDist, double maxDist, double dScale ) {
        ArrayList<Double> originalDistToTry = new ArrayList<Double>();
        for ( double distance = minDist; distance < maxDist; distance = distance * dScale ) {
            originalDistToTry.add( distance );
        }
        return originalDistToTry;
    }

    private Double getBestDist( ImmutableVector2D cartoonPosition, ImmutableVector2D parentPosition, double angle, ArrayList<Double> distances ) {
        final HashMap<Double, Double> map = new HashMap<Double, Double>();
        for ( double distance : distances ) {
            ImmutableVector2D guess = parentPosition.getAddedInstance( ImmutableVector2D.parseAngleAndMagnitude( distance, angle ) );
            ImmutableVector2D guessedCartoonLocation = toCartoon( "test", guess, parentPosition );
            double error = guessedCartoonLocation.getDistance( cartoonPosition );
//            System.out.println( "distance = " + distance + ", result = " + guessedCartoonLocation + ", error = " + error );
            map.put( distance, error );
        }
        ArrayList<Double> set = new ArrayList<Double>( map.keySet() );
        Collections.sort( set, new Comparator<Double>() {
            public int compare( Double o1, Double o2 ) {
                return Double.compare( map.get( o1 ), map.get( o2 ) );
            }
        } );
        return set.get( 0 );
    }

    private double getAngle( ImmutableVector2D cartoonPosition, ImmutableVector2D parentPosition, double dist, ArrayList<Double> angles ) {
        final HashMap<Double, Double> map = new HashMap<Double, Double>();
        for ( double alpha : angles ) {
            ImmutableVector2D guess = parentPosition.getAddedInstance( ImmutableVector2D.parseAngleAndMagnitude( dist, alpha ) );
            ImmutableVector2D guessedCartoonLocation = toCartoon( "test", guess, parentPosition );
            double error = guessedCartoonLocation.getDistance( cartoonPosition );
//            System.out.println( "alpha = " + alpha + ", result = " + guessedCartoonLocation + ", goodness = " + error );
            map.put( alpha, error );
        }
        ArrayList<Double> set = new ArrayList<Double>( map.keySet() );
        Collections.sort( set, new Comparator<Double>() {
            public int compare( Double o1, Double o2 ) {
                return Double.compare( map.get( o1 ), map.get( o2 ) );
            }
        } );
//        System.out.println( "least error at alpha = " + set.get( 0 ) );
        return set.get( 0 );
    }

    public static void main( String[] args ) {
        //1+
    }
}
