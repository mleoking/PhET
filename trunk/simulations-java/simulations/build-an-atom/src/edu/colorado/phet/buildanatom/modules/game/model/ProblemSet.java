package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.buildanatom.developer.ProblemTypeSelectionDialog;

/**
 * Represents an ordered list of Problems corresponding to a particular difficulty level
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class ProblemSet {
    private final ArrayList<Problem> allProblems = new ArrayList<Problem>();
    private int currentProblemIndex = 0;
    public static final Random random = new Random();

    public ProblemSet( BuildAnAtomGameModel model, int numProblems ) {
        final ArrayList<AtomValue> levelPool = model.getLevelPool();
        assert levelPool.size()>=numProblems;//otherwise problems would be duplicated in a game
        ArrayList<AtomValue> remainingProblems = new ArrayList<AtomValue>( levelPool );//don't re-use the same problem twice in the same game
        for ( int i = 0; i < numProblems; i++ ) {
            final int index = random.nextInt( remainingProblems.size() );
            AtomValue atomValue = remainingProblems.get( index );
            remainingProblems.remove( index );
            Problem problem = getProblem( model, atomValue );
            addProblem( problem );
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

        // Make sure that at least one problem type is allowed.
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

    public void addProblem( Problem problem) {
        allProblems.add( problem );
    }

    public Problem getProblem( int i ) {
        return allProblems.get( i );
    }

    public int getProblemIndex( Problem problem ) {
        return allProblems.indexOf( problem );
    }

    public int getTotalNumProblems() {
        return allProblems.size();
    }

    public Problem getCurrentProblem(){
        return allProblems.get( currentProblemIndex );
    }

    /**
     * @return
     */
    public boolean isLastProblem() {
        return currentProblemIndex == allProblems.size() -1;
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
}
