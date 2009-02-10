/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSStrings {
    
    /* not intended for instantiation */
    private ABSStrings() {}
    
    public static final String TITLE_SOLUTIONS_MODULE = ABSResources.getString( "title.solutionsModule" );
    public static final String TITLE_COMPARING_MODULE = ABSResources.getString( "title.comparingModule" );
    public static final String TITLE_MATCHING_GAME_MODULE = ABSResources.getString( "title.matchingGameModule" );
    public static final String TITLE_FIND_THE_UNKNOWN_MODULE = ABSResources.getString( "title.findTheUnknownModule" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );
    public static final String TITLE_STRENGTH = ABSResources.getString( "title.strength" );
    public static final String TITLE_SOLUTION = ABSResources.getString( "title.solution" );
    
    public static final String MESSAGE_NOT_A_CONFIG = ABSResources.getString( "message.notAConfigFile" );
    
    // labels
    public static final String LABEL_CONCENTRATION = ABSResources.getString( "label.concentration" );
    public static final String LABEL_PH = ABSResources.getString( "label.pH" );
    
    // units
    public static final String UNITS_LITERS = ABSResources.getString( "units.liters" );
    public static final String UNITS_MOLES = ABSResources.getString( "units.moles" );
    public static final String UNITS_MOLES_PER_LITER = ABSResources.getString( "units.molesPerLiter" );
    
    public static final String PURE_WATER = ABSResources.getString( "pureWater" );
    
    // acids
    public static final String STRONG_ACID = ABSResources.getString( "acid.strongAcid" );
    public static final String CUSTOM_WEAK_ACID = ABSResources.getString( "acid.customWeakAcid" );
    public static final String HYDORCHLORIC_ACID = ABSResources.getString( "acid.hydrochloricAcid" );
    public static final String HYPOCHLOROUS_ACID = ABSResources.getString( "acid.hypochlorousAcid" );
    
    // bases
    public static final String STRONG_BASE = ABSResources.getString( "base.strongBase" );
    public static final String CUSTOM_WEAK_BASE = ABSResources.getString( "base.customWeakBase" );
    public static final String METHYLAMINE = ABSResources.getString( "base.methylamine" );
    public static final String SODIUM_HYDROXIDE = ABSResources.getString( "base.sodiumHydroxide" );
}
