// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.ICircuit;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;

/**
 * Control panel for selecting a circuit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CircuitChoiceControl extends PhetTitledPanel {

    public CircuitChoiceControl( ArrayList<ICircuit> circuits, Property<ICircuit> currentCircuitProperty ) {
        super( CLStrings.CIRCUITS );

        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( GridPanel.Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column

        // add a radio button for each circuit choice
        for ( ICircuit circuit : circuits ) {
            innerPanel.add( new PropertyRadioButton<ICircuit>( circuit.getDisplayName(), currentCircuitProperty, circuit ) );
        }

        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
