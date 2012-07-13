// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.common.AbstractFractionsModule;

/**
 * @author Sam Reid
 */
public class BuildAMixedFractionModule extends AbstractFractionsModule {
    public BuildAMixedFractionModule( boolean dev, boolean standaloneApp ) {
        this( new BuildAFractionModel(), dev );
    }

    public BuildAMixedFractionModule( BuildAFractionModel model, boolean dev ) {
        super( Components.buildAFractionTab, "Mixed Fractions", model.clock );
        setSimulationPanel( new BuildAFractionCanvas( model, dev ) );
    }
}