// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.Color;
import java.awt.GridBagConstraints;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.view.BalancedRepresentation;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Panel of radio buttons for selecting the visual representation for "balanced".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancedRepresentationChoiceNode extends PhetPNode {

    /**
     * Constructor
     *
     * @param balanceChoiceProperty the property that denotes the current representation selection
     * @param backgroundProperty color of the background
     */
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
                    SwingUtils.setBackgroundDeep( BalanceChoicePanel.this, backgroundProperty.get() );
                }
            } );
        }
    }
}
