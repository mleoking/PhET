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
    
    private PNode _positiveNode, _negativeNode;
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
        
        _positiveNode = createPositiveNode( size, thickness );
        addChild( _positiveNode );
        
        _negativeNode = createNegativeNode( size, thickness );
        addChild( _negativeNode );
        
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
        
        // if the scale is zero, hide the charges so we don't attempt to apply a zero scale
        _positiveNode.setVisible( scale > 0 );
        _negativeNode.setVisible( scale > 0 );
        
        // position and scale the charges
        if ( scale > 0 ) {
            
            double x, y;
            
            // positive charge
            _positiveNode.setScale( scale );
            if ( electricFieldX > 0 ) {
                x = -_viewBeadRadius + _viewMargin;
            }
            else {
                x = _viewBeadRadius - _positiveNode.getFullBoundsReference().getWidth() - _viewMargin;
            }
            y = -_positiveNode.getFullBoundsReference().getHeight() / 2;
            _positiveNode.setOffset( x, y );
        
            // negative charge
            _negativeNode.setScale( scale );
            if ( electricFieldX > 0 ) {
                x = _viewBeadRadius - _negativeNode.getFullBoundsReference().getWidth() - _viewMargin;
            }
            else {
                x = -_viewBeadRadius + _viewMargin;
            }
            y = -_negativeNode.getFullBoundsReference().getHeight() / 2;
            _negativeNode.setOffset( x, y );
        }
    }
    
}
