/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics;

import static edu.colorado.phet.nuclearphysics.NuclearPhysicsResources.getString;

/**
 * NuclearPhysicsStrings is the collection of localized strings used by this simulations.
 * We load all strings as statics so that we will be warned at startup time of any missing strings.
 *
 * @author John Blanco
 */
public class NuclearPhysicsStrings {
    
    /* not intended for instantiation */
    private NuclearPhysicsStrings() {}
    
    public static final String TITLE_SINGLE_ATOM_ALPHA_DECAY_MODULE = getString( "ModuleTitle.SingleAtomAlphaDecayModule" );
    public static final String TITLE_MULTI_ATOM_ALPHA_DECAY_MODULE = getString( "ModuleTitle.MultiAtomAlphaDecayModule" );
    public static final String TITLE_SINGLE_ATOM_BETA_DECAY_MODULE = getString( "ModuleTitle.SingleAtomBetaDecayModule" );
    public static final String TITLE_MULTI_ATOM_BETA_DECAY_MODULE = getString( "ModuleTitle.MultiAtomBetaDecayModule" );
    public static final String TITLE_FISSION_ONE_NUCLEUS_MODULE = getString( "ModuleTitle.SingleNucleusFissionModule" );
    public static final String TITLE_CHAIN_REACTION_MODULE = getString( "ModuleTitle.MultipleNucleusFissionModule" );
    public static final String TITLE_NUCLEAR_REACTOR_MODULE = getString( "ModuleTitle.ControlledReaction" );
    public static final String TITLE_RADIOMETRIC_ELEMENT_HALF_LIFE = getString( "ModuleTitle.RadiometricElementHalfLife" );
    public static final String TITLE_RADIOMETRIC_MEASUREMENT = getString( "ModuleTitle.RadiometricMeasurement" );
    public static final String TITLE_RADIOACTIVE_DATING_GAME = getString( "ModuleTitle.RadioactiveDatingGame" );
    public static final String TITLE_LOTS_OF_NUCLEI_DECAYING = getString( "ModuleTitle.LotsOfNucleiDecaying" );

    public static final String LEGEND_BORDER_LABEL = getString( "NuclearPhysicsControlPanel.LegendBorder" );
    public static final String NUCLEUS_SELECTION_BORDER_LABEL = getString( "AlphaDecayControlPanel.NucleusSelectionBorder" );
    public static final String ISOTOPE_SELECTION_BORDER_LABEL = getString( "HalfLifeControlPanel.IsotopeSelectionBorder" );

    public static final String NEUTRON_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.NeutronLabel" );
    public static final String PROTON_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.ProtonLabel" );
    public static final String ALPHA_PARTICLE_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.AlphaParticleLabel" );
    public static final String ELECTRON_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.ElectronLabel" );
    public static final String ANTINEUTRINO_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.AntineutrinoLabel" );

    public static final String HYDROGEN_3_CHEMICAL_SYMBOL = getString( "Hydrogen3Graphic.Symbol" );
    public static final String HYDROGEN_3_ISOTOPE_NUMBER = getString( "Hydrogen3Graphic.Number" );
    public static final String HYDROGEN_3_LEGEND_LABEL = getString( "Hydrogen3Label" );

    public static final String HELIUM_3_CHEMICAL_SYMBOL = getString( "Helium3Graphic.Symbol" );
    public static final String HELIUM_3_ISOTOPE_NUMBER = getString( "Helium3Graphic.Number" );
    public static final String HELIUM_3_LEGEND_LABEL = getString( "Helium3Label" );

    public static final String CARBON_14_CHEMICAL_SYMBOL = getString( "Carbon14Graphic.Symbol" );
    public static final String CARBON_14_ISOTOPE_NUMBER = getString( "Carbon14Graphic.Number" );
    public static final String CARBON_14_LEGEND_LABEL = getString( "Carbon14Label" );

    public static final String NITROGEN_14_CHEMICAL_SYMBOL = getString( "Nitrogen14Graphic.Symbol" );
    public static final String NITROGEN_14_ISOTOPE_NUMBER = getString( "Nitrogen14Graphic.Number" );
    public static final String NITROGEN_14_LEGEND_LABEL = getString( "Nitrogen14Label" );

    public static final String URANIUM_235_CHEMICAL_SYMBOL = getString( "Uranium235Graphic.Symbol" );
    public static final String URANIUM_235_ISOTOPE_NUMBER = getString( "Uranium235Graphic.Number" );
    public static final String URANIUM_235_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Uranium235Label" );

    public static final String URANIUM_236_CHEMICAL_SYMBOL = getString( "Uranium236Graphic.Symbol" );
    public static final String URANIUM_236_ISOTOPE_NUMBER = getString( "Uranium236Graphic.Number" );
    public static final String URANIUM_236_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Uranium236Label" );

    public static final String URANIUM_238_CHEMICAL_SYMBOL = getString( "Uranium238Graphic.Symbol" );
    public static final String URANIUM_238_ISOTOPE_NUMBER = getString( "Uranium238Graphic.Number" );
    public static final String URANIUM_238_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Uranium238Label" );

    public static final String URANIUM_239_CHEMICAL_SYMBOL = getString( "Uranium239Graphic.Symbol" );
    public static final String URANIUM_239_ISOTOPE_NUMBER = getString( "Uranium239Graphic.Number" );
    public static final String URANIUM_239_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Uranium239Label" );

    public static final String POLONIUM_211_CHEMICAL_SYMBOL = getString( "Polonium211Graphic.Symbol" );
    public static final String POLONIUM_211_ISOTOPE_NUMBER = getString( "Polonium211Graphic.Number" );
    public static final String POLONIUM_211_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Polonium211Label" );

    public static final String LEAD_207_CHEMICAL_SYMBOL = getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_207_ISOTOPE_NUMBER = getString( "Lead207Graphic.Number" );
    public static final String LEAD_207_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Lead207Label" );

    public static final String LEAD_206_CHEMICAL_SYMBOL = getString( "Lead207Graphic.Symbol" );
    public static final String LEAD_206_ISOTOPE_NUMBER = getString( "Lead207Graphic.Number" );
    public static final String LEAD_206_LEGEND_LABEL = getString( "NuclearPhysicsControlPanel.Lead207Label" );

    public static final String CUSTOM_NUCLEUS_CHEMICAL_SYMBOL = getString( "AlphaDecayControlPanel.CustomNucleusSymbol" );
    public static final String CUSTOM_NUCLEUS_LEGEND_LABEL = getString( "AlphaDecayControlPanel.CustomNucleusLabel" );
    public static final String DECAYED_CUSTOM_NUCLEUS_LEGEND_LABEL = getString( "AlphaDecayControlPanel.DecayedCustomNucleusLabel" );
    public static final String DECAYED_CUSTOM_NUCLEUS_CHEMICAL_SYMBOL = getString( "AlphaDecayControlPanel.DecayedCustomNucleusSymbol" );

    public static final String CUSTOM_PARENT_NUCLEUS_LABEL = getString( "HalfLifeControlPanel.CustomParentNucleusLabel" );
    public static final String CUSTOM_DAUGHTER_NUCLEUS_LABEL = getString( "HalfLifeControlPanel.CustomDaughterNucleusLabel" );

    public static final String RESET_ALL_NUCLEI = getString( "MultipleNucleiAlphaDecay.ResetAllNuclei" );
    public static final String ADD_TEN = getString( "MultipleNucleiAlphaDecay.AddTen" );
    
    public static final String DAUGHTER_NUCLEI_LABEL = getString( "NuclearPhysicsControlPanel.DaughterNucleiLabel" );

    public static final String DECAY_TIME_CHART_X_AXIS_LABEL = getString( "DecayTimeChart.XAxisLabel" );
    public static final String DECAY_TIME_CHART_LABEL_ATOMIC_WEIGHT = getString( "DecayTimeChart.AtomicWeight" );
    public static final String DECAY_TIME_CHART_Y_AXIS_LABEL_ISOTOPE = getString( "DecayTimeChart.Isotope" );
    public static final String DECAY_TIME_LABEL = getString( "DecayTimeChart.DecayTimeLabel" );
    public static final String DECAY_TIME_UNITS = getString( "DecayTimeChart.DecayTimeUnits" );
    public static final String DECAY_TIME_CLEAR_CHART = getString( "DecayTimeChart.ClearChart" );
    public static final String DECAY_EVENT = getString( "DecayTimeChart.DecayEvent" );

    public static final String HALF_LIFE_LABEL = getString( "HalfLife" );
    public static final String HALF_LIVES_LABEL = getString( "HalfLives" );

    public static final String RESET_NUCLEUS = getString( "SharedLabels.ResetNucleus" );
    public static final String RESET_NUCLEI = getString( "SharedLabels.ResetNuclei" );
    public static final String SOUND_ENABLED = getString( "SharedLabels.SoundEnabled" );
    
    public static final String DECAY_ENERGY_PROFILE_X_AXIS_LABEL = getString( "DecayEnergyProfilePanel.XAxisLabel" );
    public static final String DECAY_ENERGY_PROFILE_Y_AXIS_LABEL = getString( "DecayEnergyProfilePanel.YAxisLabel" );

    public static final String PERCENT_FISSIONED_LABEL = getString( "MultipleNucleusFissionControlPanel.FissionPercentLabel" );
    public static final String CONTAINMENT_VESSEL_CHECK_BOX = getString( "MultipleNucleusFissionControlPanel.ContainmentCheckBox" );
    public static final String MULTI_NUCLEUS_CONTROLS_BORDER = getString( "MultipleNucleusFissionControlPanel.ControlBorder" );
    public static final String RESET_BUTTON_LABEL = getString( "MultipleNucleusFissionControlPanel.ResetButton" );
    public static final String EXPLOSION_LABEL = getString( "MultipleNucleusFissionControlPanel.ExplosionLabel" );

    public static final String FIRE_NEUTRONS = getString( "ControlledFissionControlPanel.FireButton" );
    public static final String CONTROL_ROD_ADJUSTER_LABEL = getString( "ControlledFissionControlPanel.ControlRodAdjuster" );
    public static final String ENERGY_GRAPH_LABEL = getString( "ControlledFissionControlPanel.EnergyGraphLabel" );
    public static final String ENERGY_GRAPH_UNITS = getString( "ControlledFissionControlPanel.EnergyGraphUnits" );
    public static final String POWER_GRAPH_LABEL = getString( "ControlledFissionControlPanel.PowerGraphLabel" );
    public static final String POWER_GRAPH_UNITS = getString( "ControlledFissionControlPanel.PowerGraphUnits" );
    public static final String REACTOR_CONTROLS_BORDER = getString( "ControlledFissionControlPanel.ControlBorder" );
    public static final String ENERGY_GRAPHS_TITLE = getString( "ControlledFissionControlPanel.EnergyGraphs" );
    
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_1 = getString( "PotentialProfilePanel.YAxisLabel1" );
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_2 = getString( "PotentialProfilePanel.YAxisLabel2" );
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_3 = getString( "PotentialProfilePanel.YAxisLabel3" );
    public static final String POTENTIAL_PROFILE_Y_AXIS_LABEL_4 = getString( "PotentialProfilePanel.YAxisLabel4" );
    public static final String POTENTIAL_PROFILE_X_AXIS_LABEL = getString( "PotentialProfilePanel.XAxisLabel" );
    public static final String POTENTIAL_PROFILE_LEGEND_TITLE = getString( "PotentialProfilePanel.legend.title" );
    public static final String POTENTIAL_PROFILE_POTENTIAL_ENERGY = getString( "PotentialProfilePanel.legend.PotentialEnergy" );
    public static final String POTENTIAL_PROFILE_TOTAL_ENERGY = getString( "PotentialProfilePanel.legend.TotalEnergy" );
    
    public static final String REACTOR_PICTURE_CAPTION = getString( "NuclearReactorCorePhoto.Caption" );
    public static final String SHOW_REACTOR_IMAGE = getString( "ControlledFissionControlPanel.ShowReactorImage" );

    public static final String U235_LABEL= getString( "ChainReactionControlPanel.U235" );
    public static final String U238_LABEL= getString( "ChainReactionControlPanel.U238" );
    public static final String NUCLEI_LABEL= getString( "ChainReactionControlPanel.Nuclei" );

    public static final String READOUT_UNITS_MILLISECONDS= getString( "DecayTimeChart.Units.Milliseconds" );
    public static final String READOUT_UNITS_SECONDS= getString( "DecayTimeChart.Units.Seconds" );
    public static final String READOUT_UNITS_MINUTES= getString( "DecayTimeChart.Units.Minutes" );
    public static final String READOUT_UNITS_HOURS= getString( "DecayTimeChart.Units.Hours" );
    public static final String READOUT_UNITS_DAYS= getString( "DecayTimeChart.Units.Days" );
    public static final String READOUT_UNITS_YEARS= getString( "DecayTimeChart.Units.Years" );
    public static final String READOUT_UNITS_YRS= getString( "DecayTimeChart.Units.Yrs" );
    
    public static final String TIME_GRAPH_UNITS_MILLISECONDS= getString( "LogarithmicTimeLine.Units.Milliseconds" );
    public static final String TIME_GRAPH_UNITS_SECONDS= getString( "LogarithmicTimeLine.Units.Seconds" );
    public static final String TIME_GRAPH_UNITS_MINUTES= getString( "LogarithmicTimeLine.Units.Minutes" );
    public static final String TIME_GRAPH_UNITS_HOURS= getString( "LogarithmicTimeLine.Units.Hours" );
    public static final String TIME_GRAPH_UNITS_DAYS= getString( "LogarithmicTimeLine.Units.Days" );
    public static final String TIME_GRAPH_UNITS_YRS= getString( "LogarithmicTimeLine.Units.Yrs" );
    public static final String TIME_GRAPH_UNITS_MILLENIA= getString( "LogarithmicTimeLine.Units.Millenia" );
    public static final String TIME_GRAPH_UNITS_MILLION_YRS= getString( "LogarithmicTimeLine.Units.MillionYrs" );
    public static final String TIME_GRAPH_UNITS_BILLION_YRS= getString( "LogarithmicTimeLine.Units.BillionYrs" );

    public static final String MEASUREMENT_CONTROL_PANEL_TITLE= getString( "RadiometericMeasurementControlPanel.Title" );
    public static final String TREE_LABEL= getString( "RadiometericMeasurementControlPanel.Tree" );
    public static final String ROCK_LABEL= getString( "RadiometericMeasurementControlPanel.Rock" );
    public static final String PLANT_TREE= getString( "RadiometericMeasurement.PlantTree" );
    public static final String KILL_TREE= getString( "RadiometericMeasurement.KillTree" );
    public static final String ERUPT_VOLCANO= getString( "RadiometericMeasurement.EruptVolcano" );
    public static final String COOL_ROCK= getString( "RadiometericMeasurement.CoolRock" );
    
    public static final String SHOW_NUCLEUS_LABELS= getString( "ControlPanel.ShowNucleusLabels" );

    public static final String MEASURE_OBJECTS= getString( "RadiometricMeter.MeasureObjects" );
    public static final String MEASURE_AIR= getString( "RadiometricMeter.MeasureAir" );
    public static final String HALF_LIFE_100_THOUSAND_YEARS= getString( "RadiometricMeter.HalfLifeOneHundredThousandYears" );
    public static final String HALF_LIFE_1_MILLION_YEARS= getString( "RadiometricMeter.HalfLifeOneMillionYears" );
    public static final String HALF_LIFE_10_MILLION_YEARS= getString( "RadiometricMeter.HalfLifeTenMillionYears" );
    public static final String HALF_LIFE_100_MILLION_YEARS= getString( "RadiometricMeter.HalfLifeOneHundredMillionYears" );

    public static final String INFINITY_SYMBOL= getString( "InfinitySymbol" );

    public static final String BUCKET_LABEL_POLONIUM= getString( "Bucket.Label.Polonium" );
    public static final String BUCKET_LABEL_ATOMS= getString( "Bucket.Label.Atoms" );

    public static final String TWENTY_FIVE_PER_CENT= getString( "DecayProprotionsChart.TwentyFivePercent" );
    public static final String FIFTY_PER_CENT= getString( "DecayProprotionsChart.FiftyPercent" );
    public static final String SEVENTY_FIVE_PER_CENT= getString( "DecayProprotionsChart.SeventyFivePercent" );
    public static final String ONE_HUNDRED_PER_CENT= getString( "DecayProprotionsChart.OneHundredPercent" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_YEARS= getString( "DecayProportionsChart.Units.Years" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_MILLION_YEARS= getString( "DecayProportionsChart.Units.MillionYears" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_BILLION_YEARS= getString( "DecayProportionsChart.Units.BillionYears" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_MILLION_YEARS_ABBREV= getString( "DecayProportionsChart.Units.MillionYearsAbbrev" );
    public static final String DECAY_PROPORTIONS_TIME_UNITS_BILLION_YEARS_ABBREV= getString( "DecayProportionsChart.Units.BillionYearsAbbrev" );
    public static final String DECAY_PROPORTIONS_Y_AXIS_LABEL_PERCENT_PRESENT= getString( "DecayProportionsChart.YAxisLabelPercentPresent" );
    public static final String DECAY_PROPORTIONS_Y_AXIS_LABEL_RATIO_C14_TO_C12= getString( "DecayProportionsChart.YAxisLabelRatioC14ToC12" );
    public static final String DECAY_PROPORTIONS_PERCENT_C14= getString( "DecayProportionsChart.PercentC14" );
    public static final String DECAY_PROPORTIONS_C14_C12_RATIO= getString( "DecayProportionsChart.C14C12Ratio" );
    
    public static final String TIME_ABBREVIATION= getString( "TimeAbbreviation" );
    
    public static final String GUESS_PROMPT=getString("DatingGame.Prompt");
    public static final String CHECK_GUESS=getString("DatingGame.CheckGuess");
    public static final String RESET_GUESSES = getString( "DatingGame.ResetGuesses" );
    public static final String GUESSES_CORRECT_TITLE = getString( "DatingGame.GuessesCorrectTitle" );
    public static final String GUESSES_CORRECT_MESSAGE = getString( "DatingGame.GuessesCorrectMessage" );
    
    public static final String METER_PROBE_TYPE = getString( "Meter.ProbeType" );
    public static final String METER_MEASURE_AIR = getString( "Meter.MeasureAir" );
    public static final String METER_MEASURE_OBJECTS = getString( "Meter.MeasureObjects" );
    public static final String METER_HALF_LIFE_EQUALS = getString( "Meter.HalfLifeEquals" );
    
}
