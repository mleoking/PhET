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
    private final ArrayList<CompleteTheModelProblem> completeTheModelProblems = new ArrayList<CompleteTheModelProblem>();
    private final ArrayList<CompleteTheSymbolFromModelProblem> completeTheSymbolProblems = new ArrayList<CompleteTheSymbolFromModelProblem>();
    private final ArrayList<HowManyParticlesProblem> howManyParticlesProblems = new ArrayList<HowManyParticlesProblem>();
    private final ArrayList<CompleteTheSymbolFromCountsProblem> completeTheSymbolFromCountsProblems = new ArrayList<CompleteTheSymbolFromCountsProblem>();
    private final ArrayList<FindTheElementFromModelProblem> findTheElementFromModelProblems = new ArrayList<FindTheElementFromModelProblem>();
    private final ArrayList<FindTheElementFromCountsProblem> findTheElementFromCountsProblems = new ArrayList<FindTheElementFromCountsProblem>();
    //keeps track by ordering
    private final ArrayList<Problem> allProblems = new ArrayList<Problem>();
    private int currentProblemIndex = 0;
    public static final Random random = new Random();

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
            final int problemType = 1; // TODO: Temporary to get one type of problem working.
            if ( problemType == 0 ) {
                addProblem( new CompleteTheModelProblem( model, atomValue ) );
            }
            else if ( problemType == 1 ) {
                addProblem( new CompleteTheSymbolFromModelProblem( model, atomValue ) );
            }
            else if ( problemType == 2 ) {
                addProblem( new HowManyParticlesProblem( model, atomValue ) );
            }
            else if ( problemType == 3 ) {
                addProblem( new CompleteTheSymbolFromCountsProblem( model, atomValue ) );
            }
            else if ( problemType == 4 ) {
                addProblem( new FindTheElementFromModelProblem( model, atomValue ) );
            }
            else if ( problemType == 5 ) {
                addProblem( new FindTheElementFromCountsProblem( model, atomValue ) );
            }
        }
    }
    public void addProblem( FindTheElementFromModelProblem problem) {
        findTheElementFromModelProblems.add( problem );
        allProblems.add( problem );
    }
    
    public void addProblem( FindTheElementFromCountsProblem problem) {
        findTheElementFromCountsProblems.add( problem );
        allProblems.add( problem );
    }

    public void addProblem( CompleteTheSymbolFromCountsProblem problem) {
        completeTheSymbolFromCountsProblems.add( problem );
        allProblems.add( problem );
    }
    public void addProblem( HowManyParticlesProblem howManyParticlesProblem ) {
        howManyParticlesProblems.add( howManyParticlesProblem );
        allProblems.add( howManyParticlesProblem );
    }

    public void addProblem( CompleteTheSymbolFromModelProblem completeTheSymbolProblem ) {
        completeTheSymbolProblems.add( completeTheSymbolProblem );
        allProblems.add( completeTheSymbolProblem );
    }

    public void addProblem( CompleteTheModelProblem completeTheModelProblem ) {
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

    public CompleteTheModelProblem getCompleteTheModelProblem( int i ) {
        return completeTheModelProblems.get( i );
    }

    public CompleteTheSymbolFromModelProblem getCompleteTheSymbolProblem( int i ) {
        return completeTheSymbolProblems.get( i );
    }

    public HowManyParticlesProblem getHowManyParticlesProblem( int i ) {
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
