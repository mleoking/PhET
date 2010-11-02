/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsResources;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * Control panel template.
 */
public class GravityAndOrbitsControlPanel extends ControlPanel {

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
    public GravityAndOrbitsControlPanel( GravityAndOrbitsModule module, Frame parentFrame, GravityAndOrbitsModel model ) {
        super();

        // Set the control panel's minimum width.
        int minimumWidth = GravityAndOrbitsResources.getInt( "int.minControlPanelWidth", 215 );
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
