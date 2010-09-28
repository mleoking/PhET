/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Visual representation of charge on a capacitor plate.
 * Total plate charge is mapped to an integer number of '+' or '-' symbols,
 * and these symbols are distributed across the top face of the plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateChargeNode extends PhetPNode {
    
    private static final double MIN_NONZERO_CHARGE = 8E-15;
    private static final double MAX_CHARGE = BatteryCapacitorCircuit.getMaxPlateCharge();
    private static final int MAX_NUMBER_OF_CHARGES = 625;
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PText numberOfChargesNode; // debug, show the number of charges
    
    public PlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void chargeChanged() {
                update();
            }
        });
        
        numberOfChargesNode = new PText();
        numberOfChargesNode.setFont( new PhetFont( 18 ) );
        numberOfChargesNode.setTextPaint( Color.BLACK );
        if ( dev ) {
            addChild( numberOfChargesNode );
        }

        update();
    }
    
    /*
     * Subclasses indicate the polarity of their plate by returning either +1 or -1.
     */
    protected abstract int getPolarity();
    
    private void update() {
        
        double plateCharge = circuit.getTotalPlateCharge();
        int numberOfCharges = getNumberOfCharges( plateCharge );
        
        if ( numberOfCharges == 0 ) {
            numberOfChargesNode.setText( "0 plate charges" );
        }
        else if ( ( getPolarity() * circuit.getBattery().getVoltage() > 0 ) ) {
            numberOfChargesNode.setText( String.valueOf( numberOfCharges ) + " plate charges (+)" );
        }
        else {
            numberOfChargesNode.setText( String.valueOf( numberOfCharges ) + " plate charges (-)" );
        }
        
        double width = mvt.modelToView( circuit.getCapacitor().getPlateSideLength() );
        double height = mvt.modelToView( circuit.getCapacitor().getPlateSideLength() );
    }
    
    private int getNumberOfCharges( double plateCharge ) {
        double absolutePlateCharge = Math.abs( plateCharge );
        int numberOfCharges = 0;
        if ( plateCharge == 0 ) {
            numberOfCharges = 0;
        }
        else if ( absolutePlateCharge <= MIN_NONZERO_CHARGE ) {
            numberOfCharges = 1;
        }
        else {
            numberOfCharges = (int) ( MAX_NUMBER_OF_CHARGES * ( absolutePlateCharge - MIN_NONZERO_CHARGE ) / ( MAX_CHARGE - MIN_NONZERO_CHARGE ) );
        }
        return numberOfCharges;
    }
    
    public static class TopPlateChargeNode extends PlateChargeNode {
        
        public TopPlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
            super( circuit, mvt, dev );
        }
        
        protected int getPolarity() {
            return +1;
        }
    }
    
    public static class BottomPlateChargeNode extends PlateChargeNode {
        
        public BottomPlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
            super( circuit, mvt, dev );
        }
        
        protected int getPolarity() {
            return -1;
        }
    }
}
