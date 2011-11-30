// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.Floor;
import edu.umd.cs.piccolo.PNode;

/**
 * Module for the "Energy Skate Park Basics" Friction tab
 *
 * @author Sam Reid
 */
public class FrictionModule extends EnergySkateParkBasicsModule {
    private PNode trackFrictionControlPanel;

    public FrictionModule( PhetFrame phetFrame ) {
        super( EnergySkateParkResources.getString( "tab.friction" ), phetFrame, false, Floor.DEFAULT_FRICTION );

        //Don't allow the user to drag their own tracks in this module
        getEnergySkateParkSimulationPanel().getRootNode().removeSplineToolbox();

        //Add a track friction control panel
        trackFrictionControlPanel = new TrackControlPanel( this, energySkateParkSimulationPanel, energyGraphControlPanel );
        energySkateParkSimulationPanel.getRootNode().addChild( trackFrictionControlPanel );

        addResetAllButton( trackFrictionControlPanel );

        //Only add the track selection control panel after adding all other controls or they won't be shown in the thumbnails
        addTrackSelectionControlPanel();

        loadDefaultTrack();
    }
}