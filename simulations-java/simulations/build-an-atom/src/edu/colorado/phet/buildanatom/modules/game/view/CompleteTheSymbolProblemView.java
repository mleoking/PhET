package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.CompleteTheSymbolProblem;
import edu.colorado.phet.buildanatom.view.InteractiveSymbolNode;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
* @author Sam Reid
* @author John Blanco
*/
public class CompleteTheSymbolProblemView extends ProblemView {

    private final PText description = new PText( "Complete the symbol:" ) {{//todo i18n
        setFont( new PhetFont( 20, true ) );
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
    }};
    private final AtomConfigText atomConfigurationText;
    private final InteractiveSymbolNode interactiveSymbolNode;

    /**
     * Constructor.
     */
    public CompleteTheSymbolProblemView( BuildAnAtomGameModel model, GameCanvas canvas, CompleteTheSymbolProblem problem) {
        super( model, canvas, problem);
        atomConfigurationText = new AtomConfigText( problem.getAtom().getNumProtons(),
                problem.getAtom().getNumNeutrons(), problem.getAtom().getNumElectrons() );
        atomConfigurationText.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width /4 - atomConfigurationText.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - atomConfigurationText.getFullBounds().getHeight() / 2 );
        interactiveSymbolNode = new InteractiveSymbolNode( problem.getGuessedProtonsProperty(),
                problem.getGuessedNeutronsProperty(), problem.getGuessedElectronsProperty(), true);
        interactiveSymbolNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width * 0.75 - interactiveSymbolNode.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - interactiveSymbolNode.getFullBounds().getHeight() / 2 );
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( atomConfigurationText );
        addChild( interactiveSymbolNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( atomConfigurationText );
        removeChild( interactiveSymbolNode );
    }
}
