/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.colorado.phet.neuron.model.PotassiumGatedChannel;
import edu.colorado.phet.neuron.model.SodiumDualGatedChannel;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for the membrane diffusion module.
 *
 * @author John Blanco
 */
public class MembraneDiffusionControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final ModelViewTransform2D CHANNEL_ICON_MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-4, -4, 8, 8));
    
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private JCheckBox showConcentrationsCheckBox;
	private MembraneDiffusionModel model;
    
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
    		public void concentrationGraphVisibilityChanged() {
    			updateConcentrationsCheckBoxState();
    		}
    	});
    	
    	// Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        addControlFullWidth(new IonsLegendPanel());
        
        // Create the buttons for stimulating the channels.
   		PNode imageNode = new MembraneChannelNode(new SodiumDualGatedChannel(), CHANNEL_ICON_MVT);
   		imageNode.rotate(-Math.PI / 2);
   		ImageIcon icon = new ImageIcon(imageNode.toImage());
        // TODO: i18n
        final JButton activateSodiumChannelsButton = new JButton("Open Sodium Gates", icon);
        activateSodiumChannelsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.forceActivationOfSodiumChannels();
			}
		});
        
   		imageNode = new MembraneChannelNode(new PotassiumGatedChannel(), CHANNEL_ICON_MVT);
   		imageNode.rotate(-Math.PI / 2);
   		icon = new ImageIcon(imageNode.toImage());
        // TODO: i18n
        final JButton activatePotassiumChannelsButton = new JButton("Open Potassium Gates", icon);
        activatePotassiumChannelsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.forceActivationOfPotassiumChannels();
			}
		});
        
        // Make the buttons the same size by placing them on a panel and by
        // setting the preferred height to be equal.
        Dimension buttonPreferredSize = new Dimension(
        		Math.max(activateSodiumChannelsButton.getPreferredSize().width, activatePotassiumChannelsButton.getPreferredSize().width),
        		Math.max(activateSodiumChannelsButton.getPreferredSize().height, activatePotassiumChannelsButton.getPreferredSize().height));
        JPanel buttonPanel = new VerticalLayoutPanel();
        activateSodiumChannelsButton.setPreferredSize(buttonPreferredSize);
        activatePotassiumChannelsButton.setPreferredSize(buttonPreferredSize);
        buttonPanel.add(activateSodiumChannelsButton);
        buttonPanel.add(createVerticalSpacingPanel(5));
        buttonPanel.add(activatePotassiumChannelsButton);

        // Add the button panel to the control panel.
        addControlFullWidth(createVerticalSpacingPanel(20));
        addControl(buttonPanel);
        
        // Add the check box for hiding/showing the concentration graphs.  It
        // is in its own panel so that it can be centered.
        addControlFullWidth(createVerticalSpacingPanel(30));
        JPanel checkBoxPanel = new JPanel();
        // TODO: i18n
        showConcentrationsCheckBox = new JCheckBox("Show Concentrations");
        showConcentrationsCheckBox.setFont(NeuronConstants.CONTROL_PANEL_CONTROL_FONT);
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
    
    private void updateConcentrationsCheckBoxState(){
    	showConcentrationsCheckBox.setSelected(model.isConcentrationGraphsVisible());
    }
}
