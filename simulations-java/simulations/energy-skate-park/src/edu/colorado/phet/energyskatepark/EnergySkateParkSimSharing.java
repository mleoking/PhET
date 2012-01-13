package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;

/**
 * Constants for sim sharing.
 *
 * @author Sam Reid
 */
public class EnergySkateParkSimSharing {

    //Anything that the model or user can interact with.  In this sim, the model can signify that the skater landed, or the user can drag the skater.
    public static enum SharedComponents implements IUserComponent, ModelObject {
        skater
    }

    public static enum UserComponents implements IUserComponent {
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
        track, controlPoint,

        energyTimePlot
    }

    public static enum ModelObjects implements ModelObject {
    }

    public static enum ModelActions implements IModelAction {
        landed, bounced
    }

    public static enum UserActions implements IUserAction {
        attached
    }

    public static enum ParameterKeys implements IParameterKey {

        //Apparently the floor doesn't count as a track (a bit surprising based on how I thought the model worked)
        trackName, numTracks, isFloor,

        skaterX, skaterY,

        //For combining tracks
        inputTrack1, inputTrack2, outputTrack,

        trackIndex
    }
}
