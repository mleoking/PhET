package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import edu.colorado.phet.buildanatom.developer.ProblemTypeSelectionDialog;

/**
 * Represents an ordered list of Problems corresponding to a particular difficulty level
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class ProblemSet {
    private static final int MIN_NUM_SCHEMATIC_PROBS_PER_SET = 2;
    private static final int MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS = 3; // Disallow schematic (Bohr model) probs above this size.

    private final ArrayList<Problem> problems = new ArrayList<Problem>();
    private int currentProblemIndex = 0;
    public static final Random random = new Random();

    /**
     * Constructor.  This is where the problems are generated and put into the
     * problem set.  They are created randomly based on the constraints of the
     * current level selection and the "pool" of potential problems for the
     * level.
     */
    public ProblemSet( BuildAnAtomGameModel model, int numProblems ) {

        // Create a pool of atom values that can be used to create problems
        // for the problem set.
        AtomValuePool atomValueList = new AtomValuePool( model );

        // There is a constraint that there must be a certain number of
        // schematic (a.k.a. Bohr) model problems in each problem set.  To
        // support this, we first add the min number of these problems to the
        // problem set.  We will randomize the order later.
        assert numProblems >= MIN_NUM_SCHEMATIC_PROBS_PER_SET;
        for ( int i = 0; i < MIN_NUM_SCHEMATIC_PROBS_PER_SET; i++){
            Problem problem = getProblem( model, atomValueList.getRandomAtomValueMaxSize( MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS ) );
            addProblem( problem );
        }

        // Now add problems of any type to the problem set.
        for ( int i = MIN_NUM_SCHEMATIC_PROBS_PER_SET; i < numProblems; i++ ) {
            Problem problem = getProblem( model, atomValueList.getRandomAtomValue() );
            addProblem( problem );
        }

        // Radomize the order of the problems.
        Collections.shuffle( problems );
    }

    /**
     * Returns the set of possible problems to choose from given a specific AtomValue
     *
     * @param model
     * @param atomValue
     * @return
     */
    private ArrayList<Problem> getPossibleProblems( BuildAnAtomGameModel model, AtomValue atomValue ) {

        // Get the developer dialog that contains the information regarding
        // which problem types are allowed.
        // TODO: We may wish to remove this once interviews are complete.
        ProblemTypeSelectionDialog allowedProbsDlg = ProblemTypeSelectionDialog.getInstance();

        // Make sure that at least one problem type is allowed based on the
        // current setting of the developer dialog.  If not, turn 'em all back
        // on.
        if ( !allowedProbsDlg.isSymbolToSchematicProblemAllowed() &&
             !allowedProbsDlg.isSchematicToElementProblemAllowed() &&
             !allowedProbsDlg.isSchematicToSymbolProblemAllowed() &&
             !allowedProbsDlg.isCountsToElementProblemAllowed() &&
             !allowedProbsDlg.isCountsToSymbolProblemAllowed() &&
             !allowedProbsDlg.isSymbolToCountsProblemAllowed() ){
            // No problem types are selected.  Warn the user via console and
            // turn on all problem types.
            System.err.println( getClass().getName() + " - Error: No problem types selected, re-selecting them all." );
            allowedProbsDlg.setAllSelected();
        }

        // Note: Due to the difficulty of being able to count the nucleons in
        // the schematic view (a.k.a. the Bohr model), a restriction has been
        // added so that we don't present this view for any atoms heavier than
        // Lithium.  HOWEVER, this restriction is problematic if someone has
        // used the developer controls to turn off all problem types except
        // those involving the schematic view.  So, the rule is that we follow
        // this restriction unless only schematic problems are enabled, and
        // then the restriction goes out the window.
        boolean onlySchematicProbsEnabled = (allowedProbsDlg.isSymbolToSchematicProblemAllowed() ||
                allowedProbsDlg.isSchematicToElementProblemAllowed() ||
                allowedProbsDlg.isSchematicToSymbolProblemAllowed()) &&
                !allowedProbsDlg.isCountsToElementProblemAllowed() &&
                !allowedProbsDlg.isCountsToSymbolProblemAllowed() &&
                !allowedProbsDlg.isSymbolToCountsProblemAllowed();

        ArrayList<Problem> problems = new ArrayList<Problem>();
        if (allowedProbsDlg.isSymbolToCountsProblemAllowed()){
            problems.add(new SymbolToCountsProblem( model, atomValue ));
        }
        if (allowedProbsDlg.isCountsToSymbolProblemAllowed()){
            problems.add(new CountsToSymbolProblem( model, atomValue ));
        }
        if (allowedProbsDlg.isCountsToElementProblemAllowed()){
            problems.add(new CountsToElementProblem( model, atomValue ));
        }
        if (allowedProbsDlg.isSymbolToSchematicProblemAllowed() && (atomValue.getProtons() <= 3 || onlySchematicProbsEnabled)){
          //only use schematic mode when Lithium or smaller
            problems.add(new SymbolToSchematicProblem( model, atomValue ));
        }
        if (allowedProbsDlg.isSchematicToSymbolProblemAllowed() && (atomValue.getProtons() <= 3 || onlySchematicProbsEnabled)){
            //only use schematic mode when Lithium or smaller
            problems.add(new SchematicToSymbolProblem( model, atomValue ));
        }
        if (allowedProbsDlg.isSchematicToElementProblemAllowed() && (atomValue.getProtons() <= 3 || onlySchematicProbsEnabled)){
            //only use schematic mode when Lithium or smaller
            problems.add(new SchematicToElementProblem( model, atomValue ));
        }
        return problems;
    }

    private Problem getProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        ArrayList<Problem> problems = getPossibleProblems( model, atomValue );
        return problems.get( random.nextInt( problems.size() ) );
    }

    private Problem getSchematicProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        ArrayList<Problem> problems = getPossibleSchematicProblems( model, atomValue );
        return problems.get( random.nextInt( problems.size() ) );
    }

    /**
     * Get all possible problems that involve an atom schematic, a.k.a. a
     * "Bohr model" representation.
     */
    private ArrayList<Problem> getPossibleSchematicProblems( BuildAnAtomGameModel model, AtomValue atomValue ) {

        assert atomValue.getProtons() < MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS;

        // Get the developer dialog that contains the information regarding
        // which problem types are allowed.
        // TODO: We may wish to remove this once interviews are complete.
        ProblemTypeSelectionDialog allowedProbsDlg = ProblemTypeSelectionDialog.getInstance();

        // Make sure that at least one schematic problem type is allowed based
        // on the current setting of the developer dialog.  If not, turn 'em
        // all back on.
        if ( !allowedProbsDlg.isSymbolToSchematicProblemAllowed() &&
             !allowedProbsDlg.isSchematicToElementProblemAllowed() &&
             !allowedProbsDlg.isSchematicToSymbolProblemAllowed() ){
            // No problem types are selected.  Warn the user via console and
            // turn on all problem types.
            System.err.println( getClass().getName() + " - Error: No problem types selected, re-selecting them all." );
            allowedProbsDlg.setAllSelected();
        }

        ArrayList<Problem> problems = new ArrayList<Problem>();
        if ( allowedProbsDlg.isSymbolToSchematicProblemAllowed() ) {
            problems.add( new SymbolToSchematicProblem( model, atomValue ) );
        }
        if ( allowedProbsDlg.isSchematicToSymbolProblemAllowed() ) {
            problems.add( new SchematicToSymbolProblem( model, atomValue ) );
        }
        if ( allowedProbsDlg.isSchematicToElementProblemAllowed() ) {
            problems.add( new SchematicToElementProblem( model, atomValue ) );
        }
        return problems;
    }

    /**
     * Get all possible problems that involve the symbol representation, i.e.
     * the white box with the element symbol in the middle and the various
     * numbers that qualify it in the corners of the box.
     */
    private ArrayList<Problem> getPossibleSymbolProblems( BuildAnAtomGameModel model, AtomValue atomValue ) {

        // Get the developer dialog that contains the information regarding
        // which problem types are allowed.
        // TODO: We may wish to remove this once interviews are complete.
        ProblemTypeSelectionDialog allowedProbsDlg = ProblemTypeSelectionDialog.getInstance();

        // Make sure that at least one symbol problem type is allowed based
        // on the current setting of the developer dialog.  If not, turn 'em
        // all back on.
        if ( !allowedProbsDlg.isSymbolToSchematicProblemAllowed() &&
                !allowedProbsDlg.isSchematicToSymbolProblemAllowed() &&
                !allowedProbsDlg.isSymbolToCountsProblemAllowed() &&
                !allowedProbsDlg.isCountsToSymbolProblemAllowed() ) {
            // No problem types are selected.  Warn the user via console and
            // turn on all problem types.
            System.err.println( getClass().getName() + " - Error: No problem types selected, re-selecting them all." );
            allowedProbsDlg.setAllSelected();
        }

        ArrayList<Problem> problems = new ArrayList<Problem>();
        if ( allowedProbsDlg.isSymbolToSchematicProblemAllowed() ) {
            problems.add( new SymbolToSchematicProblem( model, atomValue ) );
        }
        if ( allowedProbsDlg.isSchematicToSymbolProblemAllowed() ) {
            problems.add( new SchematicToSymbolProblem( model, atomValue ) );
        }
        if ( allowedProbsDlg.isSchematicToElementProblemAllowed() ) {
            problems.add( new SchematicToElementProblem( model, atomValue ) );
        }
        return problems;
    }

    public void addProblem( Problem problem) {
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

    /**
     * @return
     */
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

    private static class AtomValuePool {
        private static final Random RAND = new Random();
        private final ArrayList<AtomValue> remainingAtomValues;

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
            int index = RAND.nextInt( remainingAtomValues.size() );
            AtomValue atomValue = remainingAtomValues.get( index );
            remainingAtomValues.remove( index );
            return atomValue;
        }

        /**
         * Get a atom value from the pool that is at or below the specified
         * proton count.
         */
        public AtomValue getRandomAtomValueMaxSize( int maxProtons ){

            ArrayList<AtomValue> allowableAtomValues = new ArrayList<AtomValue>();
            for ( AtomValue av : remainingAtomValues ){
                if (av.getProtons() <= maxProtons){
                    allowableAtomValues.add( av );
                }
            }
            AtomValue atomValue;
            if ( allowableAtomValues.size() > 0){
                atomValue = allowableAtomValues.get( RAND.nextInt( allowableAtomValues.size() ) );
            }
            else{
                System.err.println( getClass().getName() + " - Warning: No remaining atoms values below specified threshold, returning arbitrary problem." );
                atomValue = remainingAtomValues.get( RAND.nextInt( allowableAtomValues.size() ) );
            }
            remainingAtomValues.remove( atomValue );
            return atomValue;
        }
    }

    private enum ProblemTypes {
        SYMBOL_TO_SCHEMATIC,
        SCHEMATIC_TO_SYMBOL,
        SYMBOL_TO_COUNTS,
        COUNTS_TO_SYMBOL,
        SCHEMATIC_TO_ELEMENT,
        COUNTS_TO_ELEMENT
    }

    private static final ProblemTypes[] LEVEL_1_SCHEMATIC_PROB_TYPES = {
            ProblemTypes.SCHEMATIC_TO_ELEMENT
            };
    private static final ProblemTypes[] LEVEL_1_ALL_PROB_TYPES = {
            ProblemTypes.SCHEMATIC_TO_ELEMENT,
            ProblemTypes.COUNTS_TO_ELEMENT
            };
    private static final ProblemTypes[] LEVEL_2_SCHEMATIC_PROB_TYPES = {
            ProblemTypes.SCHEMATIC_TO_SYMBOL,
            ProblemTypes.SYMBOL_TO_SCHEMATIC
            };
    private static final ProblemTypes[] LEVEL_2_ALL_PROB_TYPES = {
            ProblemTypes.SCHEMATIC_TO_SYMBOL,
            ProblemTypes.SYMBOL_TO_SCHEMATIC,
            ProblemTypes.SYMBOL_TO_COUNTS,
            ProblemTypes.COUNTS_TO_SYMBOL
            };
    private static final ProblemTypes[] LEVEL_3_SCHEMATIC_PROB_TYPES = {
            ProblemTypes.SCHEMATIC_TO_SYMBOL,
            ProblemTypes.SYMBOL_TO_SCHEMATIC
            };
    private static final ProblemTypes[] LEVEL_3_ALL_PROB_TYPES = {
            ProblemTypes.SCHEMATIC_TO_SYMBOL,
            ProblemTypes.SYMBOL_TO_SCHEMATIC,
            ProblemTypes.SYMBOL_TO_COUNTS,
            ProblemTypes.COUNTS_TO_SYMBOL
            };
}
