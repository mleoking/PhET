/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.view.BoxNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;


public abstract class GameBoxNode extends PhetPNode {
    
    private static final PDimension BOX_SIZE = RPALConstants.BEFORE_AFTER_BOX_SIZE;
    private static final Font TITLE_FONT = new PhetFont( 24 );
    private static final Color TITLE_COLOR = Color.BLACK;
    
    private final BoxNode boxNode;
    private final PText titleNode;

    public GameBoxNode( String title ) {
        
         // box
        boxNode = new BoxNode( BOX_SIZE );
        addChild( boxNode );
        
        // title for the box
        titleNode = new PText( title );
        titleNode.setFont( TITLE_FONT );
        titleNode.setTextPaint( TITLE_COLOR );
        addChild( titleNode );
    }
    
    protected BoxNode getBoxNode() {
        return boxNode;
    }
    
    protected PText getTitleNode() {
        return titleNode;
    }
    
    public PDimension getBoxSize() {
        return new PDimension( getBoxWidth(), getBoxHeight() );
    }
    
    /**
     * Box width, used by layout code.
     * @return
     */
    public double getBoxWidth() {
        return boxNode.getFullBoundsReference().getWidth();
    }

    /**
     * Box height, used by layout code.
     * @return
     */
    public double getBoxHeight() {
        return boxNode.getFullBoundsReference().getHeight();
    }
}
