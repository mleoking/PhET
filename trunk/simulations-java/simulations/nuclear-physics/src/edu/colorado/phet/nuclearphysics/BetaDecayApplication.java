/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.nuclearphysics.module.alphadecay.singlenucleus.SingleNucleusAlphaDecayModule;
import edu.colorado.phet.nuclearphysics.module.betadecay.multinucleus.MultiNucleusBetaDecayModule;
import edu.colorado.phet.nuclearphysics.module.betadecay.singlenucleus.SingleNucleusBetaDecayModule;

/**
 * Main application class for the Beta Decay simulation.
 * 
 * @author John Blanco
 */
public class BetaDecayApplication extends AbstractNuclearPhysicsApplication {

    private MultiNucleusBetaDecayModule  _multiNucleusAlphaDecayModule;
    private SingleNucleusBetaDecayModule _singleNucleusAlphaDecayModule;

    public BetaDecayApplication( PhetApplicationConfig config ) {
        super( config );
        
        Frame parentFrame = getPhetFrame();
    
        _multiNucleusAlphaDecayModule = new MultiNucleusBetaDecayModule( parentFrame );
        addModule( _multiNucleusAlphaDecayModule );

        _singleNucleusAlphaDecayModule = new SingleNucleusBetaDecayModule( parentFrame );
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
                return new BetaDecayApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, NuclearPhysicsConstants.PROJECT_NAME,
        		NuclearPhysicsConstants.FLAVOR_NAME_BETA_DECAY );
        
        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( NuclearPhysicsConstants.BETA_DECAY_CONTROL_PANEL_COLOR );
        appConfig.setLookAndFeel( p );

        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
