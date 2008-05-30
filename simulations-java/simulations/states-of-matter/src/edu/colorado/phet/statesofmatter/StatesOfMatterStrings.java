/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter;


/**
 * StatesOfMatterStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author John Blanco
 */
public class StatesOfMatterStrings {
    
    /* Not intended for instantiation. */
    private StatesOfMatterStrings() {}
    
    public static final String TITLE_SOLID_LIQUID_GAS_MODULE = StatesOfMatterResources.getString( "ModuleTitle.SolidLiquidGasModule" );

    public static final String MOLECULE_TYPE_SELECT_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.MoleculeSelectionLabel" );
    public static final String OXYGEN_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.OxygenLabel" );
    public static final String NITROGEN_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.NitrogenLabel" );
    public static final String CARBON_DIOXIDE_SELECTION_LABEL = StatesOfMatterResources.getString( "SolidLiquidGasControl.CarbonDioxideLabel" );

}
