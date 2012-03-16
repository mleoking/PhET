// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.statesofmatter.module.atomicinteractions.AtomicInteractionsModule;

/**
 * Main application class for the Atomic Interactions simulation flavor.
 *
 * @author John Blanco
 */
public class AtomicInteractionsApplication extends PiccoloPhetApplication implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public AtomicInteractionsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        addModule( new AtomicInteractionsModule( true ) );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {

        ApplicationConstructor applicationConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                // Create the application.
                return ( new AtomicInteractionsApplication( config ) );
            }
        };
        PhetApplicationConfig config = new PhetApplicationConfig( args, StatesOfMatterConstants.PROJECT_NAME, StatesOfMatterConstants.FLAVOR_INTERACTION_POTENTIAL );

        config.setFrameSetup( StatesOfMatterConstants.FRAME_SETUP );

        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        config.setLookAndFeel( p );
        new PhetApplicationLauncher().launchSim( config, applicationConstructor );
    }
}
