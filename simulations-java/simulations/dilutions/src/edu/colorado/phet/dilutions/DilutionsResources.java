// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import java.awt.Image;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionsResources {

    public static final String PROJECT_NAME = "dilutions";
    public static final String MOLARITY_FLAVOR = "molarity";
    public static final String DILUTIONS_FLAVOR = "dilutions";
    public static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    // Strings
    public static class Strings {
        public static final String BEERS_LAW = RESOURCES.getLocalizedString( "beersLaw" );
        public static final String BEERS_LAW_LAB = RESOURCES.getLocalizedString( "beersLawLab" );
        public static final String BIG = RESOURCES.getLocalizedString( "big" );
        public static final String COBALT_II_NITRATE = RESOURCES.getLocalizedString( "cobaltIINitrate" );
        public static final String COBALT_CHLORIDE = RESOURCES.getLocalizedString( "cobaltChloride" );
        public static final String CONCENTRATION_M1 = RESOURCES.getLocalizedString( "concentrationM1" );
        public static final String CONCENTRATION_M2 = RESOURCES.getLocalizedString( "concentrationM2" );
        public static final String COPPER_SULFATE = RESOURCES.getLocalizedString( "copperSulfate" );
        public static final String DILUTION = RESOURCES.getLocalizedString( "dilution" );
        public static final String EMPTY = RESOURCES.getLocalizedString( "empty" );
        public static final String FULL = RESOURCES.getLocalizedString( "full" );
        public static final String GOLD_III_CHLORIDE = RESOURCES.getLocalizedString( "goldIIIChloride" );
        public static final String HIGH = RESOURCES.getLocalizedString( "high" );
        public static final String KOOL_AID = RESOURCES.getLocalizedString( "koolAid" );
        public static final String LOW = RESOURCES.getLocalizedString( "low" );
        public static final String LOTS = RESOURCES.getLocalizedString( "lots" );
        public static final String NICKEL_II_CHLORIDE = RESOURCES.getLocalizedString( "nickelIIChloride" );
        public static final String NONE = RESOURCES.getLocalizedString( "none" );
        public static final String MAKE_DILUTIONS = RESOURCES.getLocalizedString( "makeDilutions" );
        public static final String MOLARITY = RESOURCES.getLocalizedString( "molarity" );
        public static final String PATTERN_0LABEL = RESOURCES.getLocalizedString( "pattern.0label" );
        public static final String POTASSIUM_CHROMATE = RESOURCES.getLocalizedString( "potassiumChromate" );
        public static final String POTASSIUM_DICHROMATE = RESOURCES.getLocalizedString( "potassiumDichromate" );
        public static final String POTASSIUM_PERMANGANATE = RESOURCES.getLocalizedString( "potassiumPermanganate" );
        public static final String SATURATED = RESOURCES.getLocalizedString( "saturated" );
        public static final String SMALL = RESOURCES.getLocalizedString( "small" );
        public static final String SOLUTE = RESOURCES.getLocalizedString( "solute" );
        public static final String SOLUTE_AMOUNT = RESOURCES.getLocalizedString( "soluteAmount" );
        public static final String SOLUTION = RESOURCES.getLocalizedString( "solution" );
        public static final String SOLUTION_CONCENTRATION = RESOURCES.getLocalizedString( "solutionConcentration" );
        public static final String SOLUTION_VOLUME = RESOURCES.getLocalizedString( "solutionVolume" );
        public static final String VOLUME_V1 = RESOURCES.getLocalizedString( "volumeV1" );
        public static final String VOLUME_V2 = RESOURCES.getLocalizedString( "volumeV2" );
        public static final String ZERO = RESOURCES.getLocalizedString( "zero" );
    }

    // Symbols, no i18n needed
    public static class Symbols {
        public static final String COBALT_II_NITRATE = ChemUtils.toSubscript( "Co(NO3)2" );
        public static final String COBALT_CHLORIDE = ChemUtils.toSubscript( "CoCl2" );
        public static final String COPPER_SULFATE = ChemUtils.toSubscript( "CuSO4" );
        public static final String GOLD_III_CHLORIDE = ChemUtils.toSubscript( "AuCl3" );
        public static final String KOOL_AID = Strings.KOOL_AID;
        public static final String NICKEL_II_CHLORIDE = ChemUtils.toSubscript( "NiCl2" );
        public static final String POTASSIUM_CHROMATE = ChemUtils.toSubscript( "K2CrO4" );
        public static final String POTASSIUM_DICHROMATE = ChemUtils.toSubscript( "K2Cr2O7" );
        public static final String POTASSIUM_PERMANGANATE = ChemUtils.toSubscript( "KMnO4" );
        public static final String WATER = ChemUtils.toSubscript( "H2O" );
    }

    // Images
    public static class Images {
        public static final Image BEAKER_IMAGE = RESOURCES.getImage( "beaker.png" );
    }
}
