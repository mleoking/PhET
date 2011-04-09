// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2CO2_2CO_O2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2CO_C_CO2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2HCl_H2_Cl2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2NH3_N2_3H2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2NO2_2NO_O2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2NO_N2_O2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_2SO3_2SO2_O2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_4PCl3_P4_6Cl2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_C2H6_C2H4_H2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_CH3OH_CO_2H2;
import edu.colorado.phet.balancingchemicalequations.model.DecompositionEquation.Decomposition_PCl5_PCl3_Cl2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2C2H2_5O2_4CO2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2C2H6_7O2_4CO2_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2CO2_3H2O_C2H5OH_3O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2C_2H2O_CH4_CO2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2F2_H2O_OF2_2HF;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2N2_6H2O_4NH3_3O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4CO2_2H2O_2C2H2_5O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4CO2_6H2O_2C2H6_7O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_3O2_2N2_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_5O2_4NO_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_6NO_5N2_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_7O2_4NO2_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NO2_6H2O_4NH3_7O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NO_6H2O_4NH3_5O2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_5N2_6H2O_4NH3_6NO;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_C2H4_3O2_2CO2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_C2H5OH_3O2_2CO2_3H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_C2H6_Cl2_C2H5Cl_HCl;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CH4_2O2_CO2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CH4_4S_CS2_2H2S;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CH4_H2O_3H2_CO;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_CS2_3O2_CO2_2SO2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_OF2_H2O_O2_2HF;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_SO2_2H2_S_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_SO2_3H2_H2S_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.*;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_2C_O2_2CO;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_2H2_O2_2H2O;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_2N2_O2_2N2O;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_C2H2_2H2_C2H6;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_CH2O_H2_CH3OH;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_C_2S_CS2;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_C_O2_CO2;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_H2_F2_2HF;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_P4_6F2_4PF3;
import edu.colorado.phet.balancingchemicalequations.model.SynthesisEquation.Synthesis_P4_6H2_4PH3;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Factory that creates a game.
 * A game is a set of equations to be balanced.
 * The equations are chosen from a "pool", and each game level has its own pool.
 * The design document specifies which equations are in each pool, as well as
 * the strategy for choosing equations from each pool.
 * <p>
 * Equations are instantiated using reflection because we need new equations
 * for each game, and we need to be able to exclude some types of equations
 * during the equation selection process.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class GameFactory {

    /*
     * A list that holds classes of type Equation.
     * Improves code readability by hiding messy type parameterization.
     */
    private static class EquationClassesList extends ArrayList<Class <? extends Equation>> {
        public EquationClassesList() {
            super();
        }
        public EquationClassesList( EquationClassesList list ) {
            super( list );
        }
    }

    // Level 1 pool
    private static final EquationClassesList LEVEL1_POOL = new EquationClassesList() {{
        add( Synthesis_2H2_O2_2H2O.class );
        add( Synthesis_H2_F2_2HF.class );
        add( Decomposition_2HCl_H2_Cl2.class );
        add( Decomposition_CH3OH_CO_2H2.class );
        add( Synthesis_CH2O_H2_CH3OH.class );
        add( Decomposition_C2H6_C2H4_H2.class );
        add( Synthesis_C2H2_2H2_C2H6.class );
        add( Synthesis_C_O2_CO2.class );
        add( Synthesis_2C_O2_2CO.class );
        add( Decomposition_2CO2_2CO_O2.class );
        add( Decomposition_2CO_C_CO2.class );
        add( Synthesis_C_2S_CS2.class );
        add( Decomposition_2NH3_N2_3H2.class );
        add( Decomposition_2NO_N2_O2.class );
        add( Decomposition_2NO2_2NO_O2.class );
        add( Synthesis_2N2_O2_2N2O.class );
        add( Synthesis_P4_6H2_4PH3.class );
        add( Synthesis_P4_6F2_4PF3.class );
        add( Decomposition_4PCl3_P4_6Cl2.class );
        add( Decomposition_PCl5_PCl3_Cl2.class );
        add( Decomposition_2SO3_2SO2_O2.class );
    }};

    // Level 2 pool
    private static final EquationClassesList LEVEL2_POOL = new EquationClassesList() {{
        add( Displacement_2C_2H2O_CH4_CO2.class );
        add( Displacement_CH4_H2O_3H2_CO.class );
        add( Displacement_CH4_2O2_CO2_2H2O.class );
        add( Displacement_C2H4_3O2_2CO2_2H2O.class );
        add( Displacement_C2H6_Cl2_C2H5Cl_HCl.class );
        add( Displacement_CH4_4S_CS2_2H2S.class );
        add( Displacement_CS2_3O2_CO2_2SO2.class );
        add( Displacement_SO2_2H2_S_2H2O.class );
        add( Displacement_SO2_3H2_H2S_2H2O.class );
        add( Displacement_2F2_H2O_OF2_2HF.class );
        add( Displacement_OF2_H2O_O2_2HF.class );
    }};

    // Level 3 pool
    private static final EquationClassesList LEVEL3_POOL = new EquationClassesList() {{
        add( Displacement_2C2H6_7O2_4CO2_6H2O.class );
        add( Displacement_4CO2_6H2O_2C2H6_7O2.class );
        add( Displacement_2C2H2_5O2_4CO2_2H2O.class );
        add( Displacement_4CO2_2H2O_2C2H2_5O2.class );
        add( Displacement_C2H5OH_3O2_2CO2_3H2O.class );
        add( Displacement_2CO2_3H2O_C2H5OH_3O2.class );
        add( Displacement_4NH3_3O2_2N2_6H2O.class );
        add( Displacement_2N2_6H2O_4NH3_3O2.class );
        add( Displacement_4NH3_5O2_4NO_6H2O.class );
        add( Displacement_4NO_6H2O_4NH3_5O2.class );
        add( Displacement_4NH3_7O2_4NO2_6H2O.class );
        add( Displacement_4NO2_6H2O_4NH3_7O2.class );
        add( Displacement_4NH3_6NO_5N2_6H2O.class );
        add( Displacement_5N2_6H2O_4NH3_6NO.class );
    }};

    /*
     * Maps an equation class to a list of equation classes that should be excluded from the pool.
     * Improves code readability by hiding messy type parameterization.
     */
    private static class ExclusionsMap extends HashMap<Class <? extends Equation>, EquationClassesList> {}

    /*
     *  Level 3 exclusions map
     *  <p>
     *  This mess deserves some explanation... For level 3, the design team wanted a complicated
     *  strategy for selecting equations, where selection of an equation causes other equations to be
     *  ruled out as possible choices.  For example, if we choose an equation that contains 4NH3 as
     *  a reactant, we don't want to choose any other equations with 4NH3 as a reactant, and we don't
     *  want to choose the reverse equation.  Since this "exclusion" strategy was a moving target and
     *  the rules kept changing, I implemented this general solution, whereby a list of exclusions
     *  can be specified for each equation.
     *  <p>
     *  I would like to have implemented a varargs "put" method to make this more readable, but Java
     *  cannot create an array of parameterized types, so use of varargs would result in compiler warnings.
     *  //REVIEW: the varargs problem would go away if you used instances instead of classes/reflection
     */
    private static final ExclusionsMap LEVEL3_EXCLUSIONS =
        new ExclusionsMap() {{
            put( Displacement_2C2H6_7O2_4CO2_6H2O.class,
                 new EquationClassesList() {{
                    add( Displacement_4CO2_6H2O_2C2H6_7O2.class ); /* reverse equation */
                    add( Displacement_2C2H2_5O2_4CO2_2H2O.class );
                 }}
            );
            put( Displacement_4CO2_6H2O_2C2H6_7O2.class,
                 new EquationClassesList() {{
                    add( Displacement_2C2H6_7O2_4CO2_6H2O.class ); /* reverse equation */
                    add( Displacement_4CO2_2H2O_2C2H2_5O2.class );
                 }}
            );
            put( Displacement_2C2H2_5O2_4CO2_2H2O.class,
                 new EquationClassesList() {{
                     add( Displacement_4CO2_2H2O_2C2H2_5O2.class ); /* reverse equation */
                     add( Displacement_2C2H6_7O2_4CO2_6H2O.class );
                 }}
            );
            put( Displacement_4CO2_2H2O_2C2H2_5O2.class,
                 new EquationClassesList() {{
                     add( Displacement_2C2H2_5O2_4CO2_2H2O.class ); /* reverse equation */
                     add( Displacement_4CO2_6H2O_2C2H6_7O2.class );
                 }}
            );
            put( Displacement_C2H5OH_3O2_2CO2_3H2O.class,
                    new EquationClassesList() {{
                        add( Displacement_2CO2_3H2O_C2H5OH_3O2.class ); /* reverse equation */
                    }}
               );
            put( Displacement_2CO2_3H2O_C2H5OH_3O2.class,
                 new EquationClassesList() {{
                     add( Displacement_C2H5OH_3O2_2CO2_3H2O.class ); /* reverse equation */
                 }}
            );
            put( Displacement_4NH3_3O2_2N2_6H2O.class,
                 new EquationClassesList() {{
                     add( Displacement_2N2_6H2O_4NH3_3O2.class ); /* reverse equation */
                     add( Displacement_4NH3_5O2_4NO_6H2O.class ); /* other equations with reactant 4NH3 */
                     add( Displacement_4NH3_7O2_4NO2_6H2O.class );
                     add( Displacement_4NH3_6NO_5N2_6H2O.class );
                 }}
            );
            put( Displacement_4NH3_5O2_4NO_6H2O.class,
                 new EquationClassesList() {{
                     add( Displacement_4NO_6H2O_4NH3_5O2.class ); /* reverse equation */
                     add( Displacement_4NH3_3O2_2N2_6H2O.class ); /* other equations with reactant 4NH3 */
                     add( Displacement_4NH3_7O2_4NO2_6H2O.class );
                     add( Displacement_4NH3_6NO_5N2_6H2O.class );
                 }}
            );
            put( Displacement_4NH3_7O2_4NO2_6H2O.class,
                 new EquationClassesList() {{
                     add( Displacement_4NO2_6H2O_4NH3_7O2.class ); /* reverse equation */
                     add( Displacement_4NH3_3O2_2N2_6H2O.class ); /* other equations with reactant 4NH3 */
                     add( Displacement_4NH3_5O2_4NO_6H2O.class );
                     add( Displacement_4NH3_6NO_5N2_6H2O.class );
                 }}
            );
            put( Displacement_4NH3_6NO_5N2_6H2O.class,
                 new EquationClassesList() {{
                     add( Displacement_5N2_6H2O_4NH3_6NO.class ); /* reverse equation */
                     add( Displacement_4NH3_3O2_2N2_6H2O.class ); /* other equations with reactant 4NH3 */
                     add( Displacement_4NH3_5O2_4NO_6H2O.class );
                     add( Displacement_4NH3_7O2_4NO2_6H2O.class );
                 }}
            );
            put( Displacement_2N2_6H2O_4NH3_3O2.class,
                 new EquationClassesList() {{
                     add( Displacement_4NH3_3O2_2N2_6H2O.class ); /* reverse equation */
                     add( Displacement_4NO_6H2O_4NH3_5O2.class ); /* other equations with product 4NH3 */
                     add( Displacement_4NO2_6H2O_4NH3_7O2.class );
                     add( Displacement_5N2_6H2O_4NH3_6NO.class );
                 }}
            );
            put( Displacement_4NO_6H2O_4NH3_5O2.class,
                 new EquationClassesList() {{
                     add( Displacement_4NH3_5O2_4NO_6H2O.class ); /* reverse equation */
                     add( Displacement_2N2_6H2O_4NH3_3O2.class ); /* other equations with product 4NH3 */
                     add( Displacement_4NO2_6H2O_4NH3_7O2.class );
                     add( Displacement_5N2_6H2O_4NH3_6NO.class );
                 }}
            );
            put( Displacement_4NO2_6H2O_4NH3_7O2.class,
                 new EquationClassesList() {{
                     add( Displacement_4NH3_7O2_4NO2_6H2O.class ); /* reverse equation */
                     add( Displacement_2N2_6H2O_4NH3_3O2.class ); /* other equations with product 4NH3 */
                     add( Displacement_4NO_6H2O_4NH3_5O2.class );
                     add( Displacement_5N2_6H2O_4NH3_6NO.class );
                 }}
            );
            put( Displacement_5N2_6H2O_4NH3_6NO.class,
                 new EquationClassesList() {{
                     add( Displacement_4NH3_6NO_5N2_6H2O.class ); /* reverse equation */
                     add( Displacement_2N2_6H2O_4NH3_3O2.class ); /* other equations with product 4NH3 */
                     add( Displacement_4NO_6H2O_4NH3_5O2.class );
                     add( Displacement_4NO2_6H2O_4NH3_7O2.class );
                 }}
            );
        }};

    // map of game levels to strategies for selecting equations
    private static HashMap< Integer, IGameStrategy> STRATEGIES = new HashMap<Integer, IGameStrategy>() {{
        put( 1, new RandomStrategy( LEVEL1_POOL, false ) );
        put( 2, new RandomStrategy( LEVEL2_POOL, true ) );
        put( 3, new RandomWithExclusionsStrategy( LEVEL3_POOL, LEVEL3_EXCLUSIONS, true ) );
    }};

    // dev map, these strategies return the complete pool for each game level
    private static HashMap< Integer, IGameStrategy> DEV_STRATEGIES = new HashMap<Integer, IGameStrategy>() {{
        put( 1, new EntirePoolStrategy( LEVEL1_POOL ) );
        put( 2, new EntirePoolStrategy( LEVEL2_POOL ) );
        put( 3, new EntirePoolStrategy( LEVEL3_POOL ) );
    }};

    /*
     * Developer control.
     * If true, the factory returns the complete set of equations for a specified level.
     */
    private Property<Boolean> playAllEquationsProperty;

    /**
     * Default constructor.
     */
    public GameFactory( Property<Boolean> playAllEquationsProperty ) {
        this.playAllEquationsProperty = playAllEquationsProperty;
    }

    /**
     * Creates a set of equations to be used in the game.
     * @param numberOfEquations
     * @param level 1-N
     */
    public ArrayList<Equation> createEquations( int numberOfEquations, int level ) {

        // validate level
        if ( !STRATEGIES.containsKey( level ) ) {
            throw new IllegalArgumentException( "unsupported level: " + level );
        }

        // get strategy for level
        IGameStrategy strategy = playAllEquationsProperty.getValue() ? DEV_STRATEGIES.get( level ) : STRATEGIES.get( level );

        // get equation classes
        EquationClassesList equationClasses = strategy.getEquationClasses( numberOfEquations );

        // instantiate equations
        ArrayList<Equation> equations = new ArrayList<Equation>();
        for ( Class<? extends Equation> equationClass : equationClasses ) {
            equations.add( instantiateEquation( equationClass ) );
        }
        return equations;
    }

    /*
     * Uses reflection to instantiate an Equation by class.
     */
    private static Equation instantiateEquation( Class<? extends Equation> equationClass ) {
        Equation equation = null;
        try {
            equation = equationClass.newInstance();
        }
        catch ( InstantiationException e ) {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return equation;
    }

    /*
     * Strategy for selecting equations for a game.
     */
    private interface IGameStrategy {
        public EquationClassesList getEquationClasses( int numberOfEquations );
    }

    /*
     * Ignores the number of equations and returns the entire pool of equations.
     * Used in dev mode, this allows testing of all equations for a level.
     */
    private static class EntirePoolStrategy implements IGameStrategy {

        private final EquationClassesList pool;

        public EntirePoolStrategy( EquationClassesList pool ) {
            this.pool = pool;
        }

        public EquationClassesList getEquationClasses( int numberOfEquations ) {
            return new EquationClassesList( pool );
        }
    }

    /*
     * Selects a random set from a pool of equations, with no duplicates.
     * Use the firstBigMolecule flag to specify whether it's OK if the first
     * equation in the set contains a "big" molecule.
     */
    private static class RandomStrategy extends RandomWithExclusionsStrategy {
        public RandomStrategy( EquationClassesList pool, boolean firstBigMolecules ) {
            super( pool, new ExclusionsMap() /* empty exclusions */, firstBigMolecules );
        }
    }

    /*
     * Selects a random set from a pool of equations, with no duplicates.
     * Selection of an equation may cause other equations to be excluded from the pool.
     * Use the firstBigMolecule flag to specify whether it's OK if the first
     * equation in the set contains a "big" molecule.
     */
    private static class RandomWithExclusionsStrategy implements IGameStrategy {

        private final EquationClassesList pool;
        private final ExclusionsMap exclusions;
        private final boolean firstBigMolecule; // can the first equation in the set contain a "big" molecule?

        public RandomWithExclusionsStrategy( EquationClassesList pool, ExclusionsMap exclusions, boolean firstBigMolecules ) {
            this.pool = pool;
            this.exclusions = exclusions;
            this.firstBigMolecule = firstBigMolecules;
        }

        public EquationClassesList getEquationClasses( int numberOfEquations ) {

            // operate on a copy of the pool, so that we can prune the pool as we select equations
            EquationClassesList poolCopy = new EquationClassesList( pool );

            EquationClassesList equationClasses = new EquationClassesList();
            for ( int i = 0; i < numberOfEquations; i++ ) {

                // randomly select an equation
                int randomIndex = (int) ( Math.random() * poolCopy.size() );
                Class<? extends Equation> equationClass = poolCopy.get( randomIndex );

                // If the first equation isn't supposed to contain any "big" molecules,
                // then find an equation in the pool that has no big molecules.
                if ( i == 0 && !firstBigMolecule && hasBigMolecule( equationClass ) ) {

                    // start the search at a random index
                    final int startIndex = (int) ( Math.random() * poolCopy.size() );

                    int index = startIndex;
                    boolean done = false;
                    while ( !done ) {

                        // next equation in the pool
                        equationClass = poolCopy.get( index );

                        if ( !hasBigMolecule( equationClass ) ) {
                            done = true; // success, this equation has no big molecules
                        }
                        else {
                            // increment index to point to next in pool
                            index++;
                            if ( index > poolCopy.size() - 1 ) {
                                index = 0;
                            }

                            // give up if we've examined all equations in the pool
                            if ( index == startIndex ) {
                                done = true;
                                System.err.println( "ERROR: first equation contains big molecules because we ran out of equations" );
                            }
                        }
                    }
                }

                // add the equation to the game
                equationClasses.add( equationClass );

                // remove the equation from the pool so it won't be selected again
                poolCopy.remove( equationClass );

                // if the selected equation has exclusions, remove them from the pool
                EquationClassesList exclusedEquations = exclusions.get( equationClass );
                if ( exclusedEquations != null ) {
                    poolCopy.removeAll( exclusedEquations );
                }

                /*
                 *  If the pool size goes to zero prematurely, print diagnostics and bail.
                 *  If this happens, you'll get fewer equations than requested, but the
                 *  application will continue to run (unless assertions are enabled).
                 */
                if ( i < numberOfEquations - 1 && poolCopy.size() == 0 ) {
                    System.err.print( "ERROR: GameFactory.RandomWithExclusionsStrategy.getEquationClasses ran out of equations, " );
                    System.err.print( "numberOfEquations=" + numberOfEquations );
                    System.err.println( " equationClasses=" + equationClasses.toString() );
                    break;
                }
            }

            assert ( equationClasses.size() == numberOfEquations );
            assert ( !( !firstBigMolecule && hasBigMolecule( equationClasses.get( 0 ) ) ) );
            return equationClasses;
        }

        private boolean hasBigMolecule( Class<? extends Equation> equationClass ) {
            return instantiateEquation( equationClass ).hasBigMolecule();
        }
    }

    // test
    public static void main( String[] args ) {
        GameFactory factory = new GameFactory( new Property<Boolean>( false ) );
        for ( int level = 1; level < 4; level++ ) {
            System.out.println( "LEVEL " + level );
            ArrayList<Equation> equations = factory.createEquations( 5, level );
            for ( Equation equation : equations ) {
                System.out.println( equation.getName() );
            }
            System.out.println( "(" + equations.size() + " equations)" );
            System.out.println();
        }
    }
}
