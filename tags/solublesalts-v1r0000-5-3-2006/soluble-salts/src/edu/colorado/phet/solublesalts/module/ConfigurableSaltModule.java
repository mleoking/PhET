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
import edu.colorado.phet.solublesalts.control.ConfigurableSaltControlPanel;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.ion.ConfigurableAnion;
import edu.colorado.phet.solublesalts.model.ion.ConfigurableCation;
import edu.colorado.phet.solublesalts.model.salt.ConfigurableSalt;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConfigurableSaltModule extends SolubleSaltsModule {

    /**
     * Only constructor
     *
     * @param clock
     */
    public ConfigurableSaltModule( IClock clock ) {
        super( SimStrings.get( "Module.configurableSalt" ),
               clock,
               new SolubleSaltsConfig.Calibration( 1.5E-16 / 500,
                                                   1E-16,
                                                   0.5E-16,
                                                   0.1E-16 ) );

//               new SolubleSaltsConfig.Calibration( 7.83E-16 / 500,
//                                            5E-16,
//                                            1E-16,
//                                            0.5E-16 ));

        // Set up the control panel
        setControlPanel( new ConfigurableSaltControlPanel( this ) );

        // Set the default salt
        ConfigurableCation.setClassCharge( 1 );
        ConfigurableAnion.setClassCharge( -1 );
        ConfigurableSalt.configure();
        ( (SolubleSaltsModel)getModel() ).setCurrentSalt( new ConfigurableSalt() );
    }
}
