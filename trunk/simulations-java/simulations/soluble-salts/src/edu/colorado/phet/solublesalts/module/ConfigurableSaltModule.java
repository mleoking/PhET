// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.module;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.solublesalts.SolubleSaltResources;
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
 */
public class ConfigurableSaltModule extends SolubleSaltsModule {

    /**
     * Only constructor
     *
     * @param clock
     */
    public ConfigurableSaltModule( IClock clock ) {
        super( SolubleSaltResources.getString( "Module.configurableSalt" ),
               clock,
               new SolubleSaltsConfig.Calibration( 1.5E-16 / 500,
                                                   1E-16,
                                                   0.5E-16,
                                                   0.1E-16 ) );

        // Set up the control panel
        setControlPanel( new ConfigurableSaltControlPanel( this ) );

        // Set the default salt
        ConfigurableCation.setClassCharge( 1 );
        ConfigurableAnion.setClassCharge( -1 );
        ConfigurableSalt.configure();
        ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new ConfigurableSalt() );
    }
}
