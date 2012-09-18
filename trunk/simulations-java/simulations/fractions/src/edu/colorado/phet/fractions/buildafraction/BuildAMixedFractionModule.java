// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

//REVIEW recommend renaming this to MixedNumbersModule

/**
 * Main module for "Mixed Numbers" tab, which is only visible in the standalone "Build a Fraction" sim.
 *
 * @author Sam Reid
 */
class BuildAMixedFractionModule extends AbstractFractionsModule {
    public BuildAMixedFractionModule( BuildAFractionModel model ) {
        super( Components.mixedNumbersTab, Strings.MIXED_NUMBERS, model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model, Strings.BUILD_A_MIXED_FRACTION ) );
    }
}