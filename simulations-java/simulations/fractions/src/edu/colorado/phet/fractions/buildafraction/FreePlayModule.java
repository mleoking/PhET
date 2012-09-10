// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.FreePlayCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

/**
 * Main module for "build a fraction with mixed numbers" tab, which is only visible in the standalone "Build a Fraction" sim.
 *
 * @author Sam Reid
 */
public class FreePlayModule extends AbstractFractionsModule {
    public FreePlayModule( BuildAFractionModel model ) {
        super( Components.freePlayModule, Strings.FREE_PLAY, model.clock );
        setSimulationPanel( new FreePlayCanvas( model, Strings.BUILD_A_MIXED_FRACTION ) );
    }
}