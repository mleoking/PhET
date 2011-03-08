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

    // Level 3
    private static final ArrayList<Class <? extends Equation>>LEVEL3_CLASSES = new ArrayList<Class<? extends Equation>>() {{
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

    // map of game levels to lists of equation classes
    private static HashMap< Integer, ArrayList<Class <? extends Equation>>> LEVEL_TO_CLASSES_MAP = new HashMap<Integer, ArrayList<Class<? extends Equation>>>() {{
        put( 1, LEVEL1_CLASSES );
        put( 2, LEVEL2_CLASSES );
        put( 3, LEVEL3_CLASSES );
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
        if ( !LEVEL_TO_CLASSES_MAP.containsKey( level ) ) {
            throw new IllegalArgumentException( "unsupported level: " + level );
        }

        // get classes
        ArrayList<Class<? extends Equation>> equationClasses = null;
        if ( playAllEquationsProperty.getValue() ) {
            equationClasses = getEquationClasses( level ); // gets all classes for the level
        }
        else {
            equationClasses = getEquationClasses( level, numberOfEquations );
        }

        // instantiate equations
        ArrayList<Equation> equations = new ArrayList<Equation>();
        for ( Class<? extends Equation> equationClass : equationClasses ) {
            equations.add( instantiateEquation( equationClass ) );
        }
        return equations;
    }

    /*
     * Gets all equation classes for a level.
     */
    private ArrayList<Class <? extends Equation>> getEquationClasses( int level ) {
        return LEVEL_TO_CLASSES_MAP.get( level );
    }

    /*
     * Gets a set of equation classes to be used in the game.
     * The set will contain no duplicates if numberOfEquations <= the number of equation classes for the level.
     * @param numberOfEquations
     * @param level 1, 2 or 3
     */
    private ArrayList<Class<? extends Equation>> getEquationClasses( int level, int numberOfEquations ) {
        ArrayList<Class<? extends Equation>> equationClasses = new ArrayList<Class<? extends Equation>>();
        for ( int i = 0; i < numberOfEquations; i++ ) {
            equationClasses.add( getRandomEquationClass( level, equationClasses ) );
        }
        assert ( equationClasses.size() == numberOfEquations );
        return equationClasses;
    }

    /*
     * Creates a random equation class for a specified level.
     * Will not select a class that is already in the equation set, unless the equation set
     * already contains all possible equations for the level.
     */
    private Class<? extends Equation> getRandomEquationClass( int level, ArrayList<Class<? extends Equation>> equationClasses ) {

        // Select a random equation class from the array for the specified level.
        int equationIndex = getRandomEquationIndex( level );
        Class<? extends Equation> equationClass = getEquationClass( level, equationIndex );

        // If this is a duplicate, find the next one that's not in the equation set.
        final int originalIndex = equationIndex;
        while ( equationClasses.contains( equationClass ) ) {
            equationIndex = getNextEquationIndex( level, equationIndex );
            equationClass = getEquationClass( level, equationIndex );
            if ( equationIndex == originalIndex ) {
                // we're back where we started, bail
                break;
            }
        }

        return equationClass;
    }

    /*
     * Gets an Equation class for a specified level and index.
     */
    private Class<? extends Equation> getEquationClass( int level, int equationIndex ) {
        return getEquationClasses( level ).get( equationIndex );
    }

    /*
     * Gets the number of equations for a specified level.
     */
    private int getNumberOfEquations( int level ) {
        return getEquationClasses( level ).size();
    }

    /*
     * Gets a random equation index for a specified level.
     */
    private int getRandomEquationIndex( int level ) {
        return (int) ( Math.random() * getNumberOfEquations( level ) );
    }

    /*
     * Gets the next equation index for a specified level and current equation index.
     */
    private int getNextEquationIndex( int level, int currentIndex ) {
        int nextIndex = currentIndex + 1;
        if ( nextIndex > getNumberOfEquations( level ) - 1 ) {
            nextIndex = 0;
        }
        return nextIndex;
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
