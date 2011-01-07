// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SymbolToCountsView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

//TODO
/**
 * @author Sam Reid
 */
public class SymbolToCountsProblem extends Problem {
    public SymbolToCountsProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SymbolToCountsView( model, gameCanvas, this );
    }
}
