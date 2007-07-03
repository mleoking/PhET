/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.*;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.colorado.phet.opticaltweezers.util.ColorUtils;
import edu.colorado.phet.opticaltweezers.util.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 * LaserElectricFieldNode is the visual representation of the laser's electric field.
 * A collection of vectors are shown distibuted across the laser beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserElectricFieldNode extends PhetPNode implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String ELECTRIC_FIELD_UNITS = "V/nm";
    
    // vector color and stroke
    private static final Color DEFAULT_VECTOR_COLOR = new Color( 230, 57, 5 ); // dark red
    private static final Stroke VECTOR_STROKE = new BasicStroke( 2f );
    
    // layout of vectors
    private static final double X_MARGIN = 30; // nm
    private static final double Y_MARGIN = 30; // nm
    private static final double X_SPACING = 10; // nm
    private static final int NUMBER_OF_VECTORS_AT_WAIST = 5; // must be an odd number for symmetry!
    private static final int NUMBER_OF_VECTOR_ROWS = 11; // must be an odd number for symmetry!
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Laser _laser;
    private final ModelViewTransform _modelViewTransform;
    
    private final PNode _vectorNodesParent;
    private final List _vectorNodes; // array of ElectricFieldVectorNode
    private Color _vectorColor;
    private boolean _valuesVisible;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor
     * 
     * @param laser
     * @param modelViewTransform
     */
    public LaserElectricFieldNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _vectorNodesParent = new PComposite();
        addChild( _vectorNodesParent );
        
        _vectorNodes = new ArrayList();
        _vectorColor = DEFAULT_VECTOR_COLOR;
        _valuesVisible = false;
        
        initVectors();
    }

    /**
     * Calls this method before releasing all references to this object.
     */
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Vector initialization
    //----------------------------------------------------------------------------
    
    /*
     * Chooses sample points that are evenly distributed across the laser's beam,
     * and initializes the vector nodes that represent the electric field at those
     * sample points.
     */
    private void initVectors() {
        
        assert( NUMBER_OF_VECTORS_AT_WAIST % 2 == 1 );
        assert( NUMBER_OF_VECTOR_ROWS % 2 == 1 );
        
        removeAllElectricFieldVectorNodes();
        
        double xMax = _laser.getRadius( 0 ) - X_MARGIN;
        final double yMax = _laser.getDistanceFromObjectiveToWaist() - Y_MARGIN;
        
        final double dx = xMax / ( NUMBER_OF_VECTORS_AT_WAIST / 2 );
        final double dy = yMax / ( NUMBER_OF_VECTOR_ROWS / 2 );
        final double referenceMagnitude = _laser.getMaxElectricField().getMagnitude();
        final double referenceLength = _modelViewTransform.modelToView( dx - X_SPACING );
        
        double xOffsetFromLaser = 0;
        double yOffsetFromLaser = 0;
        
        while ( yOffsetFromLaser <= yMax ) {
            
            xOffsetFromLaser = 0;
            xMax = _laser.getRadius( yOffsetFromLaser ) - X_MARGIN;
            
            while ( xOffsetFromLaser <= xMax ) {

                if ( xOffsetFromLaser == 0 && yOffsetFromLaser == 0 ) {
                    addElectricFieldVectorNode( xOffsetFromLaser, yOffsetFromLaser, referenceMagnitude, referenceLength );
                }
                else if ( xOffsetFromLaser == 0 ) {
                    addElectricFieldVectorNode( xOffsetFromLaser, yOffsetFromLaser, referenceMagnitude, referenceLength );
                    addElectricFieldVectorNode( xOffsetFromLaser, -yOffsetFromLaser, referenceMagnitude, referenceLength );
                }
                else {
                    addElectricFieldVectorNode( xOffsetFromLaser, yOffsetFromLaser, referenceMagnitude, referenceLength );
                    addElectricFieldVectorNode( xOffsetFromLaser, -yOffsetFromLaser, referenceMagnitude, referenceLength );
                    addElectricFieldVectorNode( -xOffsetFromLaser, yOffsetFromLaser, referenceMagnitude, referenceLength );
                    addElectricFieldVectorNode( -xOffsetFromLaser, -yOffsetFromLaser, referenceMagnitude, referenceLength );
                }

                xOffsetFromLaser += dx;
            }
            
            yOffsetFromLaser += dy;
        }
        
        updateVectors();
    }
    
    /*
     * Adds a vector node to represent the eletric field at a specific distance from the laser's origin.
     * 
     * @param xOffsetFromLaser
     * @param yOffsetFromLaser
     * @param referenceMagnitude
     * @param referenceLength
     */
    private void addElectricFieldVectorNode( double xOffsetFromLaser, double yOffsetFromLaser, double referenceMagnitude, double referenceLength ) {
        ElectricFieldVectorNode vectorNode = new ElectricFieldVectorNode( -xOffsetFromLaser, yOffsetFromLaser, referenceMagnitude, referenceLength );
        _vectorNodes.add( vectorNode );
        _vectorNodesParent.addChild( vectorNode );
        double xOffsetView = _modelViewTransform.modelToView( vectorNode.getXOffset() + ( _laser.getDiameterAtObjective() / 2 ) + xOffsetFromLaser );
        double yOffsetView = _modelViewTransform.modelToView( vectorNode.getYOffset() + _laser.getY() + yOffsetFromLaser );
        vectorNode.setOffset( xOffsetView, yOffsetView );
        
        // Show value for vector at center of laser only !!
        if ( xOffsetFromLaser == 0 && yOffsetFromLaser == 0 ) {
            vectorNode.setValueVisible( _valuesVisible );
        }
    }
    
    /*
     * Removes all vector nodes.
     */
    private void removeAllElectricFieldVectorNodes() {
        _vectorNodes.clear();
        _vectorNodesParent.removeAllChildren();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Controls visibility of values on the electric field vectors.
     * 
     * @param visible
     */
    public void setValuesVisible( boolean visible ) {
        if ( visible != _valuesVisible ) {
            _valuesVisible = visible;
            initVectors();
        }
    }
    
    /**
     * Sets the color used for the vectors.
     * 
     * @param vectorColor
     */
    public void setVectorColor( Color vectorColor ) {
        _vectorColor = vectorColor;
        updateVectors();
    }
    
    /**
     * Gets the color used for the vectors.
     * 
     * @return Color
     */
    public Color getVectorColor() {
        return _vectorColor;
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    /**
     * Observe the laser while this node is visible.
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( visible != getVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                _laser.addObserver( this );
                updateVectors();
            }
            else {
                _laser.deleteObserver( this );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_ELECTRIC_FIELD || arg == Laser.PROPERTY_POWER || arg == Laser.PROPERTY_RUNNING ) {
                updateVectors();
            }
            else if ( arg == Laser.PROPERTY_ELECTRIC_FIELD_SCALE ) {
                initVectors();
            }
        }
    }
    
    /*
     * Updates each vector to correspond to the electric field strength
     * at the vector's location in the laser beam.
     */
    private void updateVectors() {
        final Vector2D maxElectricField = _laser.getMaxElectricField();
        Iterator i = _vectorNodes.iterator();
        while ( i.hasNext() ) {
            
            ElectricFieldVectorNode vectorNode = (ElectricFieldVectorNode) i.next();

            // magnitude
            Point2D offsetFromLaser = vectorNode.getOffsetFromLaser();
            Vector2D electricField = _laser.getElectricField( offsetFromLaser );
            vectorNode.setXY( electricField.getX(), electricField.getY() );

            // color, alpha component based on field strength
            int alpha = (int)( 255 * electricField.getMagnitude() / maxElectricField.getMagnitude() );
            Color c = ColorUtils.addAlpha( _vectorColor, alpha );
            vectorNode.setArrowStrokePaint( c );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Stores the sample point that the vector node represents, draws the vector.
     */
    private static class ElectricFieldVectorNode extends Vector2DNode {
        
        private final Point2D _offsetFromLaser; // offset from laser origin, nm (model coordinates)
        
        public ElectricFieldVectorNode( double xOffsetFromLaser, double yOffsetFromLaser, double referenceMagnitude, double referenceLength ) {
            super( 0, 0, referenceMagnitude, referenceLength );
            _offsetFromLaser = new Point2D.Double( xOffsetFromLaser, yOffsetFromLaser );
            setArrowStroke( VECTOR_STROKE );
            setArrowFillPaint( null );
            setUnits( ELECTRIC_FIELD_UNITS );
        }
        
        public Point2D getOffsetFromLaser() {
            return _offsetFromLaser;
        }
    }
}
