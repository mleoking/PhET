// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class CartoonModeList extends ModeList {
    public CartoonModeList( Property<Boolean> clockPausedProperty, Property<Boolean> gravityEnabledProperty, Property<Scale> scaleProperty, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScaleProperty, final double alpha ) {
        super( new ModeListParameter( clockPausedProperty, gravityEnabledProperty, scaleProperty, stepping, rewinding, timeSpeedScaleProperty ),
               new SunEarth() {{
                   sun.radius *= 50;
                   earth.radius *= 1100;
               }}, new SunEarthMoon() {{
                    sun.radius *= 50;
                    earth.radius *= 1100;
                    moon.radius *= 800;
                    final int earthMassScaleFactor = 20000;
                    earth.mass *= earthMassScaleFactor;
                    moon.vx *= alpha;
                    moon.y = earth.radius * 2;
                    moon.mass *= earthMassScaleFactor;
                    forceScale *= 0.8 / earthMassScaleFactor;//to balance increased mass
                }}, new EarthMoon() {{
                    earth.radius *= 15;
                    moon.radius *= 15;
                }}, new EarthSpaceStation() {{
                    earth.radius *= 0.8;
                    spaceStation.radius *= 8;
                }} );
    }
}
