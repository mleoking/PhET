// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.BCEConstants;
import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Panel of radio buttons for selecting an equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationChoiceNode extends PhetPNode {

    /**
     * Constructor.
     * @param equations the set of Equation choices
     * @param currentEquationProperty the property that denotes the current equation selection
     * @param backgroundProperty color of the background
     */
    public EquationChoiceNode( ArrayList<Equation> equations, Property<Equation> currentEquationProperty, Property<Color> backgroundProperty ) {
        addChild( new PSwing( new EquationChoicePanel( equations, currentEquationProperty, backgroundProperty ) ) );
        scale( BCEConstants.SWING_SCALE ); // scale this control, setting font size for radio buttons doesn't increase button size on Mac
    }

    /*
     * Swing component.
     */
    private static class EquationChoicePanel extends GridPanel {
        public EquationChoicePanel( ArrayList<Equation> equations, Property<Equation> currentEquationProperty, final Property<Color> backgroundProperty ) {
            setAnchor( Anchor.WEST );
            setGridX( GridBagConstraints.RELATIVE ); // horizontal layout
            setGridY( 0 );

            for ( Equation equation : equations ) {
                add( new PropertyRadioButton<Equation>( HTMLUtils.toHTMLString( equation.getName() ), currentEquationProperty, equation ) );
            }

            // #2710 workaround, must do this after adding all components!
            backgroundProperty.addObserver( new SimpleObserver() {
                public void update() {
                    SwingUtils.setBackgroundDeep( EquationChoicePanel.this, backgroundProperty.get() );
                }
            } );
        }
    }
}
