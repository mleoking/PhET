package edu.colorado.phet.fractionsintro.buildafraction;

import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

/**
 * Main module for "build a fraction" tab
 *
 * @author Sam Reid
 */
public class BuildAFractionModule extends AbstractFractionsModule {
    public BuildAFractionModule() {
        this( new BuildAFractionModel() );
    }

    private BuildAFractionModule( BuildAFractionModel model ) {
        super( Components.buildAFractionTab, "Build a Fraction", model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model ) );
    }
}