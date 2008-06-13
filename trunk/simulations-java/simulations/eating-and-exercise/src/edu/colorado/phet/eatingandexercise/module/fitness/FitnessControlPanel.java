/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.eatingandexercise.module.fitness;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.eatingandexercise.FitnessResources;

/**
 * FitnessControlPanel is the control panel for FitnessModule.
 */
public class FitnessControlPanel extends ControlPanel {

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
    public FitnessControlPanel( FitnessModule module, Frame parentFrame ) {
        super();

        // Set the control panel's minimum width.
        int minimumWidth = FitnessResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Create sub-panels
//        _fitnessSubPanel = new FitnessSubPanel();

        // Layout
        {
//            addControlFullWidth(new HumanControlPanel( module.getHuman()) );
//            addSeparator();
//            addResetAllButton( module );
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