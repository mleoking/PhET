package edu.colorado.phet.buildanatom.modules.game.model;


/**
 * Represents one of the Problems in the game, formerly called Challenge.
 */
public abstract class Problem extends State {
    private final AtomValue atom;
    private int numGuesses = 0;
    private int score = 0;
    private boolean solvedCorrectly =false;

    public Problem( BuildAnAtomGameModel model, AtomValue atom ) {
        super( model );
        this.atom = atom;
    }

    public boolean isGuessCorrect( AtomValue guess ) {
        return atom.equals( guess );
    }

//    //TODO: use these or Property<Integer>?
//    public void setGuessedProtons(int numProtons) {
//        guessedAtom.setNumProtons( numProtons );
//    }
//
//    public void setGuessedNeutrons(int numNeutrons) {
//        guessedAtom.setNumNeutrons( numNeutrons );
//    }
//
//    public void setGuessedElectrons(int numElectrons) {
//        guessedAtom.setNumElectrons( numElectrons );
//    }

    public void processGuess(AtomValue guess) {
        numGuesses++;
        if ( isGuessCorrect(guess ) ) {
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

    //Sets the state of the guess to be the correct value.
    //Observers that are depicting the guess will therefore display the correct value
//    public void showAnswer() {
//        guessedAtom.setState(atom);
//    }

    public AtomValue getAnswer(){
        return atom;
    }
}
