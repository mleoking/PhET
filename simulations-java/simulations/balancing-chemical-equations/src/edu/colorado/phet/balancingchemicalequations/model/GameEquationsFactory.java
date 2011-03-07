// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.util.ArrayList;

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
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2C_2H2O_CH4_CO2;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_2F2_H2O_OF2_2HF;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_3O2_2N2_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_5O2_4NO_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_6NO_5N2_6H2O;
import edu.colorado.phet.balancingchemicalequations.model.DisplacementEquation.Displacement_4NH3_7O2_4NO2_6H2O;
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
import edu.colorado.phet.common.phetcommon.model.Property;

/**
 * Factory that creates sets of equations, based on game level.
 * The design document specifies which equations correspond to which game levels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameEquationsFactory {

    // Level 1
    private static final ArrayList<Class <? extends Equation>> LEVEL1_LIST = new ArrayList<Class<? extends Equation>>() {{
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
    private static final ArrayList<Class <? extends Equation>> LEVEL2_LIST = new ArrayList<Class<? extends Equation>>() {{
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
    private static final ArrayList<Class <? extends Equation>>LEVEL3_LIST = new ArrayList<Class<? extends Equation>>() {{
        add( Displacement_2C2H6_7O2_4CO2_6H2O.class );
        add( Displacement_2C2H2_5O2_4CO2_2H2O.class );
        add( Displacement_C2H5OH_3O2_2CO2_3H2O.class );
        add( Displacement_4NH3_3O2_2N2_6H2O.class );
        add( Displacement_4NH3_5O2_4NO_6H2O.class );
        add( Displacement_4NH3_7O2_4NO2_6H2O.class );
        add( Displacement_4NH3_6NO_5N2_6H2O.class );
    }};

    // list of lists, so we can use level as an index to the proper list
    private static ArrayList< ArrayList<Class <? extends Equation>>> LEVEL_LISTS = new ArrayList<ArrayList<Class<? extends Equation>>>() {{
        add( LEVEL1_LIST );
        add( LEVEL2_LIST );
        add( LEVEL3_LIST );
    }};

    /*
     * Developer control.
     * If true, the factory returns the complete set of equations for a specified level.
     */
    private Property<Boolean> playAllEquationsProperty;

    /**
     * Default constructor.
     */
    public GameEquationsFactory( Property<Boolean> playAllEquationsProperty ) {
        this.playAllEquationsProperty = playAllEquationsProperty;
    }

    /**
     * Creates a set of equations to be used in the game.
     * @param numberOfEquations
     * @param level 1-N
     */
    public Equation[] createEquations( int numberOfEquations, int level ) {
        if ( level < 1 || level > LEVEL_LISTS.size() ) {
            throw new IllegalArgumentException( "unsupported level: " + level );
        }
        if ( playAllEquationsProperty.getValue() ) {
            return createAllEquations( level );
        }
        else {
            return createNEquations( numberOfEquations, level );
        }
    }

    /*
     * Creates a set of equations to be used in the game.
     * The set will contain no duplicates if numberOfEquations <= the number of equations for the level.
     * @param numberOfEquations
     * @param level 1, 2 or 3
     */
    private Equation[] createNEquations( int numberOfEquations, int level ) {
        ArrayList<Class<? extends Equation>> equationClasses = new ArrayList<Class<? extends Equation>>();
        for ( int i = 0; i < numberOfEquations; i++ ) {
            equationClasses.add( getRandomEquationClass( level, equationClasses ) );
        }
        assert ( equationClasses.size() == numberOfEquations );

        Equation[] equations = new Equation[equationClasses.size()];
        for ( int i = 0; i < equations.length; i++ ) {
            equations[i] = instantiateEquation( equationClasses.get( i ) );
        }

        return equations;
    }

    /*
     * Creates a complete set of equations for a specified level.
     * This is used for debugging in dev mode.
     * @param level
     */
    private Equation[] createAllEquations( int level ) {
        ArrayList<Class<? extends Equation>> equationClasses = LEVEL_LISTS.get( level - 1 );
        Equation[] equations = new Equation[equationClasses.size()];
        for ( int i = 0; i < equations.length; i++ ) {
            equations[i] = instantiateEquation( equationClasses.get( i ) );
        }
        return equations;
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
    private Class<? extends Equation> getEquationClass( int level, int reactionIndex ) {
        return getEquationList( level ).get( reactionIndex );
    }

    /*
     * Gets the number of equations for a specified level.
     */
    private int getNumberOfEquations( int level ) {
        return getEquationList( level ).size();
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
     * Gets the list of equations for a level.
     * Levels are numbered from 1-N, as in the model.
     */
    private ArrayList<Class <? extends Equation>> getEquationList( int level ) {
        return LEVEL_LISTS.get( level - 1 );
    }

    /*
     * Uses reflection to instantiate an Equation by class.
     */
    private static Equation instantiateEquation( Class<? extends Equation> c ) {
        Equation equation = null;
        try {
            equation = c.newInstance();
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
        GameEquationsFactory factory = new GameEquationsFactory( new Property<Boolean>( false ) );
        for ( int level = 1; level < 4; level++ ) {
            System.out.println( "LEVEL " + level );
            Equation[] equations = factory.createEquations( 5, level );
            for ( Equation equation : equations ) {
                System.out.println( equation.getName() );
            }
        }
    }
}
