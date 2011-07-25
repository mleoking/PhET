// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * @author John Blanco
 */
public class TestPlank {

    public static void main( String[] args ) {
        Vector2D testVector = new Vector2D( 0, -1 );
        System.out.println( "Initial vector angle = " + testVector.getAngle() );
        testVector.setAngle( Math.PI / 2 );
        System.out.println( "Vector = " + testVector );
        System.out.println( "New vector angle = " + testVector.getAngle() );
    }
}
