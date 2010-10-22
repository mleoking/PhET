package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.Atom;

/**
 * Represents one of the Problems in the game, formerly called Challenge.
 */
public class Problem extends State {
    private final AtomValue atom;
    private final AtomValue guessedAtom = new AtomValue(0, 0, 0);
    private int numGuesses = 0;
    private int points = 0;
    private boolean solvedCorrectly =false;

    public Problem( BuildAnAtomGameModel model, AtomValue atom ) {
        super( model );
        this.atom = atom;
    }

    public Atom getAtom() {
        return atom;
    }

    public boolean isGuessCorrect() {
        return atom.guessEquals(guessedAtom);
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

    public GuessResult processGuess() {
        numGuesses++;
        if ( isGuessCorrect() ) {
            if ( numGuesses == 1 ) {
                points = 2;
                solvedCorrectly = true;
            }
            else if ( numGuesses == 2 ) {
                points = 1;
                solvedCorrectly = true;
            }
        }
        return new GuessResult( isGuessCorrect(), points );
    }

    public int getNumGuesses() {
        return numGuesses;
    }

    public boolean isSolvedCorrectly() {
        return solvedCorrectly;
    }
}
