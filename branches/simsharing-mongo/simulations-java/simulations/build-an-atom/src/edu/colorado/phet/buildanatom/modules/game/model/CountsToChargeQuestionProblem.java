// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.CountsToChargeQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Game problem where the user is shown the neutron/proton/electron counts and is asked to predict the charge.
 *
 * @author John Blanco
 */
public class CountsToChargeQuestionProblem extends ToElementProblem {
    public CountsToChargeQuestionProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new CountsToChargeQuestionView( model, gameCanvas, this );
    }
}
