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

    private static final boolean USE_GENERAL_APPROACH = true;

    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final PNode parentNode; // parent node for charges
    private final double maxExcessDielectricPlateCharge;
    private final IGridSizeStrategy gridSizeStrategy;

    // true=show only charges on the edges, because the dielectric is opaque
    // false=show all charges, because the dielectric is transparent
    private final boolean showEdgeChargesOnly;

    public DielectricExcessChargeNode( Capacitor capacitor, CLModelViewTransform3D mvt, double maxExcessDielectricPlateCharge, boolean showEdgeChargesOnly ) {

        this.capacitor = capacitor;
        this.mvt = mvt;
        this.maxExcessDielectricPlateCharge = maxExcessDielectricPlateCharge;
        this.gridSizeStrategy = GridSizeStrategyFactory.createStrategy();
        this.showEdgeChargesOnly = showEdgeChargesOnly;

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

            // compute the size of the grid
            Dimension gridSize = gridSizeStrategy.getGridSize( numberOfExcessCharges, contactWidth, dielectricDepth );

            // #2928, constrain number of charges to be <= number of charges displayed along edge of plate
            int rows = gridSize.height;
            int columns = gridSize.width;

            // distance between charges
            final double dx = contactWidth / columns;
            final double dz = dielectricDepth / rows;

            // offset to move us to the center of columns
            final double xOffset = dx / 2;
            final double zOffset = dz / 2;

            final double yMargin = mvt.viewToModelDelta( 0, new PositiveChargeNode().getFullBoundsReference().getHeight() + 1 ).getY();

            if ( USE_GENERAL_APPROACH ) {
                /*
                 * This approach uses one algorithm to draw the grids, and leaves
                 * out specific cells if we're only drawing charges at the edges.
                 */
                for ( int row = 0; row < rows; row++ ) {
                    for ( int column = 0; column < columns; column++ ) {

                        boolean isVisibleEdgeCell = ( row == 0 ) || ( ( column == columns - 1 ) && capacitor.getDielectricOffset() == 0 );
                        if ( !showEdgeChargesOnly || ( showEdgeChargesOnly && isVisibleEdgeCell ) ) {

                            // top and bottom charges
                            PNode topChargeNode = getTopChargeNode( excessCharge );
                            PNode bottomChargeNode = getBottomChargeNode( excessCharge );
                            parentNode.addChild( topChargeNode );
                            parentNode.addChild( bottomChargeNode );

                            // position the charges in cells in the grid
                            double x = ( -dielectricWidth / 2 ) + xOffset + ( column * dx );
                            double y = yMargin;
                            double z = -( dielectricDepth / 2 ) + zOffset + ( row * dz );
                            topChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                            y = capacitor.getDielectricHeight() - yMargin;
                            bottomChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                        }
                    }
                }
            }
            else {
                /*
                 * This approach draws the edge charges separately, using the original 1.00 algorithm.
                 * It's impossible to get these edge charges to line up with the edge charges in the grid.
                 */
                if ( showEdgeChargesOnly ) {

                    // front edge of top and bottom
                    for ( int i = 0; i < columns; i++ ) {

                        // add a pair of charges
                        PNode topChargeNode = getTopChargeNode( excessCharge );
                        PNode bottomChargeNode = getBottomChargeNode( excessCharge );
                        parentNode.addChild( topChargeNode );
                        parentNode.addChild( bottomChargeNode );

                        // position the charges at the top and bottom edges of the dielectric's front face
                        double x = ( -dielectricWidth / 2 ) + xOffset + ( i * dx );
                        double y = yMargin;
                        double z = -( dielectricDepth / 2 );
                        topChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                        y = capacitor.getDielectricHeight() - yMargin;
                        bottomChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                    }

                    // right-side edge top and bottom, charges only shown with dielectric fully inserted
                    if ( capacitor.getDielectricOffset() == 0 ) {

                        for ( int i = 0; i < rows; i++ ) {

                            // add a pair of charges
                            PNode topChargeNode = getTopChargeNode( excessCharge );
                            PNode bottomChargeNode = getBottomChargeNode( excessCharge );
                            parentNode.addChild( topChargeNode );
                            parentNode.addChild( bottomChargeNode );

                            // position the charges at the top and bottom edges of the dielectric's side face
                            double x = dielectricWidth / 2;
                            double y = yMargin;
                            double z = ( -dielectricDepth / 2 ) + zOffset + ( i * dz );
                            Point2D topOffset = mvt.modelToView( x, y, z );
                            topChargeNode.setOffset( topOffset );
                            y = capacitor.getDielectricHeight() - yMargin;
                            Point2D bottomOffset = mvt.modelToView( x, y, z );
                            bottomChargeNode.setOffset( bottomOffset );
                        }
                    }
                }
                else {
                    // top
                    {
                        // front edge of top
                        double x = 0;
                        for ( int i = 0; i < columns; i++ ) {
                            PNode topChargeNode = getTopChargeNode( excessCharge );
                            parentNode.addChild( topChargeNode );
                            // position the charge
                            x = ( -dielectricWidth / 2 ) + xOffset + ( i * dx );
                            double y = yMargin;
                            double z = -( dielectricDepth / 2 );
                            topChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                        }

                        // right-side edge of top
                        x += xOffset; // start from where we left off with the front edge
                        for ( int i = 0; i < rows; i++ ) {
                            PNode topChargeNode = getTopChargeNode( excessCharge );
                            parentNode.addChild( topChargeNode );
                            // position the charge
                            double y = yMargin;
                            double z = ( -dielectricDepth / 2 ) + zOffset + ( i * dz );
                            Point2D topOffset = mvt.modelToView( x, y, z );
                            topChargeNode.setOffset( topOffset );
                        }
                    }

                    // complete surface of bottom
                    final double y = capacitor.getDielectricHeight() - yMargin;
                    for ( int row = 0; row < rows; row++ ) {
                        for ( int column = 0; column < columns; column++ ) {
                            PNode bottomChargeNode = getBottomChargeNode( excessCharge );
                            parentNode.addChild( bottomChargeNode );
                            // position the charge in cell in the grid
                            double x = ( -dielectricWidth / 2 ) + xOffset + ( column * dx );
                            double z = -( dielectricDepth / 2 ) + zOffset + ( row * dz );
                            bottomChargeNode.setOffset( mvt.modelToView( x, y, z ) );
                        }
                    }
                }
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
