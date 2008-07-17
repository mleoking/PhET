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
    
    public static final String STOVE_CONTROL_PANEL_TITLE = StatesOfMatterResources.getString( "Stove.Title" );
    public static final String STOVE_CONTROL_PANEL_ADD_LABEL = StatesOfMatterResources.getString( "Stove.Add" );
    public static final String STOVE_CONTROL_PANEL_REMOVE_LABEL = StatesOfMatterResources.getString( "Stove.Remove" );
    public static final String STOVE_CONTROL_PANEL_ZERO_LABEL = StatesOfMatterResources.getString( "Stove.Zero" );

    public static final String TITLE_SOLID_LIQUID_GAS_MODULE = StatesOfMatterResources.getString( "ModuleTitle.SolidLiquidGasModule" );
    public static final String TITLE_PHASE_CHANGES_MODULE = StatesOfMatterResources.getString( "ModuleTitle.PhaseChangesModule" );

    public static final String MOLECULE_TYPE_SELECT_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.MoleculeSelection");
    public static final String OXYGEN_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Oxygen");
    public static final String NEON_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Neon");
    public static final String ARGON_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Argon");
    public static final String WATER_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Water");

    public static final String STATE_TYPE_SELECT_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.StateSelection");
    public static final String PHASE_STATE_SOLID = StatesOfMatterResources.getString("SolidLiquidGasControl.Solid");
    public static final String PHASE_STATE_LIQUID = StatesOfMatterResources.getString("SolidLiquidGasControl.Liquid");
    public static final String PHASE_STATE_GAS = StatesOfMatterResources.getString("SolidLiquidGasControl.Gas");
}
