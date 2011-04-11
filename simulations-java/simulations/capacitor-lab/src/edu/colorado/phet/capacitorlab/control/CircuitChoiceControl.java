// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.PreconfiguredCircuitChoices;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;

/**
 * Control panel for selecting one of the pre-configured circuits.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CircuitChoiceControl extends PhetTitledPanel {

    public CircuitChoiceControl( Property<PreconfiguredCircuitChoices> circuitChoice ) {
        super( CLStrings.CIRCUITS );

        JRadioButton b1 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.SINGLE, circuitChoice, PreconfiguredCircuitChoices.SINGLE );
        JRadioButton b2 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.TWO_IN_SERIES, circuitChoice, PreconfiguredCircuitChoices.TWO_IN_SERIES );
        JRadioButton b3 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.THREE_IN_SERIES, circuitChoice, PreconfiguredCircuitChoices.THREE_IN_SERIES );
        JRadioButton b4 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.TWO_IN_PARALLEL, circuitChoice, PreconfiguredCircuitChoices.TWO_IN_PARALLEL );
        JRadioButton b5 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.THREE_IN_PARALLEL, circuitChoice, PreconfiguredCircuitChoices.THREE_IN_PARALLEL );
        JRadioButton b6 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.COMBINATION_1, circuitChoice, PreconfiguredCircuitChoices.COMBINATION_1 );
        JRadioButton b7 = new PropertyRadioButton<PreconfiguredCircuitChoices>( CLStrings.COMBINATION_2, circuitChoice, PreconfiguredCircuitChoices.COMBINATION_2 );

        ButtonGroup group = new ButtonGroup();
        group.add( b1 );
        group.add( b2 );
        group.add( b3 );
        group.add( b4 );
        group.add( b5 );
        group.add( b6 );
        group.add( b7 );

        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( GridPanel.Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( b1 );
        innerPanel.add( b2 );
        innerPanel.add( b3 );
        innerPanel.add( b4 );
        innerPanel.add( b5 );
        innerPanel.add( b6 );
        innerPanel.add( b7 );

        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
