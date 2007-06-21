/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Point2D;
import java.util.*;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
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
    
    private static final double REFERENCE_LENGTH = 100; //XXX this should be based on # of vectors across laser waist
    
    // layout of vectors
    private static final double X_MARGIN = 20; // nm
    private static final double Y_MARGIN = 20; // nm
    private static final double X_SPACING = 10; // nm
    private static final double Y_SPACING = 10; // nm
    private static final int NUMBER_OF_VECTORS_AT_WAIST = 5;
    private static final int NUMBER_OF_VECTOR_ROWS = 9;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Laser _laser;
    private final ModelViewTransform _modelViewTransform;
    
    private final List _vectorNodes; // array of ElectricFieldVectorNode
    
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
        
        initVectors();
        updateVectors();
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    private void initVectors() {
        
        final Vector2D referenceMagnitude = _laser.getMaxElectricField();
        final double xMagnitudeDefault = 0;
        final double yMagnitudeDefault = 0;

        Point2D offsetFromLaser = new Point2D.Double( 0, 0 );
        
        // create vectors at sample points distributed across laser shape
        {
            ElectricFieldVectorNode vectorNode = new ElectricFieldVectorNode( offsetFromLaser, 
                    xMagnitudeDefault, yMagnitudeDefault, referenceMagnitude.getMagnitude(), REFERENCE_LENGTH );
            _vectorNodes.add( vectorNode );
            addChild( vectorNode );

            final double xOffsetView = _modelViewTransform.modelToView( vectorNode.getXOffset() + _laser.getDiameterAtObjective() / 2 );
            final double yOffsetView = _modelViewTransform.modelToView( vectorNode.getYOffset() + _laser.getY() );
            vectorNode.setOffset( xOffsetView, yOffsetView );
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
        Iterator i = _vectorNodes.iterator();
        while ( i.hasNext() ) {
            ElectricFieldVectorNode vectorNode = (ElectricFieldVectorNode) i.next();
            Point2D offsetFromLaser = vectorNode.getOffsetFromLaser();
            Vector2D electricField = _laser.getElectricField( offsetFromLaser );
            vectorNode.setXY( electricField.getX(), electricField.getY() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    private static class ElectricFieldVectorNode extends Vector2DNode {
        
        private final Point2D _offsetFromLaser; // offset from laser origin, nm
        
        public ElectricFieldVectorNode( Point2D offsetFromLaser, double xMagnitude, double yMagnitude, double referenceMagnitude, double referenceLength ) {
            super( xMagnitude, yMagnitude, referenceMagnitude, referenceLength );
            _offsetFromLaser = new Point2D.Double( offsetFromLaser.getX(), offsetFromLaser.getY() );
        }
        
        public Point2D getOffsetFromLaser() {
            return _offsetFromLaser;
        }
    }
}
