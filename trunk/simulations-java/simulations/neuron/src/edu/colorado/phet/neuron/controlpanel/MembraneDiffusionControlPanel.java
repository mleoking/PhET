/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.NeuronStrings;

/**
 * Control panel for the membrane diffusion module.
 *
 * @author John Blanco
 */
public class MembraneDiffusionControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private JCheckBox showConcentrationsCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public MembraneDiffusionControlPanel( PiccoloModule module, Frame parentFrame ) {

    	// Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        addControlFullWidth(new IonsLegendPanel());
        
        // Add the buttons for stimulating the channels.
        // TODO: i18n
        final JButton activateSodiumChannelsButton = new JButton("Open Sodium Gates");
        activateSodiumChannelsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: TBD
			}
		});
        addControlFullWidth(createVerticalSpacingPanel(10));
        addControl(activateSodiumChannelsButton);
        
        // TODO: i18n
        final JButton activatePotassiumChannelsButton = new JButton("Open Potassium Gates");
        activatePotassiumChannelsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: TBD
			}
		});
        addControlFullWidth(createVerticalSpacingPanel(10));
        addControl(activatePotassiumChannelsButton);

        // Add the check box for hiding/showing the concentration graphs.  It
        // is in its own panel so that it can be centered.
        addControlFullWidth(createVerticalSpacingPanel(30));
        // TODO: i18n
        JPanel checkBoxPanel = new JPanel();
        showConcentrationsCheckBox = new JCheckBox("Show Concentrations");
        showConcentrationsCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// TODO: TBD
			}
		});
        showConcentrationsCheckBox.setAlignmentX(CENTER_ALIGNMENT);
        checkBoxPanel.add(showConcentrationsCheckBox);
        addControlFullWidth(checkBoxPanel);
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(60));
        addResetAllButton( module );
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
}
