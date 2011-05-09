// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;

public class EatingAndExerciseControlPanel extends ControlPanel {

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
    public EatingAndExerciseControlPanel( EatingAndExerciseModule module, Frame parentFrame ) {
        super();

        // Set the control panel's minimum width.
        int minimumWidth = EatingAndExerciseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
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