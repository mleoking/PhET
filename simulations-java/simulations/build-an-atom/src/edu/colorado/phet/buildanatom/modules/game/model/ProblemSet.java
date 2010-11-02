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

    private static final int MIN_NUM_SCHEMATIC_PROBS_PER_SET = 2;
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

        // There is a constraint that there must be a certain number of
        // schematic (a.k.a. Bohr model) problems in each problem set.  To
        // support this, we first add the min number of these problems to the
        // problem set.  We will randomize the order later.
        assert numProblems >= MIN_NUM_SCHEMATIC_PROBS_PER_SET;
        for ( int i = 0; i < MIN_NUM_SCHEMATIC_PROBS_PER_SET; i++){
            Problem problem = generateSchematicProblem( model, atomValueList );
            if ( problem != null ){
                addProblem( problem );
            }
        }

        // Now add problems of any type to the problem set.
        for ( int i = problems.size() - 1; i < numProblems; i++ ) {
            Problem problem = generateProblem( model, atomValueList );
            if ( problem != null ){
                addProblem( problem );
            }
        }

        // Radomize the order of the problems.
        Collections.shuffle( problems );

        if (problems.size() == 0){
            System.err.println( getClass().getName() + " - Warning: Empty problem set, probably due to developer dialog settings." );
        }
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
        return problems.get( RAND.nextInt( problems.size() ) );
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

    private static final ArrayList<ProblemType> LEVEL_1_SCHEMATIC_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_ELEMENT );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_1_ALL_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_ELEMENT );
            add( ProblemType.COUNTS_TO_ELEMENT );
        }
    };
    private static final ArrayList<ProblemType> LEVEL_2_SCHEMATIC_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_SYMBOL );
            add( ProblemType.SYMBOL_TO_SCHEMATIC );
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
    private static final ArrayList<ProblemType> LEVEL_3_SCHEMATIC_PROB_TYPES = new ArrayList<ProblemType>() {
        {
            add( ProblemType.SCHEMATIC_TO_SYMBOL );
            add( ProblemType.SYMBOL_TO_SCHEMATIC );
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
     * Map the game level and a flag for whether only schematic problems are
     * desired to the set of problem types that can be used.
     *
     * @param level
     * @param schematicProbsOnly
     * @return
     */
    private ArrayList<ProblemType> getProblemTypesForLevel(int level, boolean schematicProbsOnly ){
        ArrayList<ProblemType> problemTypes;
        switch( level ){
        case 1:
            if (schematicProbsOnly){
                problemTypes = LEVEL_1_SCHEMATIC_PROB_TYPES;
            }
            else{
                problemTypes = LEVEL_1_ALL_PROB_TYPES;
            }
            break;
        case 2:
            if (schematicProbsOnly){
                problemTypes = LEVEL_2_SCHEMATIC_PROB_TYPES;
            }
            else{
                problemTypes = LEVEL_2_ALL_PROB_TYPES;
            }
            break;
        case 3:
            if (schematicProbsOnly){
                problemTypes = LEVEL_3_SCHEMATIC_PROB_TYPES;
            }
            else{
                problemTypes = LEVEL_3_ALL_PROB_TYPES;
            }
            break;
        default:
            System.err.println( getClass().getName() + " - Error: Undefined game level." );
            problemTypes = LEVEL_1_ALL_PROB_TYPES; // Arbitrary.
            break;
        }
        return problemTypes;
    }

    /**
     * Generate a single schematic problem (e.g. a problem that includes a
     * schematic representation, also called a Bohr model, of the atom) given
     * the model (which contains the current level setting) and a pool of
     * atoms values that can be used for the problem.
     */
    private Problem generateSchematicProblem( BuildAnAtomGameModel model, AtomValuePool availableAtomValues ){
        ArrayList<ProblemType> possibleProbTypes = getProblemTypesForLevel( model.getLevelProperty().getValue(), true  );
        possibleProbTypes = filterProblemTypes( possibleProbTypes );
        if (possibleProbTypes.size() == 0){
            // There are no schematic problem types enabled that match this
            // level's constraints.
            System.err.println( getClass().getName() + " - Warning: No schematic problem types enabled for level " + model.getLevelProperty().getValue() );
            return null;
        }
        ProblemType problemType = possibleProbTypes.get( RAND.nextInt( possibleProbTypes.size() ) );
        AtomValue atomValue = availableAtomValues.getRandomAtomValueMaxSize( MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS );
        return createProblem( model, problemType, atomValue );
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
    private Problem generateProblem ( BuildAnAtomGameModel model, AtomValuePool availableAtomValues ){
        ArrayList<ProblemType> possibleProbTypes = getProblemTypesForLevel( model.getLevelProperty().getValue(), false );
        possibleProbTypes = filterProblemTypes( possibleProbTypes );
        if (possibleProbTypes.size() == 0){
            // There are no problem types enabled that match this level's
            // constraints.
            System.err.println( getClass().getName() + " - Warning: No problem types enabled for level " + model.getLevelProperty().getValue() );
            return null;
        }
        ProblemType problemType = possibleProbTypes.get( RAND.nextInt( possibleProbTypes.size() ) );
        AtomValue atomValue;
        if ( isSchematicProbType( problemType )){
            // Need to limit size of atom value.
            atomValue = availableAtomValues.getRandomAtomValueMaxSize( MAX_PROTON_NUMBER_FOR_SCHEMATIC_PROBS );
        }
        else{
            atomValue = availableAtomValues.getRandomAtomValue();
        }
        return createProblem( model, problemType, atomValue );
    }

    /**
     * Create a single problem given a problem type (e.g. Schematic to
     * Element) and an atom value that defines that atom configuration.
     */
    private Problem createProblem( BuildAnAtomGameModel model, ProblemType problemType, AtomValue atomValue ){
        Problem problem = null;
        switch ( problemType ){
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

            ArrayList<AtomValue> allowableAtomValues = new ArrayList<AtomValue>();
            for ( AtomValue av : remainingAtomValues ){
                if (av.getProtons() <= maxProtons){
                    allowableAtomValues.add( av );
                }
            }
            AtomValue atomValue;
            if ( allowableAtomValues.size() > 0){
                atomValue = allowableAtomValues.get( AVP_RAND.nextInt( allowableAtomValues.size() ) );
                remainingAtomValues.remove( atomValue );
                usedAtomValues.add( atomValue );
            }
            else{
                System.err.println( getClass().getName() + " - Warning: No remaining atoms values below specified threshold, returning previously used atom value." );
                atomValue = usedAtomValues.get( AVP_RAND.nextInt( usedAtomValues.size() ) );
            }
            return atomValue;
        }
    }
}
