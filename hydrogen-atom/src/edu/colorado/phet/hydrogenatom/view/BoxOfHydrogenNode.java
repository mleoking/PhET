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
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;


public class BoxOfHydrogenNode extends PhetPNode {

    private static final Paint BOX_FRONT_PAINT = Color.GRAY;

    private static final Color TOP_COLOR_FRONT = Color.GRAY;
    private static final Color TOP_COLOR_BACK = Color.DARK_GRAY;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_COLOR = Color.BLACK;
    
    private static final float BACK_OFFSET = 0.15f;
    
    private static final Font LABEL_FONT = new Font( HAConstants.FONT_NAME, Font.PLAIN, 16 );
    
    public BoxOfHydrogenNode( double width, double height, double depth  ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );

        PNode boxNode = new PNode();
        addChild( boxNode );
        {
            final float w = (float)width;
            final float d = (float)depth;
            GeneralPath topPath = new GeneralPath();
            topPath.moveTo( BACK_OFFSET * w, 0 );
            topPath.lineTo( ( 1 - BACK_OFFSET ) * w, 0 );
            topPath.lineTo( w, d );
            topPath.lineTo( 0, d ); 
            topPath.closePath();
            PPath topNode = new PPath();
            topNode.setPathTo( topPath );
            topNode.setPaint( new GradientPaint( 0f, 0f, TOP_COLOR_BACK, 0f, d, TOP_COLOR_FRONT ) );
            topNode.setStroke( STROKE );
            topNode.setStrokePaint( STROKE_COLOR );
            
            PPath frontNode = new PPath( new Rectangle2D.Double( 0, d, width, height ) );
            frontNode.setPaint( BOX_FRONT_PAINT );
            frontNode.setStroke( STROKE );
            frontNode.setStrokePaint( STROKE_COLOR );
            
            boxNode.addChild( frontNode );
            boxNode.addChild( topNode );
        }

        HTMLNode labelNode = new HTMLNode();
        labelNode.setHTML( SimStrings.get( "label.boxOfHydrogen" ) );
        labelNode.setHTMLColor( HAConstants.CANVAS_LABELS_COLOR );
        labelNode.setFont( LABEL_FONT );
        addChild( labelNode );
        
        PBounds lb = labelNode.getFullBounds();
        PBounds bb = boxNode.getFullBounds();
        
        OriginNode originNode = new OriginNode( Color.RED );
        if ( HAConstants.SHOW_ORIGIN_NODES ) {
            addChild( originNode );
        }
        
        boxNode.setOffset( -bb.getWidth() / 2, -bb.getHeight() );
        bb = boxNode.getFullBounds();
        labelNode.setOffset( -lb.getWidth() / 2, bb.getY() - lb.getHeight() - 5 );
    }
}
