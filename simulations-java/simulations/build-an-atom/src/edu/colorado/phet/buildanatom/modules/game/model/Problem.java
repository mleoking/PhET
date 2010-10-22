package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.Atom;

/**
 * Represents one of the Problems in the game, formerly called Challenge.
 */
public class Problem extends State {
    private final Atom atom;

    public Problem( BuildAnAtomGameModel model, ProblemSet problemSet, Atom atom ) {
        super( model );
        this.atom = atom;
    }

    public Atom getAtom() {
        return atom;
    }

    public boolean checkGuess() {
        return true;
  }
}
