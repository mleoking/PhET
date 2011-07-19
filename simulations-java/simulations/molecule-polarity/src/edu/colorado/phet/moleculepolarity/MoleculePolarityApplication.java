// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.moleculepolarity.realmolecules.RealMoleculesModule;
import edu.colorado.phet.moleculepolarity.threeatoms.ThreeAtomsModule;
import edu.colorado.phet.moleculepolarity.twoatoms.TwoAtomsModule;

/**
 * Main class for the "Molecule Polarity" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MoleculePolarityApplication extends PiccoloPhetApplication {

    public MoleculePolarityApplication( PhetApplicationConfig config ) {
        super( config );

        Frame parentFrame = getPhetFrame();
        addModule( new TwoAtomsModule( parentFrame ) );
        addModule( new ThreeAtomsModule( parentFrame ) );
        addModule( new RealMoleculesModule( parentFrame ) );

        // start with a specific module for development
        if ( config.isDev() ) {
            setStartModule( getModule( 2 ) );
        }
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, MPConstants.PROJECT_NAME, MoleculePolarityApplication.class );
    }
}
