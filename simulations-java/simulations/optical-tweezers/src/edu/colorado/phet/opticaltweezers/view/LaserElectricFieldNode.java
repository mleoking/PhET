/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private Point2D[] _samplePoints; // sample points, relative to laser origin
    private Vector2DNode[] _vectorNodes; // vector display for each element in _samplePoints
    
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
        
        _samplePoints = new Point2D.Double[ 1 ];
        _samplePoints[0] = new Point2D.Double( 0, 0 );
        
        final Vector2D maxElectricField = laser.getMaxElectricField();
        System.out.println( "Emax.x=" + maxElectricField.getX() );//XXX
        
        _vectorNodes = new Vector2DNode[ _samplePoints.length ];
        for ( int i = 0; i < _vectorNodes.length; i++ ) {
            Vector2DNode vectorNode = new Vector2DNode( 0, 0, maxElectricField.getMagnitude(), REFERENCE_LENGTH );
            _vectorNodes[i] = vectorNode;
            Point2D samplePoint = _samplePoints[i];
            addChild( vectorNode );
            final double x = _modelViewTransform.modelToView( samplePoint.getX() + laser.getDiameterAtObjective() / 2 );
            final double y = _modelViewTransform.modelToView( samplePoint.getY() + laser.getY() );
            vectorNode.setOffset( x, y );
        }
        
        updateVectors();
    }

    public void cleanup() {
        _laser.deleteObserver( this );
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
        
        for ( int i = 0; i < _samplePoints.length; i++ ) {
            Point2D samplePoint = _samplePoints[i];
            Vector2D electricField = _laser.getElectricField( samplePoint.getX(), samplePoint.getY() );
            Vector2DNode vectorNode = _vectorNodes[i];
            vectorNode.setXY( electricField.getX(), electricField.getY() );
        }
    }
}
