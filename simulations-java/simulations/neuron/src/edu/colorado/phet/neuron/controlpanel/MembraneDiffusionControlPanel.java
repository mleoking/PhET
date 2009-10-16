/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannelTypes;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.colorado.phet.neuron.model.PotassiumIon;
import edu.colorado.phet.neuron.model.SodiumIon;
import edu.colorado.phet.neuron.view.NeuronCanvas;
import edu.colorado.phet.neuron.view.ParticleNode;

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
	private AxonModel axonModel;
	private NeuronCanvas neuronCanvas;
	private LeakChannelSlider sodiumLeakChannelControl;
	private LeakChannelSlider potassiumLeakChannelControl;
	private ConcentrationSlider sodiumConcentrationControl;
	private ConcentrationSlider potassiumConcentrationControl;
	private ZoomSlider zoomSlider;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public MembraneDiffusionControlPanel( PiccoloModule module, Frame parentFrame, AxonModel model, NeuronCanvas canvas ) {
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
        		new ArrayList<ParticleType>(Arrays.asList(ParticleType.SODIUM_ION, ParticleType.POTASSIUM_ION))));
        
        // Add the control for the number of sodium leakage channels.
        sodiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.SODIUM_LEAK_CHANNELS, axonModel,
        		ParticleType.SODIUM_ION); 
        addControlFullWidth(sodiumLeakChannelControl);
        
        // Add the control for the number of potassium leakage channels.
        potassiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.POTASSIUM_LEAK_CHANNELS, axonModel, 
        		ParticleType.POTASSIUM_ION); 
        addControlFullWidth(potassiumLeakChannelControl);
        
        // Add the control for sodium concentration.
        sodiumConcentrationControl = new ConcentrationSlider(NeuronStrings.SODIUM_CONCENTRATION, axonModel,
        		ParticleType.SODIUM_ION);
        addControlFullWidth(sodiumConcentrationControl);
        
        // Add the control for potassium concentration.
        potassiumConcentrationControl = new ConcentrationSlider(NeuronStrings.POTASSIUM_CONCENTRATION, axonModel, 
        		ParticleType.POTASSIUM_ION);
        addControlFullWidth(potassiumConcentrationControl);
        
        // Add the check box for hiding/showing the voltmeter.
        addControlFullWidth(createVerticalSpacingPanel(30));
        final JCheckBox chartControlCheckbox = new JCheckBox(NeuronStrings.SHOW_VOLTMETER);
        chartControlCheckbox.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				neuronCanvas.setVoltmeterVisible(chartControlCheckbox.isSelected());
			}
		});
        chartControlCheckbox.setAlignmentX(CENTER_ALIGNMENT);
        addControlFullWidth(chartControlCheckbox);
        
        // Add the zoom slider.
        zoomSlider = new ZoomSlider("Zoom Control", neuronCanvas);
        addControlFullWidth(zoomSlider);
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(30));
        addResetAllButton( module );
        
        // Update the states of the controls.
        updateChannelControlSliders();
        updateConcentrationControlSliders();
        updateZoomSlider();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private void updateChannelControlSliders(){
    	
    	if (sodiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL)){
    		
    		sodiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL));
    	}
    	if (potassiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL)){
    		
    		potassiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL));
    	}
    }
    
    private void updateConcentrationControlSliders(){
    	
    	if (sodiumConcentrationControl.getValue() != axonModel.getProportionOfParticlesInside(ParticleType.SODIUM_ION)){
    		sodiumConcentrationControl.setValue( axonModel.getProportionOfParticlesInside(ParticleType.SODIUM_ION));
    	}
    	if (potassiumConcentrationControl.getValue() != axonModel.getProportionOfParticlesInside(ParticleType.POTASSIUM_ION)){
    		potassiumConcentrationControl.setValue( axonModel.getProportionOfParticlesInside(ParticleType.POTASSIUM_ION));
    	}
    }
    
    private void updateZoomSlider(){
    	
    	if ( zoomSlider.getValue() != neuronCanvas.getCameraScale()){
    		zoomSlider.setValue(neuronCanvas.getCameraScale());
    	}
    }
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    private static class ZoomSlider extends LinearValueControl{
    	
        private static final Font LABEL_FONT = new PhetFont(12);
        private static final double MIN_VAL = 0.5;
        private static final double MAX_VAL = 3;
        
		public ZoomSlider(String title, final NeuronCanvas neuronCanvas) {
            super( MIN_VAL, MAX_VAL, title, "0", "");
            setUpDownArrowDelta( 0.01 );
            setTextFieldVisible(false);
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(false);
            
            // Put in the labels for the left and right bottom portion of the
            // slider.
            Hashtable<Double, JLabel> concentrationSliderLabelTable = new Hashtable<Double, JLabel>();
            JLabel leftLabel = new JLabel("Far");
            leftLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( MAX_VAL ), leftLabel );
            JLabel rightLabel = new JLabel("Close");
            rightLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( MAX_VAL ), rightLabel );
            setTickLabels( concentrationSliderLabelTable );

            // Set up the change listener for this control.
            addChangeListener(new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
            		double value = getValue();
            		if ( value != neuronCanvas.getCameraScale() ){
            			neuronCanvas.setCameraScale(value);
            		}
            	}
            });
		}
    }
}
