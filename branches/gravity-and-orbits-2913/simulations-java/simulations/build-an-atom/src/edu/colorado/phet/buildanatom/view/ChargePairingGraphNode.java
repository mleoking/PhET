// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a PNode that portrays the charges within the atom
 * in a side-by-side way so that the user can easily see how the changes
 * cancel one another out.
 *
 * @author John Blanco
 */
public class ChargePairingGraphNode extends PNode {

    private static final double VERTICAL_INTER_ICON_SPACING = 3;
    private static final double HORIZONTAL_INTER_ICON_SPACING = 3;
    private static final double THICKNESS_FACTOR = 0.3;

    private static final PPath chargeCancellationEnclosingBox = new PhetPPath( (Paint)null, new BasicStroke(1f), Color.BLACK );

    private final ArrayList<PositiveChargeIconNode> positiveChargeIconList = new ArrayList<PositiveChargeIconNode>();
    private final ArrayList<NegativeChargeIconNode> negativeChargeIconList = new ArrayList<NegativeChargeIconNode>();

    /**
     * Constructor.
     */
    public ChargePairingGraphNode( final IDynamicAtom atom ) {

        addChild( chargeCancellationEnclosingBox );

        atom.addAtomListener( new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                int negChargeDiff = atom.getNumElectrons() - negativeChargeIconList.size();
                if ( negChargeDiff > 0 ) {
                    addNegativeIcons( negChargeDiff );
                }
                else if ( negChargeDiff < 0 ) {
                    removeNegativeIcons( -negChargeDiff );
                }

                int posChargeDiff = atom.getNumProtons() - positiveChargeIconList.size();
                if ( posChargeDiff > 0 ) {
                    addPositiveIcons( posChargeDiff );
                }
                else if ( posChargeDiff < 0 ) {
                    removePositiveIcons( -posChargeDiff );
                }

                updateBoundingBox();
            }
        } );
    }

    private void addPositiveIcons( int numToAdd ) {
        assert numToAdd >= 0;
        for ( int i = 0; i < numToAdd; i++ ) {
            PositiveChargeIconNode icon = new PositiveChargeIconNode();
            positiveChargeIconList.add( icon );
            icon.setOffset(
                    ( positiveChargeIconList.size() - 1 ) * ( ChargeIconNode.CHARGE_ICON_SIZE.getWidth() + HORIZONTAL_INTER_ICON_SPACING ),
                    0 );
            addChild( icon );
        }
    }

    private void addNegativeIcons( int numToAdd ) {
        assert numToAdd >= 0;
        for ( int i = 0; i < numToAdd; i++ ) {
            NegativeChargeIconNode icon = new NegativeChargeIconNode();
            negativeChargeIconList.add( icon );
            icon.setOffset(
                    ( negativeChargeIconList.size() - 1 ) * ( ChargeIconNode.CHARGE_ICON_SIZE.getWidth() + HORIZONTAL_INTER_ICON_SPACING ),
                    ChargeIconNode.CHARGE_ICON_SIZE.getHeight() + VERTICAL_INTER_ICON_SPACING );
            addChild( icon );
        }
    }

    private void removePositiveIcons( int numToRemove ) {
        assert numToRemove >= 0;
        assert positiveChargeIconList.size() >= numToRemove;
        for ( int i = 0; i < numToRemove; i++ ) {
            PositiveChargeIconNode icon = positiveChargeIconList.get( positiveChargeIconList.size() - 1 );
            positiveChargeIconList.remove( icon );
            removeChild( icon );
        }
    }

    private void removeNegativeIcons( int numToRemove ) {
        assert numToRemove >= 0;
        assert negativeChargeIconList.size() >= numToRemove;
        for ( int i = 0; i < numToRemove; i++ ) {
            NegativeChargeIconNode icon = negativeChargeIconList.get( negativeChargeIconList.size() - 1 );
            negativeChargeIconList.remove( icon );
            removeChild( icon );
        }
    }

    static class ChargeIconNode extends PNode {

        protected static final Dimension2D CHARGE_ICON_SIZE = new PDimension( 20, 20 );

        public ChargeIconNode( Shape symbol, Color foregroundColor, Color backgroundColor ) {
            PhetPPath symbolNode = new PhetPPath( symbol, foregroundColor, new BasicStroke( 1f ), Color.BLACK );
            symbolNode.setOffset( CHARGE_ICON_SIZE.getWidth() / 2, CHARGE_ICON_SIZE.getHeight() / 2 );
            addChild( symbolNode );
        }
    }

    static class PositiveChargeIconNode extends ChargeIconNode {

        private static Color BACKGROUND_COLOR = new Color( 248, 203, 203 );
        private static Color SYMBOL_COLOR = Color.RED;

        /**
         * Constructor.
         */
        public PositiveChargeIconNode() {
            super( drawPlusSign( CHARGE_ICON_SIZE.getWidth() * 0.6 ), SYMBOL_COLOR, BACKGROUND_COLOR );
        }
    }

    static class NegativeChargeIconNode extends ChargeIconNode {

        private static Color BACKGROUND_COLOR = new Color( 200, 217, 234 );
        private static Color SYMBOL_COLOR = Color.BLUE;

        /**
         * Constructor.
         */
        public NegativeChargeIconNode() {
            super( drawMinusSign( CHARGE_ICON_SIZE.getWidth() * 0.5 ), SYMBOL_COLOR, BACKGROUND_COLOR );
        }
    }

    /**
     * Update the box the bounds the set of charges that cancel each other
     * out.  Note that this box has a negative offset so that the set of
     * charge indicators is at the (0, 0) location.
     */
    private void updateBoundingBox() {
        int cancellingCharges = Math.min( positiveChargeIconList.size(), negativeChargeIconList.size() );
        chargeCancellationEnclosingBox.setVisible( cancellingCharges > 0 );

        // Update the bounding box (whether or not it is visible).
        RoundRectangle2D boxShape = new RoundRectangle2D.Double(
                -HORIZONTAL_INTER_ICON_SPACING / 2,
                -VERTICAL_INTER_ICON_SPACING / 2,
                cancellingCharges * ChargeIconNode.CHARGE_ICON_SIZE.getWidth() + cancellingCharges * HORIZONTAL_INTER_ICON_SPACING,
                ChargeIconNode.CHARGE_ICON_SIZE.getHeight() * 2 + VERTICAL_INTER_ICON_SPACING * 2,
                4,
                4 );
        chargeCancellationEnclosingBox.setPathTo( boxShape );
    }

    private static Shape drawPlusSign( double width ) {
        DoubleGeneralPath path = new DoubleGeneralPath();
        double thickness = width * THICKNESS_FACTOR;
        double halfThickness = thickness / 2;
        double halfWidth = width / 2;
        path.moveTo( -halfWidth, -halfThickness );
        path.lineTo( -halfWidth, halfThickness );
        path.lineTo( -halfThickness, halfThickness );
        path.lineTo( -halfThickness, halfWidth );
        path.lineTo( halfThickness, halfWidth );
        path.lineTo( halfThickness, halfThickness );
        path.lineTo( halfWidth, halfThickness );
        path.lineTo( halfWidth, -halfThickness );
        path.lineTo( halfThickness, -halfThickness );
        path.lineTo( halfThickness, -halfWidth );
        path.lineTo( -halfThickness, -halfWidth );
        path.lineTo( -halfThickness, -halfThickness );
        path.closePath();
        return path.getGeneralPath();
    }

    private static Shape drawMinusSign( double width ) {
        double height = width * THICKNESS_FACTOR;
        return new Rectangle2D.Double( -width / 2, -height / 2, width, height );
    }

}
