/* Copyright 2007, University of Colorado */

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
    
    private PPath _topEdgeNode, _bottomEdgeNode, _centerNode;
    
    public GlassSlideNode( Fluid fluid, ModelViewTransform modelViewTransform ) {
        super();
         
        setPickable( false );
        setChildrenPickable( false );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _canvasWidth = 1;
        
        _topEdgeNode = new PPath();
        _topEdgeNode.setStroke( EDGE_STROKE );
        _topEdgeNode.setStrokePaint( EDGE_STROKE_COLOR );
        _topEdgeNode.setPaint( EDGE_FILL_COLOR );
        
        _bottomEdgeNode = new PPath();
        _bottomEdgeNode.setStroke( EDGE_STROKE );
        _bottomEdgeNode.setStrokePaint( EDGE_STROKE_COLOR );
        _bottomEdgeNode.setPaint( EDGE_FILL_COLOR );
        
        _centerNode = new PPath();
        _centerNode.setStroke( null );
        _centerNode.setPaint( CENTER_FILL_COLOR );
        
        addChild( _centerNode );
        addChild( _topEdgeNode );
        addChild( _bottomEdgeNode );
        
        update();
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    public Rectangle2D getCenterGlobalBounds() {
        return _centerNode.getGlobalFullBounds();
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
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    private void update() {
        
        // fluid flow must be left-to-right or right-to-left
        assert( _fluid.getOrientation() ==  Math.toRadians( 0 ) || _fluid.getOrientation() == Math.toRadians( 90 ) );
        
        final double height = _modelViewTransform.transform( _fluid.getWidth() );
        final double y = _modelViewTransform.transform( _fluid.getY() );
        
        // create each part with (0,0) at top right
        _topEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, EDGE_HEIGHT ) );
        _bottomEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, EDGE_HEIGHT ) );
        _centerNode.setPathTo( new Rectangle2D.Double( 0, 0, _canvasWidth, height ) );
        
        // translate parts so that (0,0) of the slide is at left center
        _topEdgeNode.setOffset( 0, -( height / 2 ) - EDGE_HEIGHT );
        _centerNode.setOffset( 0, -( height / 2 ) );
        _bottomEdgeNode.setOffset( 0, +( height / 2 ) );
        
        setOffset( 0, y );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        //XXX
    }
}
