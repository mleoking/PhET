// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;

/**
 * View for the problem that presents a list of an atom's constituent
 * particles and asks the user about the atomic mass.
 *
 * @author John Blanco
 */
public class CountsToMassQuestionView extends CountsToQuestionView {

    /**
     * Constructor.
     */
    public CountsToMassQuestionView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem, BuildAnAtomStrings.GAME_ANSWER_THE_MASS_QUESTION, 0, 100, new DecimalFormat( "0" ) );
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        getGuessProperty().setValue( answer.getMassNumber() );
        getQuestion().setEditable( false );
    }

    @Override
    protected AtomValue getGuess() {
        // For the particular case of this problem type, the guess is a little
        // tricky, because this is supposed to return the configuration of the
        // atom that was guessed, but the user has only input a mass value and
        // nothing else.  So basically, if the mass value is correct, we
        // return the matching atom, and if not, we return a null atom.
        AtomValue answer = null;
        if ( getProblem().getAnswer().getMassNumber() == getGuessProperty().getValue() ){
            answer = getProblem().getAnswer();
        }
        else{
            answer = new AtomValue( 0, 0, 0 );
        }
        return answer;
    }
}
