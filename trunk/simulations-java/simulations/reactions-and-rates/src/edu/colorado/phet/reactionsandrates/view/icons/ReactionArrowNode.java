/* Copyright 2003-2009, University of Colorado */

package edu.colorado.phet.reactionsandrates.view.icons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/*
 * ReactionArrowNode (rewritten for Unfuddle #592)
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactionArrowNode extends PNode {
    
    private static final double ARROW_LENGTH = 30;
    private static final double HEAD_HEIGHT = 12;
    private static final double HEAD_WIDTH = 8;

    public ReactionArrowNode( Paint arrowColor ) {
        PNode topNode = new HalfArrowNode();
        PNode bottomNode = new HalfArrowNode();
        bottomNode.rotate( Math.PI );
        bottomNode.setOffset( ARROW_LENGTH, topNode.getFullBoundsReference().getMaxY() + 7 );
        addChild( topNode );
        addChild( bottomNode );
    }
    
    /*
     * Creates an arrow that looks like this:
     * 
     *    ______\
     */
    private static class HalfArrowNode extends PPath {
        
        public HalfArrowNode() {
            super();
            setPaint( Color.BLACK );
            setStroke( new BasicStroke( 2f ) );
            GeneralPath topPath = new GeneralPath();
            final float yOffset = (float)(HEAD_WIDTH / 2 );
            topPath.moveTo( 0f, yOffset );
            topPath.lineTo( (float) ARROW_LENGTH, yOffset );
            topPath.lineTo( (float)( ARROW_LENGTH - HEAD_HEIGHT ), 0f );
            PPath topNode = new PPath( topPath );
            addChild( topNode );
        }
    }
}
