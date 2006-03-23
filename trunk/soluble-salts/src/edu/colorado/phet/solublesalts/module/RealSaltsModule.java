/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.module;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.control.RealSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RealSaltsModule extends SolubleSaltsModule {

    /**
     * Only constructor
     *
     * @param clock
     */
    public RealSaltsModule( IClock clock ) {
        super( SimStrings.get( "Module.title" ),
               clock,
               new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                            5E-23,
                                            1E-23,
                                            0.5E-23 ));

        // Set up the control panel
        setControlPanel( new RealSaltsControlPanel( this ) );

        // Set the default salt
        ((SolubleSaltsModel)getModel()).setCurrentSalt( SolubleSaltsConfig.DEFAULT_SALT );
    }
}
