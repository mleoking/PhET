/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.buildanatom.view.SignedIntegerFormat;

/**
 * View for the problem that presents a schematic view of an atom and asks
 * the user about the atomic mass.
 *
 * @author John Blanco
 */
public class SchematicToChargeQuestionView extends SchematicToQuestionView {

    /**
     * Constructor.
     */
    public SchematicToChargeQuestionView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        // i18n
        super( model, gameCanvas, problem, "<html>What is the<br>total charge?", -50, 50, new SignedIntegerFormat() );
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        getAnswerProperty().setValue( answer.getCharge() );
        getQuestion().setEditable( false );
    }

    @Override
    protected AtomValue getGuess() {
        // For the particular case of this problem type, the guess is a little
        // tricky, because this is supposed to return the configuration of the
        // atom that was guessed, but the user has only input a charge value and
        // nothing else.  So basically, if the charge value is correct, we
        // return the matching atom, and if not, we return a null atom.
        AtomValue answer = null;
        if ( getProblem().getAnswer().getCharge() == getAnswerProperty().getValue() ){
            answer = getProblem().getAnswer();
        }
        else{
            answer = new AtomValue( 0, 0, 0 );
        }
        return answer;
    }
}
