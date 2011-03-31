// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.Dimension;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.capacitorlab.view.IPlateChargeGridSizeStrategy.GridSizeStrategyFactory;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for representation of plate charge.
 * Plate charge is represented as an integer number of '+' or '-' symbols.
 * These symbols are distributed across some portion of the plate's top face.
 * <p>
 * All model coordinates are relative to the capacitor's local coordinate frame.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateChargeNode extends PhetPNode {

    private static final boolean DEBUG_OUTPUT_ENABLED = false;

    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final Polarity polarity;
    private final PNode parentNode; // parent node for charges
    private final IPlateChargeGridSizeStrategy gridSizeStrategy;

    public PlateChargeNode( Capacitor capacitor, CLModelViewTransform3D mvt, Polarity polarity ) {

        this.capacitor = capacitor;
        this.mvt = mvt;
        this.polarity = polarity;
        this.gridSizeStrategy = GridSizeStrategyFactory.createStrategy();

        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );

        parentNode = new PComposite();
        addChild( parentNode );

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

    protected Capacitor getCapacitor() {
        return capacitor;
    }

    private boolean isPositivelyCharged() {
        return ( getPlateCharge() >= 0 && polarity == Polarity.POSITIVE ) || ( getPlateCharge() < 0 && polarity == Polarity.NEGATIVE );
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

    /*
     * Updates the view to match the model.
     * Charges are arranged in a grid.
     */
    private void update() {

        double plateCharge = getPlateCharge();
        int numberOfCharges = getNumberOfCharges( plateCharge );

        // remove existing charges
        parentNode.removeAllChildren();

        // compute grid dimensions
        if ( numberOfCharges > 0 ) {

            final double zMargin = mvt.viewToModelDelta( new PositiveChargeNode().getFullBoundsReference().getWidth(), 0 ).getX();

            final double contactWidth = getContactWidth(); // contact between plate and dielectric
            final double plateDepth = getCapacitor().getPlateDepth() - ( 2 * zMargin );

            // grid dimensions
            Dimension gridSize = gridSizeStrategy.getGridSize( numberOfCharges, contactWidth, plateDepth );
            final int rows = gridSize.height;
            final int columns = gridSize.width;

            // distance between cells
            final double dx = contactWidth / columns;
            final double dz = plateDepth / rows;

            // offset to move us to the center of cells
            final double xOffset = dx / 2;
            final double zOffset = dz / 2;

            // populate the grid
            for ( int row = 0; row < rows; row++ ) {
                for ( int column = 0; column < columns; column++ ) {
                    // add a charge
                    PNode chargeNode = isPositivelyCharged() ? new PositiveChargeNode() : new NegativeChargeNode();
                    parentNode.addChild( chargeNode );

                    // position the charge in cell in the grid
                    double x = getContactXOrigin() + xOffset + ( column * dx );
                    double y = 0;
                    double z = -( plateDepth / 2 ) + ( zMargin / 2 ) + zOffset + ( row * dz );
                    chargeNode.setOffset( mvt.modelToView( x, y, z ) );
                }
            }

            // debug output
            if ( DEBUG_OUTPUT_ENABLED ) {
                System.out.println( getClass().getName() + " " + numberOfCharges + " charges computed, " + ( rows * columns ) + " charges displayed" );
            }
        }
    }

    /*
     * Computes number of charges, linearly proportional to plate charge.
     * All non-zero values below some minimum are mapped to 1 charge.
     */
    private int getNumberOfCharges( double plateCharge ) {

        double absCharge = Math.abs( plateCharge );
        double maxCharge = BatteryCapacitorCircuit.getMaxPlateCharge(); //TODO pass this max in via constructor

        int numberOfCharges = (int) ( CLConstants.NUMBER_OF_PLATE_CHARGES.getMax() * absCharge / maxCharge );
        if ( absCharge > 0 && numberOfCharges < CLConstants.NUMBER_OF_PLATE_CHARGES.getMin() ) {
            numberOfCharges = CLConstants.NUMBER_OF_PLATE_CHARGES.getMin();
        }
        return numberOfCharges;
    }

    /**
     * Portion of the plate charge due to the dielectric.
     * Charges appear on the portion of the plate that is in contact with the dielectric.
     */
    public static class DielectricPlateChargeNode extends PlateChargeNode {

        public DielectricPlateChargeNode( Capacitor capacitor, CLModelViewTransform3D mvt, Polarity polarity ) {
            super( capacitor, mvt, polarity );
        }

        // Gets the portion of the plate charge due to the dielectric.
        protected double getPlateCharge() {
            return getCapacitor().getDielectricPlateCharge();
        }

        // Gets the x offset (relative to the plate's origin) of the portion of the plate that is in contact with the dielectric.
        protected double getContactXOrigin() {
            return -( getCapacitor().getPlateWidth() / 2 ) + getCapacitor().getDielectricOffset();
        }

        // Gets the width of the portion of the plate that is in contact with the dielectric.
        protected double getContactWidth() {
            return Math.max( 0, getCapacitor().getPlateWidth() - getCapacitor().getDielectricOffset() );
        }
    }

    /**
     * Portion of the plate charge due to the air.
     * Charges appear on the portion of the plate that is in contact with air (not in contact with the dielectric.)
     */
    public static class AirPlateChargeNode extends PlateChargeNode {

        public AirPlateChargeNode( Capacitor capacitor, CLModelViewTransform3D mvt, Polarity polarity ) {
            super( capacitor, mvt, polarity );
        }

        // Gets the portion of the plate charge due to air.
        protected double getPlateCharge() {
            return getCapacitor().getAirPlateCharge();
        }

        // Gets the x offset (relative to the plate origin) of the portion of the plate that is in contact with air.
        protected double getContactXOrigin() {
            return -getCapacitor().getPlateWidth() / 2;
        }

        // Gets the width of the portion of the plate that is in contact with air.
        protected double getContactWidth() {
            return Math.min( getCapacitor().getDielectricOffset(), getCapacitor().getPlateWidth() );
        }
    }
}
