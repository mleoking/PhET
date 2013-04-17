// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.CountsToElementView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Game problem where the user is shown the neutron/proton/electron counts and is asked to predict the element.
 *
 * @author Sam Reid
 */
public class CountsToElementProblem extends ToElementProblem {
    public CountsToElementProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new CountsToElementView( model, gameCanvas, this );
    }
}
