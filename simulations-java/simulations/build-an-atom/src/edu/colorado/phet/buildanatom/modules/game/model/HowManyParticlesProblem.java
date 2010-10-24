package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.HowManyParticlesProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class HowManyParticlesProblem extends Problem {
    public HowManyParticlesProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new HowManyParticlesProblemView( model, gameCanvas, this );
    }
}
