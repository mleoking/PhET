/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics;

/**
 * NuclearPhysicsStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author John Blanco
 */
public class NuclearPhysicsStrings {
    
    /* not intended for instantiation */
    private NuclearPhysicsStrings() {}
    
    public static final String TITLE_SINGLE_ATOM_ALPHA_DECAY_MODULE = NuclearPhysicsResources.getString( "ModuleTitle.SingleAtomAlphaDecayModule" );
    public static final String TITLE_MULTI_ATOM_ALPHA_DECAY_MODULE = NuclearPhysicsResources.getString( "ModuleTitle.MultiAtomAlphaDecayModule" );
    public static final String TITLE_FISSION_ONE_NUCLEUS_MODULE = NuclearPhysicsResources.getString( "ModuleTitle.SingleNucleusFissionModule" );
    public static final String TITLE_CHAIN_REACTION_MODULE = NuclearPhysicsResources.getString( "ModuleTitle.MultipleNucleusFissionModule" );
    public static final String TITLE_NUCLEAR_REACTOR_MODULE = NuclearPhysicsResources.getString( "ModuleTitle.ControlledReaction" );

    public static final String MENU_OPTIONS = NuclearPhysicsResources.getString( "menu.options" );

    public static final String LEGEND_BORDER_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.LegendBorder" );
    public static final String NUCLEUS_SELECTION_BORDER_LABEL = NuclearPhysicsResources.getString( "AlphaDecayControlPanel.NucleusSelectionBorder" );

    public static final String NEUTRON_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.NeutronLabel" );
    public static final String PROTON_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.ProtonLabel" );
    public static final String ALPHA_PARTICLE_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.AlphaParticleLabel" );

    public static final String URANIUM_235_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Uranium235Graphic.Symbol" );
    public static final String URANIUM_235_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Uranium235Graphic.Number" );
    public static final String URANIUM_235_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Uranium235Label" );

    public static final String URANIUM_236_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Uranium236Graphic.Symbol" );
    public static final String URANIUM_236_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Uranium236Graphic.Number" );
    public static final String URANIUM_236_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Uranium236Label" );

    public static final String URANIUM_238_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Uranium238Graphic.Symbol" );
    public static final String URANIUM_238_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Uranium238Graphic.Number" );
    public static final String URANIUM_238_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Uranium238Label" );

    public static final String URANIUM_239_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Uranium239Graphic.Symbol" );
    public static final String URANIUM_239_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Uranium239Graphic.Number" );
    public static final String URANIUM_239_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Uranium239Label" );

    public static final String POLONIUM_211_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Polonium211Graphic.Symbol" );
    public static final String POLONIUM_211_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Polonium211Graphic.Number" );
    public static final String POLONIUM_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Polonium211Label" );

    public static final String LEAD_207_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_207_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Lead207Graphic.Number" );
    public static final String LEAD_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Lead207Label" );

    public static final String DAUGHTER_NUCLEI_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.DaughterNucleiLabel" );

    public static final String DECAY_TIME_CHART_X_AXIS_LABEL = NuclearPhysicsResources.getString( "DecayTimeChart.XAxisLabel" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL1 = NuclearPhysicsResources.getString( "DecayTimeChart.YAxisLabel1" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL2 = NuclearPhysicsResources.getString( "DecayTimeChart.YAxisLabel2" );
    public static final String DECAY_TIME_LABEL = NuclearPhysicsResources.getString( "DecayTimeChart.DecayTimeLabel" );
    public static final String DECAY_TIME_UNITS = NuclearPhysicsResources.getString( "DecayTimeChart.DecayTimeUnits" );
    public static final String DECAY_TIME_CLEAR_CHART = NuclearPhysicsResources.getString( "DecayTimeChart.ClearChart" );
    public static final String DECAY_EVENT = NuclearPhysicsResources.getString( "DecayTimeChart.DecayEvent" );

    public static final String DECAY_TIME_CHART_HALF_LIFE = NuclearPhysicsResources.getString( "DecayTimeChart.HalfLife" );

    public static final String RESET_NUCLEUS = NuclearPhysicsResources.getString( "SharedLabels.ResetNucleus" );
    
    public static final String PERCENT_FISSIONED_LABEL = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.FissionPercentLabel" );
    public static final String CONTAINMENT_VESSEL_CHECK_BOX = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ContainmentCheckBox" );
    public static final String MULTI_NUCLEUS_CONTROLS_BORDER = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ControlBorder" );
    public static final String RESET_BUTTON_LABEL = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ResetButton" );
    public static final String EXPLOSION_LABEL = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ExplosionLabel" );

    public static final String ENERGY_GRAPHS_CHECK_BOX = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.EnergyGraphControl" );
    public static final String FIRE_NEUTRONS_BUTTON_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.FireButton" );
    public static final String CONTROL_ROD_ADJUSTER_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.ControlRodAdjuster" );
    public static final String ENERGY_GRAPH_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.EnergyGraphLabel" );
    public static final String ENERGY_GRAPH_UNITS = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.EnergyGraphUnits" );
    public static final String POWER_GRAPH_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.PowerGraphLabel" );
    public static final String POWER_GRAPH_UNITS = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.PowerGraphUnits" );
    public static final String REACTOR_CONTROLS_BORDER = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.ControlBorder" );
    
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_1 = NuclearPhysicsResources.getString( "PotentialProfilePanel.YAxisLabel1" );
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_2 = NuclearPhysicsResources.getString( "PotentialProfilePanel.YAxisLabel2" );
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_3 = NuclearPhysicsResources.getString( "PotentialProfilePanel.YAxisLabel3" );
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_4 = NuclearPhysicsResources.getString( "PotentialProfilePanel.YAxisLabel4" );
    public static final String POTENTIAL_PROFILE_X_AXIS_LABEL = NuclearPhysicsResources.getString( "PotentialProfilePanel.XAxisLabel" );
    public static final String POTENTIAL_PROFILE_LEGEND_TITLE = NuclearPhysicsResources.getString( "PotentialProfilePanel.legend.title" );
    public static final String POTENTIAL_PROFILE_POTENTIAL_ENERGY = NuclearPhysicsResources.getString( "PotentialProfilePanel.legend.PotentialEnergy" );
    public static final String POTENTIAL_PROFILE_TOTAL_ENERGY = NuclearPhysicsResources.getString( "PotentialProfilePanel.legend.TotalEnergy" );
    
    public static final String REACTOR_PICTURE_CAPTION = NuclearPhysicsResources.getString( "NuclearReactorCorePhoto.Caption" );
    public static final String SHOW_REACTOR_IMAGE = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.ShowReactorImage" );

    public static final String U235_LABEL= NuclearPhysicsResources.getString( "ChainReactionControlPanel.U235" );
    public static final String U238_LABEL= NuclearPhysicsResources.getString( "ChainReactionControlPanel.U238" );
    public static final String NUCLEI_LABEL= NuclearPhysicsResources.getString( "ChainReactionControlPanel.Nuclei" );
}
