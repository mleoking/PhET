/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * View for the problem that presents a schematic view of an atom and asks
 * the user about the atomic mass.
 *
 * @author John Blanco
 */
public class SchematicToMassQuestionView extends ProblemView {

    private final SchematicAtomNode gameAtomModelNode;
    private final PText question;

    /**
     * Constructor.
     */
    public SchematicToMassQuestionView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock(), problem.getAnswer(), true ) ;

        gameAtomModelNode = new SchematicAtomNode( buildAnAtomModel, ProblemView.SCHEMATIC_PROBLEM_MVT, new BooleanProperty( true ) ){{
            setPickable( false );
            setChildrenPickable( false );
        }};

        question = new PText("What is the mass number?");
        question.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width * 3 / 4 - question.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - question.getFullBounds().getHeight() / 2 );
    }

    @Override
    public void init() {
        super.init();
        addChild( gameAtomModelNode );
        addChild( question );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( gameAtomModelNode );
        removeChild( question );
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        // TODO Auto-generated method stub

    }

    @Override
    protected AtomValue getGuess() {
        return null;
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        // TODO Auto-generated method stub

    }
}
