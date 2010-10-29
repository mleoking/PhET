package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SymbolToSchematicProblem;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
* @author Sam Reid
*/
public class SymbolToSchematicView extends ProblemView {
    private final ProblemDescriptionNode description = new ProblemDescriptionNode( "Complete the model:" );//todo i18n
    private final SymbolIndicatorNode symbolIndicatorNode;
    private final InteractiveSchematicAtomNode interactiveSchematicAtomNode;

    public SymbolToSchematicView( BuildAnAtomGameModel model, GameCanvas canvas, SymbolToSchematicProblem problem) {
        super( model, canvas, problem);
        symbolIndicatorNode = new SymbolIndicatorNode( problem.getAnswer().toAtom(getClock() ), true );
        symbolIndicatorNode.scale( 2 );
        symbolIndicatorNode.setOffset( 100, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );

        interactiveSchematicAtomNode=new InteractiveSchematicAtomNode(new BuildAnAtomModel( getClock() ){{reset();}}, new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.70 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.35 ) ),
                1.5,
                true ), new BooleanProperty( true) );
        description.centerAbove( interactiveSchematicAtomNode );
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
