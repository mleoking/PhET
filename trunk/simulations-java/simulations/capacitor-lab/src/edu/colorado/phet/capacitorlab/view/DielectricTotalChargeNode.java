/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the total dielectric charge.
 * Spacing of positive and negative charges remains constant, and they appear in positive/negative pairs.
 * The spacing between the positive/negative pairs changes proportional to Q_excess_dielectric.
 * Outside the capacitor, the spacing between the pairs is at a minimum to reprsent no charge.
 * <p>
 * All model coordinates are relative to the dielectric's local coordinate frame,
 * where the origin is at the 3D geometric center of the dielectric.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricTotalChargeNode extends PhetPNode {
    
    private static final int SPACING_BETWEEN_PAIRS = 10; // view coordinates
    private static final IntegerRange SPACING_BETWEEN_CHARGES = new IntegerRange( (int)( 0.05 * SPACING_BETWEEN_PAIRS ), (int)( 0.4 * SPACING_BETWEEN_PAIRS ) ); // view coordinates
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PNode parentNode; // parent node for charges

    public DielectricTotalChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        this.parentNode = new PComposite();
        addChild( parentNode );
        
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void capacitanceChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
            @Override
            public void voltageChanged() {
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
        
        final double excessCharge = circuit.getExcessDielectricPlateCharge();
        final double dielectricWidth = circuit.getCapacitor().getPlateSideLength();
        final double dielectricHeight = circuit.getCapacitor().getDielectricHeight();
    }
}
