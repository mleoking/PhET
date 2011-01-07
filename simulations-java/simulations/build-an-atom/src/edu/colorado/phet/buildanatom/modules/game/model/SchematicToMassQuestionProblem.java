/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToMassQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Problem that presents a schematic (a.k.a. a "Bohr model") and asks the user
 * a question about the mass number.
 *
 * @author John Blanco
 */
public class SchematicToMassQuestionProblem extends Problem {

    /**
     * Constructor.
     */
    public SchematicToMassQuestionProblem( BuildAnAtomGameModel model, AtomValue atom ) {
        super( model, atom );
    }

    @Override
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SchematicToMassQuestionView( model, gameCanvas, this );
    }
}
