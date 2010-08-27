/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.controlpanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;
import edu.colorado.phet.membranediffusion.MembraneDiffusionResources;
import edu.colorado.phet.membranediffusion.MembraneDiffusionStrings;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;

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
	private MembraneDiffusionModel model;

    private JButton sodiumGatedChannelControlButton;
    private JButton potassiumGatedChannelControlButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public MembraneDiffusionControlPanel( PiccoloModule module, final MembraneDiffusionModel model ) {

    	this.model = model;
    	
    	// Listen to the model for events that interest this class.
    	model.addListener(new MembraneDiffusionModel.Adapter(){
    	    @Override
    		public void concentrationGraphVisibilityChanged() {
    			updateConcentrationsCheckBoxState();
    		}

            @Override
            public void sodiumGateOpennessChanged() {
                updateMembraneChannelControlButtons();
            }

            @Override
            public void potassiumGateOpennessChanged() {
                updateMembraneChannelControlButtons();
            }
    	});
    	
    	// Set the control panel's minimum width.
        int minimumWidth = MembraneDiffusionResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Put some space at the top of the panel.
        addControlFullWidth(createVerticalSpacingPanel(20));

        // Create the buttons for stimulating the channels.
        sodiumGatedChannelControlButton = new JButton();
        sodiumGatedChannelControlButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    model.setGatedSodiumChannelsOpen( model.getGatedSodiumChannelOpenness() < 0.5 );
			}
		});
        
        potassiumGatedChannelControlButton = new JButton();
        potassiumGatedChannelControlButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    model.setGatedPotassiumChannelsOpen( model.getGatedPotassiumChannelOpenness() < 0.5 );
			}
		});
        
        updateMembraneChannelControlButtons();
        
        // Make the buttons the same size by placing them on a panel and by
        // setting the preferred height to be equal.
        Dimension buttonPreferredSize = new Dimension(
        		Math.max(sodiumGatedChannelControlButton.getPreferredSize().width, potassiumGatedChannelControlButton.getPreferredSize().width),
        		Math.max(sodiumGatedChannelControlButton.getPreferredSize().height, potassiumGatedChannelControlButton.getPreferredSize().height));
        JPanel buttonPanel = new VerticalLayoutPanel();
        sodiumGatedChannelControlButton.setPreferredSize(buttonPreferredSize);
        potassiumGatedChannelControlButton.setPreferredSize(buttonPreferredSize);
        buttonPanel.add(sodiumGatedChannelControlButton);
        buttonPanel.add(createVerticalSpacingPanel(15));
        buttonPanel.add(potassiumGatedChannelControlButton);

        // Add the button panel to the control panel.
        addControlFullWidth(createVerticalSpacingPanel(15));
        addControl(buttonPanel);
        
        // Add a button for removing all particles.
        addControl(createVerticalSpacingPanel(15));
        JPanel clearButtonPanel = new JPanel();
        // TODO: i18n
        JButton removeAllParticlesButton = new JButton("Clear Particles");
        removeAllParticlesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.removeAllParticles();
            }
        });
        clearButtonPanel.add( removeAllParticlesButton );
        addControlFullWidth(clearButtonPanel);
        
        // Add the check box for hiding/showing the concentration graphs.  It
        // is in its own panel so that it can be centered.
        addControlFullWidth(createVerticalSpacingPanel(15));
        JPanel checkBoxPanel = new JPanel();
        // TODO: i18n
        showConcentrationsCheckBox = new JCheckBox("Show Concentrations");
        showConcentrationsCheckBox.setFont(MembraneDiffusionConstants.CONTROL_PANEL_CONTROL_FONT);
        showConcentrationsCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.setConcentrationGraphsVisible(showConcentrationsCheckBox.isSelected());
			}
		});
        showConcentrationsCheckBox.setAlignmentX(CENTER_ALIGNMENT);
        checkBoxPanel.add(showConcentrationsCheckBox);
        addControlFullWidth(checkBoxPanel);
        updateConcentrationsCheckBoxState();
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(50));
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
    
    private void updateConcentrationsCheckBoxState(){
    	showConcentrationsCheckBox.setSelected(model.isConcentrationGraphsVisible());
    }
    
    private void updateMembraneChannelControlButtons(){

        if (model.getGatedSodiumChannelOpenness() > 0.5){
            // TODO: i18n
            sodiumGatedChannelControlButton.setText( MembraneDiffusionStrings.CLOSE_CHANNELS );
            sodiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneDiffusionResources.getImage( "red_gate_close_icon.png" ) ) );
        }
        else{
            // TODO: i18n
            sodiumGatedChannelControlButton.setText( MembraneDiffusionStrings.OPEN_CHANNELS );
            sodiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneDiffusionResources.getImage( "red_gate_open_icon.png" ) ) );
        }
        
        if (model.getGatedPotassiumChannelOpenness() > 0.5){
            // TODO: i18n
            potassiumGatedChannelControlButton.setText( MembraneDiffusionStrings.CLOSE_CHANNELS );
            potassiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneDiffusionResources.getImage( "blue_gate_close_icon.png" ) ) );
        }
        else{
            // TODO: i18n
            potassiumGatedChannelControlButton.setText( MembraneDiffusionStrings.OPEN_CHANNELS );
            potassiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneDiffusionResources.getImage( "blue_gate_open_icon.png" ) ) );
        }
    }
}
