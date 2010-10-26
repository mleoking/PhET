package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CompleteTheSymbolFromModelProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.CompleteTheSymbolProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class CompleteTheSymbolFromModelProblem extends CompleteTheSymbolProblem {
    public CompleteTheSymbolFromModelProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CompleteTheSymbolFromModelProblemView( model, gameCanvas, this );
    }
}
