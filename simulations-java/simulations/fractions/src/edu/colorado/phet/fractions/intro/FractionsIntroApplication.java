// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.fractions.intro.fractionmakergame.FractionMakerGame;
import edu.colorado.phet.fractions.intro.matchinggame.MatchingGameModule;

/**
 * @author Sam Reid
 */
public class FractionsIntroApplication extends PiccoloPhetApplication {
    public FractionsIntroApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new FractionsIntroModule() );
        addModule( new FractionsLabModule() );
        addModule( new MatchingGameModule() );
        addModule( new FractionMakerGame() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "fractions", FractionsIntroApplication.class );
    }
}