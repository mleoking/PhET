// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;

/**
 * Main application for "Energy Skate Park Basics"
 *
 * @author Sam Reid
 */
public class EnergySkateParkBasicsApplication extends PiccoloPhetApplication {
    public EnergySkateParkBasicsApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new IntroModule( getPhetFrame() ) );
        addModule( new FrictionModule( getPhetFrame() ) );
        addModule( new TrackPlaygroundModule( getPhetFrame() ) );
    }

    public static void main( String[] args ) {
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new EnergySkateParkBasicsApplication( config );
            }
        };
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, "energy-skate-park", "energy-skate-park-basics" );
        appConfig.setLookAndFeel( new EnergySkateParkLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}