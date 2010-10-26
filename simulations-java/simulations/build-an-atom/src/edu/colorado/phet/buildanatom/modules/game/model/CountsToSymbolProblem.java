package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CountsToSymbolView;
import edu.colorado.phet.buildanatom.modules.game.view.ToSymbolProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class CountsToSymbolProblem extends ToSymbolProblem {
    public CountsToSymbolProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CountsToSymbolView( model, gameCanvas,this );
    }
}
