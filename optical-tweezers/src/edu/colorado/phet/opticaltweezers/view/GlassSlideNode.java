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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.opticaltweezers.model.GlassSlide;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class GlassSlideNode extends PhetPNode {

    private static final Stroke SLIDE_EDGE_STROKE = new BasicStroke( 1f );
    private static final Color SLIDE_EDGE_STROKE_COLOR = Color.BLACK;
    private static final Color SLIDE_EDGE_FILL_COLOR = new Color( 176, 218, 200 );
    
    private static final Color SLIDE_CENTER_FILL_COLOR = new Color( 220, 239, 239 );

    private GlassSlide _glassSlide;
    private ModelViewTransform _modelViewTransform;
    private double _canvasWidth;
    
    private PPath topEdgeNode, bottomEdgeNode, centerNode;
    
    public GlassSlideNode( GlassSlide glassSlide, ModelViewTransform modelViewTransform ) {
        super();
         
        _glassSlide = glassSlide;
        _modelViewTransform = modelViewTransform;
        _canvasWidth = 1;
        
        topEdgeNode = new PPath();
        topEdgeNode.setStroke( SLIDE_EDGE_STROKE );
        topEdgeNode.setStrokePaint( SLIDE_EDGE_STROKE_COLOR );
        topEdgeNode.setPaint( SLIDE_EDGE_FILL_COLOR );
        
        bottomEdgeNode = new PPath();
        bottomEdgeNode.setStroke( SLIDE_EDGE_STROKE );
        bottomEdgeNode.setStrokePaint( SLIDE_EDGE_STROKE_COLOR );
        bottomEdgeNode.setPaint( SLIDE_EDGE_FILL_COLOR );
        
        centerNode = new PPath();
        centerNode.setStroke( null );
        centerNode.setPaint( SLIDE_CENTER_FILL_COLOR );
        
        addChild( centerNode );
        addChild( topEdgeNode );
        addChild( bottomEdgeNode );
        
        update();
    }
    
    public void setCanvasWidth( double canvasWidth ) {
        if ( canvasWidth <= 0 ) {
            throw new IllegalArgumentException( "canvasWidth must be > 0: " + canvasWidth );
        }
        if ( canvasWidth != _canvasWidth ) {
            _canvasWidth = canvasWidth;
            update();
        }
    }
    
    private void update() {
        
        final double height = _modelViewTransform.transform( _glassSlide.getHeight() );
        final double edgeHeight = _modelViewTransform.transform( _glassSlide.getEdgeHeight() );
        final double y = _modelViewTransform.transform( _glassSlide.getY() );
        
        topEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, edgeHeight ) );
        bottomEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, edgeHeight ) );
        centerNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, height - ( 2 * edgeHeight ) ) );
        
        topEdgeNode.setOffset( 0, 0 );
        centerNode.setOffset( 0, topEdgeNode.getHeight() );
        bottomEdgeNode.setOffset( 0, topEdgeNode.getHeight() + centerNode.getHeight() );
        
        setOffset( 0, y );
    }
}
