package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CountsToChargeQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author John Blanco
 */
public class CountsToChargeQuestionProblem extends ToElementProblem{
    public CountsToChargeQuestionProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super(model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CountsToChargeQuestionView( model, gameCanvas, this );
    }
}
