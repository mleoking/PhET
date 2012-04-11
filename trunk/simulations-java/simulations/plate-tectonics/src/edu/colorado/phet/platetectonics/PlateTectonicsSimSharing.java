// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics;

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

        ruler, thermometer, densityMeter
    }

    public static enum UserActions implements IUserAction {
        removedFromToolbox,
        putBackInToolbox
    }
}
