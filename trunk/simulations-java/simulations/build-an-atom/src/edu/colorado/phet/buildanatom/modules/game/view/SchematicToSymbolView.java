package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SchematicToSymbolProblem;

/**
 * @author Sam Reid
 * @author John Blanco
 */
public class SchematicToSymbolView extends ToSymbolProblemView {
    private SchematicAtomNode gameAtomModelNode;

    /**
     * Constructor.
     */
    public SchematicToSymbolView( BuildAnAtomGameModel model, GameCanvas canvas, SchematicToSymbolProblem problem ) {
        super( model, canvas, problem );
        gameAtomModelNode = new SchematicAtomNode( problem.getAnswer(),getClock() );
    }

    @Override
    public void init() {
        super.init();
        addChild( gameAtomModelNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( gameAtomModelNode );
    }
}
