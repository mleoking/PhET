// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

/**
 * Main module for "Build a Fraction" tab
 *
 * @author Sam Reid
 */
public class BuildAFractionModule extends AbstractFractionsModule {
    public static final int ANIMATION_TIME = 200;
    public final BuildAFractionCanvas canvas;

    public BuildAFractionModule( BuildAFractionModel model ) {
        super( Components.buildAFractionTab, Strings.BUILD_A_FRACTION, model.clock );
        canvas = new BuildAFractionCanvas( model, Strings.BUILD_A_FRACTION );
        setSimulationPanel( canvas );
    }
}