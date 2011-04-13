// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Configuration file for "real" tab modes, uses physically accurate parameters.
 *
 * @author Sam Reid
 */
public class RealModeList extends ModeList {
    public RealModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new SunEarth(), new SunEarthMoon(), new EarthMoon(), new EarthSpaceStation() );
    }
}
