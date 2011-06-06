// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the total dielectric charge.
 * Spacing of positive and negative charges remains constant, and they appear in positive/negative pairs.
 * The spacing between the positive/negative pairs changes proportional to E_dielectric.
 * Outside the capacitor, the spacing between the pairs is at a minimum to represent no charge.
 * <p/>
 * All model coordinates are relative to the dielectric's local coordinate frame,
 * where the origin is at the 3D geometric center of the dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricTotalChargeNode extends PhetPNode {

    private static final int SPACING_BETWEEN_PAIRS = 45; // view coordinates
    private static final DoubleRange NEGATIVE_CHARGE_OFFSET = new DoubleRange( 0, SPACING_BETWEEN_PAIRS / 2 ); // view coordinates
    private static final double SPACING_BETWEEN_CHARGES_EXPONENT = 1 / 4d;

    private final Capacitor capacitor;
    private final CLModelViewTransform3D mvt;
    private final PNode parentNode; // parent node for charges
    private final double maxDielectricEField;

    public DielectricTotalChargeNode( Capacitor capacitor, CLModelViewTransform3D mvt, double maxDielectricEField ) {

        this.capacitor = capacitor;
        this.mvt = mvt;
        this.maxDielectricEField = maxDielectricEField;

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

    private void update() {

        // remove existing charges
        parentNode.removeAllChildren();

        // offset of negative charges
        final double eField = capacitor.getDielectricEField();
        final double negativeChargeOffset = getNegativeChargeOffset( eField );

        // spacing between pairs
        final double spacingBetweenPairs = mvt.viewToModelDelta( SPACING_BETWEEN_PAIRS, 0 ).getX();

        // rows and columns
        final double dielectricWidth = capacitor.getDielectricWidth();
        final double dielectricHeight = capacitor.getDielectricHeight();
        final double dielectricDepth = capacitor.getDielectricDepth();
        final int rows = (int) ( dielectricHeight / spacingBetweenPairs );
        final int columns = (int) ( dielectricWidth / spacingBetweenPairs );

        // margins and offsets
        final double xMargin = ( dielectricWidth - ( columns * spacingBetweenPairs ) ) / 2;
        final double yMargin = ( dielectricHeight - ( rows * spacingBetweenPairs ) ) / 2;
        final double zMargin = xMargin;
        final double offset = spacingBetweenPairs / 2;

        // polarity
        final Polarity polarity = ( eField >= 0 ) ? Polarity.NEGATIVE : Polarity.POSITIVE;

        // front face
        double xPlateEdge = -( dielectricWidth / 2 ) + ( dielectricWidth - capacitor.getDielectricOffset() );
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {

                // front face
                {
                    // charge pair
                    ChargePairNode pairNode = new ChargePairNode();
                    parentNode.addChild( pairNode );

                    // location
                    double x = -( dielectricWidth / 2 ) + offset + xMargin + ( column * spacingBetweenPairs );
                    double y = yMargin + offset + ( row * spacingBetweenPairs );
                    double z = ( -dielectricDepth / 2 );
                    pairNode.setOffset( mvt.modelToView( x, y, z ) );

                    // spacing between charges
                    if ( x <= xPlateEdge ) {
                        pairNode.setNegativeChargeOffset( negativeChargeOffset, polarity );
                    }
                    else {
                        pairNode.setNegativeChargeOffset( NEGATIVE_CHARGE_OFFSET.getMin(), polarity );
                    }
                }

                // side face
                {
                    // charge pair
                    ChargePairNode pairNode = new ChargePairNode();
                    parentNode.addChild( pairNode );

                    // location
                    double x = ( dielectricWidth / 2 );
                    double y = yMargin + offset + ( row * spacingBetweenPairs );
                    double z = ( -dielectricDepth / 2 ) + offset + zMargin + ( column * spacingBetweenPairs );
                    pairNode.setOffset( mvt.modelToView( x, y, z ) );

                    // spacing between charges
                    if ( capacitor.getDielectricOffset() == 0 ) {
                        pairNode.setNegativeChargeOffset( negativeChargeOffset, polarity );
                    }
                    else {
                        pairNode.setNegativeChargeOffset( NEGATIVE_CHARGE_OFFSET.getMin(), polarity );
                    }
                }
            }
        }
    }

    /*
     * Offset of negative charges is non-linearly proportion to the E-field.
     */
    private double getNegativeChargeOffset( double eField ) {
        double absEField = Math.abs( eField );
        double percent = Math.pow( absEField / maxDielectricEField, SPACING_BETWEEN_CHARGES_EXPONENT );
        return NEGATIVE_CHARGE_OFFSET.getMin() + ( percent * NEGATIVE_CHARGE_OFFSET.getLength() );
    }

    /*
     * A positive and negative charge pair.
     * The vertical spacing is variable.
     * Origin is at the geometric center when vertical spacing is zero.
     */
    private static class ChargePairNode extends PComposite {

        private final PNode positiveNode, negativeNode;

        public ChargePairNode() {
            positiveNode = new PositiveChargeNode();
            addChild( positiveNode );
            negativeNode = new NegativeChargeNode();
            addChild( negativeNode ); // put negative charge on top, so we can see it when they overlap
        }

        public void setNegativeChargeOffset( double offset, Polarity polarity ) {
            double yOffset = ( polarity == Polarity.POSITIVE ) ? offset : -offset;
            negativeNode.setOffset( negativeNode.getXOffset(), yOffset );
        }
    }
}
