/**
 * Class: TestVectors
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 20, 2004
 */
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;

public class TestVectors {
    public static void main( String[] args ) {
        AbstractVector2D.Double v = new ImmutableVector2D.Double( 3, 4 );
        System.out.println( "v = " + v );

        Vector2D.Double dir = new Vector2D.Double( 7, 8 );
        dir.setX( 123 );
        System.out.println( "dir = " + dir );


    }
}
