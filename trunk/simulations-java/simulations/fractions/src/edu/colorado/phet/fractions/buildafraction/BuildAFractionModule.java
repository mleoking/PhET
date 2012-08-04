// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroApplication.runModule;

/**
 * Main module for "build a fraction" tab
 *
 * @author Sam Reid
 */
public class BuildAFractionModule extends AbstractFractionsModule {
    public BuildAFractionModule( BuildAFractionModel model, boolean dev ) {
        super( Components.buildAFractionTab, Strings.BUILD_A_FRACTION, model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model ) );
    }

    //Test main for launching this module in an application by itself for testing
    public static void main( String[] args ) { runModule( args, new BuildAFractionModule( new BuildAFractionModel(), true ) ); }
}