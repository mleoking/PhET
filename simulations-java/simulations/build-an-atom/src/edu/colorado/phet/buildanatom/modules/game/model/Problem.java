package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.Atom;

/**
 * Represents one of the Problems in the game, formerly called Challenge.
 */
public class Problem extends State {
    private final ProblemSet problemSet;
    private Atom atom;

    public Problem( BuildAnAtomGameModel model, ProblemSet problemSet, Atom atom ) {
        super( model );
        this.problemSet = problemSet;
        this.atom = atom;
    }

    public Atom getAtom() {
        return atom;
    }

    public void checkGuess() {
        if ( problemSet.isLastProblem( this ) ) {
            model.setState( model.getGameOverState() );
        }
        else {
            model.setState( problemSet.getNextProblem( this ) );
        }
    }
}
