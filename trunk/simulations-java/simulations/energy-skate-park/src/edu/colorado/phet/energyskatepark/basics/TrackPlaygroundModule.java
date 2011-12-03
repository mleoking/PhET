// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

/**
 * Module for the "Energy Skate Park Basics" Track Playground tab
 *
 * @author Sam Reid
 */
public class TrackPlaygroundModule extends EnergySkateParkBasicsModule {
    public TrackPlaygroundModule( PhetFrame phetFrame ) {
        super( EnergySkateParkResources.getString( "tab.trackPlayground" ), phetFrame, true );

        final TrackControlPanel trackControlPanel = new TrackControlPanel( this );
        energySkateParkSimulationPanel.getRootNode().addChild( trackControlPanel );

        addResetAllButton( trackControlPanel );

        loadDefaultTrack();
    }

    protected void loadDefaultTrack() {
        super.loadDefaultTrack();

        //Don't start with any spline surfaces in this mode, the user must create them.
        energyModel.removeAllSplineSurfaces();
    }
}