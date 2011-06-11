// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro;

import edu.colorado.phet.solublesalts.SolubleSaltsApplication.SolubleSaltsClock;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;

/**
 * Micro tab that shows the NaCl ions and Sucrose molecules
 *
 * @author Sam Reid
 */
public class MicroModule extends SolubleSaltsModule {
    public MicroModule( SugarAndSaltSolutionsColorScheme configuration ) {
        super( "Micro",
               new SolubleSaltsClock(),
               new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                   5E-23,
                                                   1E-23,
                                                   0.5E-23 ) );

        // Use NaCl by default
        ( (SolubleSaltsModel) getModel() ).setCurrentSalt( new SodiumChloride() );

//        getSimulationPanel()
    }
}
