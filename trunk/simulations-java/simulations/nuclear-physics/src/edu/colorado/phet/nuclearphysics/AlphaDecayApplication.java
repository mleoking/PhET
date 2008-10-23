/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusAlphaDecayModule;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModule;

/**
 * Main application class for the Alpha Decay simulation.
 * 
 * @author John Blanco
 */
public class AlphaDecayApplication extends AbstractNuclearPhysicsApplication {

    private SingleNucleusAlphaDecayModule _singleNucleusAlphaDecayModule;
    private MultiNucleusAlphaDecayModule  _multiNucleusAlphaDecayModule;

    public AlphaDecayApplication( PhetApplicationConfig config ) {
        super( config );
        
        Frame parentFrame = getPhetFrame();
    
        _multiNucleusAlphaDecayModule = new MultiNucleusAlphaDecayModule( parentFrame );
        addModule( _multiNucleusAlphaDecayModule );

        _singleNucleusAlphaDecayModule = new SingleNucleusAlphaDecayModule( parentFrame );
        addModule( _singleNucleusAlphaDecayModule );
    }
    
    /**
     * Main entry point.
     * 
     * @param args command line arguments
     */
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new AlphaDecayApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args,
                                                                     NuclearPhysicsConstants.PROJECT_NAME, NuclearPhysicsConstants.FLAVOR_NAME_ALPHA_DECAY );
        
        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( NuclearPhysicsConstants.ALPHA_DECAY_CONTROL_PANEL_COLOR );
        appConfig.setLookAndFeel( p );

        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
