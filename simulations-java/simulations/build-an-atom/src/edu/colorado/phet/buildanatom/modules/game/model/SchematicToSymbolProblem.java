package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.SchematicToSymbolView;
import edu.colorado.phet.buildanatom.modules.game.view.ToSymbolProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class SchematicToSymbolProblem extends ToSymbolProblem {
    public SchematicToSymbolProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new SchematicToSymbolView( model, gameCanvas, this );
    }
}
