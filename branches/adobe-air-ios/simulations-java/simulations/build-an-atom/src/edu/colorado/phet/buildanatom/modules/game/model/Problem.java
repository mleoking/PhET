// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;

/**
 * Represents one of the Problems in the game, formerly called Challenge.
 * The problems are generally ones where the user needs to venture a guess
 * about an atom's configuration.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public abstract class Problem extends State {

    private final ImmutableAtom atom;
    private int numGuesses = 0;
    private int score = 0;
    private boolean solvedCorrectly = false;

    public Problem( BuildAnAtomGameModel model, ImmutableAtom atom ) {
        super( model );
        this.atom = atom;
    }

    public boolean isGuessCorrect( ImmutableAtom guess ) {
        return atom.equals( guess );
    }

    public void processGuess( ImmutableAtom guess ) {
        numGuesses++;
        if ( isGuessCorrect( guess ) ) {
            solvedCorrectly = true;
            if ( numGuesses == 1 ) {
                score = 2;
            }
            else if ( numGuesses == 2 ) {
                score = 1;
            }
        }
    }

    public int getNumGuesses() {
        return numGuesses;
    }

    public boolean isSolvedCorrectly() {
        return solvedCorrectly;
    }

    public Integer getScore() {
        return score;
    }

    public ImmutableAtom getAnswer() {
        return atom;
    }
}
