// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractionsintro.equalitylab.EqualityLabModule;
import edu.colorado.phet.fractionsintro.fractionmakergame.CreationGameModule;
import edu.colorado.phet.fractionsintro.intro.FractionsIntroModule;
import edu.colorado.phet.fractionsintro.matchinggame.MatchingGameModule;

/**
 * "Fractions Intro" PhET Application
 *
 * @author Sam Reid
 */
public class FractionsIntroApplication extends PiccoloPhetApplication {
    public FractionsIntroApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FractionsIntroModule() );
        addModule( new EqualityLabModule() );
        addModule( new MatchingGameModule() );
        addModule( new CreationGameModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", FractionsIntroApplication.class );
    }
}