package edu.colorado.phet.buildanatom.modules.game.model;

/**
 * @author Sam Reid
 */
public class CompleteTheModelProblem extends Problem {
    public CompleteTheModelProblem( GameModel model, int level, boolean timerOn, boolean soundOn, ProblemSet problemSet ) {
        super( model, problemSet, new AtomValue( 1, 0, 1 ) );
    }
}
