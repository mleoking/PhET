package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.ToSymbolProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Base class for all problems where the user is adjusting an interactive
 * symbol representation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class ToSymbolProblem extends Problem {
    public ToSymbolProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }
}