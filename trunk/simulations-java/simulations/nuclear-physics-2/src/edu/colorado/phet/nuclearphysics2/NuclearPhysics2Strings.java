/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2;

/**
 * TemplateStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NuclearPhysics2Strings {
    
    /* not intended for instantiation */
    private NuclearPhysics2Strings() {}
    
    public static final String TITLE_ALPHA_RADIATION_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.AlphaDecayModule" );
    public static final String TITLE_FISSION_ONE_NUCLEUS_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.SingleNucleusFissionModule" );
    public static final String TITLE_CHAIN_REACTION_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.MultipleNucleusFissionModule" );
    public static final String TITLE_NUCLEAR_REACTOR_MODULE = NuclearPhysics2Resources.getString( "ModuleTitle.ControlledReaction" );

    public static final String NEUTRON_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.NeutronLabel" );
    public static final String PROTON_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.ProtonLabel" );
    public static final String ALPHA_PARTICLE_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.AlphaParticleLabel" );

    public static final String URANIUM_235_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Uranium235Graphic.Symbol" );
    public static final String URANIUM_235_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Uranium235Graphic.Number" );
    public static final String URANIUM_235_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.Uranium235Label" );

    public static final String URANIUM_236_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Uranium236Graphic.Symbol" );
    public static final String URANIUM_236_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Uranium236Graphic.Number" );
    public static final String URANIUM_236_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.Uranium236Label" );

    public static final String URANIUM_238_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Uranium238Graphic.Symbol" );
    public static final String URANIUM_238_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Uranium238Graphic.Number" );
    public static final String URANIUM_238_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.Uranium238Label" );

    public static final String URANIUM_239_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Uranium239Graphic.Symbol" );
    public static final String URANIUM_239_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Uranium239Graphic.Number" );
    public static final String URANIUM_239_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.Uranium239Label" );

    public static final String POLONIUM_211_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Polonium211Graphic.Symbol" );
    public static final String POLONIUM_211_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Polonium211Graphic.Number" );
    public static final String POLONIUM_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.Polonium211Label" );

    public static final String LEAD_207_CHEMICAL_SYMBOL = NuclearPhysics2Resources.getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_207_ISOTOPE_NUMBER = NuclearPhysics2Resources.getString( "Lead207Graphic.Number" );
    public static final String LEAD_LEGEND_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.Lead207Label" );

    public static final String DAUGHTER_NUCLEI_LABEL = NuclearPhysics2Resources.getString( "NuclearPhysicsControlPanel.DaughterNucleiLabel" );

    public static final String DECAY_TIME_CHART_X_AXIS_LABEL = NuclearPhysics2Resources.getString( "DecayTimeChart.XAxisLabel" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL1 = NuclearPhysics2Resources.getString( "DecayTimeChart.YAxisLabel1" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL2 = NuclearPhysics2Resources.getString( "DecayTimeChart.YAxisLabel2" );
    public static final String DECAY_TIME_LABEL = NuclearPhysics2Resources.getString( "DecayTimeChart.DecayTimeLabel" );
    public static final String DECAY_TIME_UNITS = NuclearPhysics2Resources.getString( "DecayTimeChart.DecayTimeUnits" );
    public static final String DECAY_TIME_CLEAR_CHART = NuclearPhysics2Resources.getString( "DecayTimeChart.ClearChart" );
    public static final String DECAY_EVENT = NuclearPhysics2Resources.getString( "DecayTimeChart.DecayEvent" );

    public static final String DECAY_TIME_CHART_HALF_LIFE = NuclearPhysics2Resources.getString( "DecayTimeChart.HalfLife" );

    public static final String RESET_NUCLEUS = NuclearPhysics2Resources.getString( "SharedLabels.ResetNucleus" );
    
    public static final String PERCENT_FISSIONED_LABEL = NuclearPhysics2Resources.getString( "MultipleNucleusFissionControlPanel.FissionPercentLabel" );
    public static final String CONTAINMENT_VESSEL_CHECK_BOX = NuclearPhysics2Resources.getString( "MultipleNucleusFissionControlPanel.ContainmentCheckBox" );
    public static final String CONTROLS_BORDER = NuclearPhysics2Resources.getString( "MultipleNucleusFissionControlPanel.ControlBorder" );
    public static final String RESET_BUTTON_LABEL = NuclearPhysics2Resources.getString( "MultipleNucleusFissionControlPanel.ResetButton" );

    public static final String ENERGY_GRAPHS_CHECK_BOX = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.EnergyGraphControl" );
    public static final String FIRE_NEUTRONS_BUTTON_LABEL = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.FireButton" );
    public static final String CONTROL_ROD_ADJUSTER_LABEL = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.ControlRodAdjuster" );
    public static final String ENERGY_GRAPH_LABEL = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.EnergyGraphLabel" );
    public static final String ENERGY_GRAPH_UNITS = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.EnergyGraphUnits" );
    public static final String POWER_GRAPH_LABEL = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.PowerGraphLabel" );
    public static final String POWER_GRAPH_UNITS = NuclearPhysics2Resources.getString( "ControlledFissionControlPanel.PowerGraphUnits" );
}
