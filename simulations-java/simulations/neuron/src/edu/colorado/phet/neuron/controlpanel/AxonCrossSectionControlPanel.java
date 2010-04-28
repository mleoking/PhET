/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AxonModel;

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
    public AxonCrossSectionControlPanel( PiccoloModule module, Frame parentFrame, final AxonModel model ) {
        super();
        
        this.axonModel = model;

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
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Add the legend.
        addControlFullWidth(new IonsAndChannelsLegendPanel());

        // Add the check boxes.
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
        chartControlCheckbox.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				axonModel.setPotentialChartVisible(chartControlCheckbox.isSelected());
				
			}
		});
        chartControlCheckbox.setAlignmentX(CENTER_ALIGNMENT);
        checkBoxPanel.add(chartControlCheckbox);
        addControlFullWidth(checkBoxPanel);
        updateChartVisibilityCheckBox();
        
        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(30));
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
    
    private void updateChartVisibilityCheckBox(){
		chartControlCheckbox.setSelected(axonModel.isPotentialChartVisible());
    }
    
    private void updateShowAllIonsCheckBox(){
		showAllIons.setSelected(axonModel.isAllIonsSimulated());
    }
}
