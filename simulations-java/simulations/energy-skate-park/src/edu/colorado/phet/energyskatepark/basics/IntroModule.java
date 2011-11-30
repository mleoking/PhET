// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;

/**
 * Module for the "Energy Skate Park Basics" Introduction
 *
 * @author Sam Reid
 */
public class IntroModule extends EnergySkateParkBasicsModule {
    public IntroModule( PhetFrame phetFrame ) {
        super( EnergySkateParkResources.getString( "tab.introduction" ), phetFrame, false, 0.0 );

        //Don't allow the user to drag their own tracks in this module
        getEnergySkateParkSimulationPanel().getRootNode().removeSplineToolbox();

        //Show the reset all button below the bottom control panel
        addResetAllButton( energyGraphControlPanel );

        //Show buttons that allows the user to choose different tracks
        addTrackSelectionControlPanel();

        loadDefaultTrack();
    }
}