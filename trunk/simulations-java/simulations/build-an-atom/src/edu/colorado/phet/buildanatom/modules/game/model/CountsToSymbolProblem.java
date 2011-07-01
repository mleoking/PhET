// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.CountsToSymbolView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Game problem where the user is shown the neutron/proton/electron counts and is asked to fill in the symbol.
 *
 * @author Sam Reid
 */
public class CountsToSymbolProblem extends ToSymbolProblem {
    public CountsToSymbolProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue, boolean configurableProtonCount,
                                  boolean configurableMass, boolean configurableCharge ) {
        super( model, atomValue, configurableProtonCount, configurableMass, configurableCharge );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new CountsToSymbolView( model, gameCanvas, this );
    }
}
