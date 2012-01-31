// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molarity;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityResources {

    public static final String PROJECT_NAME = "molarity";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    // Strings
    public static class Strings {
        public static final String COBALT_II_NITRATE = RESOURCES.getLocalizedString( "cobaltIINitrate" );
        public static final String COBALT_CHLORIDE = RESOURCES.getLocalizedString( "cobaltChloride" );
        public static final String COPPER_SULFATE = RESOURCES.getLocalizedString( "copperSulfate" );
        public static final String FULL = RESOURCES.getLocalizedString( "full" );
        public static final String GOLD_III_CHLORIDE = RESOURCES.getLocalizedString( "goldIIIChloride" );
        public static final String HIGH = RESOURCES.getLocalizedString( "high" );
        public static final String KOOL_AID = RESOURCES.getLocalizedString( "koolAid" );
        public static final String LITERS = RESOURCES.getLocalizedString( "liters" );
        public static final String LOW = RESOURCES.getLocalizedString( "low" );
        public static final String LOTS = RESOURCES.getLocalizedString( "lots" );
        public static final String NICKEL_II_CHLORIDE = RESOURCES.getLocalizedString( "nickelIIChloride" );
        public static final String NONE = RESOURCES.getLocalizedString( "none" );
        public static final String MOLES = RESOURCES.getLocalizedString( "moles" );
        public static final String MOLARITY = RESOURCES.getLocalizedString( "molarity" );
        public static final String PATTERN_0LABEL = RESOURCES.getLocalizedString( "pattern.0label" );
        public static final String PATTERN_0VALUE_1UNITS = RESOURCES.getLocalizedString( "pattern.0value.1units" );
        public static final String PATTERN_PARENTHESES_0TEXT = RESOURCES.getLocalizedString( "pattern.parentheses.0text" );
        public static final String POTASSIUM_CHROMATE = RESOURCES.getLocalizedString( "potassiumChromate" );
        public static final String POTASSIUM_DICHROMATE = RESOURCES.getLocalizedString( "potassiumDichromate" );
        public static final String POTASSIUM_PERMANGANATE = RESOURCES.getLocalizedString( "potassiumPermanganate" );
        public static final String SATURATED = RESOURCES.getLocalizedString( "saturated" );
        public static final String SHOW_VALUES = RESOURCES.getLocalizedString( "showValues" );
        public static final String SOLUTE = RESOURCES.getLocalizedString( "solute" );
        public static final String SOLUTE_AMOUNT = RESOURCES.getLocalizedString( "soluteAmount" );
        public static final String SOLUTION_CONCENTRATION = RESOURCES.getLocalizedString( "solutionConcentration" );
        public static final String SOLUTION_VOLUME = RESOURCES.getLocalizedString( "solutionVolume" );
        public static final String UNITS_LITERS = RESOURCES.getLocalizedString( "units.liters" );
        public static final String UNITS_MOLARITY = RESOURCES.getLocalizedString( "units.molarity" );
        public static final String UNITS_MOLES = RESOURCES.getLocalizedString( "units.moles" );
        public static final String ZERO = RESOURCES.getLocalizedString( "zero" );
    }

    // Images
    public static class Images {
        public static final Image BEAKER_IMAGE = RESOURCES.getImage( "beaker.png" );
    }
}
