package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.view.Function0;

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
    private Problem[] getPossibleProblems( BuildAnAtomGameModel model, AtomValue atomValue ) {
        if ( atomValue.getProtons() <= 3 ) {//only use schematic mode when Lithium or smaller
            return new Problem[] {
                    new SymbolToSchematicProblem( model, atomValue ),
                    new SchematicToSymbolProblem( model, atomValue ),
                    new SymbolToCountsProblem( model, atomValue ),
                    new CountsToSymbolProblem( model, atomValue ),
                    new SchematicToElementProblem( model, atomValue ),
                    new CountsToElementProblem( model, atomValue ),
            };
        }
        else {
            return new Problem[] {
                    new SymbolToCountsProblem( model, atomValue ),
                    new CountsToSymbolProblem( model, atomValue ),
                    new CountsToElementProblem( model, atomValue ),
            };
        }
    }

    private Problem getProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        Problem[] problems = getPossibleProblems( model, atomValue );
        return problems[random.nextInt( problems.length )];
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
