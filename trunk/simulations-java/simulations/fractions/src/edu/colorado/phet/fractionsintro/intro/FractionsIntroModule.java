// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.model.pieset.factories.FactorySet;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;

/**
 * Module for "Fractions Intro" sim
 *
 * @author Sam Reid
 */
public class FractionsIntroModule extends AbstractFractionsModule {
    public FractionsIntroModule() {
        this( new FractionsIntroModel( IntroState.INTRO_STATE, FactorySet.IntroTab() ) );
    }

    private FractionsIntroModule( FractionsIntroModel model ) {
        super( "Intro", model.getClock() );
        setSimulationPanel( new FractionsIntroCanvas( model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) {
        final ApplicationConstructor constructor = new ApplicationConstructor() {
            @Override public PhetApplication getApplication( PhetApplicationConfig c ) {
                return new PhetApplication( c ) {{addModule( new FractionsIntroModule() );}};
            }
        };
        new PhetApplicationLauncher().launchSim( args, "fractions", "fractions-intro", constructor );
    }
}