// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.trackPlaygroundTab;

/**
 * Module for the "Energy Skate Park Basics" Track Playground tab
 *
 * @author Sam Reid
 */
public class TrackPlaygroundModule extends EnergySkateParkBasicsModule {
    public TrackPlaygroundModule( PhetFrame phetFrame ) {
        super( trackPlaygroundTab, EnergySkateParkResources.getString( "tab.trackPlayground" ), phetFrame, true );

        addResetAndRestartButtons( controlPanel );

        loadDefaultTrack();
    }

    @Override protected ControlPanelNode createControlPanel() {
        return new FrictionModuleControlPanel( this );
    }

    protected void loadDefaultTrack() {
        super.loadDefaultTrack();

        //Don't start with any spline surfaces in this mode, the user must create them.
        energyModel.removeAllSplineSurfaces();
    }
}