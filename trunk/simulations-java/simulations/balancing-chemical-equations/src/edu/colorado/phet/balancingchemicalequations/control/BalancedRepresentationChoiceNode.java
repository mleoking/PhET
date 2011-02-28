// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.Color;
import java.awt.GridBagConstraints;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Panel of radio buttons for selecting the visual representation for "balanced".
 * "Balanced" is represented as either a bar chart, or a balance scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancedRepresentationChoiceNode extends PhetPNode {

    public static enum BalancedRepresentation { NONE, BALANCE_SCALES, BAR_CHARTS };

    public BalancedRepresentationChoiceNode( Property<BalancedRepresentation> balanceChoiceProperty, Property<Color> backgroundProperty ) {
        addChild( new PSwing( new BalanceChoicePanel( balanceChoiceProperty, backgroundProperty ) ) );
        scale( BCEConstants.SWING_SCALE ); // scale this control, setting font size for radio buttons doesn't increase button size on Mac
    }

    /*
     * Swing component.
     */
    private static class BalanceChoicePanel extends GridPanel {
        public BalanceChoicePanel( Property<BalancedRepresentation> balanceChoiceProperty, final Property<Color> backgroundProperty ) {
            setAnchor( Anchor.WEST );
            setGridX( GridBagConstraints.RELATIVE ); // horizontal layout
            setGridY( 0 ); // horizontal layout

            add( new PropertyRadioButton<BalancedRepresentation>( BCEStrings.NONE, balanceChoiceProperty, BalancedRepresentation.NONE ) );
            add( new PropertyRadioButton<BalancedRepresentation>( BCEStrings.BALANCE_SCALES, balanceChoiceProperty, BalancedRepresentation.BALANCE_SCALES ) );
            add( new PropertyRadioButton<BalancedRepresentation>( BCEStrings.BAR_CHARTS, balanceChoiceProperty, BalancedRepresentation.BAR_CHARTS ) );

            // #2710 workaround, must do this after adding all components!
            backgroundProperty.addObserver( new SimpleObserver() {
                public void update() {
                    SwingUtils.setBackgroundDeep( BalanceChoicePanel.this, backgroundProperty.getValue() );
                }
            } );
        }
    }
}
