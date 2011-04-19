// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.module;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Configuration file for "real" tab modes, uses physically accurate parameters.
 *
 * @author Sam Reid
 */
public class RealModeList extends ModeList {
    public RealModeList( Property<Boolean> clockPaused, Property<Boolean> gravityEnabled, Property<Boolean> stepping, Property<Boolean> rewinding, Property<Double> timeSpeedScale ) {
        super( new ModeListParameterList( clockPaused, gravityEnabled, stepping, rewinding, timeSpeedScale ),
               new SunEarthModeConfig(), new SunEarthMoonModeConfig(), new EarthMoonModeConfig(), new EarthSpaceStationModeConfig() );
    }
}
