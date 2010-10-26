package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents an ordered list of Problems corresponding to a particular difficulty level
 *
 * @author Sam Reid
 */
public class ProblemSet {
    //keep track by type
    private final ArrayList<SymbolToSchematicProblem> completeTheModelProblems = new ArrayList<SymbolToSchematicProblem>();
    private final ArrayList<SchematicToSymbolProblem> completeTheSymbolProblems = new ArrayList<SchematicToSymbolProblem>();
    private final ArrayList<SymbolToCountsProblem> howManyParticlesProblems = new ArrayList<SymbolToCountsProblem>();
    private final ArrayList<CountsToSymbolProblem> completeTheSymbolFromCountsProblems = new ArrayList<CountsToSymbolProblem>();
    private final ArrayList<SchematicToElementProblem> findTheElementFromModelProblems = new ArrayList<SchematicToElementProblem>();
    private final ArrayList<CountsToElementProblem> findTheElementFromCountsProblems = new ArrayList<CountsToElementProblem>();
    //keeps track by ordering
    private final ArrayList<Problem> allProblems = new ArrayList<Problem>();
    private int currentProblemIndex = 0;
    public static final Random random = new Random();

    public static boolean iterateThrough = true;//for debugging

    public ProblemSet( BuildAnAtomGameModel model, int numProblems ) {
        // TODO: We need to make sure that the same problem is not generated
        // twice.
        final ArrayList<AtomValue> levelPool = model.getLevelPool();
        assert levelPool.size()>=numProblems;//otherwise problems would be duplicated in a game
        ArrayList<AtomValue> remainingProblems = new ArrayList<AtomValue>( levelPool );//don't re-use the same problem twice in the same game
        for ( int i = 0; i < numProblems; i++ ) {
            final int index = random.nextInt( remainingProblems.size() );
            AtomValue atomValue = remainingProblems.get( index );
            remainingProblems.remove( index );
//            final int problemType = random.nextInt( 3 );
            final int problemType = iterateThrough?i:3; // TODO: Temporary to get one type of problem working.
            if ( problemType == 0 ) {
                addProblem( new SymbolToSchematicProblem( model, atomValue ) );
            }
            else if ( problemType == 1 ) {
                addProblem( new SchematicToSymbolProblem( model, atomValue ) );
            }
            else if ( problemType == 2 ) {
                addProblem( new SymbolToCountsProblem( model, atomValue ) );
            }
            else if ( problemType == 3 ) {
                addProblem( new CountsToSymbolProblem( model, atomValue ) );
            }
            else if ( problemType == 4 ) {
                addProblem( new SchematicToElementProblem( model, atomValue ) );
            }
            else if ( problemType == 5 ) {
                addProblem( new CountsToElementProblem( model, atomValue ) );
            }
        }
    }
    public void addProblem( SchematicToElementProblem problem) {
        findTheElementFromModelProblems.add( problem );
        allProblems.add( problem );
    }

    public void addProblem( CountsToElementProblem problem) {
        findTheElementFromCountsProblems.add( problem );
        allProblems.add( problem );
    }

    public void addProblem( CountsToSymbolProblem problem) {
        completeTheSymbolFromCountsProblems.add( problem );
        allProblems.add( problem );
    }
    public void addProblem( SymbolToCountsProblem howManyParticlesProblem ) {
        howManyParticlesProblems.add( howManyParticlesProblem );
        allProblems.add( howManyParticlesProblem );
    }

    public void addProblem( SchematicToSymbolProblem completeTheSymbolProblem ) {
        completeTheSymbolProblems.add( completeTheSymbolProblem );
        allProblems.add( completeTheSymbolProblem );
    }

    public void addProblem( SymbolToSchematicProblem completeTheModelProblem ) {
        completeTheModelProblems.add( completeTheModelProblem );
        allProblems.add( completeTheModelProblem );
    }

    public int getNumCompleteTheModelProblems() {
        return completeTheModelProblems.size();
    }

    public int getNumCompleteTheSymbolProblems() {
        return completeTheSymbolProblems.size();
    }

    public int getNumHowManyParticlesProblems() {
        return howManyParticlesProblems.size();
    }

    public SymbolToSchematicProblem getCompleteTheModelProblem( int i ) {
        return completeTheModelProblems.get( i );
    }

    public SchematicToSymbolProblem getCompleteTheSymbolProblem( int i ) {
        return completeTheSymbolProblems.get( i );
    }

    public SymbolToCountsProblem getHowManyParticlesProblem( int i ) {
        return howManyParticlesProblems.get( i );
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
