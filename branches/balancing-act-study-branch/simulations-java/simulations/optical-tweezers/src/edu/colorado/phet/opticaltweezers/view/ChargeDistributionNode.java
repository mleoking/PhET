// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.view;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.OTModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * ChargeDistributionNode displays the distibution of charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargeDistributionNode extends AbstractChargeNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_CHARGE_MOTION_SCALE = "chargeMotionScale";
    
    private static final int GRID_SIZE = 5; // charges will be created on a GRID_SIZE*GRID_SIZE rectangular grid
    
    private static final double CHARGE_SIZE = 20; // nm
    private static final double CHARGE_STROKE_WIDTH = 5; // nm, width of the stroke used to draw the charges
    private static final double MARGIN = 5; // nm, how close the charges are to the edge of the bead at the bead's waist 
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DoubleRange _chargeMotionScaleRange;
    private double _chargeMotionScale;
    private ArrayList _positiveNodeList, _negativeNodeList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param bead
     * @param laser
     * @param modelViewTransform
     * @param chargeMotionScaleRange
     */
    public ChargeDistributionNode( Bead bead, Laser laser, OTModelViewTransform modelViewTransform, DoubleRange chargeMotionScaleRange ) {
        super( bead, laser, modelViewTransform );
        assert( GRID_SIZE > 1 );
        _chargeMotionScaleRange = chargeMotionScaleRange;
        _chargeMotionScale = _chargeMotionScaleRange.getDefault();
    }
    
    //----------------------------------------------------------------------------
    // AbstractChargeNode implementation
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the nodes and other member data.
     */
    protected void initialize() {
        
        Bead bead = getBead();
        OTModelViewTransform modelViewTransform = getModelViewTransform();
        final double beadDiameter = modelViewTransform.modelToView( bead.getDiameter() );
        final double chargeSize = modelViewTransform.modelToView( CHARGE_SIZE );
        final double strokeWidth = modelViewTransform.modelToView( CHARGE_STROKE_WIDTH );
        
        // Clip the charges to the shape of the bead
        Ellipse2D clipShape = new Ellipse2D.Double( -beadDiameter/2, -beadDiameter/2, beadDiameter, beadDiameter );
        PClip clipNode = new PClip();
        addChild( clipNode );
        clipNode.setPathTo( clipShape );
        clipNode.setStroke( null );
        
        _positiveNodeList = new ArrayList();
        _negativeNodeList = new ArrayList();
        
        final int numberOfCharges = GRID_SIZE * GRID_SIZE;
        for ( int i = 0; i < numberOfCharges; i++ ) {
            
            PNode positiveChargeNode = createPositiveNode( chargeSize, strokeWidth );
            _positiveNodeList.add( positiveChargeNode );
            clipNode.addChild( positiveChargeNode );

            PNode negativeChargeNode = createNegativeNode( chargeSize, strokeWidth );
            _negativeNodeList.add( negativeChargeNode );
            clipNode.addChild( negativeChargeNode );
        }
        
        layoutPositiveCharges(); // positive charges are stationary, do layout once
        
        updateCharges();
    }
    
    /*
     * Updates negative charges to reflect the e-field at the bead's position.
     * Positive charges remain stationary.
     */
    protected void updateCharges() {
        layoutNegativeCharges();
    }
    
    //----------------------------------------------------------------------------
    // Developer controls
    //----------------------------------------------------------------------------
    
    public void setChargeMotionScale( double motionScale ) {
        if ( !_chargeMotionScaleRange.contains( motionScale ) ) {
            throw new IllegalArgumentException( "motionScale out of range: " + motionScale );
        }
        if ( motionScale != _chargeMotionScale ) {
            _chargeMotionScale = motionScale;
        }
    }
    
    public double getChargeMotionScale() {
        return _chargeMotionScale;
    }
    
    public DoubleRange getChargeMotionScaleRange() {
        return _chargeMotionScaleRange;
    }
    
    //----------------------------------------------------------------------------
    // Charge layout
    //----------------------------------------------------------------------------
    
    /*
     * Positive charges remain stationary.
     * Charges are arranged on a rectangular grid.
     */
    private void layoutPositiveCharges() {
        
        Bead bead = getBead();
        OTModelViewTransform modelViewTransform = getModelViewTransform();
        final double beadDiameter = modelViewTransform.modelToView( bead.getDiameter() );
        final double margin = modelViewTransform.modelToView( MARGIN );
        final double chargeSize = modelViewTransform.modelToView( CHARGE_SIZE );
        
        final double numberOfRows = GRID_SIZE;
        final double numberOfColumns = GRID_SIZE;
        final double xSpacing = ( beadDiameter - chargeSize - ( 2 * margin ) ) / ( GRID_SIZE - 1 );
        final double ySpacing = xSpacing;
        int nodeIndex = 0;
        for ( int row = 0; row < numberOfRows; row++ ) {
            double yOffset = -( beadDiameter / 2 ) + margin + ( row * ySpacing );
            for ( int column = 0; column < numberOfColumns; column++ ) {
                
                // Positive charges remain stationary
                PNode positiveNode = (PNode) _positiveNodeList.get( nodeIndex );
                double xOffset = -( beadDiameter / 2 ) + margin + ( column * xSpacing );
                positiveNode.setOffset( xOffset, yOffset );
                
                nodeIndex++;
            }
        }
    }
    
    /*
     * Negative charges are distributed based on the e-field strength at the bead's position.
     * Negative charges are pulled to the edge of the bead that is opposite the direction
     * of the electric field vector.
     * Charges are arranged on a rectangular grid.
     */
    private void layoutNegativeCharges() {
        
        Bead bead = getBead();
        OTModelViewTransform modelViewTransform = getModelViewTransform();
        
        final double electricFieldX = bead.getElectricFieldX();
        final double scale = getChargeScale( electricFieldX ) * _chargeMotionScale;
        final double beadDiameter = modelViewTransform.modelToView( bead.getDiameter() );
        final double margin = modelViewTransform.modelToView( MARGIN );
        final double chargeSize = modelViewTransform.modelToView( CHARGE_SIZE );
        
        final double numberOfRows = GRID_SIZE;
        final double numberOfColumns = GRID_SIZE;
        final double xSpacing = ( beadDiameter - chargeSize - ( 2 * margin ) ) / ( GRID_SIZE - 1 );
        final double ySpacing = xSpacing;
        int nodeIndex = 0;
        for ( int row = 0; row < numberOfRows; row++ ) {
            double yOffset = -( beadDiameter / 2 ) + margin + ( row * ySpacing );
            for ( int column = 0; column < numberOfColumns; column++ ) {
                
                // Negative charges are displaced based on e-field magnitude
                PNode negativeNode = (PNode) _negativeNodeList.get( nodeIndex );
                
                double xOffset = -( beadDiameter / 2 ) + margin + ( column * ( 1 - scale ) * xSpacing );
                if ( electricFieldX > 0 ) {
                    negativeNode.setOffset( xOffset, yOffset );
                }
                else {
                    negativeNode.setOffset( -xOffset - chargeSize, yOffset );
                }
                
                nodeIndex++;
            }
        }
    }
}
