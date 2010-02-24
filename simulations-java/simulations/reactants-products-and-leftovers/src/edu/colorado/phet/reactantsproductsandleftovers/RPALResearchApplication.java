/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;

/**
 * A research version of the "Reactants, Products and Leftovers" simulation.
 * This version has some additional features that the productions version does not.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALResearchApplication extends ReactantsProductsAndLeftoversApplication {

    public RPALResearchApplication( PhetApplicationConfig config ) {
        super( config, true /* researchFlag */ );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, RPALConstants.PROJECT_NAME, RPALConstants.FLAVOR_RPAL_RESEARCH, RPALResearchApplication.class );
    }
}
