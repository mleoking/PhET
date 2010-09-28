/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of charge on a capacitor plate.
 * Total plate charge is mapped to an integer number of '+' or '-' symbols,
 * and these symbols are distributed across the top face of the plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateChargeNode extends PhetPNode {
    
    private static final double PLUS_MINUS_WIDTH = 10;
    private static final double PLUS_MINUS_HEIGHT = 3;
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PText numberOfChargesNode; // debug, shows the number of charges
    private final PNode chargesParentNode;
    
    public PlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void chargeChanged() {
                update();
            }
        });
        
        chargesParentNode = new PComposite();
        addChild( chargesParentNode );
        
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
    
    private boolean isPositivelyCharged() {
        return ( getPolarity() * circuit.getBattery().getVoltage() > 0 );
    }
    
    private void update() {
        
        double plateCharge = circuit.getTotalPlateCharge();
        int numberOfCharges = getNumberOfCharges( plateCharge );
        
        // numeric display
        if ( numberOfCharges == 0 ) {
            numberOfChargesNode.setText( "0 plate charges" );
        }
        else if ( isPositivelyCharged() ) {
            numberOfChargesNode.setText( String.valueOf( numberOfCharges ) + " plate charges (+)" );
        }
        else {
            numberOfChargesNode.setText( String.valueOf( numberOfCharges ) + " plate charges (-)" );
        }
        
        // create charges
        chargesParentNode.removeAllChildren();
        double plateSize = circuit.getCapacitor().getPlateSideLength();
        for ( int i = 0; i < numberOfCharges; i++ ) {
            
            // add a charge
            PNode chargeNode = null;
            if ( isPositivelyCharged() ) {
                chargeNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, CLPaints.POSITIVE_CHARGE );
            }
            else {
                chargeNode = new MinusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, CLPaints.NEGATIVE_CHARGE );
            }
            chargesParentNode.addChild( chargeNode );
            
            // randomly position the charge on the plate
            double x = -( plateSize / 2 ) + ( plateSize * Math.random() );
            double y = 0;
            double z = -( plateSize / 2 ) + ( plateSize * Math.random() );
            Point2D offset = mvt.modelToView( x, y, z );
            chargeNode.setOffset( offset );
        }
    }
    
    private int getNumberOfCharges( double plateCharge ) {
        
        double absolutePlateCharge = Math.abs( plateCharge );
        double minCharge = CLConstants.MIN_NONZERO_PLATE_CHARGE;
        double maxCharge = BatteryCapacitorCircuit.getMaxPlateCharge();
        
        int numberOfCharges = 0;
        if ( absolutePlateCharge == 0 ) {
            numberOfCharges = 0;
        }
        else if ( absolutePlateCharge <= minCharge ) {
            numberOfCharges = 1;
        }
        else {
            numberOfCharges = (int) ( CLConstants.MAX_NUMBER_OF_PLATE_CHARGES * ( absolutePlateCharge - minCharge ) / ( maxCharge - minCharge ) );
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
