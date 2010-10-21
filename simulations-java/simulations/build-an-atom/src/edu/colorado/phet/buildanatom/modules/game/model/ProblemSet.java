package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;

/**
 * Represents an ordered list of Problems corresponding to a particular difficulty level
 *
 * @author Sam Reid
 */
public class ProblemSet {
    //keep track by type
    private ArrayList<CompleteTheModelProblem> completeTheModelProblems = new ArrayList<CompleteTheModelProblem>();
    private ArrayList<CompleteTheSymbolProblem> completeTheSymbolProblems = new ArrayList<CompleteTheSymbolProblem>();
    private ArrayList<HowManyParticlesProblem> howManyParticlesProblems = new ArrayList<HowManyParticlesProblem>();
    //keeps track by ordering
    private ArrayList<Problem> allProblems = new ArrayList<Problem>();

    public ProblemSet( GameModel model, int level, boolean timerOn, boolean soundOn ) {
    }

    public void addProblem( HowManyParticlesProblem howManyParticlesProblem ) {
        howManyParticlesProblems.add( howManyParticlesProblem );
        allProblems.add( howManyParticlesProblem );
    }

    public void addProblem( CompleteTheSymbolProblem completeTheSymbolProblem ) {
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

    public CompleteTheSymbolProblem getCompleteTheSymbolProblem( int i ) {
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

    public boolean isLastProblem( Problem problem ) {
        return getProblemIndex( problem ) == getTotalNumProblems() - 1;
    }

    public Problem getNextProblem( Problem problem ) {
        return getProblem( getProblemIndex( problem ) + 1 );
    }
}
