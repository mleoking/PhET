/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * ChargeExcessNode displays the excess charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargeExcessNode extends AbstractChargeNode {
    
    private static final double MAX_SIZE = 70; // nm, size of biggest dimension
    private static final double MAX_THICKNESS = 10; // nm
    private static final double MARGIN = 10; // nm
    
    private PNode _positiveLeftNode, _positiveRightNode;
    private PNode _negativeLeftNode, _negativeRightNode;
    private AffineTransform _positiveLeftTransform, _positiveRightTransform;
    private AffineTransform _negativeLeftTransform, _negativeRightTransform;
    private double _viewBeadRadius;
    private double _viewMargin;
    private double _maxElectricFieldX;
    
    public ChargeExcessNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform ) {
        super( bead, laser, modelViewTransform );
    }
    
    protected void initialize() {
        
        ModelViewTransform modelViewTransform = getModelViewTransform();
        final double size = modelViewTransform.modelToView( MAX_SIZE );
        final double thickness = modelViewTransform.modelToView( MAX_THICKNESS );
        
        _positiveLeftNode = createPositiveNode( size, thickness );
        addChild( _positiveLeftNode );
        
        _positiveRightNode = createPositiveNode( size, thickness );
        addChild( _positiveRightNode );
        
        _negativeLeftNode = createNegativeNode( size, thickness );
        addChild( _negativeLeftNode );
        
        _negativeRightNode = createNegativeNode( size, thickness );
        addChild( _negativeRightNode );
        
        _positiveLeftTransform = new AffineTransform();
        _positiveRightTransform = new AffineTransform();
        _negativeLeftTransform = new AffineTransform();
        _negativeRightTransform = new AffineTransform();
        
        Bead bead = getBead();
        _viewBeadRadius = modelViewTransform.modelToView( bead.getDiameter() / 2 );
        _viewMargin = modelViewTransform.modelToView( MARGIN );
        
        Laser laser = getLaser();
        _maxElectricFieldX = laser.getMaxElectricFieldX();
        
        updateCharge();
    }
    
    protected void updateCharge() {
        
        Bead bead = getBead();
        final double electricFieldX = bead.getElectricFieldX();
        final double scale = Math.abs( electricFieldX / _maxElectricFieldX );
        
        _positiveLeftNode.setVisible( electricFieldX > 0 );
        _positiveRightNode.setVisible( electricFieldX < 0 );
        _negativeLeftNode.setVisible( _positiveRightNode.getVisible() );
        _negativeRightNode.setVisible( _positiveLeftNode.getVisible() );
        
        double x, y;
        
        if ( _positiveLeftNode.getVisible() ) {
            x = -_viewBeadRadius + _viewMargin;
            y = -_positiveLeftNode.getFullBoundsReference().getHeight() / 2;
            _positiveLeftTransform.setToTranslation( x, y );
            _positiveLeftTransform.scale( scale, scale );
            _positiveLeftNode.setTransform( _positiveLeftTransform );
            
        }
        
        if ( _positiveRightNode.getVisible() ) {
            x = _viewBeadRadius - _positiveRightNode.getFullBoundsReference().getWidth() - _viewMargin;
            y = -_positiveRightNode.getFullBoundsReference().getHeight() / 2;
            _positiveRightTransform.setToTranslation( x, y );
            _positiveRightTransform.scale( scale, scale );
            _positiveRightNode.setTransform( _positiveRightTransform );
        }
        
        if ( _negativeLeftNode.getVisible() ) {
            x = -_viewBeadRadius + _viewMargin;
            y = -_negativeLeftNode.getFullBoundsReference().getHeight() / 2;
            _negativeLeftTransform.setToTranslation( x, y );
            _negativeLeftTransform.scale( scale, scale );
            _negativeLeftNode.setTransform( _negativeLeftTransform );
        }
        
        if ( _negativeRightNode.getVisible() ) {
            x = _viewBeadRadius - _negativeRightNode.getFullBoundsReference().getWidth() - _viewMargin;
            y = -_negativeRightNode.getFullBoundsReference().getHeight() / 2;
            _negativeRightTransform.setToTranslation( x, y );
            _negativeRightTransform.scale( scale, scale );
            _negativeRightNode.setTransform( _negativeRightTransform );
        }
    }
    
}
