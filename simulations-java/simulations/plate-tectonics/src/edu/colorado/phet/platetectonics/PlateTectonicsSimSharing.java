// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * @author Sam Reid
 */
public class PlateTectonicsSimSharing {
    public static enum UserComponents implements IUserComponent {
        plateMotionTab, crustTab, zoomSlider, timeSpeedSlider,

        densityView, temperatureView, bothView,
        compositionSlider, temperatureSlider, thicknessSlider,

        showLabels, showWater,

        ruler, thermometer, densityMeter, crustPiece,

        manualMode, automaticMode,

        handle,

        convergentMotion, divergentMotion, transformMotion
    }

    public static enum UserActions implements IUserAction {
        removedFromToolbox,
        putBackInToolbox,

        droppedCrustPiece,
        putBackInCrustPicker,
        attemptedToDropOnExistingCrust
    }

    public static enum ParameterKeys implements IParameterKey {
        plateType,
        motionType,
        side,
        leftPlateType,
        rightPlateType,
        timeChangeMillionsOfYears
    }

    public static enum ModelComponents implements IModelComponent {
        motion,
        time
    }

    public static enum ModelActions implements IModelAction {
        transformMotion,
        continentalCollisionMotion,
        rightPlateSubductingMotion,
        leftPlateSubductingMotion,
        divergentMotion,

        maximumTimeReached
    }
}
