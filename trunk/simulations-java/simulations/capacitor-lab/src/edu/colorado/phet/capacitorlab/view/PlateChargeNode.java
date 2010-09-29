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
 * Total plate charge is represented as an integer number of '+' or '-' symbols.
 * These symbols are distributed across the top face of the plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateChargeNode extends PhetPNode {
    
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
    
    private boolean isPositivelyCharged() {
        return ( ( !circuit.isBatteryConnected() && polarity == Polarity.POSITIVE ) ||  
                 ( circuit.isBatteryConnected() && polarity == Polarity.POSITIVE && circuit.getBattery().getVoltage() >= 0 ) ||
                 ( circuit.isBatteryConnected() && polarity == Polarity.NEGATIVE && circuit.getBattery().getVoltage() < 0 ) );
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
            double x = getRandomCoordinate( plateSize );
            double y = 0;
            double z = getRandomCoordinate( plateSize );;
            Point2D offset = mvt.modelToView( x, y, z );
            chargeNode.setOffset( offset );
        }
    }
    
    private double getRandomCoordinate( double plateSize ) {
        double margin = mvt.viewToModel( PLUS_MINUS_WIDTH ); // to keep charges fully inside the plate
        return -( plateSize / 2 ) + margin + ( ( plateSize - ( 2 * margin ) ) * Math.random() );
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
}
