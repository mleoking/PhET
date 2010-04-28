/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.view.IZoomable;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * Control panel for the axon cross section module.
 *
 * @author John Blanco
 */
public class AxonCrossSectionControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private AxonModel axonModel;
	private NeuronCanvas neuronCanvas;
	
	// TODO: Removed the following sliders based on design mods that were
	// made in December 2009.  Delete permanently if and when finalized.
//	private ConcentrationSlider sodiumConcentrationControl;
//	private ConcentrationSlider potassiumConcentrationControl;
	
	// TODO: March 31 2010 - Removed the zoom slider since one has now been
	// added to the play area.  Remove permanently (search the file for other
	// commented-out code) when this is finalized.
//	private ZoomSlider zoomSlider;
	private JCheckBox showAllIons;
	private JCheckBox chartControlCheckbox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public AxonCrossSectionControlPanel( PiccoloModule module, Frame parentFrame, final AxonModel model, NeuronCanvas canvas ) {
        super();
        
        this.axonModel = model;
        this.neuronCanvas = canvas;

        // Listen to the canvas for changes that affect this control panel.
//        neuronCanvas.addZoomListener(new ZoomListener() {
//			public void zoomFactorChanged() {
//				updateZoomSlider();
//			}
//		});
        
        // Listen to the model for changes that affect this control panel.
        model.addListener(new AxonModel.Adapter(){
        	@Override
    		public void potentialChartVisibilityChanged() {
    			updateChartVisibilityCheckBox();
    		}

        	@Override
    		public void allIonsSimulatedChanged() {
    			updateShowAllIonsCheckBox();
    		}

        	@Override
    		public void stimulationLockoutStateChanged() {
        		// When stimulation is locked out, we also lock out the
        		// ability to change the "All Ions Simulated" state, since
        		// otherwise ions would have to disappear during an action potential,
        		// which would be tricky.
        		showAllIons.setEnabled(!model.isStimulusInitiationLockedOut());
        	}
        	
            /*
             * TODO: Decoupled sliders from the model on Feb 3 2009 because it
             * has been decided that they should be opened/closed, not added and
             * removed.  This code should be removed or revised based on what
             * the design ends up as.
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
         */
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the legend.
        addControlFullWidth(new IonsAndChannelsLegendPanel());
        
        /*
         * TODO: Feb 19 2010 - There have been some radical changes made, and
         * we are now thinking that the user should not be able to add and
         * remove gates, so these sliders are being commented out.  They
         * should be removed permanently when the design is a bit more solid.
         *  
        // Add the control for the number of sodium leakage channels.
        sodiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.SODIUM_LEAK_CHANNELS, axonModel,
        		ParticleType.SODIUM_ION); 
        addControlFullWidth(sodiumLeakChannelControl);
        
        // Add the control for the number of potassium leakage channels.
        potassiumLeakChannelControl = new LeakChannelSlider(NeuronStrings.POTASSIUM_LEAK_CHANNELS, axonModel, 
        		ParticleType.POTASSIUM_ION); 
        addControlFullWidth(potassiumLeakChannelControl);
         */
        
        /*
         * See TODO at top of this file for information about why the
         * the following is commented out.
        // Add the control for sodium concentration.
        sodiumConcentrationControl = new ConcentrationSlider(NeuronStrings.SODIUM_CONCENTRATION, axonModel,
        		ParticleType.SODIUM_ION);
        addControlFullWidth(sodiumConcentrationControl);
        
        // Add the control for potassium concentration.
        potassiumConcentrationControl = new ConcentrationSlider(NeuronStrings.POTASSIUM_CONCENTRATION, axonModel, 
        		ParticleType.POTASSIUM_ION);
        addControlFullWidth(potassiumConcentrationControl);
         */
        
        // Add the zoom slider.
//        zoomSlider = new ZoomSlider("Zoom Control", neuronCanvas);
//        addControlFullWidth(zoomSlider);
        
        JPanel checkBoxPanel = new VerticalLayoutPanel();
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NeuronStrings.CONTROL,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        checkBoxPanel.setBorder(titledBorder);

        // Add the check box for hiding/showing all ions.
        showAllIons = new JCheckBox(NeuronStrings.SHOW_ALL_IONS);
        showAllIons.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				axonModel.setAllIonsSimulated(showAllIons.isSelected());
			}
		});
        showAllIons.setAlignmentX(CENTER_ALIGNMENT);
        checkBoxPanel.add(showAllIons);
        updateShowAllIonsCheckBox();
        
        // Add the check box for hiding/showing the potential chart.
        addControlFullWidth(createVerticalSpacingPanel(5));
        chartControlCheckbox = new JCheckBox(NeuronStrings.SHOW_POTENTIAL_CHART);
//        chartControlCheckbox.addChangeListener(new ChangeListener() {
//			
//			public void stateChanged(ChangeEvent e) {
//				axonModel.setPotentialChartVisible(chartControlCheckbox.isSelected());
//			}
//		});
        chartControlCheckbox.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				axonModel.setPotentialChartVisible(chartControlCheckbox.isSelected());
				
			}
		});
        chartControlCheckbox.setAlignmentX(CENTER_ALIGNMENT);
        checkBoxPanel.add(chartControlCheckbox);
        addControlFullWidth(checkBoxPanel);
        updateChartVisibilityCheckBox();
        
        /*
         * TODO: Feb 19, 2010 - Got rid of this and put a button on the canvas
         * instead.  This should be removed permanently if an when it is
         * decided that the canvas button does the trick.
        // Add a button for sending a stimulus to the neuron.
        
        addControlFullWidth(createVerticalSpacingPanel(30));
        final JButton stimulusButton = new JButton(NeuronStrings.STIMULUS_PULSE);
        stimulusButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				model.initiateStimulusPulse();
			}
		});
        addControl(stimulusButton);
         */
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(30));
        addResetAllButton( module );
        
        // Update the states of the controls.
//        updateChannelControlSliders();
//        updateConcentrationControlSliders();
//        updateZoomSlider();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    /*
    // See TODO at top of this file for information about why the
    // the following is commented out.
    private void updateConcentrationControlSliders(){
    	
    	if (sodiumConcentrationControl.getValue() != axonModel.getProportionOfParticlesInside(ParticleType.SODIUM_ION)){
    		sodiumConcentrationControl.setValue( axonModel.getProportionOfParticlesInside(ParticleType.SODIUM_ION));
    	}
    	if (potassiumConcentrationControl.getValue() != axonModel.getProportionOfParticlesInside(ParticleType.POTASSIUM_ION)){
    		potassiumConcentrationControl.setValue( axonModel.getProportionOfParticlesInside(ParticleType.POTASSIUM_ION));
    	}
    }
    */
    
//    private void updateZoomSlider(){
//    	if ( zoomSlider.getValue() != neuronCanvas.getZoomFactor()){
//    		zoomSlider.setValue(neuronCanvas.getZoomFactor());
//    	}
//    }
    
    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    private void updateChartVisibilityCheckBox(){
		chartControlCheckbox.setSelected(axonModel.isPotentialChartVisible());
    }
    
    private void updateShowAllIonsCheckBox(){
		showAllIons.setSelected(axonModel.isAllIonsSimulated());
    }
    
    private static class ZoomSlider extends LinearValueControl{
    	
        private static final Font LABEL_FONT = new PhetFont(12);
        private static final double MIN_VAL = 0.6;
        private static final double MAX_VAL = 7;
        
		public ZoomSlider(String title, final IZoomable neuronCanvas) {
            super( MIN_VAL, MAX_VAL, title, "0", "");
            setUpDownArrowDelta( 0.01 );
            setTextFieldVisible(false);
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createRaisedBevelBorder() );
            setSnapToTicks(false);
            
            // Put in the labels for the left and right bottom portion of the
            // slider.
            Hashtable<Double, JLabel> concentrationSliderLabelTable = new Hashtable<Double, JLabel>();
            JLabel leftLabel = new JLabel("Far");
            leftLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( MIN_VAL ), leftLabel );
            JLabel rightLabel = new JLabel("Close");
            rightLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( MAX_VAL ), rightLabel );
            setTickLabels( concentrationSliderLabelTable );

            // Set up the change listener for this control.
            addChangeListener(new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
            		double value = getValue();
           			neuronCanvas.setZoomFactor(value);
            	}
            });
		}
    }
}
