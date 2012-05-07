// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro;

import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;

import static edu.colorado.phet.fractionsintro.FractionsIntroApplication.runModule;
import static edu.colorado.phet.fractionsintro.intro.model.IntroState.createFactorySet;

/**
 * Module for "Fractions Intro" sim
 *
 * @author Sam Reid
 */
public class FractionsIntroModule extends AbstractFractionsModule {
    public FractionsIntroModule() {
        this( new FractionsIntroModel( IntroState.newState( 1, createFactorySet(), System.currentTimeMillis() ), createFactorySet() ) );
    }

    private FractionsIntroModule( FractionsIntroModel model ) {
        super( Components.introTab, "Intro", model.getClock() );
        setSimulationPanel( new FractionsIntroCanvas( model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new FractionsIntroModule() ); }
}