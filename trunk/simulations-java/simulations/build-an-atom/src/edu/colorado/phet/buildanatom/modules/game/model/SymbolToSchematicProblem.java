package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.SymbolToSchematicView;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

//DOC
/**
 * @author Sam Reid
 */
public class SymbolToSchematicProblem extends Problem {
    public SymbolToSchematicProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SymbolToSchematicView( model, gameCanvas, this );
    }
}
