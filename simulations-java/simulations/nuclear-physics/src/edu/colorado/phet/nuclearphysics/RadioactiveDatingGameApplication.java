/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics;



import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.nuclearphysics.module.chainreaction.ChainReactionModule;
import edu.colorado.phet.nuclearphysics.module.fissiononenucleus.FissionOneNucleusModule;
import edu.colorado.phet.nuclearphysics.module.nuclearreactor.NuclearReactorModule;

/**
 * RadioactiveDatingGameApplication is the main application class for the
 * "Radioactive Dating Game" simulation.
 *
 * @author John Blanco
 * 
 */
public class RadioactiveDatingGameApplication extends AbstractNuclearPhysicsApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    private FissionOneNucleusModule _fissionOneNucleusModule;
    private ChainReactionModule _chainReactionModule;
    private NuclearReactorModule _nuclearReactorModule;

    /**
     * Sole constructor.
     *
     * @param config - The configuration for the application.
     */
    public RadioactiveDatingGameApplication( PhetApplicationConfig config )
    {
        super( config );
        initTabbedPane();
        
        Frame parentFrame = getPhetFrame();

        _fissionOneNucleusModule = new FissionOneNucleusModule( parentFrame );
        addModule( _fissionOneNucleusModule );
    
        _chainReactionModule = new ChainReactionModule( parentFrame );
        addModule( _chainReactionModule );
    
        _nuclearReactorModule = new NuclearReactorModule( parentFrame );
        addModule( _nuclearReactorModule );

        initMenubar( config.getCommandLineArgs() );
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new RadioactiveDatingGameApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args,
            NuclearPhysicsConstants.PROJECT_NAME, NuclearPhysicsConstants.FLAVOR_NAME_RADIOACTIVE_DATING_GAME );
        
        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( NuclearPhysicsConstants.RADIOACTIVE_DATING_GAME_CONTROL_PANEL_COLOR );
        appConfig.setLookAndFeel( p );

        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
