// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Class for testing some bounds issues that came up while developing this sim.
 *
 * @author John Blanco
 */
public class BoundsTesting {
    public static void main( String[] args ) {
        // Test: Create a rectangle at a non-zero point, then try to shift
        // bounds to compensate.
        PNode nonZeroRect = new PhetPPath( new Rectangle2D.Double( -10, -10, 20, 20 ), Color.RED );
        System.out.println( "nonZeroRect.getFullBoundsReference() = " + nonZeroRect.getFullBoundsReference() );
        PNode compensatedNonZeroRect = new ZeroOffsetNode( nonZeroRect );
        System.out.println( "compensatedNonZeroRect.getFullBoundsReference() = " + compensatedNonZeroRect.getFullBoundsReference() );
        // Test: Create a rectangular node at (0, 0), then  point, then try to
        // shift bounds to compensate.
        PNode zeroRect = new PhetPPath( new Rectangle2D.Double( 0, 0, 20, 20 ), Color.RED );
        zeroRect.setOffset( -100, -100 );
        System.out.println( "zeroRect.getFullBoundsReference() = " + zeroRect.getFullBoundsReference() );
        PNode compensatedZeroRect = new ZeroOffsetNode( zeroRect );
        System.out.println( "compensatedZeroRect.getFullBoundsReference() = " + compensatedZeroRect.getFullBoundsReference() );
    }
}
