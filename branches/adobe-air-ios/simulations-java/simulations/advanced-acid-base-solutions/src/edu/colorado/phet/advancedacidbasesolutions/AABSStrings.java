// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AABSStrings {
    
    /* not intended for instantiation */
    private AABSStrings() {}
    
    public static final String TITLE_SOLUTIONS_MODULE = AABSResources.getString( "title.solutionsModule" );
    public static final String TITLE_COMPARING_MODULE = AABSResources.getString( "title.comparingModule" );
    public static final String TITLE_MATCHING_GAME_MODULE = AABSResources.getString( "title.matchingGameModule" );
    public static final String TITLE_EQUILIBRIUM_EXPRESSION = AABSResources.getString( "title.equilibriumExpression" );
    public static final String TITLE_SYMBOL_LEGEND = AABSResources.getString( "title.symbolLegend" );
    public static final String TITLE_VIEW = AABSResources.getString( "title.view" );
    
    public static final String MESSAGE_NOT_A_CONFIG = PhetCommonResources.getString( "XMLPersistenceManager.message.notAConfigFile" );
    
    // labels
    public static final String LABEL_CONCENTRATION = AABSResources.getString( "label.concentration" );
    public static final String LABEL_PH = AABSResources.getString( "label.pH" );
    public static final String LABEL_EQUATION_SCALING = AABSResources.getString( "label.equationScaling" );
    public static final String LABEL_STRENGTH = AABSResources.getString( "label.strength" );
    public static final String LABEL_WEAK = AABSResources.getString( "label.weak" );
    public static final String LABEL_WEAKER = AABSResources.getString( "label.weaker" );
    public static final String LABEL_STRONG = AABSResources.getString( "label.strong" );
    public static final String LABEL_STRONGER = AABSResources.getString( "label.stronger" );
    public static final String LABEL_CONCENTRATION_GRAPH_Y_AXIS = AABSResources.getString("label.concentrationGraph.yAxis" );
    public static final String LABEL_SOLUTE = AABSResources.getString( "label.solute" );
    public static final String LABEL_POINTS = AABSResources.getString( "label.points" );
    public static final String LABEL_SOLUTIONS = AABSResources.getString( "label.solutions" );
    
    // legend text
    public static final String LEGEND_HA = AABSResources.getString( "legend.HA" );
    public static final String LEGEND_B = AABSResources.getString( "legend.B" );
    public static final String LEGEND_MOH = AABSResources.getString( "legend.MOH" );
    
    // game
    public static final String GAME_ACIDBASE_QUESTION = AABSResources.getString( "game.acidbase.question" );
    public static final String GAME_ACIDBASE_CORRECT = AABSResources.getString( "game.acidbase.correct" );
    public static final String GAME_ACIDBASE_WRONG = AABSResources.getString( "game.acidbase.wrong" );
    public static final String GAME_MATCH_QUESTION = AABSResources.getString( "game.match.question" );
    public static final String GAME_MATCH_CORRECT = AABSResources.getString( "game.match.correct" );
    public static final String GAME_MATCH_WRONG = AABSResources.getString( "game.match.wrong" );
    public static final String GAME_CONTINUE = AABSResources.getString( "game.continue" );
    
    // check boxes
    public static final String CHECK_BOX_MOLECULE_COUNTS = AABSResources.getString( "checkBox.moleculeCounts" );
    public static final String CHECK_BOX_BEAKER_LABEL = AABSResources.getString( "checkBox.beakerLabel" );
    public static final String CHECK_BOX_SOLUTE_RATIO_SPECIFIC = AABSResources.getString( "checkBox.soluteRatio-reactant_product" );
    public static final String CHECK_BOX_SOLUTE_RATIO_GENERAL = AABSResources.getString( "checkBox.soluteRatio" );
    public static final String CHECK_BOX_H3O_OH_RATIO = AABSResources.getString( "checkBox.ratio-H3O_OH" );
    public static final String CHECK_BOX_SYMBOL_LEGEND = AABSResources.getString( "checkBox.symbolLegend" );
    
    // buttons
    public static final String BUTTON_NEW_SOLUTION = AABSResources.getString( "button.newSolution" );
    public static final String BUTTON_CHECK_MATCH = AABSResources.getString( "button.checkMatch" );
    public static final String BUTTON_ACID = AABSResources.getString( "button.acid" );
    public static final String BUTTON_BASE = AABSResources.getString( "button.base" );
    public static final String BUTTON_GRAPH = AABSResources.getString( "button.graph" );
    public static final String BUTTON_EQUILIBRIUM_EXPRESSIONS = AABSResources.getString( "button.equilibriumExpressions" );
    public static final String BUTTON_REACTION_EQUATIONS = AABSResources.getString( "button.reactionEquations" );
    
    // radio buttons
    public static final String RADIO_BUTTON_EQUATION_SCALING_OFF = AABSResources.getString( "radioButton.equationScaling.off" );
    public static final String RADIO_BUTTON_EQUATION_SCALING_ON = AABSResources.getString( "radioButton.equationScaling.on" );
    public static final String RADIO_BUTTON_BEAKERS = AABSResources.getString( "radioButton.beakers" );
    public static final String RADIO_BUTTON_GRAPHS = AABSResources.getString( "radioButton.graphs" );
    public static final String RADIO_BUTTON_EQUATIONS = AABSResources.getString( "radioButton.equations" );
    
    // units
    public static final String UNITS_LITERS = AABSResources.getString( "units.liters" );
    public static final String UNITS_MOLES_PER_LITER = AABSResources.getString( "units.molesPerLiter" );
    public static final String UNITS_MOLAR = AABSResources.getString( "units.molar" );
    
    // water
    public static final String WATER = AABSResources.getString( "water" );
    public static final String PURE_WATER = AABSResources.getString( "pureWater" );
    public static final String NO_SOLUTE = AABSResources.getString( "noSolute" );
    
    // acids
    public static final String CUSTOM_ACID = AABSResources.getString( "acid.customAcid" );
    public static final String HYDROCHLORIC_ACID = AABSResources.getString( "acid.hydrochloricAcid" );
    public static final String PERCHLORIC_ACID = AABSResources.getString( "acid.perchloridAcid" );
    public static final String CHLOROUS_ACID = AABSResources.getString( "acid.chlorousAcid" );
    public static final String HYPOCHLOROUS_ACID = AABSResources.getString( "acid.hypochlorousAcid" );
    public static final String HYDROFLUORIC_ACID = AABSResources.getString( "acid.hydrofluoricAcid" );
    public static final String ACETIC_ACID = AABSResources.getString( "acid.aceticAcid" );
    
    // bases
    public static final String CUSTOM_BASE = AABSResources.getString( "base.customBase" );
    public static final String SODIUM_HYDROXIDE = AABSResources.getString( "base.sodiumHydroxide" );
    public static final String AMMONIA = AABSResources.getString( "base.ammonia" );
    public static final String PYRIDINE = AABSResources.getString( "base.pyridine" );
    
    // values
    public static final String VALUE_LARGE = AABSResources.getString( "value.large" );
    public static final String VALUE_NEGLIGIBLE = AABSResources.getString( "value.negligible" );
}
