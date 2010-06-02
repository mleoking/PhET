/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.membranediffusion.controlpanel;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.membranediffusion.SimTemplateResources;
import edu.colorado.phet.membranediffusion.model.SimTemplateModel;
import edu.colorado.phet.membranediffusion.module.SimTemplateModule;

/**
 * Control panel template.
 */
public class SimTemplateControlPanel extends ControlPanel {

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
    public SimTemplateControlPanel( SimTemplateModule module, Frame parentFrame, SimTemplateModel model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = SimTemplateResources.getInt( "int.minControlPanelWidth", 215 );
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
