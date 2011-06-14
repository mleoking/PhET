// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;

/**
 * Translated strings for "Bending Light" are loaded eagerly to make sure everything exists on sim startup.
 *
 * @author Sam Reid
 */
public class BendingLightStrings {
    public static final String AIR = getString( "air" );
    public static final String MISS = getString( "miss" );
    public static final String MATERIAL = getString( "material" );
    public static final String OBJECTS = getString( "objects" );
    public static final String ENVIRONMENT = getString( "environment" );
    public static final String INDEX_OF_REFRACTION_COLON = getString( "indexOfRefractionColon" );
    public static final String INDEX_OF_REFRACTION = getString( "indexOfRefraction" );
    public static final String LOW = getString( "low" );
    public static final String HIGH = getString( "high" );
    public static final String N_UNKNOWN = getString( "nUnknown" );
    public static final String CUSTOM = getString( "custom" );
    public static final String WATER = getString( "water" );
    public static final String INTENSITY = getString( "intensity" );
    public static final String LASER_VIEW = getString( "laserView" );
    public static final String RAY = getString( "ray" );
    public static final String WAVE = getString( "wave" );
    public static final String GLASS = getString( "glass" );
    public static final String DIAMOND = getString( "diamond" );
    public static final String MYSTERY_A = getString( "mysteryA" );
    public static final String MYSTERY_B = getString( "mysteryB" );
    public static final String TOOLBOX = getString( "toolbox" );
    public static final String SHOW_NORMAL = getString( "showNormal" );
    public static final String INTRO = getString( "intro" );
    public static final String PRISMS = getString( "prisms" );
    public static final String ONE_COLOR = getString( "oneColor" );
    public static final String WHITE_LIGHT = getString( "whiteLight" );
    public static final String SINGLE_RAY = getString( "singleRay" );
    public static final String MULTIPLE_RAYS = getString( "multipleRays" );
    public static final String SHOW_REFLECTIONS = getString( "showReflections" );
    public static final String SHOW_PROTRACTOR = getString( "showProtractor" );
    public static final String PRISM_BREAK = getString( "prismBreak" );
    public static final String TIME = getString( "time" );
    public static final String MORE_TOOLS = getString( "moreTools" );

    //Common Strings
    public static final String RESET = PhetCommonResources.getString( PhetCommonResources.STRING_RESET_ALL );

    //String patterns
    public static final String PATTERN_SPEED_OF_LIGHT_READOUT_VALUE_C = getString( "pattern.value_c" );

    //Method to get the translated string for the specified key
    public static String getString( String key ) {
        return RESOURCES.getLocalizedString( key );
    }
}
