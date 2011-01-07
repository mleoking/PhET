// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Color;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.util.Function0;

/**
 * View for the problem that presents a schematic view of an atom and asks
 * the user about the proton count.
 *
 * TODO: This problem type may be too simple, and as such we are considering
 * getting rid of it.  If it is unused by the time we publish this sim with
 * the game, we should get rid of this class.
 *
 * @author John Blanco
 */
public class SchematicToProtonCountQuestionView extends SchematicToQuestionView {

    /**
     * Constructor.
     */
    public SchematicToProtonCountQuestionView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem, BuildAnAtomStrings.GAME_ANSWER_THE_PROTON_COUNT_QUESTION, 0, 100, new DecimalFormat( "0" ) );

        // Display the value in red.
        getQuestion().setValueColorFunction( new Function0.Constant<Color>( Color.RED ) );
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        getGuessProperty().setValue( answer.getNumProtons() );
        getQuestion().setEditable( false );
    }

    @Override
    protected AtomValue getGuess() {
        // For the particular case of this problem type, the guess is a little
        // tricky, because this is supposed to return the configuration of the
        // atom that was guessed, but the user has only input a proton count
        // and nothing else.  So basically, if the mass value is correct, we
        // return the matching atom, and if not, we return a null atom.
        AtomValue answer = null;
        if ( getProblem().getAnswer().getNumProtons() == getGuessProperty().getValue() ){
            answer = getProblem().getAnswer();
        }
        else{
            answer = new AtomValue( 0, 0, 0 );
        }
        return answer;
    }
}
