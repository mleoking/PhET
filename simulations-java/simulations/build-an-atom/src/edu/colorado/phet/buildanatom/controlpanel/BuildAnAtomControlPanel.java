/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.controlpanel;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.module.BuildAnAtomModule;

/**
 * Control panel template.
 */
public class BuildAnAtomControlPanel extends ControlPanel {

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
    public BuildAnAtomControlPanel( BuildAnAtomModule module, Frame parentFrame, BuildAnAtomModel model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = BuildAnAtomResources.getInt( "int.minControlPanelWidth", 215 );
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
