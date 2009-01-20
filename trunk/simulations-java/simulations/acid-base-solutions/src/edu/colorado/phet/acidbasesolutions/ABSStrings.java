/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Collection of localized & unlocalized strings used by this simulations.
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
    public static final String MESSAGE_NOT_A_CONFIG = ABSResources.getString( "message.notAConfigFile" );
    
    public static final String LABEL_PH = ABSResources.getString( "label.pH" );
    
    public static final String UNITS_LITERS = ABSResources.getString( "units.liters" );
    public static final String UNITS_MOLES = ABSResources.getString( "units.moles" );
    public static final String UNITS_MOLES_PER_LITER = ABSResources.getString( "units.molesPerLiter" );
    
    // equations that do not require localization
    public static final String LABEL_H2O = "H<sub>2</sub>O";
    public static final String LABEL_H3O = "H<sub>3</sub>O<sup>+</sup>";
    public static final String LABEL_OH = "OH<sup>-</sup>";
}
