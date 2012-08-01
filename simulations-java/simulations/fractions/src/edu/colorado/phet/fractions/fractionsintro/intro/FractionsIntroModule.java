// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro;

import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractions.fractionsintro.intro.model.IntroState;
import edu.colorado.phet.fractions.fractionsintro.intro.view.FractionsIntroCanvas;

import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroApplication.runModule;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.IntroState.createFactorySet;

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