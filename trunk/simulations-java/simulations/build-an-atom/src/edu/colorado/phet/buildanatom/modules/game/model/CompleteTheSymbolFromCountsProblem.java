package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CompleteTheSymbolFromCountsProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.CompleteTheSymbolProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class CompleteTheSymbolFromCountsProblem extends CompleteTheSymbolProblem {
    public CompleteTheSymbolFromCountsProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CompleteTheSymbolFromCountsProblemView( model, gameCanvas,this );
    }
}
