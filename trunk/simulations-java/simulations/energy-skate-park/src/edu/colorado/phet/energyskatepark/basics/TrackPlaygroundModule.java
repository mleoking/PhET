// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.Floor;

/**
 * Module for the "Energy Skate Park Basics" Track Playground tab
 *
 * @author Sam Reid
 */
public class TrackPlaygroundModule extends EnergySkateParkBasicsModule {
    public TrackPlaygroundModule( PhetFrame phetFrame ) {
        super( EnergySkateParkStrings.getString( "tab.trackPlayground" ), phetFrame, true, Floor.DEFAULT_FRICTION );

        final TrackControlPanel trackControlPanel = new TrackControlPanel( this, energySkateParkSimulationPanel, energyGraphControlPanel );
        energySkateParkSimulationPanel.getRootNode().addChild( trackControlPanel );

        addResetAllButton( trackControlPanel );

        loadDefaultTrack();
    }
}