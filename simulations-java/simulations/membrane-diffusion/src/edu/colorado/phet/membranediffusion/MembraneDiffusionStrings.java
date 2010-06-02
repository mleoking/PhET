/* Copyright 2008, University of Colorado */

package edu.colorado.phet.membranediffusion;

import static edu.colorado.phet.membranediffusion.MembraneDiffusionResources.getString;

/**
 * A collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 */
public class MembraneDiffusionStrings {
    
    /* not intended for instantiation */
    private MembraneDiffusionStrings() {}
    
    public static final String TITLE_MEMBRANE_DIFFUSION_MODULE = getString( "ModuleTitle.MembraneDiffusionModule" );

    public static final String POTASSIUM_CHEMICAL_SYMBOL = getString( "PotassiumChemicalSymbol" );
    public static final String SODIUM_CHEMICAL_SYMBOL = getString( "SodiumChemicalSymbol" );
    
    public static final String CONTROL = getString( "Control.title" );

    public static final String LEGEND_TITLE = getString( "Legend.title" );
    public static final String LEGEND_SODIUM_ION = getString( "Legend.sodiumIon" );
    public static final String LEGEND_POTASSIUM_ION = getString( "Legend.potassiumIon" );
    public static final String LEGEND_SODIUM_GATED_CHANNEL = getString( "Legend.sodiumGatedChannel" );
    public static final String LEGEND_POTASSIUM_GATED_CHANNEL = getString( "Legend.potassiumGatedChannel" );
    public static final String LEGEND_SODIUM_LEAK_CHANNEL = getString( "Legend.sodiumLeakChannel" );
    public static final String LEGEND_POTASSIUM_LEAK_CHANNEL = getString( "Legend.potassiumLeakChannel" );
}
