// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.CountsToMassQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

//DOC
/**
 * @author John Blanco
 */
public class CountsToMassQuestionProblem extends ToElementProblem{
    public CountsToMassQuestionProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super(model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new CountsToMassQuestionView( model, gameCanvas, this );
    }
}
