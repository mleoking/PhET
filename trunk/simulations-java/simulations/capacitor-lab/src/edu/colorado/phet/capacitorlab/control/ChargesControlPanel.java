/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls related to the capacitor's dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChargesControlPanel extends CLTitledControlPanel {
    
    private final JRadioButton chargesHiddenRadioButton, showAllChargesRadioButton, showExcessChargesRadioButton;

    public ChargesControlPanel() {
        super( CLStrings.TITLE_CHARGES );
        
        chargesHiddenRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_CHARGES_HIDDEN );
        
        showAllChargesRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_SHOW_ALL_CHARGES );
        
        showExcessChargesRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_SHOW_EXCESS_CHARGES );
        
        ButtonGroup group = new ButtonGroup();
        group.add( chargesHiddenRadioButton );
        group.add( showAllChargesRadioButton );
        group.add( showExcessChargesRadioButton );

        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( chargesHiddenRadioButton, row++, column );
        layout.addComponent( showAllChargesRadioButton, row++, column );
        layout.addComponent( showExcessChargesRadioButton, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        chargesHiddenRadioButton.setSelected( true );
    }
}
