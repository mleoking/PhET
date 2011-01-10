// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToProtonCountQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Problem that presents a schematic (a.k.a. a "Bohr model") and asks the user
 * a question about the proton count.
 *
 * TODO: This problem type may be too simple, and as such we are considering
 * getting rid of it.  If it is unused by the time we publish this sim with
 * the game, we should get rid of this class.
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
    public StateView createView( BuildAnAtomGameCanvas gameCanvas ) {
        return new SchematicToProtonCountQuestionView( model, gameCanvas, this );
    }
}
