// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * Module for the "Energy Skate Park Basics" Track Playground tab
 *
 * @author Sam Reid
 */
public class TrackPlaygroundModule extends EnergySkateParkBasicsModule {
    public TrackPlaygroundModule( PhetFrame phetFrame ) {
        super( "Track Playground", phetFrame );

        final TrackFrictionControlPanel trackFrictionControlPanel = new TrackFrictionControlPanel( energySkateParkSimulationPanel, energyGraphControlPanel );
        energySkateParkSimulationPanel.getRootNode().addChild( trackFrictionControlPanel );

        addResetAllButton( trackFrictionControlPanel );

        //Load the initial track
        load( PARABOLA, true );
    }
}