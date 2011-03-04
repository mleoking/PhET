// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class ModeListParameter {
    public final Property<Boolean> clockPausedProperty;
    public final Property<Boolean> gravityEnabledProperty;
    public final Property<Scale> scaleProperty;
    public final Property<Boolean> stepping;
    public final Property<Boolean> rewinding;
    public final Property<Double> timeSpeedScaleProperty;

    public ModeListParameter( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        this.clockPausedProperty = clockPausedProperty;
        this.gravityEnabledProperty = gravityEnabledProperty;
        this.scaleProperty = scaleProperty;
        this.stepping = stepping;
        this.rewinding = rewinding;
        this.timeSpeedScaleProperty = timeSpeedScaleProperty;
    }
}
