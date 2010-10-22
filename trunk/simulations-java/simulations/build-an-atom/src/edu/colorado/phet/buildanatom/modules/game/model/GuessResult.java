package edu.colorado.phet.buildanatom.modules.game.model;

/**
 * @author Sam Reid
 */
public class GuessResult {
    private final boolean correct;
    private final int points;

    public GuessResult( boolean correct, int points ) {
        this.correct = correct;
        this.points = points;
    }

    public boolean isCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return "GuessResult{" +
               "correct=" + correct +
               ", points=" + points +
               '}';
    }
}
