/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.Capacitor;
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
        numberOfChargesNode.setFont( new PhetFont( 14 ) );
        numberOfChargesNode.setTextPaint( Color.BLACK );
        numberOfChargesNode.setOffset( getNumberOfChargesOffset() );
        if ( dev ) {
            addChild( numberOfChargesNode );
        }
        
        update();
    }
    
    /*
     * Charge on the portion of the plate that this node handles.
     */
    protected abstract double getPlateCharge();
    
    /*
     * X offset of the portion of the plate that this node handles.
     * This is relative to the plate's origin, and specified in model coordinates.
     */
    protected abstract double getContactXOrigin();
    
    /*
     * Width of the portion of the plate that this node handles.
     * Specified in model coordinates.
     */
    protected abstract double getContactWidth();
    
    /*
     * Offset of numberOfChargesNode debug node, to prevent overlap between subclass nodes.
     * Specified in view coordinated. 
     */
    protected abstract Point2D getNumberOfChargesOffset();
    
    /*
     * Label used on numberOfChargesNode debug node.
     */
    protected abstract String getNumberOfChargesLabel();
    
    protected BatteryCapacitorCircuit getCircuit() {
        return circuit;
    }
    
    private boolean isPositivelyCharged() {
        return ( ( !circuit.isBatteryConnected() && polarity == Polarity.POSITIVE ) ||  
                 ( circuit.isBatteryConnected() && polarity == Polarity.POSITIVE && circuit.getBattery().getVoltage() >= 0 ) ||
                 ( circuit.isBatteryConnected() && polarity == Polarity.NEGATIVE && circuit.getBattery().getVoltage() < 0 ) );
    }
    
    /*
     * Updates the view to match the model.
     * Charges are arranged in a grid.
     */
    private void update() {
        
        double plateCharge = getPlateCharge();
        int numberOfCharges = getNumberOfCharges( plateCharge );
        
        // numeric display
        String label = getNumberOfChargesLabel();
        if ( numberOfCharges == 0 ) {
            numberOfChargesNode.setText( "0 " + label );
        }
        else if ( isPositivelyCharged() ) {
            numberOfChargesNode.setText( String.valueOf( numberOfCharges ) + " " + label + " (+)" );
        }
        else {
            numberOfChargesNode.setText( String.valueOf( numberOfCharges ) + " " + label + " (-)" );
        }
        
        // remove existing charges
        chargesParentNode.removeAllChildren();
        
        // compute grid dimensions
        final double contactWidth = getContactWidth();
        final double plateDepth = circuit.getCapacitor().getPlateSideLength();
        final double alpha = Math.sqrt( numberOfCharges / contactWidth / plateDepth );
        final int rows = (int) ( plateDepth * alpha ); // casting may result in some charges being thrown out, but that's OK
        final int columns = (int) ( contactWidth * alpha );
        
        // populate the grid with charges
        double dx = contactWidth / columns;
        double dz = plateDepth / rows;
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                // add a charge
                PNode chargeNode = null;
                if ( isPositivelyCharged() ) {
                    chargeNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, CLPaints.POSITIVE_CHARGE );
                }
                else {
                    chargeNode = new MinusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, CLPaints.NEGATIVE_CHARGE );
                }
                chargesParentNode.addChild( chargeNode );
                
                // position the charge in cell in the grid
                double x = getContactXOrigin() + ( column * dx );
                double y = 0;
                double z = -( plateDepth / 2 ) + ( row * dz );
                Point2D offset = mvt.modelToView( x, y, z );
                chargeNode.setOffset( offset );
            }
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
        
        // Gets the x offset (relative to the plate's origin) of the portion of the plate that is in contact with the dielectric.
        public double getContactXOrigin() {
            return -( getCircuit().getCapacitor().getPlateSideLength() / 2 ) + getCircuit().getCapacitor().getDielectricOffset();
        }
        
        // Gets the width of the portion of the plate that is in contact with the dielectric.
        protected double getContactWidth() {
            Capacitor capacitor = getCircuit().getCapacitor();
            return Math.max( 0, capacitor.getPlateSideLength() - capacitor.getDielectricOffset() );
        }
        
        // offset for debug node, to prevent overlap with other subclasses
        protected Point2D getNumberOfChargesOffset() {
            return new Point2D.Double( 10, -80 );
        }
        
        // label used on debug node
        protected String getNumberOfChargesLabel() {
            return "charges due to dielectric";
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
        
        // Gets the x offset (relative to the plate origin) of the portion of the plate that is in contact with air.
        public double getContactXOrigin() {
            return -getCircuit().getCapacitor().getPlateSideLength() / 2;
        }
        
        // Gets the width of the portion of the plate that is in contact with air.
        public double getContactWidth() {
            Capacitor capacitor = getCircuit().getCapacitor();
            return Math.min( capacitor.getDielectricOffset(), capacitor.getPlateSideLength() );
        }
        
        // offset for debug node, to prevent overlap with other subclasses
        protected Point2D getNumberOfChargesOffset() {
            return new Point2D.Double( 10, -60 );
        }
        
        // label used on debug node
        protected String getNumberOfChargesLabel() {
            return "charges due to air";
        }
    }
}
