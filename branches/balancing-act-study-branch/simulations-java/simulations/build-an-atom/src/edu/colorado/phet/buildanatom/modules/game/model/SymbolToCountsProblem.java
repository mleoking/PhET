// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;
import edu.colorado.phet.buildanatom.modules.game.view.SymbolToCountsView;

/**
 * Game problem in which the user is shown the symbol view of an atom and is asked to fill in the proton/neutron/electron counts.
 *
 * @author Sam Reid
 */
public class SymbolToCountsProblem extends Problem {
    public SymbolToCountsProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SymbolToCountsView( model, gameCanvas, this );
    }
}
