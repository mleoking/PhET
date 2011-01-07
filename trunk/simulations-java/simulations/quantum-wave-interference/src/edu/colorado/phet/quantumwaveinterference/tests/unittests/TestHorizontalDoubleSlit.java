// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.tests.unittests;

import edu.colorado.phet.quantumwaveinterference.model.potentials.HorizontalDoubleSlit;
import junit.framework.TestCase;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 1, 2006
 * Time: 8:20:11 AM
 */

public class TestHorizontalDoubleSlit extends TestCase {
    public void testMediumEvenExample() {
        HorizontalDoubleSlit horizontalDoubleSlit = new HorizontalDoubleSlit( 8, 1, 0, 1, 2, 2, 1.0 );
        Rectangle[] knownSlits = new Rectangle[]{
                new Rectangle( 1, 0, 2, 1 ),
                new Rectangle( 5, 0, 2, 1 )
        };
        assertRectangleArrayEquals( "medium even slits", knownSlits, horizontalDoubleSlit.getSlitAreas() );
        Rectangle[] knownBlocks = new Rectangle[]{
                new Rectangle( 0, 0, 1, 1 ),
                new Rectangle( 3, 0, 2, 1 ),
                new Rectangle( 7, 0, 1, 1 )
        };
        assertRectangleArrayEquals( "medium even blocks", knownBlocks, horizontalDoubleSlit.getBlockAreas() );
    }

    public void testMediumExample() {
        HorizontalDoubleSlit horizontalDoubleSlit = new HorizontalDoubleSlit( 13, 1, 0, 1, 2, 3, 1.0 );
        Rectangle[] knownSlits = new Rectangle[]{
                new Rectangle( 3, 0, 2, 1 ),
                new Rectangle( 8, 0, 2, 1 )
        };
        assertRectangleArrayEquals( "medium odd slits", knownSlits, horizontalDoubleSlit.getSlitAreas() );
        Rectangle[] knownBlocks = new Rectangle[]{
                new Rectangle( 0, 0, 3, 1 ),
                new Rectangle( 5, 0, 3, 1 ),
                new Rectangle( 10, 0, 3, 1 )
        };
        assertRectangleArrayEquals( "medium odd blocks", knownBlocks, horizontalDoubleSlit.getBlockAreas() );
    }

    public void testSmallExample() {
        HorizontalDoubleSlit horizontalDoubleSlit = new HorizontalDoubleSlit( 5, 1, 0, 1, 1, 1, 1.0 );
        Rectangle[] knownSlits = new Rectangle[]{
                new Rectangle( 1, 0, 1, 1 ),
                new Rectangle( 3, 0, 1, 1 )
        };

        Rectangle[] knownBlocks = new Rectangle[]{
                new Rectangle( 0, 0, 1, 1 ),
                new Rectangle( 2, 0, 1, 1 ),
                new Rectangle( 4, 0, 1, 1 )
        };
        assertRectangleArrayEquals( "small odd slits", knownSlits, horizontalDoubleSlit.getSlitAreas() );
        assertRectangleArrayEquals( "small odd blocks", knownBlocks, horizontalDoubleSlit.getBlockAreas() );
    }

    private void assertRectangleArrayEquals( String type, Rectangle[] expected, Rectangle[] actual ) {
        assertEquals( type + " array size", expected.length, actual.length );
        for( int i = 0; i < expected.length; i++ ) {
            assertEquals( type + " element[" + i + "]", expected[i], actual[i] );
        }
    }
}
