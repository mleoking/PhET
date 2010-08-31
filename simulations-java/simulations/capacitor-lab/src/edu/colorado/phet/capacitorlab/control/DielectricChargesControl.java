/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls related to the dielectric's charges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricChargesControl extends JPanel {
    
    private final JRadioButton hideAllRadioButton, showAllRadioButton, showExcessRadioButton;

    public DielectricChargesControl() {
        
        JLabel chargesLabel = new JLabel( CLStrings.LABEL_DIELECTRIC_CHARGES );
        
        hideAllRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_HIDE_ALL_CHARGES );
        
        showAllRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_SHOW_ALL_CHARGES );
        
        showExcessRadioButton = new JRadioButton( CLStrings.RADIOBUTTON_SHOW_EXCESS_CHARGES );
        
        ButtonGroup group = new ButtonGroup();
        group.add( hideAllRadioButton );
        group.add( showAllRadioButton );
        group.add( showExcessRadioButton );

        // layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( chargesLabel, row++, column );
        layout.addComponent( hideAllRadioButton, row++, column );
        layout.addComponent( showAllRadioButton, row++, column );
        layout.addComponent( showExcessRadioButton, row++, column );
        
        // make everything left justify when put in the main control panel
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // default state
        hideAllRadioButton.setSelected( true );
    }
}
