/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.util.ArrayList;

import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.OneProductReactions.*;
import edu.colorado.phet.reactantsproductsandleftovers.model.TwoProductReactions.*;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;

/**
 * Level-of-difficulty is based on the number values that the user must solve for.
 * This was used for the first round of interviews on this sim.
 * Behavior is: 
 * <ul>
 * <li>Level 1: one or two products random, Before (2 variables)
 * <li>Level 2: one product random, After 
 * <li>Level 3: two products random, After (4 variables)
 * </ul>
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NumberOfVariablesChallengeFactory extends AbstractChallengeFactory {
    
    // level 2 is all the one-product reactions
    private static final ArrayList<Class <? extends ChemicalReaction>> LEVEL2_LIST = new ArrayList<Class<? extends ChemicalReaction>>();
    static {
        LEVEL2_LIST.add( WaterReaction.class );
        LEVEL2_LIST.add( Reaction_H2_F2__2HF.class );
        LEVEL2_LIST.add( Reaction_H2_Cl2__2HCl.class );
        LEVEL2_LIST.add( Reaction_CO_2H2__CH3OH.class );
        LEVEL2_LIST.add( Reaction_CH2O_H2__CH3OH.class );
        LEVEL2_LIST.add( Reaction_C2H4_H2__C2H6.class );
        LEVEL2_LIST.add( Reaction_C2H2_2H2__C2H6.class );
        LEVEL2_LIST.add( Reaction_C_O2__CO2.class );
        LEVEL2_LIST.add( Reaction_2C_O2__2CO.class );
        LEVEL2_LIST.add( Reaction_2CO_O2__2CO2.class );
        LEVEL2_LIST.add( Reaction_C_CO2__2CO.class );
        LEVEL2_LIST.add( Reaction_C_2S__CS2.class );
        LEVEL2_LIST.add( AmmoniaReaction.class );
        LEVEL2_LIST.add( Reaction_N2_O2__2NO.class );
        LEVEL2_LIST.add( Reaction_2NO_O2__2NO2.class );
        LEVEL2_LIST.add( Reaction_2N2_O2__2NO2.class );
        LEVEL2_LIST.add( Reaction_P4_6H2__4PH3.class );
        LEVEL2_LIST.add( Reaction_P4_6F2__4PF3.class );
        LEVEL2_LIST.add( Reaction_P4_6Cl2__4PCl3.class );
        LEVEL2_LIST.add( Reaction_P4_10Cl2__4PCl5.class );
        LEVEL2_LIST.add( Reaction_PCl3_Cl2__PCl5.class );
        LEVEL2_LIST.add( Reaction_2SO2_O2__2SO3.class );
    };

    // level 3 is all the two-product reactions
    private static final ArrayList<Class <? extends ChemicalReaction>>LEVEL3_LIST = new ArrayList<Class<? extends ChemicalReaction>>();
    static {
        LEVEL3_LIST.add( Reaction_2C_2H2O__CH4_CO2.class ); 
        LEVEL3_LIST.add( Reaction_CH4_H2O__3H2_CO.class );
        LEVEL3_LIST.add( MethaneReaction.class );
        LEVEL3_LIST.add( Reaction_2C2H6_7O2__4CO2_6H2O.class );
        LEVEL3_LIST.add( Reaction_C2H4_3O2__2CO2_2H2O.class );
        LEVEL3_LIST.add( Reaction_2C2H2_5O2__4CO2_2H2O.class );
        LEVEL3_LIST.add( Reaction_C2H5OH_3O2__2CO2_3H2O.class );
        LEVEL3_LIST.add( Reaction_C2H6_Cl2__C2H5Cl_HCl.class );
        LEVEL3_LIST.add( Reaction_CH4_4S__CS2_2H2S.class );
        LEVEL3_LIST.add( Reaction_CS2_3O2__CO2_2SO2.class );
        LEVEL3_LIST.add( Reaction_4NH3_3O2__2N2_6H2O.class );
        LEVEL3_LIST.add( Reaction_4NH3_5O2__4NO_6H2O.class );
        LEVEL3_LIST.add( Reaction_4NH3_7O2__4NO2_6H2O.class );
        LEVEL3_LIST.add( Reaction_4NH3_6NO__5N2_6H2O.class );
        LEVEL3_LIST.add( Reaction_SO2_2H2__S_2H2O.class );
        LEVEL3_LIST.add( Reaction_SO2_3H2__H2S_2H2O.class );
        LEVEL3_LIST.add( Reaction_2F2_H2O__OF2_2HF.class );
        LEVEL3_LIST.add( Reaction_OF2_H2O__O2_2HF.class );
    };
    
    // level 1 is all the reactions
    private static final ArrayList<Class <? extends ChemicalReaction>>LEVEL1_LIST = new ArrayList<Class<? extends ChemicalReaction>>();
    static {
        LEVEL1_LIST.addAll( LEVEL2_LIST );
        LEVEL1_LIST.addAll( LEVEL3_LIST );
    };

    // list of lists, so we can use level to index the proper list
    private static ArrayList< ArrayList<Class <? extends ChemicalReaction>>> REACTIONS = new ArrayList<ArrayList<Class<? extends ChemicalReaction>>>();
    static {
        REACTIONS.add( LEVEL1_LIST );
        REACTIONS.add( LEVEL2_LIST );
        REACTIONS.add( LEVEL3_LIST );
    };
    
    /**
     * Default constructor.
     */
    public NumberOfVariablesChallengeFactory() {}

    /**
     * Creates challenges.
     *
     * @param numberOfChallenges
     * @param level 1-N
     * @param maxQuantity
     * @param imagesVisible
     */
    public GameChallenge[] createChallenges( int numberOfChallenges, int level, int maxQuantity, boolean imagesVisible ) {

        if ( level < 1 || level > REACTIONS.size() ) {
            throw new IllegalArgumentException( "unsupported level: " + level );
        }
        
        GameChallenge[] challenges = new GameChallenge[ numberOfChallenges ];
        ChemicalReaction previousReaction = null;
        for ( int i = 0; i < challenges.length; i++ ) {

            // reaction
            ChemicalReaction reaction = getRandomReaction( level, previousReaction );

            // challenge type
            ChallengeType challengeType;
            if ( level == 1 ) {
                challengeType = ChallengeType.BEFORE;
            }
            else if ( level == 2 ) {
                challengeType = ChallengeType.AFTER;
            }
            else /* level 3 */ {
                challengeType = ChallengeType.AFTER;
            }

            // quantities
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( getRandomQuantity( maxQuantity ) );
            }
            fixQuantityRangeViolation( reaction, maxQuantity ); // do this *before* creating the challenge, see #2156
            
            challenges[i] = new GameChallenge( reaction, challengeType, imagesVisible );
            previousReaction = reaction;
        }
        return challenges;
    }
    
    /*
     * Creates a random reaction for a specified level.
     */
    private ChemicalReaction getRandomReaction( int level, ChemicalReaction previousReaction ) {
        
        // Select a random reaction from the array for the specified level.
        int reactionIndex = getRandomReactantIndex( level );
        Class<? extends ChemicalReaction> reactionClass = getReactionClass( level, reactionIndex );
        
        // If same as the previous reaction, simply get the next reaction in the array.
        if ( previousReaction != null && reactionClass.equals( previousReaction.getClass() ) ) {
            reactionIndex = getNextReactantIndex( level, reactionIndex );
            reactionClass = getReactionClass( level, reactionIndex );
        }
        
        return instantiateReaction( reactionClass );
    }
    
    /*
     * Gets a reaction class for a specified level and index.
     */
    private Class<? extends ChemicalReaction> getReactionClass( int level, int reactionIndex ) {
        return getReactionList( level ).get( reactionIndex );
    }
    
    /*
     * Gets the number of reactions for a specified level.
     */
    private int getNumberOfReactions( int level ) {
        return getReactionList( level ).size();
    }
    
    /*
     * Gets a random reaction index for a specified level.
     */
    private int getRandomReactantIndex( int level ) {
        return (int) ( Math.random() * getNumberOfReactions( level ) );
    }
    
    /*
     * Gets the next reaction index for a specified level and current reaction index.
     */
    private int getNextReactantIndex( int level, int currentIndex ) {
        int nextIndex = currentIndex + 1;
        if ( nextIndex > getNumberOfReactions( level ) - 1 ) {
            nextIndex = 0;
        }
        return nextIndex;
    }
    
    /*
     * Gets the list of reactions for a level.
     * Levels are numbered from 1-N, as in the model.
     */
    private ArrayList<Class <? extends ChemicalReaction>> getReactionList( int level ) {
        return REACTIONS.get( level - 1 );
    }

    // test for range violations inherent in reactions, and verify that they are fixable.
    public static void main( String[] args ) {
        
        // put all reactions in a container, removing duplicates.
        NumberOfVariablesChallengeFactory factory = new NumberOfVariablesChallengeFactory();
        ArrayList<Class<? extends ChemicalReaction>> reactionClasses = new ArrayList<Class<? extends ChemicalReaction>>();
        for ( int level = 1; level <= REACTIONS.size(); level++ ) {
            for ( int reactionIndex = 0; reactionIndex < factory.getNumberOfReactions( level ); reactionIndex++ ) {
                Class<? extends ChemicalReaction> reactionClass = factory.getReactionClass( level, reactionIndex );
                if ( !reactionClasses.contains( reactionClass ) ) {
                    reactionClasses.add( reactionClass );
                }
            }
        }
        
        // look for range violations in all reactions
        int maxQuantity = GameModel.getQuantityRange().getMax();
        for ( Class<? extends ChemicalReaction> reactionClass : reactionClasses ) {
            ChemicalReaction reaction = instantiateReaction( reactionClass );
            // set all reactant quantities to their max values.
            for ( Reactant reactant : reaction.getReactants() ) {
                reactant.setQuantity( maxQuantity );
            }
            // look for violations and try to fix them.
            fixQuantityRangeViolation( reaction, maxQuantity );
        }
    }
}
