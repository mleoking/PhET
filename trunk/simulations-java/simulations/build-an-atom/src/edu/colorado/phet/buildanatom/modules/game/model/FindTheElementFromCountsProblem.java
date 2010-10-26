package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.FindTheElementFromCountsProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.FindTheElementFromModelProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class FindTheElementFromCountsProblem extends FindTheElementProblem{
    public FindTheElementFromCountsProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super(model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new FindTheElementFromCountsProblemView( model, gameCanvas, this );
    }
}
