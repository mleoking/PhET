/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


public class GlassSlideNode extends PhetPNode {

    private static final double SLIDE_WIDTH = 1000; //XXX make infinitely wide
    
    private static final double SLIDE_EDGE_HEIGHT = 10; //XXX get from model
    private static final Stroke SLIDE_EDGE_STROKE = new BasicStroke( 1f );
    private static final Color SLIDE_EDGE_STROKE_COLOR = Color.BLACK;
    private static final Color SLIDE_EDGE_FILL_COLOR = new Color( 176, 218, 200 );
    
    private static final double SLIDE_CENTER_HEIGHT = 500; //XXX get from model
    private static final Color SLIDE_CENTER_FILL_COLOR = new Color( 220, 239, 239 );

    public GlassSlideNode() {
        super();
         
        PPath topEdgeNode = new PPath( new Rectangle2D.Double( 0, 0, SLIDE_WIDTH, SLIDE_EDGE_HEIGHT ) );
        topEdgeNode.setStroke( SLIDE_EDGE_STROKE );
        topEdgeNode.setStrokePaint( SLIDE_EDGE_STROKE_COLOR );
        topEdgeNode.setPaint( SLIDE_EDGE_FILL_COLOR );
        
        PPath bottomEdgeNode = new PPath( new Rectangle2D.Double( 0, 0, SLIDE_WIDTH, SLIDE_EDGE_HEIGHT ) );
        bottomEdgeNode.setStroke( SLIDE_EDGE_STROKE );
        bottomEdgeNode.setStrokePaint( SLIDE_EDGE_STROKE_COLOR );
        bottomEdgeNode.setPaint( SLIDE_EDGE_FILL_COLOR );
        
        PPath centerNode = new PPath( new Rectangle2D.Double( 0, 0, SLIDE_WIDTH, SLIDE_CENTER_HEIGHT ) );
        centerNode.setStroke( null );
        centerNode.setPaint( SLIDE_CENTER_FILL_COLOR );
        
        PComposite parentNode = new PComposite();
        parentNode.addChild( centerNode );
        parentNode.addChild( topEdgeNode );
        parentNode.addChild( bottomEdgeNode );
        
        topEdgeNode.setOffset( 0, 0 );
        centerNode.setOffset( 0, topEdgeNode.getHeight() );
        bottomEdgeNode.setOffset( 0, topEdgeNode.getHeight() + centerNode.getHeight() );
        
        PImage imageNode = new PImage();
        imageNode.setImage( parentNode.toImage() );
        addChild( imageNode );
    }
}
