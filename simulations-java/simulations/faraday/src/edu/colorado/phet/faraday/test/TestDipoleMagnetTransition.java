/* Copyright 2010, University of Colorado */

package edu.colorado.phet.faraday.test;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.BarMagnet;
import edu.colorado.phet.faraday.util.Vector2D;

/**
 * Unfuddle #2236
 * Tests the DipoleMagnet's inside-outside B-field transition, to verify that we don't have a large
 * discontinuity. Info printed to System.out tells you how to modify DipoleMagnet.FUDGE_FACTOR.
 * <p>
 * TODO: Make DipoleMagnet self calibrating, so that it determines the appropriate FUDGE_FACTOR value.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestDipoleMagnetTransition {

    public static void main( String[] args ) {

        // create a dipole magnet
        Dimension magnetSize = FaradayConstants.BAR_MAGNET_SIZE;
        BarMagnet magnet = new BarMagnet();
        magnet.setSize( magnetSize.getWidth(), magnetSize.getHeight() );
        magnet.setLocation( new Point2D.Double( 0, 0 ) );
        magnet.setMaxStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MAX );
        magnet.setMinStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MIN );
        magnet.setStrength( FaradayConstants.BAR_MAGNET_STRENGTH_MAX );
        magnet.setDirection( 0 );
        
        // pick points inside and outside the magnet, very close together
        double deltaX = 0.000000000001;
        Point2D pInside = new Point2D.Double( magnetSize.getWidth()/2 - deltaX, 0 );
        Point2D pOutside = new Point2D.Double( magnetSize.getWidth()/2 + deltaX, 0 );
        
        // measure the B-field inside and outside to check for a smooth transition
        Vector2D bInside = magnet.getBField( pInside );
        Vector2D bOutside = magnet.getBField( pOutside );
        double difference = bInside.getMagnitude() - bOutside.getMagnitude();
        
        // print values
        System.out.println( "pInside=" + pInside + " strength=" + bInside );
        System.out.println( "pOutside=" + pOutside + " strength=" + bOutside );
        System.out.println( "difference=" + difference );
        
        // evaluate the results
        if ( difference < 0 ) {
            System.out.println( "difference is negative, make DipoleMagnet.FUDGE_FACTOR smaller" );
        }
        else if ( difference >= 1 ) {
            System.out.println( "difference is >= 1, make DipoleMagnet.FUDGE_FACTOR larger" );
        }
        else {
            System.out.println( "difference is < 1, DipoleMagnet.FUDGE_FACTOR is perfect" );
        }
    }
}
