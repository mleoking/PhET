// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Parameter object pattern, compositing multiple parameters that are passed to multiple modes.
 *
 * @author Sam Reid
 */
public class ModeListParameterList {
    public final Property<Boolean> clockPaused;
    public final Property<Boolean> gravityEnabled;
    public final Property<Boolean> stepping;//True if the user is pressing the "step" button, to support storing states for the rewind feature
    public final Property<Boolean> rewinding;//Flag to indicate if a "rewind" event is taking place, to support storing states for the rewind feature
    public final Property<Double> timeSpeedScale;

    public ModeListParameterList( Property<Boolean> clockPaused, Property<Boolean> gravityEnabled, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScale ) {
        this.clockPaused = clockPaused;
        this.gravityEnabled = gravityEnabled;
        this.stepping = stepping;
        this.rewinding = rewinding;
        this.timeSpeedScale = timeSpeedScale;
    }
}
