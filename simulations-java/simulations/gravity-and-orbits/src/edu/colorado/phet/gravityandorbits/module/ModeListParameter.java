// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

//REVIEW ModeListParameters (plural) might be a better name for this, since (as the javadoc says) it composites multiple parameters.
//REVIEW this is a nit, but the javadoc should refer to parameters, not arguments (there is a difference)

/**
 * Parameter object pattern, compositing multiple arguments that are passed to multiple modes.
 *
 * @author Sam Reid
 */
public class ModeListParameter {
    public final Property<Boolean> clockPausedProperty;
    public final Property<Boolean> gravityEnabledProperty;
    public final Property<Boolean> stepping;
    public final Property<Boolean> rewinding;
    public final Property<Double> timeSpeedScaleProperty;

    //REVIEW semantics of stepping and rewinding params is not obvious, please doc either params or public members
    public ModeListParameter( Property<Boolean> clockPaused, Property<Boolean> gravityEnabled, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScale ) {
        this.clockPausedProperty = clockPaused;
        this.gravityEnabledProperty = gravityEnabled;
        this.stepping = stepping;
        this.rewinding = rewinding;
        this.timeSpeedScaleProperty = timeSpeedScale;
    }
}
