/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.capacitorlab.view.PlateNode.Polarity;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for representation of plate charge.
 * Plate charge is represented as an integer number of '+' or '-' symbols.
 * These symbols are distributed across some portion of the plate's top face.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateChargeNode extends PhetPNode {
    
    private static final double PLUS_MINUS_WIDTH = 7;
    private static final double PLUS_MINUS_HEIGHT = 1;
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final Polarity polarity;
    private final PText numberOfChargesNode; // debug, shows the number of charges
    private final PNode chargesParentNode;

    public PlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev, Polarity polarity ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        this.polarity = polarity;
        
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
    
    protected abstract double getPlateCharge();
    
    protected abstract double getContactX();
    
    protected abstract double getContactWidth();
    
    protected BatteryCapacitorCircuit getCircuit() {
        return circuit;
    }
    
    private boolean isPositivelyCharged() {
        return ( ( !circuit.isBatteryConnected() && polarity == Polarity.POSITIVE ) ||  
                 ( circuit.isBatteryConnected() && polarity == Polarity.POSITIVE && circuit.getBattery().getVoltage() >= 0 ) ||
                 ( circuit.isBatteryConnected() && polarity == Polarity.NEGATIVE && circuit.getBattery().getVoltage() < 0 ) );
    }
    
    private void update() {
        
        double plateCharge = getPlateCharge();
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
        double plateDepth = circuit.getCapacitor().getPlateSideLength();
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
            double x = getContactX() + ( Math.random() * getContactWidth() );
            double y = 0;
            double z = -( plateDepth / 2 ) + ( Math.random() * plateDepth );
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
    
    /**
     * Portion of the plate charge due to the dielectric.
     * Charges appear on the portion of the plate that is in contact with the dielectric.
     */
    public static class DielectricPlateChargeNode extends PlateChargeNode {

        public DielectricPlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev, Polarity polarity ) {
            super( circuit, mvt, dev, polarity );
        }
        
        // Gets the portion of the plate charge due to the dielectric.
        protected double getPlateCharge() {
            return getCircuit().getDielectricPlateCharge();
        }
        
        // Gets the x location (relative to the plate) of the portion of the plate that is in contact with the dielectric.
        public double getContactX() {
            return -( getCircuit().getCapacitor().getPlateSideLength() / 2 ) + getCircuit().getCapacitor().getDielectricOffset();
        }
        
        // Gets the width of the portion of the plate that is in contact with the dielectric.
        protected double getContactWidth() {
            return getCircuit().getCapacitor().getPlateSideLength() - getCircuit().getCapacitor().getDielectricOffset();
        }
    }
    
    /**
     * Portion of the plate charge due to the air.
     * Charges appear on the portion of the plate that is in contact with air (not in contact with the dielectric.)
     */
    public static class AirPlateChargeNode extends PlateChargeNode {

        public AirPlateChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev, Polarity polarity ) {
            super( circuit, mvt, dev, polarity );
        }
        
        // Gets the portion of the plate charge due to air.
        public double getPlateCharge() {
            return getCircuit().getAirPlateCharge();
        }
        
        // Gets the x location (relative to the plate) of the portion of the plate that is in contact with air.
        public double getContactX() {
            return -getCircuit().getCapacitor().getPlateSideLength() / 2;
        }
        
        // Gets the width of the portion of the plate that is in contact with air.
        public double getContactWidth() {
            return getCircuit().getCapacitor().getDielectricOffset();
        }
    }
}
