// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToChargeQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Problem that presents a schematic (a.k.a. a "Bohr model") and asks the user
 * a question about the charge.
 *
 * @author John Blanco
 */
public class SchematicToChargeQuestionProblem extends Problem {

    /**
     * Constructor.
     */
    public SchematicToChargeQuestionProblem( BuildAnAtomGameModel model, ImmutableAtom atom ) {
        super( model, atom );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SchematicToChargeQuestionView( model, gameCanvas, this );
    }
}
