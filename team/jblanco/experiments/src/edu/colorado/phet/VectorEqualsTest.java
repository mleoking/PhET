// Copyright 2002-2012, University of Colorado
package edu.colorado.phet;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * @author John Blanco
 */
public class VectorEqualsTest {
    public static void main( String[] args ) {
        Vector2D immutableVector = new Vector2D( 1, 1 );
        MutableVector2D mutableVector = new MutableVector2D();
        mutableVector.setComponents( 1, 1 );
        System.out.println( "immutableVector.equals( mutableVector ) = " + immutableVector.equals( mutableVector ) );
    }
}
