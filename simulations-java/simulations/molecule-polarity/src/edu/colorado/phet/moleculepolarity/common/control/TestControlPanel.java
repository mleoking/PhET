// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.control;

import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.moleculepolarity.MPStrings;

/**
 * "Test" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestControlPanel extends MPControlPanel {

    public TestControlPanel( final Property<Boolean> eFieldEnabled ) {
        super( MPStrings.TEST );
        add( new GridPanel() {{
            setGridY( 0 ); // horizontal
            setAnchor( Anchor.WEST ); // left justified
            add( new JLabel( MessageFormat.format( MPStrings.PATTERN_0LABEL, MPStrings.ELECTRIC_FIELD ) ) );
            add( new PropertyRadioButton<Boolean>( MPStrings.ON, eFieldEnabled, true ) );
            add( new PropertyRadioButton<Boolean>( MPStrings.OFF, eFieldEnabled, false ) );
        }} );
    }
}
