// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.view.SkaterCharacterSet;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.introTab;

/**
 * Module for the "Energy Skate Park Basics" Introduction
 *
 * @author Sam Reid
 */
public class IntroModule extends EnergySkateParkBasicsModule {
    public IntroModule( PhetFrame phetFrame ) {
        super( introTab, EnergySkateParkResources.getString( "tab.introduction" ), phetFrame, false );

        //Don't allow the user to drag their own tracks in this module
        getEnergySkateParkSimulationPanel().getRootNode().removeSplineToolbox();

        //Show the reset all button below the bottom control panel
        addResetAndRestartButtons( controlPanel );

        //Show buttons that allows the user to choose different tracks
        addTrackSelectionControlPanel();

        loadDefaultTrack();

        mass.addObserver( new VoidFunction1<Double>() {
            public void apply( Double mass ) {
                setSkaterCharacter( SkaterCharacterSet.getDefaultCharacter( mass ) );
            }
        } );
    }

    @Override protected ControlPanelNode createControlPanel() {
        return new IntroControlPanel( this );
    }
}