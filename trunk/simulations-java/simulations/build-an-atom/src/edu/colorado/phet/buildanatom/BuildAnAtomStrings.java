// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom;

import static edu.colorado.phet.buildanatom.BuildAnAtomResources.getString;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * A collection of localized strings used by this simulation.  We load all
 * strings as statics so that we will be warned at startup time of any missing strings.
 */
public class BuildAnAtomStrings {

    public static final String ATOM_ISOTOPE_NAME_PATTERN = getString( "pattern.0element.1massNumber" );

    /* not intended for instantiation */
    private BuildAnAtomStrings() {}

    public static final String ELECTRONS_NAME = getString( "electrons.name" );
    public static final String ELECTRONS_READOUT = getString( "electrons.readout" );
    public static final String PROTONS_NAME = getString( "protons.name" );
    public static final String PROTONS_READOUT = getString( "protons.readout" );
    public static final String NEUTRONS_NAME = getString( "neutrons.name" );
    public static final String NEUTRONS_READOUT = getString( "neutrons.readout" );
    public static final String ELECTRON_MODEL = getString( "electron.model" );
    public static final String ELECTRON_MODEL_ORBITS = getString( "electron.model.orbits" );
    public static final String ELECTRON_MODEL_CLOUD = getString( "electron.model.cloud" );
    public static final String SHOW_ELEMENT_NAME = getString( "show.element.name" );
    public static final String SHOW_NEUTRAL_ION = getString( "show.neutralIon" );
    public static final String SHOW_STABLE_UNSTABLE = getString( "show.stableUnstable" );
    public static final String INDICATOR_ELEMENT = getString( "indicator.element" );
    public static final String INDICATOR_SYMBOL = getString( "indicator.symbol" );
    public static final String INDICATOR_MASS_NUMBER = getString( "indicator.mass.number" );
    public static final String INDICATOR_CHARGE = getString( "indicator.charge" );
    public static final String POSITIVE_ION = getString( "positive.ion" );
    public static final String NEGATIVE_ION = getString( "negative.ion" );
    public static final String NEUTRAL_ATOM = getString( "neutral.atom" );
    public static final String UNSTABLE = getString( "unstable" );
    public static final String STABLE = getString( "stable" );
    public static final String ELEMENT_NONE_SYMBOL = getString( "element.none.symbol" );
    public static final String ELEMENT_NONE_NAME = getString( "element.none.name" );
    public static final String ELEMENT_HYDROGEN_NAME = getString( "element.hydrogen.name" );
    public static final String ELEMENT_HELIUM_NAME = getString( "element.helium.name" );
    public static final String ELEMENT_LITHIUM_NAME = getString( "element.lithium.name" );
    public static final String ELEMENT_BERYLLIUM_NAME = getString( "element.beryllium.name" );
    public static final String ELEMENT_BORON_NAME = getString( "element.boron.name" );
    public static final String ELEMENT_CARBON_NAME = getString( "element.carbon.name" );
    public static final String ELEMENT_NITROGEN_NAME = getString( "element.nitrogen.name" );
    public static final String ELEMENT_OXYGEN_NAME = getString( "element.oxygen.name" );
    public static final String ELEMENT_FLUORINE_NAME = getString( "element.fluorine.name" );
    public static final String ELEMENT_NEON_NAME = getString( "element.neon.name" );
    public static final String ELEMENT_SODIUM_NAME = getString( "element.sodium.name" );
    public static final String ELEMENT_MAGNESIUM_NAME = getString( "element.magnesium.name" );
    public static final String ELEMENT_ALUMINUM_NAME = getString( "element.aluminium.name" );
    public static final String ELEMENT_SILICON_NAME = getString( "element.silicon.name" );
    public static final String ELEMENT_PHOSPHORUS_NAME = getString( "element.phosphorus.name" );
    public static final String ELEMENT_SULFUR_NAME = getString( "element.sulfur.name" );
    public static final String ELEMENT_CHLORINE_NAME = getString( "element.chlorine.name" );
    public static final String ELEMENT_ARGON_NAME = getString( "element.argon.name" );
    public static final String ELEMENT_POTASSIUM_NAME = getString( "element.potassium.name" );
    public static final String ELEMENT_CALCIUM_NAME = getString( "element.calcium.name" );

    //game
    public static final String GAME_PROBLEM_INDEX_READOUT_PATTERN = getString( "game.problemIndexReadout.pattern" );
    public static final String GAME_CHECK = getString( "game.check" );
    public static final String GAME_NEXT = getString( "game.next" );
    public static final String GAME_TRY_AGAIN = getString( "game.tryAgain" );
    public static final String GAME_SHOW_ANSWER = getString( "game.showAnswer" );
    public static final String GAME_HOW_MANY_PARTICLES = getString( "game.howManyParticles" );
    public static final String GAME_COMPLETE_THE_MODEL = getString( "game.completeTheModel" );
    public static final String GAME_FIND_THE_ELEMENT = getString( "game.findTheElement" );
    public static final String GAME_COMPLETE_THE_SYMBOL_ALL = getString( "game.completeTheSymbol" );
    public static final String GAME_COMPLETE_THE_SYMBOL_PROTON_COUNT = getString( "game.howManyProtons" );
    public static final String GAME_COMPLETE_THE_SYMBOL_MASS = getString( "game.whatIsTheMassNumber" );
    public static final String GAME_COMPLETE_THE_SYMBOL_CHARGE = getString( "game.whatIsTheTotalCharge" );
    public static final String GAME_ANSWER_THE_CHARGE_QUESTION = getString( "game.whatIsTheTotalChargeBr" );
    public static final String GAME_ANSWER_THE_MASS_QUESTION = getString( "game.whatIsTheMassNumberBr" );
    public static final String GAME_ANSWER_THE_PROTON_COUNT_QUESTION = getString( "game.howManyProtons" );
    public static final String GAME_NEUTRAL_ATOM = getString( "game.neutralAtom" );
    public static final String GAME_ION = getString( "game.ion" );
    public static final String IS_IT = getString( "game.isIt");

    public static final String TITLE_BUILD_ATOM_MODULE = getString( "title.buildAtomModule" );
    public static final String TITLE_GAME_MODULE = getString( "title.gameModule" );
    public static final String TITLE_INTERACTIVE_ISOTOPE_MODULE = getString( "title.interactiveIsotopeModule" );
    public static final String TITLE_ISOTOPE_MIXTURES_MODULE = getString( "title.isotopeMixtures" );
    public static final String TITLE_ERROR = PhetCommonResources.getString( "Common.title.error" );

    public static final String ABUNDANCE_IN_NATURE = getString( "abundanceInNature" );
    public static final String THIS_ISOTOPE = getString( "thisIsotope" );
    public static final String VERY_SMALL = getString( "verySmall" );
}
