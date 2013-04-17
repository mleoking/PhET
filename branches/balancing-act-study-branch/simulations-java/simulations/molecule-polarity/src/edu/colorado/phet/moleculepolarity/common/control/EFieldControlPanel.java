// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPSimSharing;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPRadioButton;
import edu.colorado.phet.moleculepolarity.common.control.MPControlPanelNode.MPVerticalPanel;

/**
 * Control panel for turning the E-field on/off.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldControlPanel extends MPVerticalPanel {

    public EFieldControlPanel( final Property<Boolean> eFieldEnabled ) {
        super( MPStrings.ELECTRIC_FIELD );
        add( new GridPanel() {{
            setGridY( 0 ); // horizontal
            setAnchor( Anchor.WEST ); // left justified
            add( new MPRadioButton<Boolean>( UserComponents.eFieldOnRadioButton, MPStrings.ON, eFieldEnabled, true ) );
            add( new MPRadioButton<Boolean>( UserComponents.eFieldOffRadioButton, MPStrings.OFF, eFieldEnabled, false ) );
        }} );
    }
}
