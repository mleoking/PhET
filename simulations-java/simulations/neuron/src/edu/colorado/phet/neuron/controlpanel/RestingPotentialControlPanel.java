/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannelTypes;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * Control panel for the membrane diffusion module.
 *
 * @author John Blanco
 */
public class RestingPotentialControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private AxonModel axonModel;
	private NeuronCanvas neuronCanvas;
	private LeakChannelSlider potassiumLeakChannelControl;
	private ConcentrationSlider potassiumConcentrationControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public RestingPotentialControlPanel( PiccoloModule module, Frame parentFrame, AxonModel model, NeuronCanvas canvas ) {
        super();
        
        this.axonModel = model;
        this.neuronCanvas = canvas;
        
        // Listen to the model for changes that affect this control panel.
        model.addListener(new AxonModel.Adapter(){
        	@Override
    		public void channelAdded(AbstractMembraneChannel channel) {
    			updateChannelControlSliders();
    		}
        	
        	@Override
			public void channelRemoved(AbstractMembraneChannel channel) {
        		updateChannelControlSliders();
			}

			@Override
    		public void concentrationRatioChanged(ParticleType atomType) {
    			updateConcentrationControlSliders();
    		}
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the legend.
        addControlFullWidth(new IonLegendPanel(
        		new ArrayList<ParticleType>(Arrays.asList(ParticleType.POTASSIUM_ION, ParticleType.PROTEIN_ION))));
        
        // Add the control for the number of potassium leakage channels.
        potassiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.POTASSIUM_LEAK_CHANNELS, axonModel, 
        		ParticleType.POTASSIUM_ION); 
        addControlFullWidth(potassiumLeakChannelControl);
        
        // Add the control for potassium concentration.
        potassiumConcentrationControl = new ConcentrationSlider(NeuronStrings.POTASSIUM_CONCENTRATION, axonModel, 
        		ParticleType.POTASSIUM_ION);
        addControlFullWidth(potassiumConcentrationControl);
        
        // Add the check box for hiding/showing the membrane potential chart.
        addControlFullWidth(createVerticalSpacingPanel(30));
        final JCheckBox chartControlCheckbox = new JCheckBox(NeuronStrings.POTENTIAL_CHART);
        chartControlCheckbox.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				neuronCanvas.setMembranePotentialChartVisible(chartControlCheckbox.isSelected());
			}
		});
        chartControlCheckbox.setAlignmentX(CENTER_ALIGNMENT);
        addControlFullWidth(chartControlCheckbox);
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(30));
        addResetAllButton( module );
        
        // Update the states of the controls.
        updateChannelControlSliders();
        updateConcentrationControlSliders();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private void updateChannelControlSliders(){
    	
    	if (potassiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL)){
    		
    		potassiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL));
    	}
    }
    
    private void updateConcentrationControlSliders(){
    	
    	if (potassiumConcentrationControl.getValue() != axonModel.getProportionOfParticlesInside(ParticleType.POTASSIUM_ION)){
    		potassiumConcentrationControl.setValue( axonModel.getProportionOfParticlesInside(ParticleType.POTASSIUM_ION));
    	}
    }
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
}
