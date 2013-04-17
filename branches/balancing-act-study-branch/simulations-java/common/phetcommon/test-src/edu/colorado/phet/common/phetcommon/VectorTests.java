// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon;

import junit.framework.TestCase;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

public class VectorTests extends TestCase {
    public void testSomething() {
        Vector2D v = new Vector2D( 0, 0 );
        Vector2D b = new Vector2D( 1, 2 );
        Vector2D c = new Vector2D( 0, 0 );
        assertTrue( !v.equals( b ) );
        assertTrue( v.equals( c ) );
        assertEquals( new Vector2D( 0, 0 ).distance( new Vector2D( 1, 1 ) ), Math.sqrt( 2 ) );
    }
}
