/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;

/**
 * Control panel for various "View" settings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends PhetTitledPanel {
    
    private final JCheckBox plateChargesCheckBox, electricFieldLinesCheckBox;

    public ViewControlPanel() {
        super( CLStrings.TITLE_VIEW );
        
        plateChargesCheckBox = new JCheckBox( CLStrings.CHECKBOX_PLATE_CHARGES );
        
        electricFieldLinesCheckBox = new JCheckBox( CLStrings.CHECKBOX_ELECTRIC_FIELD_LINES );
        
        // layout
        GridPanel innerPanel = new GridPanel();
        innerPanel.setAnchor( Anchor.WEST );
        innerPanel.setGridX( 0 ); // one column
        innerPanel.add( plateChargesCheckBox );
        innerPanel.add( electricFieldLinesCheckBox );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        //XXX
    }
}
