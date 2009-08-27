/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.module.NeuronModule;

/**
 * ExampleControlPanel is the control panel for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public NeuronControlPanel( NeuronModule module, Frame parentFrame, AxonModel model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Layout
        {
            addResetAllButton( module );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
}
