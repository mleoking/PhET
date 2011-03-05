// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new SunEarth() {{
                   sun.radius *= 50;
                   earth.radius *= 1100;
               }}, new SunEarthMoon() {{
                    sun.radius *= 50;
                    earth.radius *= 300;
                    moon.radius *= 100;

                    earth.mass *= 200;
                    moon.vx *= 4;
                    moon.y *= 10;
                }}, new EarthMoon() {{
                    earth.radius *= 15;
                    moon.radius *= 15;
                }}, new EarthSpaceStation() {{
                    earth.radius *= 0.8;
                    spaceStation.radius *= 8;
                }} );
    }
}
