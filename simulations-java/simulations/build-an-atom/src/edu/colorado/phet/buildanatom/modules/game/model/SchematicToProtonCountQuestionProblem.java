/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToProtonCountQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Problem that presents a schematic (a.k.a. a "Bohr model") and asks the user
 * a question about the proton count.
 *
 * @author John Blanco
 */
public class SchematicToProtonCountQuestionProblem extends Problem {

    /**
     * Constructor.
     */
    public SchematicToProtonCountQuestionProblem( BuildAnAtomGameModel model, AtomValue atom ) {
        super( model, atom );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new SchematicToProtonCountQuestionView( model, gameCanvas, this );
    }
}
