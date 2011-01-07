// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.controlpanel;

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
import edu.colorado.phet.membranechannels.MembraneChannelsConstants;
import edu.colorado.phet.membranechannels.MembraneChannelsResources;
import edu.colorado.phet.membranechannels.MembraneChannelsStrings;
import edu.colorado.phet.membranechannels.model.MembraneChannel;
import edu.colorado.phet.membranechannels.model.MembraneChannelsModel;
import edu.colorado.phet.membranechannels.model.Particle;

/**
 * Control panel for the membrane channels module.
 *
 * @author John Blanco
 */
public class MembraneChannelsControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
    
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private JCheckBox showConcentrationsCheckBox;
	private MembraneChannelsModel model;

    private JButton sodiumGatedChannelControlButton;
    private JButton potassiumGatedChannelControlButton;
    private JButton clearParticlesButton;
    
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public MembraneChannelsControlPanel( PiccoloModule module, final MembraneChannelsModel model ) {

    	this.model = model;
    	
    	// Listen to the model for events that interest this class.
    	model.addListener(new MembraneChannelsModel.Adapter(){
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

            @Override
            public void channelAdded( MembraneChannel channel ) {
                updateMembraneChannelControlButtons();
                channel.addListener( new MembraneChannelRemovalListener( channel, MembraneChannelsControlPanel.this ) );
            }

            @Override
            public void particleAdded( Particle particle ) {
                updateClearParticlesButton();
                particle.addListener( new ParticleRemovalListener( particle, MembraneChannelsControlPanel.this ) );
            }
    	});
    	
    	// Set the control panel's minimum width.
        int minimumWidth = MembraneChannelsResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Put some space at the top of the panel.
        addControlFullWidth(createVerticalSpacingPanel(20));

        // Define the size of the buttons.  This is based on the width of the
        // control panel and a height value that just seemed to look
        // reasonable.
        Dimension buttonSize = new Dimension((int)Math.round( minimumWidth * 0.9 ), 40);
        
        // Create the buttons for stimulating the channels.
        sodiumGatedChannelControlButton = new JButton();
        sodiumGatedChannelControlButton.setPreferredSize( buttonSize );
        sodiumGatedChannelControlButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    model.setGatedSodiumChannelsOpen( model.getGatedSodiumChannelOpenness() < 0.5 );
			}
		});
        
        potassiumGatedChannelControlButton = new JButton();
        potassiumGatedChannelControlButton.setPreferredSize( buttonSize );
        potassiumGatedChannelControlButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    model.setGatedPotassiumChannelsOpen( model.getGatedPotassiumChannelOpenness() < 0.5 );
			}
		});
        
        updateMembraneChannelControlButtons();
        
        JPanel buttonPanel = new VerticalLayoutPanel();
        buttonPanel.add(sodiumGatedChannelControlButton);
        buttonPanel.add(createVerticalSpacingPanel(15));
        buttonPanel.add(potassiumGatedChannelControlButton);

        // Add the button panel to the control panel.
        addControlFullWidth(createVerticalSpacingPanel(15));
        addControl(buttonPanel);
        
        // Add a button for removing all particles.
        addControl(createVerticalSpacingPanel(15));
        JPanel clearButtonPanel = new JPanel();
        clearParticlesButton = new JButton(MembraneChannelsStrings.CLEAR_PARTICLES);
        clearParticlesButton.setPreferredSize( buttonSize );
        clearParticlesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.removeAllParticles();
            }
        });
        clearButtonPanel.add( clearParticlesButton );
        addControlFullWidth(clearButtonPanel);
        updateClearParticlesButton();
        
        // Add the check box for hiding/showing the concentration graphs.  It
        // is in its own panel so that it can be centered.
        addControlFullWidth(createVerticalSpacingPanel(15));
        JPanel checkBoxPanel = new JPanel();
        showConcentrationsCheckBox = new JCheckBox(MembraneChannelsStrings.SHOW_CONCENTRATIONS);
        showConcentrationsCheckBox.setFont(MembraneChannelsConstants.CONTROL_PANEL_CONTROL_FONT);
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
        addControlFullWidth(createVerticalSpacingPanel(70));
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
        
        // Update the enabled/disabled state.
        sodiumGatedChannelControlButton.setEnabled( model.getNumGatedSodiumChannels() > 0 );
        potassiumGatedChannelControlButton.setEnabled( model.getNumGatedPotassiumChannels() > 0 );

        // Update the icon and the text.
        if (model.getGatedSodiumChannelOpenness() > 0.5){
            sodiumGatedChannelControlButton.setText( MembraneChannelsStrings.CLOSE_CHANNELS );
            sodiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneChannelsResources.getImage( "green_gate_close_icon.png" ) ) );
        }
        else{
            sodiumGatedChannelControlButton.setText( MembraneChannelsStrings.OPEN_CHANNELS );
            sodiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneChannelsResources.getImage( "green_gate_open_icon.png" ) ) );
        }
        
        if (model.getGatedPotassiumChannelOpenness() > 0.5){
            potassiumGatedChannelControlButton.setText( MembraneChannelsStrings.CLOSE_CHANNELS );
            potassiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneChannelsResources.getImage( "blue_gate_close_icon.png" ) ) );
        }
        else{
            potassiumGatedChannelControlButton.setText( MembraneChannelsStrings.OPEN_CHANNELS );
            potassiumGatedChannelControlButton.setIcon( new ImageIcon( MembraneChannelsResources.getImage( "blue_gate_open_icon.png" ) ) );
        }
    }
    
    private void updateClearParticlesButton(){
        clearParticlesButton.setEnabled( model.getParticles().size() > 0 );
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    /**
     * Class that listens for removal of a membrane channel, updates the
     * control panel, and removes itself as a listener.
     */
    private static class MembraneChannelRemovalListener extends MembraneChannel.Adapter{
        
        final private MembraneChannel membraneChannel;
        final private MembraneChannelsControlPanel membraneChannelsControlPanel;

        public MembraneChannelRemovalListener( MembraneChannel membraneChannel,
                MembraneChannelsControlPanel membraneChannelsControlPanel ) {
            this.membraneChannel = membraneChannel;
            this.membraneChannelsControlPanel = membraneChannelsControlPanel;
        }

        @Override
        public void removed() {
            membraneChannelsControlPanel.updateMembraneChannelControlButtons();
            membraneChannel.removeListener( this );
        }
    }
    
    /**
     * Class that listens for removal of a particle from the model and updates
     * the control panel appropriately.
     */
    private static class ParticleRemovalListener extends Particle.Adapter{
        
        final private Particle particle;
        final private MembraneChannelsControlPanel membraneChannelsControlPanel;

        public ParticleRemovalListener( Particle particle, MembraneChannelsControlPanel membraneChannelsControlPanel ) {
            this.particle = particle;
            this.membraneChannelsControlPanel = membraneChannelsControlPanel;
        }

        @Override
        public void removedFromModel() {
            particle.removeListener( this );
            membraneChannelsControlPanel.updateClearParticlesButton();
        }
    }
}
