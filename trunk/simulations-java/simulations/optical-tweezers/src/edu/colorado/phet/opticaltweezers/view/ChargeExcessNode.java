// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * ChargeExcessNode displays the excess charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargeExcessNode extends AbstractChargeNode {
    
    private static final double MAX_CHARGE_SIZE = 80; // nm, size of biggest dimension for the charges
    private static final double MAX_CHARGE_STROKE_WIDTH = 20; // nm, width of the stroke used to draw the charges
    private static final double MARGIN = 10; // nm, how close the charges are to the edge of the bead
    
    private PNode _positiveNode, _negativeNode;
    private double _viewBeadRadius;
    private double _viewMargin;
    
    /**
     * Constructor.
     * 
     * @param bead
     * @param laser
     * @param modelViewTransform
     */
    public ChargeExcessNode( Bead bead, Laser laser, OTModelViewTransform modelViewTransform ) {
        super( bead, laser, modelViewTransform );
    }
    
    /*
     * Initializes the nodes and other member data.
     * The positive and negative charge nodes are created at a size that 
     * corresponds to the laser's maximum electric field.
     */
    protected void initialize() {
        
        OTModelViewTransform modelViewTransform = getModelViewTransform();
        final double size = modelViewTransform.modelToView( MAX_CHARGE_SIZE );
        final double strokeWidth = modelViewTransform.modelToView( MAX_CHARGE_STROKE_WIDTH );
        
        _positiveNode = createPositiveNode( size, strokeWidth );
        addChild( _positiveNode );
        
        _negativeNode = createNegativeNode( size, strokeWidth );
        addChild( _negativeNode );
        
        Bead bead = getBead();
        _viewBeadRadius = modelViewTransform.modelToView( bead.getDiameter() / 2 );
        _viewMargin = modelViewTransform.modelToView( MARGIN );
        
        updateCharges();
    }
    
    /*
     * Scales the positive and negative charge nodes to 
     * reflect the magnitude of the electric field's x-component.
     */
    protected void updateCharges() {
        
        Bead bead = getBead();
        final double electricFieldX = bead.getElectricFieldX();
        final double scale = getChargeScale( electricFieldX );
        
        // if the scale is zero, hide the charges so we don't attempt to apply a zero scale
        _positiveNode.setVisible( scale > 0 );
        _negativeNode.setVisible( scale > 0 );
        
        // position and scale the charges
        if ( scale > 0 ) {
            
            double x, y;
            PBounds positiveBounds = _positiveNode.getFullBoundsReference();
            PBounds negativeBounds = _negativeNode.getFullBoundsReference();
            
            // positive charge
            _positiveNode.setScale( scale );
            if ( electricFieldX > 0 ) {
                x = _viewBeadRadius - positiveBounds.getWidth() - _viewMargin;
            }
            else {
                x = -_viewBeadRadius + _viewMargin;
            }
            y = -positiveBounds.getHeight() / 2;
            _positiveNode.setOffset( x, y );
        
            // negative charge
            _negativeNode.setScale( scale );
            if ( electricFieldX > 0 ) {
                x = -_viewBeadRadius + _viewMargin;
            }
            else {
                x = _viewBeadRadius - negativeBounds.getWidth() - _viewMargin;
            }
            y = -negativeBounds.getHeight() / 2;
            _negativeNode.setOffset( x, y );
        }
    }
    
}
