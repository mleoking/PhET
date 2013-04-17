// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToElementView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Game problem where the user is shown the schematic view of an atom and is asked to select the element from the periodic table.
 *
 * @author Sam Reid
 */
public class SchematicToElementProblem extends ToElementProblem {
    public SchematicToElementProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SchematicToElementView( model, gameCanvas, this );
    }
}
