/** Sam Reid*/
package edu.colorado.phet.cck.tests;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 9:24:10 PM
 * Copyright (c) May 25, 2004 by Sam Reid
 */
public class TestAffineTransform {
    public static void main( String[] args ) {
        AffineTransform a = new AffineTransform();
        a.rotate( -Math.PI / 4 );
        a.translate( 0, -1 );

        AffineTransform b = new AffineTransform();
        b.translate( 0, -1 );
        b.rotate( -Math.PI / 4 );
        Point2D pt = new Point2D.Double( 0, 1 );

        System.out.println( "a.transform( pt,null) = " + a.transform( pt, null ) );
        System.out.println( "b.transform( pt,null) = " + b.transform( pt, null ) );
    }
}
