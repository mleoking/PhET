// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.frictionTab;

/**
 * Module for the "Energy Skate Park Basics" Friction tab
 *
 * @author Sam Reid
 */
public class FrictionModule extends EnergySkateParkBasicsModule {

    public FrictionModule( PhetFrame phetFrame ) {
        super( frictionTab, EnergySkateParkResources.getString( "tab.friction" ), phetFrame, false );

        //Don't allow the user to drag their own tracks in this module
        getEnergySkateParkSimulationPanel().getRootNode().removeSplineToolbox();

        addResetAndRestartButtons( controlPanel );

        //Only add the track selection control panel after adding all other controls or they won't be shown in the thumbnails
        addTrackSelectionControlPanel();

        loadDefaultTrack();
    }

    //Show additional controls
    @Override protected ControlPanelNode createControlPanel() {
        return new FrictionModuleControlPanel( this );
    }
}