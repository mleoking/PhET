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
    private static final Color DEFAULT_VECTOR_COLOR = Color.GREEN.darker();
    private static final Stroke VECTOR_STROKE = new BasicStroke( 2f );
    
    // layout of vectors
    private static final double X_MARGIN = 30; // nm
    private static final double Y_MARGIN = 30; // nm
    private static final double X_SPACING = 10; // nm
    private static final int NUMBER_OF_VECTORS_AT_WAIST = 5; // must be an odd number for symmetry!
    private static final int NUMBER_OF_VECTOR_ROWS = 9; // must be an odd number for symmetry!
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Laser _laser;
    private final ModelViewTransform _modelViewTransform;
    
    private final List _vectorNodes; // array of ElectricFieldVectorNode
    private Color _vectorColor;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LaserElectricFieldNode( Laser laser, ModelViewTransform modelViewTransform ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        _vectorNodes = new ArrayList();
        _vectorColor = DEFAULT_VECTOR_COLOR;
        
        initVectors();
        updateVectors();
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    private void initVectors() {
        
        assert( NUMBER_OF_VECTORS_AT_WAIST % 2 == 1 );
        assert( NUMBER_OF_VECTOR_ROWS % 2 == 1 );
        
        final double referenceMagnitude = _laser.getMaxElectricField().getMagnitude();
        final double xMagnitudeDefault = 0;
        final double yMagnitudeDefault = 0;

        final double radiusAtWaist = _laser.getRadius( 0 );
        final double deltaX = ( radiusAtWaist - X_MARGIN ) / ( NUMBER_OF_VECTORS_AT_WAIST / 2 );
        final double referenceLength = _modelViewTransform.modelToView( deltaX - X_SPACING );
        System.out.println( "initVectors: radiusAtWaist=" + radiusAtWaist + " referenceMagnitude=" + referenceMagnitude + " referenceLength=" + referenceLength );//XXX
        
        ElectricFieldVectorNode vectorNode = null;
        double xOffsetFromLaser = 0;
        double yOffsetFromLaser = 0;
        double xOffsetView = 0;
        double yOffsetView = 0;
        
        // row of sample points at waist
        for ( int i = 0; i <= ( NUMBER_OF_VECTORS_AT_WAIST / 2 ); i++ ) {
            
            xOffsetFromLaser = i * deltaX;
            yOffsetFromLaser = 0;
            
            // vector to the right of center
            vectorNode = new ElectricFieldVectorNode( xOffsetFromLaser, yOffsetFromLaser,
                    xMagnitudeDefault, yMagnitudeDefault, referenceMagnitude, referenceLength );
            _vectorNodes.add( vectorNode );
            addChild( vectorNode );
            xOffsetView = _modelViewTransform.modelToView( vectorNode.getXOffset() + ( _laser.getDiameterAtObjective() / 2 ) + xOffsetFromLaser );
            yOffsetView = _modelViewTransform.modelToView( vectorNode.getYOffset() + _laser.getY() );
            vectorNode.setOffset( xOffsetView, yOffsetView );
            
            // vector to the left of center
            if ( xOffsetFromLaser > 0 ) {
                vectorNode = new ElectricFieldVectorNode( -xOffsetFromLaser, yOffsetFromLaser,
                        xMagnitudeDefault, yMagnitudeDefault, referenceMagnitude, referenceLength );
                _vectorNodes.add( vectorNode );
                addChild( vectorNode );
                xOffsetView = _modelViewTransform.modelToView( vectorNode.getXOffset() + ( _laser.getDiameterAtObjective() / 2 ) - xOffsetFromLaser );
                yOffsetView = _modelViewTransform.modelToView( vectorNode.getYOffset() + _laser.getY() );
                vectorNode.setOffset( xOffsetView, yOffsetView ); 
            }
        }
        
        // rows above and below waist
        {
            //XXX implement
        }
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
        Iterator i = _vectorNodes.iterator();
        while ( i.hasNext() ) {
            ElectricFieldVectorNode vectorNode = (ElectricFieldVectorNode) i.next();
            vectorNode.setValueVisible( visible );
        }
    }
    
    public void setVectorColor( Color vectorColor ) {
        _vectorColor = vectorColor;
        updateVectors();
    }
    
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
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_ELECTRIC_FIELD ) {
                updateVectors();
            }
        }
    }
    
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
    
    private static class ElectricFieldVectorNode extends Vector2DNode {
        
        private final Point2D _offsetFromLaser; // offset from laser origin, nm
        
        public ElectricFieldVectorNode( double xOffsetFromLaser, double yOffsetFromLaser, double xMagnitude, double yMagnitude, double referenceMagnitude, double referenceLength ) {
            super( xMagnitude, yMagnitude, referenceMagnitude, referenceLength );
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
