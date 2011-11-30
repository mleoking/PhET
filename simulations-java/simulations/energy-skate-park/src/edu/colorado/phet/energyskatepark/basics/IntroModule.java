// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * Module for the "Energy Skate Park Basics" Introduction
 *
 * @author Sam Reid
 */
public class IntroModule extends EnergySkateParkBasicsModule {
    public IntroModule( PhetFrame phetFrame ) {
        super( "Introduction", phetFrame, false );

        //Don't allow the user to drag their own tracks in this module
        getEnergySkateParkSimulationPanel().getRootNode().removeSplineToolbox();

        //Show the reset all button below the bottom control panel
        addResetAllButton( energyGraphControlPanel );

        //Show buttons that allows the user to choose different tracks
        addTrackSelectionControlPanel();

        reset();
    }

    @Override public void reset() {
        super.reset();

        //Load the initial track
        load( PARABOLA, true );
    }
}