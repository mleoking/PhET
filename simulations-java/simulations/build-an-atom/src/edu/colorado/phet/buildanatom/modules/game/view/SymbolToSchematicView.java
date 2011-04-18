// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SymbolToSchematicProblem;
import edu.colorado.phet.buildanatom.view.OrbitalView;
import edu.colorado.phet.buildanatom.view.OrbitalViewProperty;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Piccolo view for the game problem in which the user is shown the symbol view of an atom and is asked to create the
 * corresponding schematic representation.
 *
 * @author Sam Reid
 */
public class SymbolToSchematicView extends ProblemView {
    private final ProblemDescriptionNode description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_COMPLETE_THE_MODEL );
    private final SymbolIndicatorNode symbolIndicatorNode;
    private final InteractiveSchematicAtomNode interactiveSchematicAtomNode;

    public SymbolToSchematicView( BuildAnAtomGameModel model, BuildAnAtomGameCanvas canvas, SymbolToSchematicProblem problem) {
        super( model, canvas, problem);
        symbolIndicatorNode = new SymbolIndicatorNode( problem.getAnswer().toAtom(getClock() ), true );
        symbolIndicatorNode.scale( 2 );
        symbolIndicatorNode.setOffset( 100, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );

        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock() ) {{reset();}};
        interactiveSchematicAtomNode=new InteractiveSchematicAtomNode( buildAnAtomModel, ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.70 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.35 ) ),
                1.5 ),
                new OrbitalViewProperty( OrbitalView.PARTICLES ) );
        buildAnAtomModel.getAtom().addAtomListener( new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                if ( buildAnAtomModel.getAtom().getMassNumber() > 0 ) {
                    enableCheckButton();
                }
            }
        } );
        description.centerAbove( interactiveSchematicAtomNode );
    }

    @Override
    protected ImmutableAtom getGuess() {
        return interactiveSchematicAtomNode.getAtomValue();
    }

    @Override
    protected void displayAnswer( ImmutableAtom answer ) {
        interactiveSchematicAtomNode.setAtomValue(answer);
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
