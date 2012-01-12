// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.MicroscopeSlide;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * MicroscopeSlideNode is the visual representation of a glass microscope slide.
 * The slide that has a fixed height and infinite width.
 * The slide has top and bottom edges that have a fixed height.
 * The slide contains either a fluid or a vacuum (the absence of fluid).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MicroscopeSlideNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // properties of the "glass slide" that the fluid sits on
    private static final Stroke EDGE_STROKE = new BasicStroke( 1f );
    
    // colors used to represent fluid
    private static final Color FLUID_EDGE_STROKE_COLOR = Color.BLACK;
    private static final Color FLUID_EDGE_FILL_COLOR = new Color( 176, 218, 200 );
    private static final Color FLUID_CENTER_FILL_COLOR = new Color( 220, 239, 239 );
    
    // colors used to represent vacuum
    private static final Color VACUUM_EDGE_STROKE_COLOR = Color.BLACK;
    private static final Color VACUUM_EDGE_FILL_COLOR = Color.DARK_GRAY;
    private static final Color VACUUM_CENTER_FILL_COLOR = Color.BLACK;
    
    // properties of the fluid velocity vectors
    private static final int NUMBER_OF_VELOCITY_VECTORS = 10;
    private static final double VELOCITY_VECTOR_HEAD_HEIGHT = 20;
    private static final double VELOCITY_VECTOR_HEAD_WIDTH = 20;
    private static final double VELOCITY_VECTOR_TAIL_WIDTH = 10;
    private static final double VELOCITY_VECTOR_MAX_TAIL_LENGTH = 125;
    private static final Stroke VELOCITY_VECTOR_STROKE = new BasicStroke( 1f );
    private static final Paint VELOCITY_VECTOR_STROKE_PAINT = Color.BLACK;
    private static final Paint VELOCITY_VECTOR_FILL_PAINT = FLUID_EDGE_FILL_COLOR;
    private static final double VELOCITY_VECTOR_X_OFFSET = 30;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final MicroscopeSlide _microscopeSlide;
    private final Fluid _fluid;
    private final OTModelViewTransform _modelViewTransform;
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
     * @param microscopeSlide
     * @param modelViewTransform
     */
    public MicroscopeSlideNode( MicroscopeSlide microscopeSlide, Fluid fluid, OTModelViewTransform modelViewTransform, double vectorReferenceMagnitude ) {
        super();
         
        setPickable( false );
        setChildrenPickable( false );
        
        _microscopeSlide = microscopeSlide;
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        _worldWidth = 1;
        
        // top edge of the miscroscope slide
        _topEdgeNode = new PPath();
        _topEdgeNode.setStroke( EDGE_STROKE );
        
        // bottom edge of the miscroscope slide
        _bottomEdgeNode = new PPath();
        _bottomEdgeNode.setStroke( EDGE_STROKE );
        
        // center portion of the microscope slide, where the bead moves
        _centerNode = new PPath();
        _centerNode.setStroke( null );
        
        // fluid velocity vectors
        final double microscopeSlideCenterHeight = _modelViewTransform.modelToView( _microscopeSlide.getCenterHeight() );
        final double referenceMagnitude = vectorReferenceMagnitude;
        final double referenceLength = VELOCITY_VECTOR_MAX_TAIL_LENGTH;
        _velocityVectorsParentNode = new PComposite();
        for ( int i = 0; i < NUMBER_OF_VELOCITY_VECTORS; i++ ) {
            // start with max vector size, so that offset can be set properly
            VelocityVectorNode vectorNode = new VelocityVectorNode( referenceMagnitude, 0, referenceMagnitude, referenceLength );
            double x = VELOCITY_VECTOR_X_OFFSET;
            double y = ( i * microscopeSlideCenterHeight / NUMBER_OF_VELOCITY_VECTORS ) + ( VELOCITY_VECTOR_HEAD_HEIGHT / 2 );
            vectorNode.setOffset( x, y );
            _velocityVectorsParentNode.addChild( vectorNode );
        }
        
        addChild( _centerNode );
        addChild( _topEdgeNode );
        addChild( _bottomEdgeNode );
        addChild( _velocityVectorsParentNode );
        
        updateSlideGeometry();
        updateSlideColors();
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
            updateSlideGeometry();
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
            else if ( arg == Fluid.PROPERTY_ENABLED ) {
                updateSlideColors();
                updateVelocityVectors();
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the microscope slide's geometry to match the model.
     */
    private void updateSlideGeometry() {
        
        final double centerHeight = _modelViewTransform.modelToView( _microscopeSlide.getCenterHeight() );
        final double edgeHeight = _modelViewTransform.modelToView( _microscopeSlide.getEdgeHeight() );
        final double y = _modelViewTransform.modelToView( _microscopeSlide.getY() );
        
        // create each part with (0,0) at top right
        _topEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _worldWidth, edgeHeight ) );
        _bottomEdgeNode.setPathTo( new Rectangle2D.Double( 0, 0, _worldWidth, edgeHeight ) );
        _centerNode.setPathTo( new Rectangle2D.Double( 0, 0, _worldWidth, centerHeight ) );
        
        // translate parts so that (0,0) of the slide is at left center
        _topEdgeNode.setOffset( 0, -( centerHeight / 2 ) - edgeHeight );
        _centerNode.setOffset( 0, -( centerHeight / 2 ) );
        _bottomEdgeNode.setOffset( 0, +( centerHeight / 2 ) );
        _velocityVectorsParentNode.setOffset( 0, 
                _centerNode.getOffset().getY() + ( ( _centerNode.getFullBoundsReference().getHeight() - _velocityVectorsParentNode.getFullBoundsReference().getHeight() ) / 2 ) );
        
        setOffset( 0, y );
    }
    
    /*
     * Updates the colors of the slide to indicate whether the slide
     * contains a fluid or vacuum.
     */
    private void updateSlideColors() {
        if ( _fluid.isEnabled() ) {
            // fluid
            _topEdgeNode.setStrokePaint( FLUID_EDGE_STROKE_COLOR );
            _topEdgeNode.setPaint( FLUID_EDGE_FILL_COLOR );
            _topEdgeNode.setPaint( FLUID_EDGE_FILL_COLOR );
            
            _bottomEdgeNode.setStrokePaint( FLUID_EDGE_STROKE_COLOR );
            _bottomEdgeNode.setPaint( FLUID_EDGE_FILL_COLOR );
            _bottomEdgeNode.setPaint( FLUID_EDGE_FILL_COLOR );
            
            _centerNode.setPaint( FLUID_CENTER_FILL_COLOR );

        }
        else {
            // vacuum
            _topEdgeNode.setStrokePaint( VACUUM_EDGE_STROKE_COLOR );
            _topEdgeNode.setPaint( VACUUM_EDGE_STROKE_COLOR );
            _topEdgeNode.setPaint( VACUUM_EDGE_FILL_COLOR );
            
            _bottomEdgeNode.setStrokePaint( VACUUM_EDGE_STROKE_COLOR );
            _bottomEdgeNode.setPaint( VACUUM_EDGE_STROKE_COLOR );
            _bottomEdgeNode.setPaint( VACUUM_EDGE_FILL_COLOR );
            
            _centerNode.setPaint( VACUUM_CENTER_FILL_COLOR );
        }
    }
    
    /*
     * Updates the fluid velocity vectors to match the model.
     */
    private void updateVelocityVectors() {
        
        final boolean fluidEnabled = _fluid.isEnabled();
        _velocityVectorsParentNode.setVisible( fluidEnabled );
        if ( fluidEnabled ) {

            final double speed = _fluid.getSpeed();
            final double direction = _fluid.getDirection();

            List childNodes = _velocityVectorsParentNode.getChildrenReference();
            Iterator i = childNodes.iterator();
            while ( i.hasNext() ) {
                Object nextChild = i.next();
                if ( nextChild instanceof VelocityVectorNode ) {
                    VelocityVectorNode velocityVectorNode = (VelocityVectorNode) nextChild;
                    velocityVectorNode.setMagnitudeAngle( speed, direction );
                }
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
        public VelocityVectorNode( double x, double y, double referenceMagnitude, double referenceLength ) {
            super( x, y, referenceMagnitude, referenceLength );
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
