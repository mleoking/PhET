/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.buildanatom.developer.ProblemTypeSelectionDialog;

/**
 * Represents an ordered list of Problems corresponding to a particular difficulty level.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class ProblemSet {

    private static final int MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS = 3; // Disallow schematic (Bohr model) probs above this size.
    public static final Random RAND = new Random();

    private final ArrayList<Problem> problems = new ArrayList<Problem>();
    private int currentProblemIndex = 0;

    /**
     * Constructor.  This is where the problems are generated and put into the
     * problem set.  They are created randomly based on the constraints of the
     * current level selection and the "pool" of potential problems for the
     * level.
     *
     * IMPORTANT NOTE: The existence of the developer dialog that allows
     * PhET users to disable various problem types makes it possible for this
     * problem set to end up empty.  Users of this class need to handle this
     * possibility.
     */
    public ProblemSet( BuildAnAtomGameModel model, int numProblems ) {

        // Create a pool of atom values that can be used to create problems
        // for the problem set.
        AtomValuePool atomValueList = new AtomValuePool( model );

        // Now add problems of any type to the problem set.
        for ( int i = problems.size(); i < numProblems; i++ ) {
            Problem problem = generateProblem( model, atomValueList );
            if ( problem != null ){
                addProblem( problem );
            }
        }

        // Radomize the order of the problems within the set.
        Collections.shuffle( problems );

        if (problems.size() == 0){
            System.err.println( getClass().getName() + " - Warning: Empty problem set, probably due to developer dialog settings." );
        }
    }

    private void addProblem( Problem problem) {
        problems.add( problem );
    }

    public Problem getProblem( int i ) {
        return problems.get( i );
    }

    public int getProblemIndex( Problem problem ) {
        return problems.indexOf( problem );
    }

    public int getTotalNumProblems() {
        return problems.size();
    }

    public Problem getCurrentProblem(){
        return problems.get( currentProblemIndex );
    }

    public boolean isLastProblem() {
        return currentProblemIndex == problems.size() -1;
    }

    /**
     * Move to the next problem in the problem set.
     *
     * @return
     */
    public Problem nextProblem() {
        assert !isLastProblem();
        currentProblemIndex++;
        return getCurrentProblem();
    }

    // Lists that define the problem types that can be used at a given level.
    private static final ArrayList<ProblemType> LEVEL_1_ALL_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_ELEMENT );
            add( ProblemType.COUNTS_TO_ELEMENT );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_2_ALL_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_SYMBOL );
            add( ProblemType.SYMBOL_TO_SCHEMATIC );
            add( ProblemType.SYMBOL_TO_COUNTS );
            add( ProblemType.COUNTS_TO_SYMBOL );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_3_ALL_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_SYMBOL );
            add( ProblemType.SYMBOL_TO_SCHEMATIC );
            add( ProblemType.SYMBOL_TO_COUNTS );
            add( ProblemType.COUNTS_TO_SYMBOL );
        }
    };

    /**
     * Filter the given set of problem types based on the settings of the
     * developer dialog.
     *
     * @param problemTypesIn
     * @return Problem types allowed based on the developer dialog settings.
     */
    private ArrayList<ProblemType> filterProblemTypes( ArrayList<ProblemType> problemTypesIn ){
        ArrayList<ProblemType> problemTypesOut = new ArrayList<ProblemType>();
        ProblemTypeSelectionDialog allowedProbsDlg = ProblemTypeSelectionDialog.getInstance();
        for ( ProblemType problemType : problemTypesIn ){
            if ( allowedProbsDlg.isProblemTypeAllowed( problemType )){
                problemTypesOut.add( problemType );
            }
        }
        return problemTypesOut;
    }

    /**
     * Get all of the problems types that are allowed for the specified level.
     */
    private ArrayList<ProblemType> getAllProblemTypesForLevel( int level ) {
        ArrayList<ProblemType> problemTypes;
        switch ( level ) {
        case 1:
            problemTypes = LEVEL_1_ALL_PROB_TYPES;
            break;
        case 2:
            problemTypes = LEVEL_2_ALL_PROB_TYPES;
            break;
        case 3:
            problemTypes = LEVEL_3_ALL_PROB_TYPES;
            break;
        default:
            System.err.println( getClass().getName() + " - Error: Undefined game level." );
            assert false;
            problemTypes = LEVEL_1_ALL_PROB_TYPES; // Arbitrary.
            break;
        }
        return problemTypes;
    }

    /**
     * Generate a single problem given the model (which contains the current
     * level setting) and a pool of atoms values that can be used for the
     * problem.
     *
     * @param model
     * @param atomValuePool
     * @return
     */
    private Problem generateProblem( BuildAnAtomGameModel model, AtomValuePool availableAtomValues ) {
        ArrayList<ProblemType> possibleProbTypes = getAllProblemTypesForLevel( model.getLevelProperty().getValue() );
        possibleProbTypes = filterProblemTypes( possibleProbTypes );
        if ( possibleProbTypes.size() == 0 ) {
            // There are no problem types enabled that match this level's
            // constraints.
            System.err.println( getClass().getName() + " - Warning: No problem types enabled for level " + model.getLevelProperty().getValue() );
            return null;
        }
        ProblemType problemType = possibleProbTypes.get( RAND.nextInt( possibleProbTypes.size() ) );
        AtomValue atomValue;
        if ( isSchematicProbType( problemType ) ) {
            // Need to limit size of atom value.
            atomValue = availableAtomValues.getRandomAtomValueMaxSize( MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS );
        }
        else {
            atomValue = availableAtomValues.getRandomAtomValue();
        }
        return createProblem( model, problemType, atomValue );
    }

    /**
     * Create a single problem given a problem type (e.g. Schematic to
     * Element) and an atom value that defines that atom configuration.
     */
    private Problem createProblem( BuildAnAtomGameModel model, ProblemType problemType, AtomValue atomValue ) {
        Problem problem = null;
        switch ( problemType ) {
        case SYMBOL_TO_SCHEMATIC:
            problem = new SymbolToSchematicProblem( model, atomValue );
            break;
        case SCHEMATIC_TO_SYMBOL:
            problem = new SchematicToSymbolProblem( model, atomValue );
            break;
        case SYMBOL_TO_COUNTS:
            problem = new SymbolToCountsProblem( model, atomValue );
            break;
        case COUNTS_TO_SYMBOL:
            problem = new CountsToSymbolProblem( model, atomValue );
            break;
        case SCHEMATIC_TO_ELEMENT:
            problem = new SchematicToElementProblem( model, atomValue );
            break;
        case COUNTS_TO_ELEMENT:
            problem = new CountsToElementProblem( model, atomValue );
            break;
        default:
            System.err.println( getClass().getName() + " - Error: Request to create unknown problem type." );
            break;
        }
        return problem;
    }

    /**
     * Helper function to determine whether a given problem type has a
     * schematic atom representation on either either side of the problem.
     *
     * @param problemType
     * @return
     */
    private boolean isSchematicProbType( ProblemType problemType ){
        return ( problemType == ProblemType.SCHEMATIC_TO_ELEMENT || problemType == ProblemType.SCHEMATIC_TO_SYMBOL || problemType == ProblemType.SYMBOL_TO_SCHEMATIC);
    }

    /**
     * Helper class for managing the list of atom values that can be used for
     * creating problems.  The two main pieces of functionality added by this
     * class is that it removes items from the enclosed list automatically,
     * and it keeps track of what we removed in case it ends up being needed
     * again.
     */
    private static class AtomValuePool {
        private static final Random AVP_RAND = new Random();
        private final ArrayList<AtomValue> remainingAtomValues;
        private final ArrayList<AtomValue> usedAtomValues = new ArrayList<AtomValue>();

        public AtomValuePool(BuildAnAtomGameModel model) {
            remainingAtomValues = new ArrayList<AtomValue>( model.getLevelPool() );
        }

        /**
         * Get a random atom value from the pool of available ones, and then
         * remove it from the list so that it won't be returned again.
         * @return
         */
        public AtomValue getRandomAtomValue(){
            assert remainingAtomValues.size() > 0;
            int index = AVP_RAND.nextInt( remainingAtomValues.size() );
            AtomValue atomValue = remainingAtomValues.get( index );
            usedAtomValues.add( atomValue );
            remainingAtomValues.remove( index );
            return atomValue;
        }

        /**
         * Get an atom value from the pool that is at or below the specified
         * proton count, and then remove it from the list of remaining atoms.
         * If there are none that match the constraint, try the list of used
         * atoms.
         */
        public AtomValue getRandomAtomValueMaxSize( int maxProtons ){

            // Make a list of the atoms that are small enough.
            ArrayList<AtomValue> allowableAtomValues = new ArrayList<AtomValue>();
            for ( AtomValue av : remainingAtomValues ){
                if (av.getProtons() <= maxProtons){
                    allowableAtomValues.add( av );
                }
            }
            if ( allowableAtomValues.size() == 0){
                // There were none available on the list of unused atoms, so
                // add some from the list of used atoms.
                System.err.println( getClass().getName() + " - Warning: No remaining atoms values below specified threshold, returning previously used atom value." );
                for ( AtomValue av : usedAtomValues ){
                    if (av.getProtons() <= maxProtons){
                        allowableAtomValues.add( av );
                    }
                }
            }

            AtomValue atomValue = null;
            if ( allowableAtomValues.size() > 0 ){
                atomValue = allowableAtomValues.get( AVP_RAND.nextInt( allowableAtomValues.size() ) );
                remainingAtomValues.remove( atomValue );
                usedAtomValues.add( atomValue );
            }
            else{
                System.err.println( getClass().getName() + " - Error: No atoms found below specified size threshold." );
            }
            return atomValue;
        }
    }
}
