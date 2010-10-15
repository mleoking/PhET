/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a PNode that portrays the charges within the atom
 * in a side-by-side way so that the user can easily see how the changes
 * cancel one another out.
 *
 * @author John Blanco
 */
public class ChargePairingGraphNode extends PNode {

    private static final Dimension2D CHARGE_ICON_SIZE = new PDimension(20, 20);
    private static final Font CHARGE_ICON_FONT = new PhetFont(20, true);
    private static final double VERTICAL_INTER_ICON_SPACING = 5;
    private static final double HORIZONTAL_INTER_ICON_SPACING = 5;

    private final ArrayList<PositiveChargeIconNode> positiveChargeIconList = new ArrayList<PositiveChargeIconNode>();
    private final ArrayList<NegativeChargeIconNode> negativeChargeIconList = new ArrayList<NegativeChargeIconNode>();

    /**
     * Constructor.
     */
    public ChargePairingGraphNode(final Atom atom) {

        atom.addObserver( new SimpleObserver() {

            public void update() {
                int negChargeDiff = atom.getNumElectrons() - negativeChargeIconList.size();
                if (negChargeDiff > 0){
                    addNegativeIcons( negChargeDiff );
                }
                else if (negChargeDiff < 0){
                    removeNegativeIcons( -negChargeDiff );
                }

                int posChargeDiff = atom.getNumProtons() - positiveChargeIconList.size();
                if (posChargeDiff > 0){
                    addPositiveIcons( posChargeDiff );
                }
                else if (posChargeDiff < 0){
                    removePositiveIcons( -posChargeDiff );
                }
            }
        });
    }

    private void addPositiveIcons( int numToAdd ) {
        assert numToAdd >= 0;
        for (int i = 0; i < numToAdd; i++){
            PositiveChargeIconNode icon = new PositiveChargeIconNode();
            positiveChargeIconList.add( icon );
            icon.setOffset(
                    positiveChargeIconList.size() * (CHARGE_ICON_SIZE.getWidth() + HORIZONTAL_INTER_ICON_SPACING),
                    CHARGE_ICON_SIZE.getHeight() + VERTICAL_INTER_ICON_SPACING );
            addChild(icon);
        }
    }

    private void addNegativeIcons( int numToAdd ) {
        assert numToAdd >= 0;
        for (int i = 0; i < numToAdd; i++){
            NegativeChargeIconNode icon = new NegativeChargeIconNode();
            negativeChargeIconList.add( icon );
            icon.setOffset(
                    negativeChargeIconList.size() * (CHARGE_ICON_SIZE.getWidth() + HORIZONTAL_INTER_ICON_SPACING),
                    0 );
            addChild(icon);
        }
    }

    private void removePositiveIcons( int numToRemove ) {
        assert numToRemove >= 0;
        assert positiveChargeIconList.size() >= numToRemove;
        for (int i = 0; i < numToRemove; i++){
            PositiveChargeIconNode icon = positiveChargeIconList.get( positiveChargeIconList.size() - 1 );
            positiveChargeIconList.remove( icon );
            removeChild( icon );
        }
    }

    private void removeNegativeIcons( int numToRemove ) {
        assert numToRemove >= 0;
        assert negativeChargeIconList.size() >= numToRemove;
        for (int i = 0; i < numToRemove; i++){
            NegativeChargeIconNode icon = negativeChargeIconList.get( negativeChargeIconList.size() - 1 );
            negativeChargeIconList.remove( icon );
            removeChild( icon );
        }
    }

    static class PositiveChargeIconNode extends PNode {

        private static Color BACKGROUND_COLOR = new Color( 228, 183, 183 );

        private final ShadowPText label = new ShadowPText("+") {{
            setFont(CHARGE_ICON_FONT);
            setTextPaint( Color.RED );
            setShadowColor( Color.BLACK );
            setOffset(CHARGE_ICON_SIZE.getWidth() / 2 - getFullBoundsReference().getWidth() / 2,
                    CHARGE_ICON_SIZE.getHeight() / 2 - getFullBoundsReference().getHeight() / 2 );
        }};

        /**
         * Constructor.
         */
        public PositiveChargeIconNode() {
            Rectangle2D backgroundRect = new Rectangle2D.Double(0, 0, CHARGE_ICON_SIZE.getWidth(),
                    CHARGE_ICON_SIZE.getHeight());
            addChild(new PhetPPath(backgroundRect, BACKGROUND_COLOR, new BasicStroke(1), Color.BLACK));
            addChild( label );
        }
    }

    static class NegativeChargeIconNode extends PNode {

        private static Color BACKGROUND_COLOR = new Color( 170, 187, 204 );

        private final ShadowPText label = new ShadowPText("-") {{
            setFont(CHARGE_ICON_FONT);
            setTextPaint( Color.BLUE );
            setShadowColor( Color.BLACK );
            setOffset(CHARGE_ICON_SIZE.getWidth() / 2 - getFullBoundsReference().getWidth() / 2,
                    CHARGE_ICON_SIZE.getHeight() / 2 - getFullBoundsReference().getHeight() / 2 );
        }};

        /**
         * Constructor.
         */
        public NegativeChargeIconNode() {
            Rectangle2D backgroundRect = new Rectangle2D.Double(0, 0, CHARGE_ICON_SIZE.getWidth(),
                    CHARGE_ICON_SIZE.getHeight());
            addChild(new PhetPPath(backgroundRect, BACKGROUND_COLOR, new BasicStroke(1), Color.BLACK));
            addChild( label );
        }
    }
}
