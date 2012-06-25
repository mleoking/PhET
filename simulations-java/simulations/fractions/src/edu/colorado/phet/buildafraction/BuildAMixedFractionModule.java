package edu.colorado.phet.buildafraction;

import edu.colorado.phet.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.common.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class BuildAMixedFractionModule extends AbstractFractionsModule {
    public BuildAMixedFractionModule( boolean dev, boolean standaloneApp ) {
        this( new BuildAFractionModel( standaloneApp ), dev );
    }

    public BuildAMixedFractionModule( BuildAFractionModel model, boolean dev ) {
        super( Components.buildAFractionTab, "Mixed Fractions", model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model, dev ) );
    }
}