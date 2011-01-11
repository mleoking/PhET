// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.control;

import java.awt.GridBagConstraints;
import java.util.ArrayList;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
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
     */
    public EquationChoiceNode( ArrayList<Equation> equations, Property<Equation> currentEquationProperty ) {
        addChild( new PSwing( new EquationChoicePanel( equations, currentEquationProperty ) ) );
    }

    /*
     * Swing component.
     */
    private static class EquationChoicePanel extends GridPanel {
        public EquationChoicePanel( ArrayList<Equation> equations, Property<Equation> currentEquationProperty ) {
            setOpaque( false );
            setAnchor( Anchor.WEST );
            setGridX( GridBagConstraints.RELATIVE ); // horizontal layout
            setGridY( 0 ); // horizontal layout
            for ( Equation equation : equations ) {
                add( new PropertyRadioButton<Equation>( equation.getName(), currentEquationProperty, equation ) );
            }
        }
    }
}
