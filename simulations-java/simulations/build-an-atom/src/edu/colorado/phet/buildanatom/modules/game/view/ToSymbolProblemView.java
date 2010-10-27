package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.ToSymbolProblem;

/**
* @author Sam Reid
* @author John Blanco
*/
public class ToSymbolProblemView extends ProblemView {

    private final ProblemDescriptionNode description = new ProblemDescriptionNode( "Complete the symbol:" ); //todo i18n
    private final InteractiveSymbolNode interactiveSymbolNode;

    /**
     * Constructor.
     */
    public ToSymbolProblemView( BuildAnAtomGameModel model, GameCanvas canvas, ToSymbolProblem problem) {
        super( model, canvas, problem);

        interactiveSymbolNode = new InteractiveSymbolNode( true, true );
        interactiveSymbolNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width * 0.75 - interactiveSymbolNode.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - interactiveSymbolNode.getFullBounds().getHeight() / 2 );

        description.centerAbove( interactiveSymbolNode );
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( interactiveSymbolNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( interactiveSymbolNode );
    }

    @Override
    protected AtomValue getGuess() {
        return interactiveSymbolNode.getGuess();
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        interactiveSymbolNode.displayAnswer(answer);
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        interactiveSymbolNode.setPickable( guessEditable );
        interactiveSymbolNode.setChildrenPickable( guessEditable );
    }
}
