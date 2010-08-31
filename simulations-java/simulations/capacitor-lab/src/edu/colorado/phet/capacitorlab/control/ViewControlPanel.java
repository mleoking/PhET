/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

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
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( plateChargesCheckBox, row++, column );
        layout.addComponent( electricFieldLinesCheckBox, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        //XXX
    }
}
