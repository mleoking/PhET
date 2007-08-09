/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.util.ArrayList;

import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * ChargeDistributionNode displays the distibution of charge on the bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargeDistributionNode extends AbstractChargeNode {

    private static final int CHARGE_DENSITY = 4; // this number squared is the number of charges of each type (+-) that will be displayed
    
    private static final double CHARGE_SIZE = 20; // nm
    private static final double CHARGE_STROKE_WIDTH = 5; // nm, width of the stroke used to draw the charges
    private static final double MARGIN = 5; // nm, how close the charges are to the edge of the bead
    
    private ArrayList _positiveNodeList, _negativeNodeList;
    
    /**
     * Constructor.
     * 
     * @param bead
     * @param laser
     * @param modelViewTransform
     */
    public ChargeDistributionNode( Bead bead, Laser laser, ModelViewTransform modelViewTransform ) {
        super( bead, laser, modelViewTransform );
        assert( CHARGE_DENSITY > 1 );
    }
    
    /*
     * Initializes the nodes and other member data.
     */
    protected void initialize() {
        
        ModelViewTransform modelViewTransform = getModelViewTransform();
        final double size = modelViewTransform.modelToView( CHARGE_SIZE );
        final double strokeWidth = modelViewTransform.modelToView( CHARGE_STROKE_WIDTH );
        
        _positiveNodeList = new ArrayList();
        _negativeNodeList = new ArrayList();
        
        PNode chargeNode = null;
        final int numberOfCharges = CHARGE_DENSITY * CHARGE_DENSITY;
        for ( int i = 0; i < numberOfCharges; i++ ) {
            
            chargeNode = createPositiveNode( size, strokeWidth );
            _positiveNodeList.add( chargeNode );
            addChild( chargeNode );

            chargeNode = createNegativeNode( size, strokeWidth );
            _negativeNodeList.add( chargeNode );
            addChild( chargeNode );
        }
        
        updateCharges();
    }
    
    /*
     * Distributes the charges inside the bead, based on the electric field
     * strength at the bead's position, relative to the laser's maximum electric field.
     * As the scale approaches 1, positive charges will be pulled further to the left,
     * negative charges to the right. If scale is zero, the charges will be evenly distributed.
     */
    protected void updateCharges() {
        
        Bead bead = getBead();
        ModelViewTransform modelViewTransform = getModelViewTransform();
        
        final double electricFieldX = bead.getElectricFieldX();
        final double scale = getChargeScale( electricFieldX );
        final double beadDiameter = modelViewTransform.modelToView( bead.getDiameter() );
        final double margin = modelViewTransform.modelToView( MARGIN );
        
        //XXX layout the charges in a rectangular grid, not what we want but OK for testing
        final double numberOfRows = CHARGE_DENSITY;
        final double numberOfColumns = CHARGE_DENSITY;
        final double spacing = ( beadDiameter - ( 2 * margin ) ) / ( CHARGE_DENSITY - 1 );
        int nodeIndex = 0;
        for ( int row = 0; row < numberOfRows; row++ ) {
            double yOffset = -( beadDiameter / 2 ) + margin + ( row * spacing );
            for ( int column = 0; column < numberOfColumns; column++ ) {
                
                PNode positiveNode = (PNode) _positiveNodeList.get( nodeIndex );
                PNode negativeNode = (PNode) _negativeNodeList.get( nodeIndex );
                
                double xOffset = -( beadDiameter / 2 ) + margin + ( column * ( 1 - scale ) * spacing );
                if ( electricFieldX > 0 ) {
                    positiveNode.setOffset( xOffset, yOffset );
                    negativeNode.setOffset( -xOffset, yOffset );
                }
                else {
                    positiveNode.setOffset( -xOffset, yOffset );
                    negativeNode.setOffset( xOffset, yOffset );
                }
                
                nodeIndex++;
            }
            yOffset += spacing;
        }
    }
}
