// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.util;

/**
 * Utilities for converting between different units.
 * These are the common conversions required when going between model and view.
 * <p>
 * NOTE: The design of this sim chose to specify the model in units that aren't really
 * appropriate for the scale of this topic.  But we thought it best to keep those units
 * when implementing the model, so that the implementation matches the specification.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class UnitsUtils {

    private static final int MILLIMETERS_PER_METER = 1000;
    private static final double PICOFARADS_PER_FARAD = 1E12;

    private UnitsUtils() {}

    public static double metersToMillimeters( double d ) {
        return d * MILLIMETERS_PER_METER;
    }

    public static double millimetersToMeters( double d ) {
        return d / MILLIMETERS_PER_METER;
    }

    public static double metersSquaredToMillimetersSquared( double d ) {
        return d * ( MILLIMETERS_PER_METER * MILLIMETERS_PER_METER );
    }

    public static double millimetersSquaredToMetersSquared( double d ) {
        return d / ( MILLIMETERS_PER_METER * MILLIMETERS_PER_METER );
    }

    public static double faradsToPicofarads( double d ) {
        return d * PICOFARADS_PER_FARAD;
    }

    public static double picofaradsToFarads( double d ) {
        return d / PICOFARADS_PER_FARAD;
    }
}
