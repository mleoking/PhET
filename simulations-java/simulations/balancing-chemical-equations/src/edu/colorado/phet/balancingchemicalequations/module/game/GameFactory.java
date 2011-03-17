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
 * The equations are chosen based on a game level.
 * The design document specifies which equations correspond to which game levels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class GameFactory {

    // Level 1
    private static final ArrayList<Class <? extends Equation>> LEVEL1_CLASSES = new ArrayList<Class<? extends Equation>>() {{
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

    // Level 2
    private static final ArrayList<Class <? extends Equation>> LEVEL2_CLASSES = new ArrayList<Class<? extends Equation>>() {{
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

    // Level 3 - select one from this list
    private static final ArrayList<Class <? extends Equation>>LEVEL3_ONE_CLASSES = new ArrayList<Class<? extends Equation>>() {{
        add( Displacement_4NH3_3O2_2N2_6H2O.class );
        add( Displacement_2N2_6H2O_4NH3_3O2.class );
        add( Displacement_4NH3_5O2_4NO_6H2O.class );
        add( Displacement_4NO_6H2O_4NH3_5O2.class );
        add( Displacement_4NH3_7O2_4NO2_6H2O.class );
        add( Displacement_4NO2_6H2O_4NH3_7O2.class );
        add( Displacement_4NH3_6NO_5N2_6H2O.class );
        add( Displacement_5N2_6H2O_4NH3_6NO.class );
    }};

    // Level 3 - select many from this list
    private static final ArrayList<Class <? extends Equation>>LEVEL3_MANY_CLASSES = new ArrayList<Class<? extends Equation>>() {{
        add( Displacement_2C2H6_7O2_4CO2_6H2O.class );
        add( Displacement_4CO2_6H2O_2C2H6_7O2.class );
        add( Displacement_2C2H2_5O2_4CO2_2H2O.class );
        add( Displacement_4CO2_2H2O_2C2H2_5O2.class );
        add( Displacement_C2H5OH_3O2_2CO2_3H2O.class );
        add( Displacement_2CO2_3H2O_C2H5OH_3O2.class );
    }};

    // map of game levels to strategies for selecting equations
    private static HashMap< Integer, IGameStrategy> LEVEL_TO_STRATEGY = new HashMap<Integer, IGameStrategy>() {{
        put( 1, new RandomNoDuplicatesStrategy( LEVEL1_CLASSES ) );
        put( 2, new RandomNoDuplicatesStrategy( LEVEL2_CLASSES ) );
        put( 3, new OneManyStrategy( LEVEL3_ONE_CLASSES, LEVEL3_MANY_CLASSES ) );
    }};

    // dev: map of game levels to strategies that return all equations for the level
    private static HashMap< Integer, IGameStrategy> LEVEL_TO_STRATEGY_DEV = new HashMap<Integer, IGameStrategy>() {{
        put( 1, new EntirePoolStrategy( LEVEL1_CLASSES ) );
        put( 2, new EntirePoolStrategy( LEVEL2_CLASSES ) );
        put( 3, new EntirePoolStrategy( new ArrayList<Class<? extends Equation>>() {{
            addAll( LEVEL3_MANY_CLASSES );
            addAll( LEVEL3_ONE_CLASSES );
        }} ) );
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
        if ( !LEVEL_TO_STRATEGY.containsKey( level ) ) {
            throw new IllegalArgumentException( "unsupported level: " + level );
        }

        // get classes
        ArrayList<Class<? extends Equation>> equationClasses = null;
        if ( playAllEquationsProperty.getValue() ) {
            equationClasses = LEVEL_TO_STRATEGY_DEV.get( level ).getEquationClasses( numberOfEquations );
        }
        else {
            equationClasses = LEVEL_TO_STRATEGY.get( level ).getEquationClasses( numberOfEquations );
        }

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
        public ArrayList<Class<? extends Equation>> getEquationClasses( int numberOfEquations );
    }

    /*
     * Ignores the number of equations and returns the entire pool of equations.
     * Used in dev mode, this allows testing of all equations for a level.
     */
    private static class EntirePoolStrategy implements IGameStrategy {

        private final ArrayList<Class <? extends Equation>> pool;

        public EntirePoolStrategy( ArrayList<Class <? extends Equation>> pool ) {
            this.pool = pool;
        }

        public ArrayList<Class<? extends Equation>> getEquationClasses( int numberOfEquations ) {
            return new ArrayList<Class<? extends Equation>>( pool );
        }
    }

    /*
     * Selects a random set from a pool of equations.
     * There will be no duplicates, unless the number of equations requested exceeds the number of equations in pool.
     */
    private static class RandomNoDuplicatesStrategy implements IGameStrategy {

        private final ArrayList<Class <? extends Equation>> pool;

        public RandomNoDuplicatesStrategy( ArrayList<Class <? extends Equation>> pool ) {
            this.pool = pool;
        }

        public ArrayList<Class<? extends Equation>> getEquationClasses( int numberOfEquations ) {
            ArrayList<Class<? extends Equation>> classesList = new ArrayList<Class<? extends Equation>>();
            for ( int i = 0; i < numberOfEquations; i++ ) {
                classesList.add( getRandomEquationClass( pool, classesList ) );
            }
            assert ( classesList.size() == numberOfEquations );
            return classesList;
        }

        /*
         * Creates a random equation class for a specified level.
         * Will not select a class that has already been selected,
         * unless the selected list already contains all possible equations for the level.
         *
         * @param pool the pool of classes to choose from
         * @param selectedList list of classes that have already been selected
         */
        private static Class<? extends Equation> getRandomEquationClass( ArrayList<Class<? extends Equation>> pool, ArrayList<Class<? extends Equation>> selectedList ) {

            // Select a random equation class from the array for the specified level.
            int equationIndex = (int) ( Math.random() * pool.size() );
            Class<? extends Equation> equationClass = pool.get( equationIndex );

            // If this is a duplicate, find the next one that's not in the equation set.
            final int originalIndex = equationIndex;
            while ( selectedList.contains( equationClass ) ) {
                equationIndex++;
                if ( equationIndex > pool.size() - 1 ) {
                    equationIndex = 0;
                }
                equationClass = pool.get( equationIndex );
                if ( equationIndex == originalIndex ) {
                    // we're back where we started, bail
                    break;
                }
            }

            return equationClass;
        }
    }

    /*
     * Takes 2 pools of equations, the "one" pool and the "many" pool.
     * Returns a set that contains one equation from the "one" pool, and the remainder from the "many" pool.
     */
    private static class OneManyStrategy implements IGameStrategy {

        private final RandomNoDuplicatesStrategy oneStrategy, manyStrategy;

        public OneManyStrategy( ArrayList<Class <? extends Equation>> onePool, ArrayList<Class <? extends Equation>> manyPool ) {
            this.oneStrategy = new RandomNoDuplicatesStrategy( onePool );
            this.manyStrategy = new RandomNoDuplicatesStrategy( manyPool );
        }

        public ArrayList<Class<? extends Equation>> getEquationClasses( int numberOfEquations ) {
            ArrayList<Class<? extends Equation>> oneList = oneStrategy.getEquationClasses( 1 );
            ArrayList<Class<? extends Equation>> manyList = manyStrategy.getEquationClasses( numberOfEquations - 1 );
            // insert the "one" equation into the list at a random location
            int randomIndex = (int)( Math.random() * manyList.size() );
            manyList.add( randomIndex, oneList.get( 0 ) );
            assert( manyList.size() == numberOfEquations );
            return manyList;
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
