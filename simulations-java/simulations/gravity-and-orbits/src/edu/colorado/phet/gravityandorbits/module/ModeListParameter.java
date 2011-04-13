// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

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

    public ModeListParameter( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        this.clockPausedProperty = clockPausedProperty;
        this.gravityEnabledProperty = gravityEnabledProperty;
        this.stepping = stepping;
        this.rewinding = rewinding;
        this.timeSpeedScaleProperty = timeSpeedScaleProperty;
    }
}
