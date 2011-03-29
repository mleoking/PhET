// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.view.CapacitorNode;
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

    public ViewControlPanel( final CapacitorNode capacitorNode ) {
        super( CLStrings.VIEW );

        // check boxes
        JCheckBox plateChargesCheckBox = new PropertyCheckBox( CLStrings.PLATE_CHARGES, capacitorNode.getPlateChargeVisibileProperty() );
        JCheckBox electricFieldLinesCheckBox = new PropertyCheckBox( CLStrings.ELECTRIC_FIELD_LINES, capacitorNode.getEFieldVisibleProperty() );

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
