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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;


public class GlassSlideNode extends PhetPNode implements Observer {

    public static final double EDGE_HEIGHT = 12; // pixels
    private static final Stroke EDGE_STROKE = new BasicStroke( 1f );
    private static final Color EDGE_STROKE_COLOR = Color.BLACK;
    private static final Color EDGE_FILL_COLOR = new Color( 176, 218, 200 );
    
    private static final Color CENTER_FILL_COLOR = new Color( 220, 239, 239 );

    private Fluid _fluid;
    private ModelViewTransform _modelViewTransform;
    private double _canvasWidth;
    
    private PPath topEdgeNode, bottomEdgeNode, centerNode;
    
    public GlassSlideNode( Fluid fluid, ModelViewTransform modelViewTransform ) {
        super();
         
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _canvasWidth = 1;
        
        topEdgeNode = new PPath();
        topEdgeNode.setStroke( EDGE_STROKE );
        topEdgeNode.setStrokePaint( EDGE_STROKE_COLOR );
        topEdgeNode.setPaint( EDGE_FILL_COLOR );
        
        bottomEdgeNode = new PPath();
        bottomEdgeNode.setStroke( EDGE_STROKE );
        bottomEdgeNode.setStrokePaint( EDGE_STROKE_COLOR );
        bottomEdgeNode.setPaint( EDGE_FILL_COLOR );
        
        centerNode = new PPath();
        centerNode.setStroke( null );
        centerNode.setPaint( CENTER_FILL_COLOR );
        
        addChild( centerNode );
        addChild( topEdgeNode );
        addChild( bottomEdgeNode );
        
        update();
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
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
        
        // fluid flow must be left-to-right or right-to-left
        assert( _fluid.getOrientation() ==  Math.toRadians( 0 ) || _fluid.getOrientation() == Math.toRadians( 90 ) );
        
        final double height = _modelViewTransform.transform( _fluid.getWidth() );
        final double y = _modelViewTransform.transform( _fluid.getY() );
        
        // create each part with (0,0) at top right
        topEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, EDGE_HEIGHT ) );
        bottomEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, EDGE_HEIGHT ) );
        centerNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, height ) );
        
        // translate parts so that (0,0) of the slide is at left center
        topEdgeNode.setOffset( 0, -( height / 2 ) - EDGE_HEIGHT );
        centerNode.setOffset( 0, -( height / 2 ) );
        bottomEdgeNode.setOffset( 0, +( height / 2 ) );
        
        setOffset( 0, y );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        //XXX
    }
}
