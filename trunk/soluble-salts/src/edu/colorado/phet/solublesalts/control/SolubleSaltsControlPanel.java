/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;

import java.text.DecimalFormat;

/**
 * SolubleSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsControlPanel extends ControlPanel {

    public SolubleSaltsControlPanel( Module module ) {
        super( module );

        // Sliders for affinity adjustment
        ModelSlider vesselIonStickSlider = new ModelSlider( "Ion stick likelihood",
                                                            "",
                                                            0,
                                                            1,
                                                            0,
                                                            new DecimalFormat( "0.00" ) );
        ModelSlider vesselIonReleaseSlider = new ModelSlider( "Ion release likelihood",
                                                              "",
                                                              0,
                                                              .1,
                                                              0,
                                                              new DecimalFormat( "0.000" ) );
        addControl( vesselIonStickSlider );
        addControl( vesselIonReleaseSlider );
    }


}
