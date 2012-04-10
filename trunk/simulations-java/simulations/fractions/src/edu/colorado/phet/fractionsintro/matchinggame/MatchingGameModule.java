// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameCanvas;

/**
 * @author Sam Reid
 */
public class MatchingGameModule extends AbstractFractionsModule {
    public MatchingGameModule( boolean dev ) {
        this( dev, new MatchingGameModel() );
    }

    public MatchingGameModule( boolean dev, MatchingGameModel model ) {
        super( Components.matchingGameTab, "Matching Game", model.clock );
        setSimulationPanel( new MatchingGameCanvas( dev, model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) {
        final ApplicationConstructor constructor = new ApplicationConstructor() {
            @Override public PhetApplication getApplication( PhetApplicationConfig c ) {
                return new PhetApplication( c ) {{addModule( new MatchingGameModule( true ) );}};
            }
        };
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", constructor );
    }
}