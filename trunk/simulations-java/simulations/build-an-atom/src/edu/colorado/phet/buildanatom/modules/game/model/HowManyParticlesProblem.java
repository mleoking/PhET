package edu.colorado.phet.buildanatom.modules.game.model;

/**
 * @author Sam Reid
 */
public class HowManyParticlesProblem extends Problem {
    public HowManyParticlesProblem( GameModel model, int level, boolean timerOn, boolean soundOn, ProblemSet problemSet ) {
        super( model, problemSet, new AtomValue( 3, 4, 3 ) );
    }
}
