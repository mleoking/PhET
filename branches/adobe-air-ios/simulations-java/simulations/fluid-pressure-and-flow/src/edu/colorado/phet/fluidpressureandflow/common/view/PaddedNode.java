// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Fixes a "ghosting" problem caused when some line strokes don't appear within the repaint rectangle by making the bounds be a bit larger than the actual bounds.
 * See Unfuddle #558 and Piccolo #181.
 *
 * @author Sam Reid
 */
public class PaddedNode extends PNode {
    public PaddedNode( PNode node ) {
        addChild( node );
    }

    @Override public PBounds computeFullBounds( PBounds dstBounds ) {
        return new PBounds( RectangleUtils.expand( super.computeFullBounds( dstBounds ), 3, 3 ) );
    }
}