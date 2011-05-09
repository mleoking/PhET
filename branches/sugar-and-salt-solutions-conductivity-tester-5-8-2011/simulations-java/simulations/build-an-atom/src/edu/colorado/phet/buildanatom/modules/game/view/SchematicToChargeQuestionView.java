// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.util.SignedIntegerFormat;

/**
 * View for the problem that presents a schematic view of an atom and asks
 * the user about the atom's overall charge.
 *
 * @author John Blanco
 */
public class SchematicToChargeQuestionView extends SchematicToQuestionView {

    /**
     * Constructor.
     */
    public SchematicToChargeQuestionView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem, BuildAnAtomStrings.GAME_ANSWER_THE_CHARGE_QUESTION, -50, 50, new SignedIntegerFormat() );

        // Cause positive charge to be colored red, negative to be blue.
        getQuestion().setValueColorFunction( new ChargeColorFunction( getGuessProperty() ) );
    }

    @Override
    protected void displayAnswer( ImmutableAtom answer ) {
        getGuessProperty().setValue( answer.getCharge() );
        getQuestion().setEditable( false );
    }

    @Override
    protected ImmutableAtom getGuess() {
        // For the particular case of this problem type, the guess is a little
        // tricky, because this is supposed to return the configuration of the
        // atom that was guessed, but the user has only input a charge value and
        // nothing else.  So basically, if the charge value is correct, we
        // return the matching atom, and if not, we return a null atom.
        ImmutableAtom answer = null;
        if ( getProblem().getAnswer().getCharge() == getGuessProperty().getValue() ){
            answer = getProblem().getAnswer();
        }
        else{
            answer = new ImmutableAtom( 0, 0, 0 );
        }
        return answer;
    }
}
