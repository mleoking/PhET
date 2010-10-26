package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SymbolToSchematicProblem;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
* @author Sam Reid
*/
public class SymbolToSchematicView extends ProblemView {
    private final PText description = new PText( "Complete the model:" ) {{//todo i18n
        setFont( new PhetFont( 20, true ) );
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
    }};
    private final SymbolIndicatorNode symbolIndicatorNode;
    private final InteractiveSchematicAtomNode interactiveSchematicAtomNode;

    public SymbolToSchematicView( BuildAnAtomGameModel model, GameCanvas canvas, SymbolToSchematicProblem problem) {
        super( model, canvas, problem);
        symbolIndicatorNode = new SymbolIndicatorNode( problem.getAnswer().toAtom() );
        symbolIndicatorNode.scale( 2 );
        symbolIndicatorNode.setOffset( 100, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );

        interactiveSchematicAtomNode=new InteractiveSchematicAtomNode();
    }

    @Override
    protected AtomValue getGuess() {
        return interactiveSchematicAtomNode.getGuess();
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        interactiveSchematicAtomNode.displayAnswer(answer);
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        //Disable the user from changing the answer
        interactiveSchematicAtomNode.setPickable( guessEditable );
        interactiveSchematicAtomNode.setChildrenPickable( guessEditable );
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( symbolIndicatorNode );
        addChild( interactiveSchematicAtomNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( symbolIndicatorNode );
        removeChild( interactiveSchematicAtomNode );
    }
}
