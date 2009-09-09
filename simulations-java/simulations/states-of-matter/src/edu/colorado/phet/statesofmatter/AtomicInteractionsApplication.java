/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.statesofmatter.module.atomicinteractions.AtomicInteractionsModule;

/**
 * Main application class for the Atomic Interactions simulation flavor.
 *
 * @author John Blanco
 */
public class AtomicInteractionsApplication extends AbstractStatesOfMatterApp implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private AtomicInteractionsModule   m_interactionPotentialModule;
    
    //----------------------------------------------------------------------------
    // Sole Constructor
    //----------------------------------------------------------------------------
    
    public AtomicInteractionsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /**
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        m_interactionPotentialModule = new AtomicInteractionsModule( parentFrame, true );
        addModule( m_interactionPotentialModule );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main(final String[] args ) {

        ApplicationConstructor applicationConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                // Create the application.
                return( new AtomicInteractionsApplication( config ) );
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
