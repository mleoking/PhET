package edu.colorado.phet.qm.tests.unittests;

import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import junit.framework.TestCase;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 1, 2006
 * Time: 8:20:11 AM
 * Copyright (c) Nov 1, 2006 by Sam Reid
 */

public class TestHorizontalDoubleSlit extends TestCase {
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
        assertRectangleArrayEquals( knownSlits, horizontalDoubleSlit.getSlitAreas() );
        assertRectangleArrayEquals( knownBlocks, horizontalDoubleSlit.getBlockAreas() );
    }

    private void assertRectangleArrayEquals( Rectangle[] knownSlits, Rectangle[] slitAreas ) {
        assertEquals( "array size", knownSlits.length, slitAreas.length );
        for( int i = 0; i < knownSlits.length; i++ ) {
            assertEquals( "element[" + i + "]", knownSlits[i], slitAreas[i] );
        }
    }
}
