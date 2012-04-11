// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.movingman;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * @author Sam Reid
 */
public class MovingManSimSharing {
    public static enum UserComponents implements IUserComponent {
        minimizePositionChartButton,
        maximizePositionChartButton,
        minimizeVelocityChartButton,
        maximizeVelocityChartButton,
        minimizeAccelerationChartButton,
        maximizeAccelerationChartButton
    }
}