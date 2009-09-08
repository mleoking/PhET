/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.nuclearphysics.module.decayrates.DecayRatesModule;
import edu.colorado.phet.nuclearphysics.module.halflife.RadiometricElementDecayModule;
import edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.RadioactiveDatingGameModule;
import edu.colorado.phet.nuclearphysics.module.radioactivedatinggame.RadiometricMeasurementModule;


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
    private RadiometricElementDecayModule _radioactiveElementDecayModule;
    private DecayRatesModule _decayRatesModule;
    private RadiometricMeasurementModule _radiometricMeasurementModule;
    private RadioactiveDatingGameModule _radioactiveDatingGameModule;

    /**
     * Sole constructor.
     *
     * @param config - The configuration for the application.
     */
    public RadioactiveDatingGameApplication( PhetApplicationConfig config )
    {
        super( config );
        
        Frame parentFrame = getPhetFrame();

        _radioactiveElementDecayModule = new RadiometricElementDecayModule( parentFrame );
        addModule( _radioactiveElementDecayModule );
    
        _decayRatesModule = new DecayRatesModule( parentFrame );
        addModule( _decayRatesModule );
    
        _radiometricMeasurementModule = new RadiometricMeasurementModule( parentFrame );
        addModule( _radiometricMeasurementModule );
    
        _radioactiveDatingGameModule = new RadioactiveDatingGameModule( parentFrame );
        addModule( _radioactiveDatingGameModule );

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
