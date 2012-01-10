package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;

/**
 * Constants for sim sharing.
 *
 * @author Sam Reid
 */
public class EnergySkateParkSimSharing {

    //Anything that the model or user can interact with.  In this sim, the model can signify that the skater landed, or the user can drag the skater.
    public static enum SharedComponents implements UserComponent, ModelObject {
        skater
    }

    public static enum UserComponents implements UserComponent {
        trackButton, slowMotionRadioButton, normalSpeedRadioButton,
        barGraphCheckBox, pieChartCheckBox, gridCheckBox, speedCheckBox,
        barGraphCheckBoxIcon, pieChartCheckBoxIcon, gridCheckBoxIcon, speedCheckBoxIcon,

        trackPlaygroundTab, introTab, frictionTab,

        returnSkaterButton, skaterMassSlider, trackFrictionSlider,
        playAreaReturnSkaterButton,

        //Parents for on/off controls
        friction, stickToTrack,

        //For dragging out of toolbox
        toolboxTrack,

        //For dragging track
        track, trackControlPoint,

        energyTimePlot
    }

    public static enum ModelObjects implements ModelObject {
    }

    public static enum ModelActions implements ModelAction {
        landed, bounced
    }

    public static enum UserActions implements UserAction {
        attached
    }

    public static enum ParameterKeys implements ParameterKey {

        //Apparently the floor doesn't count as a track (a bit surprising based on how I thought the model worked)
        trackName, numTracks, isFloor,

        //For combining tracks
        inputTrack1, inputTrack2, outputTrack,

        trackIndex
    }
}
