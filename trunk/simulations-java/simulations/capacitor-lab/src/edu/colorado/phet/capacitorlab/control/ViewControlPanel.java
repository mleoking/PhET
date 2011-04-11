// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;

/**
 * Control panel for various "View" settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends PhetTitledPanel {

    public ViewControlPanel( Property<Boolean> plateChargeVisible, Property<Boolean> eFieldVisible ) {
        super( CLStrings.VIEW );

        // check boxes
        JCheckBox plateChargesCheckBox = new PropertyCheckBox( CLStrings.PLATE_CHARGES, plateChargeVisible );
        JCheckBox electricFieldLinesCheckBox = new PropertyCheckBox( CLStrings.ELECTRIC_FIELD_LINES, eFieldVisible );

        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( plateChargesCheckBox );
        innerPanel.add( electricFieldLinesCheckBox );

        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
    }
}
