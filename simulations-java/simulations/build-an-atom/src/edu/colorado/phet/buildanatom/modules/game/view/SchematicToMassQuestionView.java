/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * View for the problem that presents a schematic view of an atom and asks
 * the user about the atomic mass.
 *
 * @author John Blanco
 */
public class SchematicToMassQuestionView extends ProblemView {

    private final SchematicAtomNode gameAtomModelNode;
    private final EntryPanel question;
    private final Property<Integer> massNumberProperty;

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

        massNumberProperty = new Property<Integer>( 0 );
        massNumberProperty.addObserver( new SimpleObserver() {
            public void update() {
                // Any change to the mass property indicates that the user
                // has entered something, so therefore it is time to enable
                // the "Check Guess" button.
                enableCheckButton();
            }
        }, false );

        question = new EntryPanel("What is the mass number?", massNumberProperty);
        question.setSpinnerX( question.getLabelWidth() + 5 );
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
        massNumberProperty.setValue( answer.getMassNumber() );
        question.setEditable( false );
    }

    @Override
    protected AtomValue getGuess() {
        // For the particular case of this problem type, the guess is a little
        // tricky, because this is supposed to return the configuration of the
        // atom that was guessed, but the user has only input a mass value and
        // nothing else.  So basically, if the mass value is correct, we
        // return the matching atom, and if not, we return a null atom.
        AtomValue answer = null;
        if ( getProblem().getAnswer().getMassNumber() == massNumberProperty.getValue() ){
            answer = getProblem().getAnswer();
        }
        else{
            answer = new AtomValue( 0, 0, 0 );
        }
        return answer;
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        // TODO: Need to implement.
        System.err.println( getClass().getName() + " - Error: Need to implement this!" );
    }
}
