package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.SchematicToElementView;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

//DOC
/**
 * @author Sam Reid
 */
public class SchematicToElementProblem extends ToElementProblem {
    public SchematicToElementProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SchematicToElementView( model, gameCanvas, this );
    }
}
