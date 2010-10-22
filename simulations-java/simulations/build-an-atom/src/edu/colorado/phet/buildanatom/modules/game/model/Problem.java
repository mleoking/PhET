package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.Atom;

/**
 * Represents one of the Problems in the game, formerly called Challenge.
 */
public class Problem extends State {
    private final Atom atom;
    private final AtomValue guessedAtom = new AtomValue(0, 0, 0);

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

    public void setGuessedProtons(int numProtons) {
        guessedAtom.setNumProtons( numProtons );
    }

    public void setGuessedNeutrons(int numNeutrons) {
        guessedAtom.setNumNeutrons( numNeutrons );
    }

    public void setGuessedElectrons(int numElectrons) {
        guessedAtom.setNumElectrons( numElectrons );
    }
}
