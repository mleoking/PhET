package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.CompleteTheSymbolFromModelProblem;

/**
 * @author Sam Reid
 * @author John Blanco
 */
public class CompleteTheSymbolFromModelProblemView extends CompleteTheSymbolProblemView {
    private GameAtomModelNode gameAtomModelNode;

    /**
     * Constructor.
     */
    public CompleteTheSymbolFromModelProblemView( BuildAnAtomGameModel model, GameCanvas canvas, CompleteTheSymbolFromModelProblem problem ) {
        super( model, canvas, problem );
        gameAtomModelNode = new GameAtomModelNode( problem.getAnswer() );
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
