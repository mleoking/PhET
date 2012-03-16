// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.view.IGridSizeStrategy.GridSizeStrategyFactory;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the excess dielectric charge (Q_excess_dielectric).
 * Charges appear on the surface of the dielectric where it contacts the plates,
 * so charges appear on the right face only when the dielectric is fully inserted.
 * <p/>
 * All model coordinates are relative to the dielectric's local coordinate frame,
 * where the origin is at the 3D geometric center of the dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricExcessChargeNode extends PhetPNode {

    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final PNode parentNode; // parent node for charges
    private final double maxExcessDielectricPlateCharge;
    private final IGridSizeStrategy gridSizeStrategy;

    public DielectricExcessChargeNode( Capacitor capacitor, CLModelViewTransform3D mvt, double maxExcessDielectricPlateCharge ) {

        this.capacitor = capacitor;
        this.mvt = mvt;
        this.maxExcessDielectricPlateCharge = maxExcessDielectricPlateCharge;
        this.gridSizeStrategy = GridSizeStrategyFactory.createStrategy();

        this.parentNode = new PComposite();
        addChild( parentNode );

        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );

        update();
    }

    // This method must be called if the model element has a longer lifetime than the view.
    public void cleanup() {
        //FUTURE capacitor.removeCapacitorChangeListener
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

    // Updates the quantity and location of plate charges.
    private void update() {

        // remove existing charges
        parentNode.removeAllChildren();

        final double excessCharge = capacitor.getExcessDielectricPlateCharge();
        final double dielectricWidth = capacitor.getDielectricWidth();
        final double dielectricDepth = capacitor.getDielectricDepth();
        final double contactWidth = Math.max( 0, dielectricWidth - capacitor.getDielectricOffset() ); // contact between plate and dielectric

        if ( excessCharge != 0 && contactWidth > 0 ) {

            // compute the number excess charges
            final int numberOfExcessCharges = getNumberOfCharges( excessCharge );

            // margins
            final double zMargin = mvt.viewToModelDelta( new PositiveChargeNode().getFullBoundsReference().getWidth(), 0 ).getX();

            // compute the grid size
            final double gridWidth = contactWidth;
            final double gridDepth = dielectricDepth - ( 2 * zMargin );
            Dimension gridSize = gridSizeStrategy.getGridSize( numberOfExcessCharges, gridWidth, gridDepth );
            final int rows = gridSize.height;
            final int columns = gridSize.width;

            // distance between charges
            final double dx = gridWidth / columns;
            final double dz = gridDepth / rows;

            // offset to move us to the center of columns
            final double xOffset = dx / 2;
            final double yOffset = mvt.viewToModelDelta( 0, new PositiveChargeNode().getFullBoundsReference().getHeight() + 1 ).getY();
            final double zOffset = dz / 2;

            // Draw a complete grid for the bottom face
            for ( int row = 0; row < rows; row++ ) {
                for ( int column = 0; column < columns; column++ ) {

                    // bottom charges
                    PNode bottomChargeNode = getBottomChargeNode( excessCharge );
                    parentNode.addChild( bottomChargeNode );

                    // position the charge in a grid cell
                    double x = ( -dielectricWidth / 2 ) + xOffset + ( column * dx );
                    double y = capacitor.getDielectricHeight() - yOffset;
                    double z = -( dielectricDepth / 2 ) + zOffset + ( row * dz );
                    bottomChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                }
            }

            // Draw front edge for top face
            double x = 0;
            final double y = yOffset;
            for ( int i = 0; i < columns; i++ ) {
                PNode topChargeNode = getTopChargeNode( excessCharge );
                parentNode.addChild( topChargeNode );
                // position the charge
                x = ( -dielectricWidth / 2 ) + xOffset + ( i * dx );
                double z = -( dielectricDepth / 2 );
                topChargeNode.setOffset( mvt.modelToView( x, y, z ) );
            }

            // Draw right-side edge for top face
            x += xOffset; // start from where we left off with the front edge
            for ( int i = 0; i < rows; i++ ) {
                PNode topChargeNode = getTopChargeNode( excessCharge );
                parentNode.addChild( topChargeNode );
                // position the charge
                double z = ( -dielectricDepth / 2 ) + zOffset + ( i * dz );
                Point2D topOffset = mvt.modelToView( x, y, z );
                topChargeNode.setOffset( topOffset );
            }
        }
    }

    private static PNode getTopChargeNode( double excessCharge ) {
        return ( excessCharge > 0 ) ? new NegativeChargeNode() : new PositiveChargeNode();
    }

    private static PNode getBottomChargeNode( double excessCharge ) {
        return ( excessCharge > 0 ) ? new PositiveChargeNode() : new NegativeChargeNode();
    }

    /*
     * Gets the number of charges on the part of each dielectric face (top and bottom) that contacts a capacitor plate.
     * We use NUMBER_OF_PLATE_CHARGES as the range so that this view is related to the plate charges view.
     */
    private int getNumberOfCharges( double excessCharge ) {
        double absCharge = Math.abs( excessCharge ); // don't take sqrt of absCharge, it's something like 1E-14 and will result in a *larger* number
        int numberOfCharges = (int) ( CLConstants.NUMBER_OF_PLATE_CHARGES.getMax() * absCharge / maxExcessDielectricPlateCharge );
        if ( absCharge > 0 && numberOfCharges < CLConstants.NUMBER_OF_PLATE_CHARGES.getMin() ) {
            numberOfCharges = CLConstants.NUMBER_OF_PLATE_CHARGES.getMin();
        }
        return numberOfCharges;
    }
}
