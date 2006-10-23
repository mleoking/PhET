/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;


public class ExperimentAtomNode extends PhetPNode {

    private static final Font FONT = new Font( HAConstants.DEFAULT_FONT_NAME, Font.PLAIN, 200 );
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color BOX_FILL_COLOR = Color.BLACK;
    private static final Color BOX_STROKE_COLOR = Color.WHITE;
    private static final Stroke BOX_STROKE = new BasicStroke( 2f );
    private static final Dimension BOX_SIZE = new Dimension( 250, 250 );
    
    public ExperimentAtomNode() {
        super();
        
        PPath boxNode = new PPath( new Rectangle2D.Double( 0, 0, BOX_SIZE.width, BOX_SIZE.height ) );
        boxNode.setPaint( BOX_FILL_COLOR );
        boxNode.setStroke( BOX_STROKE );
        boxNode.setStrokePaint( BOX_STROKE_COLOR );
        boxNode.setOffset( 0, 0 );
        
        PText textNode = new PText( "?" );
        textNode.setFont( FONT );
        textNode.setTextPaint( TEXT_COLOR );
        double x = boxNode.getX() + ( ( boxNode.getWidth() / 2 ) - ( textNode.getWidth() / 2 ) );
        double y = boxNode.getY() + ( ( boxNode.getHeight() / 2 ) - ( textNode.getHeight() / 2 ) );
        textNode.setOffset( x, y );
        
        addChild( boxNode );
        addChild( textNode );
    }
}
