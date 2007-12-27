/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;


public class IconNode extends PNode {
    
    private static final int VERTICAL_SPACING = 2; // vertical space between a tool's icon and label
    private static final Font LABEL_FONT = new PhetDefaultFont( 12 );
    private static final Color LABEL_COLOR = Color.BLACK;
    
    public IconNode( Image image, String name ) {
        super();
        
        PImage imageNode = new PImage( image );
        addChild( imageNode );
        
        HTMLNode labelNode = new HTMLNode( name );
        labelNode.setFont( LABEL_FONT );
        labelNode.setHTMLColor( LABEL_COLOR );
        addChild( labelNode );
        
        if ( imageNode.getWidth() > labelNode.getWidth() ) {
            imageNode.setOffset( 0, 0 );
            labelNode.setOffset( imageNode.getX() + ( imageNode.getWidth() - labelNode.getWidth() ) / 2, imageNode.getY() + imageNode.getHeight() + VERTICAL_SPACING );
        }
        else {
            labelNode.setOffset( 0, imageNode.getY() + imageNode.getHeight() + VERTICAL_SPACING );
            imageNode.setOffset( labelNode.getX() + ( labelNode.getWidth() - imageNode.getWidth() ) / 2, 0 );
        }
    }
}
