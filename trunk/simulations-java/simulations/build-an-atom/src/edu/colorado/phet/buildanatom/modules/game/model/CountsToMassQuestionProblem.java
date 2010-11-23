package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CountsToChargeQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.CountsToMassQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author John Blanco
 */
public class CountsToMassQuestionProblem extends ToElementProblem{
    public CountsToMassQuestionProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super(model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CountsToMassQuestionView( model, gameCanvas, this );
    }
}
