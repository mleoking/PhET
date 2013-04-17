// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;
import edu.colorado.phet.buildanatom.modules.game.view.SymbolToSchematicView;

/**
 * Game problem where the user is shown the symbol view of an atom and is asked to create the corresponding schematic
 * representation.
 *
 * @author Sam Reid
 */
public class SymbolToSchematicProblem extends Problem {
    public SymbolToSchematicProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SymbolToSchematicView( model, gameCanvas, this );
    }
}
