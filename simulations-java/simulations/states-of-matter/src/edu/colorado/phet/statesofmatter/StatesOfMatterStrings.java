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
    public static final String TITLE_INTERACTION_POTENTIAL_MODULE = StatesOfMatterResources.getString( "ModuleTitle.InteractionPotentialModule" );

    public static final String MOLECULE_TYPE_SELECT_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.MoleculeSelection");
    public static final String OXYGEN_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Oxygen");
    public static final String NEON_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Neon");
    public static final String ARGON_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Argon");
    public static final String WATER_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.Water");
    public static final String MONATOMIC_OXYGEN_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.MonatomicOxygen");
    public static final String ADJUSTABLE_ATTRACTION_SELECTION_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.AdjustableAttraction");

    public static final String STATE_TYPE_SELECT_LABEL = StatesOfMatterResources.getString("SolidLiquidGasControl.StateSelection");
    public static final String PHASE_STATE_SOLID = StatesOfMatterResources.getString("SolidLiquidGasControl.Solid");
    public static final String PHASE_STATE_LIQUID = StatesOfMatterResources.getString("SolidLiquidGasControl.Liquid");
    public static final String PHASE_STATE_GAS = StatesOfMatterResources.getString("SolidLiquidGasControl.Gas");
    
    public static final String PHASE_DIAGRAM_X_AXIS_LABEL = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.xAxisLabel");
    public static final String PHASE_DIAGRAM_X_ORIGIN_LABEL = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.xOriginLabel");
    public static final String PHASE_DIAGRAM_Y_AXIS_LABEL = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.yAxisLabel");
    public static final String PHASE_DIAGRAM_Y_ORIGIN_LABEL = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.yOriginLabel");
    public static final String PHASE_DIAGRAM_SOLID = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.Solid");
    public static final String PHASE_DIAGRAM_LIQUID = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.Liquid");
    public static final String PHASE_DIAGRAM_GAS = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.Gas");
    public static final String PHASE_DIAGRAM_TRIPLE = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.Triple");
    public static final String PHASE_DIAGRAM_CRITICAL = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.Critical");
    public static final String PHASE_DIAGRAM_POINT = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.Point");
    public static final String PHASE_DIAGRAM_BUTTON_LABEL = StatesOfMatterResources.getString("PhaseChanges.PhaseDiagram.ButtonLabel");
    
    public static final String INTERACTION_POTENTIAL_GRAPH_X_AXIS_LABEL = StatesOfMatterResources.getString("PhaseChanges.InteractionPotentialGraph.xAxisLabel");
    public static final String INTERACTION_POTENTIAL_GRAPH_Y_AXIS_LABEL = StatesOfMatterResources.getString("PhaseChanges.InteractionPotentialGraph.yAxisLabel");
    public static final String INTERACTION_POTENTIAL_BUTTON_LABEL = StatesOfMatterResources.getString("PhaseChanges.InteractionPotentialGraph.ButtonLabel");

    public static final String GRAVITY_CONTROL_TITLE = StatesOfMatterResources.getString("GravityControl.Title");
    public static final String GRAVITY_CONTROL_NONE = StatesOfMatterResources.getString("GravityControl.None");
    public static final String GRAVITY_CONTROL_LOTS = StatesOfMatterResources.getString("GravityControl.Lots");

    public static final String ATOM_DIAMETER_CONTROL_TITLE = StatesOfMatterResources.getString("AtomDiameterControl.Title");
    public static final String INTERACTION_STRENGTH_CONTROL_TITLE = StatesOfMatterResources.getString("InteractionStrengthControl.Title");
    public static final String SHOW_FORCES = StatesOfMatterResources.getString("InteractionPotential.ShowForces");
}
