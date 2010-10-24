package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CompleteTheModelProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class CompleteTheModelProblem extends Problem {
    public CompleteTheModelProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CompleteTheModelProblemView( model, gameCanvas, this );
    }
}
