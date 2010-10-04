/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the excess dielectric charge (Q_excess_dielectric).
 * The number of charges is proportional to Q_excess. 
 * Charges appear on the surface of the dielectric where it contacts the plates,
 * so charges appear on the right face only when the dielectric is fully inserted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricExcessChargeNode extends PhetPNode {
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PNode parentNode; // parent node for charges

    public DielectricExcessChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        this.parentNode = new PComposite();
        addChild( parentNode );
        
        circuit.getCapacitor().addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void plateSizeChanged() {
                update();
            }

            @Override
            public void plateSeparationChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );
        
        update();
    }
    
    /**
     * Update the node when it becomes visible.
     */
    @Override
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
        }
    }
    
    private void update() {
        
        // remove existing charges
        parentNode.removeAllChildren();
        
        Capacitor capacitor = circuit.getCapacitor();
        final double excessCharge = circuit.getExcessDielectricPlateCharge();
        final double plateWidth = capacitor.getPlateSideLength();
        final double plateDepth = capacitor.getPlateSideLength();
        final double contactWidth = Math.max( 0, plateWidth - capacitor.getDielectricOffset() );

        if ( excessCharge > 0 && contactWidth > 0 ) {

            final int numberOfCharges = getNumberOfCharges( excessCharge );

            // distance between charges
            final double dx = contactWidth / numberOfCharges;
            final double dz = plateDepth / numberOfCharges;

            // offset to move us to the center of columns
            final double xOffset = dx / 2;
            final double zOffset = dz / 2;

            // front face
            for ( int i = 0; i < numberOfCharges; i++ ) {

                // add a pair of charges
                PNode topChargeNode = ( excessCharge > 0 ) ? new PositiveChargeNode() : new NegativeChargeNode();
                PNode bottomChargeNode = ( excessCharge > 0 ) ? new NegativeChargeNode() : new PositiveChargeNode();
                parentNode.addChild( topChargeNode );
                parentNode.addChild( bottomChargeNode );
                
                // position the charges at the top and bottom edges of the dielectric
                double x = capacitor.getDielectricOffset() + xOffset + dx;
                double y = -( capacitor.getPlateSeparation() / 2 ) - capacitor.getDielectricGap();
                double z = -( plateDepth / 2 );
                Point2D topOffset = mvt.modelToView( x, y, z );
                topChargeNode.setOffset( topOffset );
                Point2D bottomOffset = mvt.modelToView( x, -y, z );
                topChargeNode.setOffset( bottomOffset );
            }

            // side face, charges only shown with dielectric fully inserted
            if ( capacitor.getDielectricOffset() == 0 ) {
                //XXX
            }
        }
    }
    
    private int getNumberOfCharges( double excessDielectricCharge ) {
        
        double absCharge = Math.abs( excessDielectricCharge );
        double maxCharge = BatteryCapacitorCircuit.getMaxExcessDielectricPlateCharge();
        
        int numberOfCharges = (int) ( CLConstants.NUMBER_OF_EXCESS_DIELECTRIC_CHARGES.getMax() * absCharge / maxCharge );
        if ( absCharge > 0 && numberOfCharges < CLConstants.NUMBER_OF_EXCESS_DIELECTRIC_CHARGES.getMin() ) {
            numberOfCharges = CLConstants.NUMBER_OF_EXCESS_DIELECTRIC_CHARGES.getMin();
        }
        return numberOfCharges;
    }
}
