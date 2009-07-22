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
    public static final String TITLE_RADIOMETRIC_ELEMENT_HALF_LIFE = NuclearPhysicsResources.getString( "ModuleTitle.RadiometricElementHalfLife" );
    public static final String TITLE_RADIOMETRIC_MEASUREMENT = NuclearPhysicsResources.getString( "ModuleTitle.RadiometricMeasurement" );
    public static final String TITLE_RADIOACTIVE_DATING_GAME = NuclearPhysicsResources.getString( "ModuleTitle.RadioactiveDatingGame" );
    public static final String TITLE_LOTS_OF_NUCLEI_DECAYING = NuclearPhysicsResources.getString( "ModuleTitle.LotsOfNucleiDecaying" );

    public static final String MENU_OPTIONS = NuclearPhysicsResources.getString( "menu.options" );

    public static final String LEGEND_BORDER_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.LegendBorder" );
    public static final String NUCLEUS_SELECTION_BORDER_LABEL = NuclearPhysicsResources.getString( "AlphaDecayControlPanel.NucleusSelectionBorder" );
    public static final String ISOTOPE_SELECTION_BORDER_LABEL = NuclearPhysicsResources.getString( "HalfLifeControlPanel.IsotopeSelectionBorder" );

    public static final String NEUTRON_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.NeutronLabel" );
    public static final String PROTON_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.ProtonLabel" );
    public static final String ALPHA_PARTICLE_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.AlphaParticleLabel" );
    public static final String ELECTRON_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.ElectronLabel" );
    public static final String ANTINEUTRINO_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.AntineutrinoLabel" );

    public static final String CARBON_14_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Carbon14Graphic.Symbol" );
    public static final String CARBON_14_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Carbon14Graphic.Number" );
    public static final String CARBON_14_LEGEND_LABEL = NuclearPhysicsResources.getString( "Carbon14Label" );

    public static final String NITROGEN_14_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Nitrogen14Graphic.Symbol" );
    public static final String NITROGEN_14_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Nitrogen14Graphic.Number" );
    public static final String NITROGEN_14_LEGEND_LABEL = NuclearPhysicsResources.getString( "Nitrogen14Label" );

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
    public static final String POLONIUM_211_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Polonium211Label" );

    public static final String LEAD_207_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_207_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Lead207Graphic.Number" );
    public static final String LEAD_207_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Lead207Label" );

    public static final String LEAD_206_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_206_ISOTOPE_NUMBER = NuclearPhysicsResources.getString( "Lead207Graphic.Number" );
    public static final String LEAD_206_LEGEND_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.Lead207Label" );

    public static final String CUSTOM_NUCLEUS_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "AlphaDecayControlPanel.CustomNucleusSymbol" );
    public static final String CUSTOM_NUCLEUS_LEGEND_LABEL = NuclearPhysicsResources.getString( "AlphaDecayControlPanel.CustomNucleusLabel" );
    public static final String DECAYED_CUSTOM_NUCLEUS_LEGEND_LABEL = NuclearPhysicsResources.getString( "AlphaDecayControlPanel.DecayedCustomNucleusLabel" );
    public static final String DECAYED_CUSTOM_NUCLEUS_CHEMICAL_SYMBOL = NuclearPhysicsResources.getString( "AlphaDecayControlPanel.DecayedCustomNucleusSymbol" );

    public static final String CUSTOM_PARENT_NUCLEUS_LABEL = NuclearPhysicsResources.getString( "HalfLifeControlPanel.CustomParentNucleusLabel" );
    public static final String CUSTOM_DAUGHTER_NUCLEUS_LABEL = NuclearPhysicsResources.getString( "HalfLifeControlPanel.CustomDaughterNucleusLabel" );

    public static final String RESET_ALL_NUCLEI = NuclearPhysicsResources.getString( "MultipleNucleiAlphaDecay.ResetAllNuclei" );
    public static final String ADD_TEN = NuclearPhysicsResources.getString( "MultipleNucleiAlphaDecay.AddTen" );
    
    public static final String DAUGHTER_NUCLEI_LABEL = NuclearPhysicsResources.getString( "NuclearPhysicsControlPanel.DaughterNucleiLabel" );

    public static final String DECAY_TIME_CHART_X_AXIS_LABEL = NuclearPhysicsResources.getString( "DecayTimeChart.XAxisLabel" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL1 = NuclearPhysicsResources.getString( "DecayTimeChart.YAxisLabel1" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL2 = NuclearPhysicsResources.getString( "DecayTimeChart.YAxisLabel2" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL_ISOTOPE = NuclearPhysicsResources.getString( "DecayTimeChart.Isotope" );
    public static final String DECAY_TIME_LABEL = NuclearPhysicsResources.getString( "DecayTimeChart.DecayTimeLabel" );
    public static final String DECAY_TIME_UNITS = NuclearPhysicsResources.getString( "DecayTimeChart.DecayTimeUnits" );
    public static final String DECAY_TIME_CLEAR_CHART = NuclearPhysicsResources.getString( "DecayTimeChart.ClearChart" );
    public static final String DECAY_EVENT = NuclearPhysicsResources.getString( "DecayTimeChart.DecayEvent" );

    public static final String HALF_LIFE_LABEL = NuclearPhysicsResources.getString( "HalfLife" );
    public static final String HALF_LIVES_LABEL = NuclearPhysicsResources.getString( "HalfLives" );

    public static final String RESET_NUCLEUS = NuclearPhysicsResources.getString( "SharedLabels.ResetNucleus" );
    public static final String RESET_NUCLEI = NuclearPhysicsResources.getString( "SharedLabels.ResetNuclei" );
    
    public static final String DECAY_ENERGY_PROFILE_X_AXIS_LABEL = NuclearPhysicsResources.getString( "DecayEnergyProfilePanel.XAxisLabel" );
    public static final String DECAY_ENERGY_PROFILE_Y_AXIS_LABEL = NuclearPhysicsResources.getString( "DecayEnergyProfilePanel.YAxisLabel" );

    public static final String PERCENT_FISSIONED_LABEL = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.FissionPercentLabel" );
    public static final String CONTAINMENT_VESSEL_CHECK_BOX = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ContainmentCheckBox" );
    public static final String MULTI_NUCLEUS_CONTROLS_BORDER = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ControlBorder" );
    public static final String RESET_BUTTON_LABEL = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ResetButton" );
    public static final String EXPLOSION_LABEL = NuclearPhysicsResources.getString( "MultipleNucleusFissionControlPanel.ExplosionLabel" );

    public static final String FIRE_NEUTRONS = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.FireButton" );
    public static final String CONTROL_ROD_ADJUSTER_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.ControlRodAdjuster" );
    public static final String ENERGY_GRAPH_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.EnergyGraphLabel" );
    public static final String ENERGY_GRAPH_UNITS = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.EnergyGraphUnits" );
    public static final String POWER_GRAPH_LABEL = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.PowerGraphLabel" );
    public static final String POWER_GRAPH_UNITS = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.PowerGraphUnits" );
    public static final String REACTOR_CONTROLS_BORDER = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.ControlBorder" );
    public static final String ENERGY_GRAPHS_TITLE = NuclearPhysicsResources.getString( "ControlledFissionControlPanel.EnergyGraphs" );
    
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

    public static final String READOUT_UNITS_MILLISECONDS= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Milliseconds" );
    public static final String READOUT_UNITS_SECONDS= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Seconds" );
    public static final String READOUT_UNITS_MINUTES= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Minutes" );
    public static final String READOUT_UNITS_HOURS= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Hours" );
    public static final String READOUT_UNITS_DAYS= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Days" );
    public static final String READOUT_UNITS_YEARS= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Years" );
    public static final String READOUT_UNITS_YRS= NuclearPhysicsResources.getString( "DecayTimeChart.Units.Yrs" );
    
    public static final String TIME_GRAPH_UNITS_MILLISECONDS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Milliseconds" );
    public static final String TIME_GRAPH_UNITS_SECONDS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Seconds" );
    public static final String TIME_GRAPH_UNITS_MINUTES= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Minutes" );
    public static final String TIME_GRAPH_UNITS_HOURS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Hours" );
    public static final String TIME_GRAPH_UNITS_DAYS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Days" );
    public static final String TIME_GRAPH_UNITS_YRS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Yrs" );
    public static final String TIME_GRAPH_UNITS_MILLENIA= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.Millenia" );
    public static final String TIME_GRAPH_UNITS_MILLION_YRS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.MillionYrs" );
    public static final String TIME_GRAPH_UNITS_BILLION_YRS= NuclearPhysicsResources.getString( "LogarithmicTimeLine.Units.BillionYrs" );

    public static final String MEASUREMENT_CONTROL_PANEL_TITLE= NuclearPhysicsResources.getString( "RadiometericMeasurementControlPanel.Title" );
    public static final String TREE_LABEL= NuclearPhysicsResources.getString( "RadiometericMeasurementControlPanel.Tree" );
    public static final String ROCK_LABEL= NuclearPhysicsResources.getString( "RadiometericMeasurementControlPanel.Rock" );
    public static final String PLANT_TREE= NuclearPhysicsResources.getString( "RadiometericMeasurement.PlantTree" );
    public static final String KILL_TREE= NuclearPhysicsResources.getString( "RadiometericMeasurement.KillTree" );
    public static final String ERUPT_VOLCANO= NuclearPhysicsResources.getString( "RadiometericMeasurement.EruptVolcano" );
    public static final String COOL_ROCK= NuclearPhysicsResources.getString( "RadiometericMeasurement.CoolRock" );

    public static final String INFINITY_SYMBOL= NuclearPhysicsResources.getString( "InfinitySymbol" );

    public static final String BUCKET_LABEL_POLONIUM= NuclearPhysicsResources.getString( "Bucket.Label.Polonium" );
    public static final String BUCKET_LABEL_ATOMS= NuclearPhysicsResources.getString( "Bucket.Label.Atoms" );

    public static final String TWENTY_FIVE_PER_CENT= NuclearPhysicsResources.getString( "DecayProprotionsChart.TwentyFivePercent" );
    public static final String FIFTY_PER_CENT= NuclearPhysicsResources.getString( "DecayProprotionsChart.FiftyPercent" );
    public static final String SEVENTY_FIVE_PER_CENT= NuclearPhysicsResources.getString( "DecayProprotionsChart.SeventyFivePercent" );
    public static final String ONE_HUNDRED_PER_CENT= NuclearPhysicsResources.getString( "DecayProprotionsChart.OneHundredPercent" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_YEARS= NuclearPhysicsResources.getString( "DecayProportionsChart.Units.Years" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_MILLION_YEARS= NuclearPhysicsResources.getString( "DecayProportionsChart.Units.MillionYears" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_BILLION_YEARS= NuclearPhysicsResources.getString( "DecayProportionsChart.Units.BillionYears" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_MILLION_YEARS_ABBREV= NuclearPhysicsResources.getString( "DecayProportionsChart.Units.MillionYearsAbbrev" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_BILLION_YEARS_ABBREV= NuclearPhysicsResources.getString( "DecayProportionsChart.Units.BillionYearsAbbrev" );
    public static final String DECAY_PROPORTIONS_Y_AXIS_LABEL= NuclearPhysicsResources.getString( "DecayProportionsChart.YAxisLabel" );
    
    public static final String TIME_ABBREVIATION= NuclearPhysicsResources.getString( "TimeAbbreviation" );
    
    public static final String CHECK_AGE=NuclearPhysicsResources.getString("DatingGame.CheckAge");
    public static final String RESET_GUESSES = NuclearPhysicsResources.getString( "DatingGame.ResetGuesses" );
    
}
