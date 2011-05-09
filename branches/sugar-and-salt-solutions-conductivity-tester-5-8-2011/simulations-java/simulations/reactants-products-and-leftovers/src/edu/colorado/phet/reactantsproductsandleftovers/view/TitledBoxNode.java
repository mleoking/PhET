// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A box with a title above it.
 * The origin is at the upper-left corner of the box.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TitledBoxNode extends PhetPNode {
    
    private static final Stroke BOX_STROKE = new BasicStroke( 1f );
    private static final Color BOX_STROKE_COLOR= Color.BLACK;
    private static final Color BOX_FILL_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    
    private static final Font TITLE_FONT = new PhetFont( 20 );
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final double TITLE_Y_SPACING = 10;
    
    private final BoxNode boxNode;
    private final PText titleNode;

    public TitledBoxNode( String title, PDimension boxSize ) {
        
         // box
        boxNode = new BoxNode( boxSize );
        addChild( boxNode );
        
        // title for the box
        titleNode = new PText( title );
        titleNode.setFont( TITLE_FONT );
        titleNode.setTextPaint( TITLE_COLOR );
        addChild( titleNode );
        
        // layout
        double x = 0;
        double y = 0;
        boxNode.setOffset( x, y );
        // title centered above box
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
    }
    
    /**
     * For layout, since many things are aligned relative to the box.
     * @return
     */
    public BoxNode getBoxNode() {
        return boxNode;
    }
    
    public static class BoxNode extends PPath {
        public BoxNode( PDimension size ) {
            super();
            setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
            setPaint( BOX_FILL_COLOR );
            setStrokePaint( BOX_STROKE_COLOR );
            setStroke( BOX_STROKE );
        }
    }
}
