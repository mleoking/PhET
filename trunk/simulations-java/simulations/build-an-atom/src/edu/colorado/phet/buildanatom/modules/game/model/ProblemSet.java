/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private static final ArrayList<ProblemType> LEVEL_1_ALLOWED_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_ELEMENT );
            add( ProblemType.COUNTS_TO_ELEMENT );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_2_ALLOWED_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.COUNTS_TO_CHARGE_QUESTION );
            add( ProblemType.COUNTS_TO_MASS_QUESTION );
            add( ProblemType.SCHEMATIC_TO_CHARGE_QUESTION );
            add( ProblemType.SCHEMATIC_TO_MASS_QUESTION );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_3_ALLOWED_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_SYMBOL_MASS );
            add( ProblemType.SCHEMATIC_TO_SYMBOL_PROTON_COUNT );
            add( ProblemType.SYMBOL_TO_SCHEMATIC );
            add( ProblemType.COUNTS_TO_SYMBOL_MASS );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_4_ALLOWED_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_SYMBOL_ALL );
            add( ProblemType.SYMBOL_TO_SCHEMATIC );
            add( ProblemType.SYMBOL_TO_COUNTS );
            add( ProblemType.COUNTS_TO_SYMBOL_ALL );
        }
    };

    // Data structure that maps lists of problem types to the various game levels.
    private static final HashMap<Integer, ArrayList<ProblemType>> mapLevelToProbTypes = new HashMap<Integer, ArrayList<ProblemType>>(){{
        put(1, LEVEL_1_ALLOWED_PROB_TYPES);
        put(2, LEVEL_2_ALLOWED_PROB_TYPES);
        put(3, LEVEL_3_ALLOWED_PROB_TYPES);
        put(4, LEVEL_4_ALLOWED_PROB_TYPES);
    }};

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
     * Generate a single problem given the model (which contains the current
     * level setting) and a pool of atoms values that can be used for the
     * problem.
     *
     * @param model
     * @param atomValuePool
     * @return
     */
    private Problem generateProblem( BuildAnAtomGameModel model, AtomValuePool availableAtomValues ) {

        // Get a list of all possible problem types for the current level.
        ArrayList<ProblemType> possibleProbTypes = mapLevelToProbTypes.get( model.getLevelProperty().getValue() );

        // Filter the prob types based on the developer dialog setting.
        possibleProbTypes = filterProblemTypes( possibleProbTypes );

        if ( possibleProbTypes.size() == 0 ) {
            // There are no problem types enabled that match this level's
            // constraints.
            System.err.println( getClass().getName() + " - Warning: No problem types enabled for level " + model.getLevelProperty().getValue() );
            return null;
        }

        // Randomly pick a problem type.
        ProblemType problemType = possibleProbTypes.get( RAND.nextInt( possibleProbTypes.size() ) );

        // Pick an atom value from the list of those remaining.  This is where
        // constraints between problem types and atom values are handled.
        AtomValue atomValue;
        if ( isSchematicProbType( problemType ) ) {
            // Need to limit size of atom value.
            if ( problemType == ProblemType.SCHEMATIC_TO_CHARGE_QUESTION || problemType == ProblemType.COUNTS_TO_CHARGE_QUESTION ){
                // Should only choose a charged atom.
                atomValue = availableAtomValues.getRandomChargedAtomValue( MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS );
            }
            else{
                atomValue = availableAtomValues.getRandomAtomValueMaxSize( MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS );
            }
        }
        else { // There are no constraints on the size of the atom.
            if ( problemType == ProblemType.SCHEMATIC_TO_CHARGE_QUESTION ){
                atomValue = availableAtomValues.getRandomChargedAtomValue( Integer.MAX_VALUE );
            }
            else{
                atomValue = availableAtomValues.getRandomAtomValue();
            }
        }
        availableAtomValues.removeAtomValue( atomValue );
        return createProblem( model, problemType, atomValue );
    }

    /**
     * Create a single problem given a problem type (e.g. Schematic to
     * Element) and an atom value that defines that atom configuration.
     */
    private Problem createProblem( BuildAnAtomGameModel model, ProblemType problemType, AtomValue atomValue ) {
        Problem problem = null;
        switch ( problemType ) {
        case COUNTS_TO_ELEMENT:
            problem = new CountsToElementProblem( model, atomValue );
            break;
        case COUNTS_TO_CHARGE_QUESTION:
            problem = new CountsToChargeQuestionProblem( model, atomValue );
            break;
        case COUNTS_TO_MASS_QUESTION:
            problem = new CountsToMassQuestionProblem( model, atomValue );
            break;
        case COUNTS_TO_SYMBOL_ALL:
            problem = new CountsToSymbolProblem( model, atomValue, true, true, true );
            break;
        case COUNTS_TO_SYMBOL_MASS:
            problem = new CountsToSymbolProblem( model, atomValue, false, true, false );
            break;
        case COUNTS_TO_SYMBOL_PROTON_COUNT:
            problem = new CountsToSymbolProblem( model, atomValue, true, false, false );
            break;
        case SCHEMATIC_TO_ELEMENT:
            problem = new SchematicToElementProblem( model, atomValue );
            break;
        case SCHEMATIC_TO_CHARGE_QUESTION:
            problem = new SchematicToChargeQuestionProblem( model, atomValue );
            break;
        case SCHEMATIC_TO_MASS_QUESTION:
            problem = new SchematicToMassQuestionProblem( model, atomValue );
            break;
        case SCHEMATIC_TO_PROTON_COUNT_QUESTION:
            problem = new SchematicToProtonCountQuestionProblem( model, atomValue );
            break;
        case SCHEMATIC_TO_SYMBOL_ALL:
            problem = new SchematicToSymbolProblem( model, atomValue, true, true, true );
            break;
        case SCHEMATIC_TO_SYMBOL_MASS:
            problem = new SchematicToSymbolProblem( model, atomValue, false, true, false );
            break;
        case SCHEMATIC_TO_SYMBOL_PROTON_COUNT:
            problem = new SchematicToSymbolProblem( model, atomValue, true, false, false );
            break;
        case SYMBOL_TO_COUNTS:
            problem = new SymbolToCountsProblem( model, atomValue );
            break;
        case SYMBOL_TO_SCHEMATIC:
            problem = new SymbolToSchematicProblem( model, atomValue );
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
        return ( problemType == ProblemType.SCHEMATIC_TO_ELEMENT ||
                 problemType == ProblemType.SCHEMATIC_TO_CHARGE_QUESTION ||
                 problemType == ProblemType.SCHEMATIC_TO_MASS_QUESTION ||
                 problemType == ProblemType.SCHEMATIC_TO_PROTON_COUNT_QUESTION ||
                 problemType == ProblemType.SCHEMATIC_TO_SYMBOL_ALL ||
                 problemType == ProblemType.SCHEMATIC_TO_SYMBOL_PROTON_COUNT ||
                 problemType == ProblemType.SCHEMATIC_TO_SYMBOL_MASS ||
                 problemType == ProblemType.SYMBOL_TO_SCHEMATIC );
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
            return atomValue;
        }

        /**
         * Remove the specified atom value from the list of those available.
         *
         * @param atomValueToRemove
         * @return true if value found, false if not.
         */
        public boolean removeAtomValue( AtomValue atomValueToRemove ){
            if ( remainingAtomValues.remove( atomValueToRemove ) ){
                usedAtomValues.add( atomValueToRemove );
                return true;
            }
            return false; // Didn't find the value on the list.
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
                if (av.getNumProtons() <= maxProtons){
                    allowableAtomValues.add( av );
                }
            }
            if ( allowableAtomValues.size() == 0){
                // There were none available on the list of unused atoms, so
                // add them from the list of used atoms instead.
                System.err.println( getClass().getName() + " - Warning: No remaining atoms values below specified threshold, searching previously used atom value." );
                for ( AtomValue av : usedAtomValues ){
                    if (av.getNumProtons() <= maxProtons){
                        allowableAtomValues.add( av );
                    }
                }
            }

            // Choose a value from the list.
            AtomValue atomValue = null;
            if ( allowableAtomValues.size() > 0 ){
                atomValue = allowableAtomValues.get( AVP_RAND.nextInt( allowableAtomValues.size() ) );
            }
            else{
                System.err.println( getClass().getName() + " - Error: No atoms found below specified size threshold." );
            }
            return atomValue;
        }

        /**
         * Get an atom value from the pool that is charged, i.e. non-neutral.  If
         * no such atoms are on the list of available remaining ones, choose one
         * from the list of used atoms.
         */
        public AtomValue getRandomChargedAtomValue( int maxProtons ){

            // Make a list of the atoms that are charged.
            ArrayList<AtomValue> allowableAtomValues = new ArrayList<AtomValue>();
            for ( AtomValue av : remainingAtomValues ){
                if ( !av.isNeutral() && av.getNumProtons() < maxProtons ){
                    allowableAtomValues.add( av );
                }
            }
            if ( allowableAtomValues.size() == 0){
                // There were none available on the list of unused atoms, so
                // add them from the list of used atoms instead.
                System.err.println( getClass().getName() + " - Warning: No remaining charged atoms values available, searching previously used atom value." );
                for ( AtomValue av : usedAtomValues ){
                    if ( !av.isNeutral() && av.getNumProtons() < maxProtons ){
                        allowableAtomValues.add( av );
                    }
                }
            }

            // Choose a value from the list.
            AtomValue atomValue = null;
            if ( allowableAtomValues.size() > 0 ){
                atomValue = allowableAtomValues.get( AVP_RAND.nextInt( allowableAtomValues.size() ) );
            }
            else{
                System.err.println( getClass().getName() + " - Error: No charged atoms found, returning a neutral atom." );
                atomValue = getRandomAtomValue();
            }
            return atomValue;
        }
    }
}
