// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * WORKAROUND for #558 (image returned by PPath.toImage is clipped on right and bottom edges).
 * This node adds a specified amount of padding to its width and height.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PadBoundsNode extends PNode {

    private final double padding;

    public PadBoundsNode() {
        this( 1 );
    }

    public PadBoundsNode( double padding ) {
        if ( ! ( padding > 0 ) ) {
            throw new IllegalArgumentException( "padding must be > 0" );
        }
        this.padding = padding;
    }

    public PadBoundsNode( PNode node ) {
        this( 1, node );
    }

    public PadBoundsNode( double padding, PNode node ) {
        this( padding );
        addChild( node );
    }

    @Override public PBounds getFullBoundsReference() {
        PBounds b = super.getFullBoundsReference();
        return new PBounds( b.getX(), b.getY(), b.getWidth() + padding, b.getHeight() + padding );
    }
}