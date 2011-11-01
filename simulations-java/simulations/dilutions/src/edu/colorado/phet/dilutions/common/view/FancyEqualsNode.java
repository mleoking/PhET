// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A fancy equals sign ("=") with a drop shadow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FancyEqualsNode extends PComposite {

    public FancyEqualsNode() {
        this( new PDimension( 50, 35 ), new Color( 225, 225, 225 ), new Color( 200, 200, 200 ) );
    }

    public FancyEqualsNode( PDimension size, Color color, Color shadowColor ) {

        // nodes
        final double ySpacing = 0.25 * size.getHeight();
        final double rectHeight = ( size.height - ySpacing ) / 2;
        PNode topNode = new HorizontalRectangleNode( size.width, rectHeight, color );
        PNode topShadowNode = new HorizontalRectangleNode( size.width, rectHeight, shadowColor ) {{
            setStroke( null );
        }};
        PNode bottomNode = new HorizontalRectangleNode( size.width, rectHeight, color );
        PNode bottomShadowNode = new HorizontalRectangleNode( size.width, rectHeight, shadowColor ) {{
            setStroke( null );
        }};

        // rendering order
        addChild( topShadowNode );
        addChild( bottomShadowNode );
        addChild( topNode );
        addChild( bottomNode );

        // layout
        final double shadowXOffset = 0.05 * size.width;
        final double shadowYOffset = shadowXOffset;
        topShadowNode.setOffset( topNode.getXOffset() + shadowXOffset, topNode.getYOffset() + shadowYOffset );
        bottomNode.setOffset( topNode.getXOffset(), topNode.getFullBoundsReference().getMaxY() + ySpacing );
        bottomShadowNode.setOffset( bottomNode.getXOffset() + shadowXOffset, bottomNode.getYOffset() + shadowYOffset );
    }

    private static class HorizontalRectangleNode extends PPath {
        public HorizontalRectangleNode( double width, double height, Color color ) {
            setPathTo( new Rectangle2D.Double( 0, 0, width, height ) );
            setPaint( color );
            setStrokePaint( new Color( 0, 0, 0, 125 ) ); // soften with transparency
        }
    }
}
