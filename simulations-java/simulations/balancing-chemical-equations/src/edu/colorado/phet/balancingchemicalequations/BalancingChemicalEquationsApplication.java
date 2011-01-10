// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.Frame;

import edu.colorado.phet.balancingchemicalequations.module.balanceequation.BalanceEquationModule;
import edu.colorado.phet.balancingchemicalequations.module.game.GameModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for the "Balancing Chemical Equations" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BalancingChemicalEquationsApplication extends PiccoloPhetApplication {

    public BalancingChemicalEquationsApplication( PhetApplicationConfig config ) {
        super( config );

        // modules
        Frame parentFrame = getPhetFrame();
        addModule( new BalanceEquationModule( parentFrame ) );
        addModule( new GameModule( parentFrame ) );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, BCEConstants.PROJECT_NAME, BCEConstants.FLAVOR_BCE, BalancingChemicalEquationsApplication.class );
    }
}
