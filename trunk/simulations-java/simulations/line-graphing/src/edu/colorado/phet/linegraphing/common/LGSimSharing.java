// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * Data-collection enums that are specific to this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LGSimSharing {

    public static enum UserComponents implements IUserComponent {
        slopeTab, slopeInterceptTab, pointSlopeTab, lineGameTab,
        equationMinimizeMaximizeButton,
        riseSpinner, runSpinner, interceptSpinner, x1Spinner, y1Spinner, x2Spinner, y2Spinner,
        saveLineButton, eraseLinesButton,
        linesCheckBox, riseOverRunCheckBox, yEqualsXCheckBox, yEqualsNegativeXCheckBox,
        pointManipulator, slopeManipulator, interceptManipulator, x1y1Manipulator, x2y2Manipulator, pointTool,
        p1Manipulator, p2Manipulator, p3Manipulator
    }

    public static enum ParameterKeys implements IParameterKey {
        x, y, x1, y1, x2, y2, rise, run, maximized
    }

    public static enum ModelComponents implements IModelComponent {
        line
    }
}
