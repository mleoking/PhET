/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * FluidNode is the visual representation of the fluid.
 * The fluid is contained on a glass microscope slide that has a fixed height
 * and infinite width.  The slide has top and bottom edges that have a fixed height.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FluidNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // properties of the "glass slide" that the fluid sits on
    public static final double EDGE_HEIGHT = 12; // pixels
    private static final Stroke EDGE_STROKE = new BasicStroke( 1f );
    private static final Color EDGE_STROKE_COLOR = Color.BLACK;
    private static final Color EDGE_FILL_COLOR = new Color( 176, 218, 200 );
    private static final Color CENTER_FILL_COLOR = new Color( 220, 239, 239 );
    
    // properties of the fluid velocity vectors
    private static final int NUMBER_OF_VELOCITY_VECTORS = 10;
    private static final double VELOCITY_VECTOR_HEAD_HEIGHT = 20;
    private static final double VELOCITY_VECTOR_HEAD_WIDTH = 20;
    private static final double VELOCITY_VECTOR_TAIL_WIDTH = 10;
    private static final double VELOCITY_VECTOR_MAX_TAIL_LENGTH = 125;
    private static final Stroke VELOCITY_VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VELOCITY_VECTOR_STROKE_PAINT = Color.BLACK;
    private static final Paint VELOCITY_VECTOR_FILL_PAINT = EDGE_FILL_COLOR;
    private static final double VELOCITY_VECTOR_X_OFFSET = 30;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    private ModelViewTransform _modelViewTransform;
    private double _worldWidth;
    
    // parts of the microscope slide
    private PPath _topEdgeNode, _bottomEdgeNode, _centerNode;
    private PNode _velocityVectorsParentNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param fluid
     * @param modelViewTransform
     */
    public FluidNode( Fluid fluid, ModelViewTransform modelViewTransform ) {
        super();
         
        setPickable( false );
        setChildrenPickable( false );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _worldWidth = 1;
        
        // top edge of the miscroscope slide
        _topEdgeNode = new PPath();
        _topEdgeNode.setStroke( EDGE_STROKE );
        _topEdgeNode.setStrokePaint( EDGE_STROKE_COLOR );
        _topEdgeNode.setPaint( EDGE_FILL_COLOR );
        
        // bottom edge of the miscroscope slide
        _bottomEdgeNode = new PPath();
        _bottomEdgeNode.setStroke( EDGE_STROKE );
        _bottomEdgeNode.setStrokePaint( EDGE_STROKE_COLOR );
        _bottomEdgeNode.setPaint( EDGE_FILL_COLOR );
        
        // center portion of the microscope slide, where the bead moves
        _centerNode = new PPath();
        _centerNode.setStroke( null );
        _centerNode.setPaint( CENTER_FILL_COLOR );
        
        // fluid velocity vectors
        final double fluidHeight = _modelViewTransform.modelToView( _fluid.getHeight() );
        final double referenceMagnitude = _fluid.getSpeedRange().getMax();
        final double referenceLength = VELOCITY_VECTOR_MAX_TAIL_LENGTH;
        Vector2D vector = new Vector2D.Polar( referenceMagnitude, 0 );
        _velocityVectorsParentNode = new PComposite();
        for ( int i = 0; i < NUMBER_OF_VELOCITY_VECTORS; i++ ) {
            VelocityVectorNode vectorNode = new VelocityVectorNode( vector, referenceMagnitude, referenceLength );
            double x = VELOCITY_VECTOR_X_OFFSET;
            double y = ( i * fluidHeight / NUMBER_OF_VELOCITY_VECTORS ) + ( ( fluidHeight / NUMBER_OF_VELOCITY_VECTORS ) - vectorNode.getFullBounds().getHeight() );
            vectorNode.setOffset( x, y );
            _velocityVectorsParentNode.addChild( vectorNode );
        }
        
        addChild( _centerNode );
        addChild( _topEdgeNode );
        addChild( _bottomEdgeNode );
        addChild( _velocityVectorsParentNode );
        
        updateSlide();
        updateVelocityVectors();
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    /**
     * Gets the global full bounds of the center portion of
     * the glass microscope slide, where the fluid lives.
     * 
     * @return Rectangle2D
     */
    public Rectangle2D getCenterGlobalBounds() {
        return _centerNode.getGlobalFullBounds();
    }
    
    /**
     * Sets the size of the PhetPCanvas' world node.
     * This makes the microscope slide change it's width to fill the canvas,
     * giving the appearance of an infinitely wide fluid area.
     * 
     * @param worldSize
     */
    public void setWorldSize( Dimension2D worldSize ) {
        final double worldWidth = worldSize.getWidth();
        if ( worldWidth <= 0 ) {
            throw new IllegalArgumentException( "worldSize must have width > 0: " + worldWidth );
        }
        if ( worldWidth != _worldWidth ) {
            _worldWidth = worldWidth;
            updateSlide();
            updateVelocityVectors();
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_SPEED ) {
                updateVelocityVectors();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the microscope slide to match the model.
     */
    private void updateSlide() {
        
        // fluid flow must be left-to-right or right-to-left
        assert( _fluid.getOrientation() ==  Math.toRadians( 0 ) || _fluid.getOrientation() == Math.toRadians( 180 ) );
        
        final double height = _modelViewTransform.modelToView( _fluid.getHeight() );
        final double y = _modelViewTransform.modelToView( _fluid.getY() );
        
        // create each part with (0,0) at top right
        _topEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _worldWidth, EDGE_HEIGHT ) );
        _bottomEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _worldWidth, EDGE_HEIGHT ) );
        _centerNode.setPathTo( new Rectangle2D.Double( 0, 0, _worldWidth, height ) );
        
        // translate parts so that (0,0) of the slide is at left center
        _topEdgeNode.setOffset( 0, -( height / 2 ) - EDGE_HEIGHT );
        _centerNode.setOffset( 0, -( height / 2 ) );
        _bottomEdgeNode.setOffset( 0, +( height / 2 ) );
        _velocityVectorsParentNode.setOffset( 0, 
                _centerNode.getOffset().getY() + ( ( _centerNode.getFullBounds().getHeight() - _velocityVectorsParentNode.getFullBounds().getHeight() ) / 2 ) );
        
        setOffset( 0, y );
    }
    
    /*
     * Updates the fluid velocity vectors to match the model.
     */
    private void updateVelocityVectors() {
        
        final double speed = _fluid.getSpeed();
        final double orientation = _fluid.getOrientation();
        
        List childNodes = _velocityVectorsParentNode.getChildrenReference();
        Iterator i = childNodes.iterator();
        while ( i.hasNext() ) {
            Object nextChild = i.next();
            if ( nextChild instanceof VelocityVectorNode ) {
                VelocityVectorNode velocityVectorNode = (VelocityVectorNode) nextChild;
                velocityVectorNode.setVectorMagnitudeAngle( speed, orientation );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * VelocityVectorNode encapsulates the "look" of a fluid velocity vector.
     */
    private class VelocityVectorNode extends Vector2DNode {
        public VelocityVectorNode( Vector2D vector, double referenceMagnitude, double referenceLength ) {
            super( vector, referenceMagnitude, referenceLength );
            setHeadSize( VELOCITY_VECTOR_HEAD_WIDTH, VELOCITY_VECTOR_HEAD_HEIGHT );
            setTailWidth( VELOCITY_VECTOR_TAIL_WIDTH );
            setArrowFillPaint( VELOCITY_VECTOR_FILL_PAINT );
            setArrowStroke( VELOCITY_VECTOR_STROKE );
            setArrowStrokePaint( VELOCITY_VECTOR_STROKE_PAINT );
            setFractionalHeadHeight( 0.5 );
            setValueVisible( false );
        }
    }
}
