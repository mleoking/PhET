package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents an ordered list of Problems corresponding to a particular difficulty level
 *
 * @author Sam Reid
 */
public class ProblemSet {
    //keeps track by ordering
    private final ArrayList<Problem> allProblems = new ArrayList<Problem>();
    private int currentProblemIndex = 0;
    public static final Random random = new Random();

    public ProblemSet( BuildAnAtomGameModel model, int numProblems ) {
        // TODO: We need to make sure that the same problem is not generated twice.
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

    private int[] getProblemTypes( AtomValue atomValue ) {
        if ( atomValue.getProtons() <= 10 ) {
            return new int[] { 0, 1, 2, 3, 4, 5 };
        }
        else {
            return new int[] { 2, 3, 5 };
        }
    }
    private Problem getProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        int [] availableTypes = getProblemTypes( atomValue );
        int problemType = availableTypes[random.nextInt( availableTypes.length )];
        if ( problemType == 0 ) {
            return new SymbolToSchematicProblem( model, atomValue ) ;
        }
        else if ( problemType == 1 ) {
            return new SchematicToSymbolProblem( model, atomValue ) ;
        }
        else if ( problemType == 2 ) {
            return new SymbolToCountsProblem( model, atomValue ) ;
        }
        else if ( problemType == 3 ) {
            return new CountsToSymbolProblem( model, atomValue ) ;
        }
        else if ( problemType == 4 ) {
            return new SchematicToElementProblem( model, atomValue ) ;
        }
        else if ( problemType == 5 ) {
            return new CountsToElementProblem( model, atomValue ) ;
        }
        else throw new RuntimeException( "No problem found");
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
