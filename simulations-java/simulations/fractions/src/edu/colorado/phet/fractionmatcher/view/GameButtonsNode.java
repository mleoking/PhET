// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import fj.F;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractionmatcher.model.MatchingGameState;
import edu.colorado.phet.fractionmatcher.view.Controller.CheckAnswer;
import edu.colorado.phet.fractionmatcher.view.Controller.Next;
import edu.colorado.phet.fractionmatcher.view.Controller.ShowAnswer;
import edu.colorado.phet.fractionmatcher.view.Controller.TryAgain;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionmatcher.model.Mode.*;
import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.*;

/**
 * Node that shows the game buttons like "try again", "show answer", etc.
 *
 * @author Sam Reid
 */
class GameButtonsNode extends PNode {

    public GameButtonsNode( MatchingGameState state, F<ButtonArgs, Button> buttonFactory, final Vector2D buttonLocation ) {

        //Only show buttons if user made a guess by putting something on both scales
        if ( state.getLeftScaleValue() > 0 && state.getRightScaleValue() > 0 ) {
            if ( state.getMode() == SHOWING_WHY_ANSWER_WRONG ) {
                if ( state.getChecks() < 2 ) {
                    addChild( buttonFactory.f( new ButtonArgs( tryAgainButton, Strings.TRY_AGAIN, PhetColorScheme.RED_COLORBLIND, buttonLocation, new TryAgain() ) ) );
                }
                else {
                    addChild( buttonFactory.f( new ButtonArgs( showAnswerButton, Strings.SHOW_ANSWER, PhetColorScheme.RED_COLORBLIND, buttonLocation, new ShowAnswer() ) ) );
                }
            }

            else if ( ( state.getChecks() == 0 && state.getMode() == USER_IS_MOVING_OBJECTS_TO_THE_SCALES ) ||
                      ( state.getChecks() == 1 && state.getMode() == USER_IS_MOVING_OBJECTS_TO_THE_SCALES && state.changedFromWrongAnswer() ) ) {
                addChild( buttonFactory.f( new ButtonArgs( Components.checkAnswerButton, Strings.CHECK_ANSWER, Color.orange, buttonLocation, new CheckAnswer() ) ) );
            }

            //If they match, show a "Keep" button. This allows the student to look at the right answer as long as they want before moving it to the scoreboard.
            if ( state.getMode() == USER_CHECKED_CORRECT_ANSWER ) {

                addChild( new VBox( new FaceNode( 120 ), new PhetPText( state.getChecks() == 1 ? "+2" : "+1", new PhetFont( 18, true ) ) ) {{
                    final Vector2D pt = buttonLocation.plus( 0, -150 );
                    centerFullBoundsOnPoint( pt.getX() - getFullBounds().getWidth() / 2, pt.getY() );
                }} );

                addChild( buttonFactory.f( new ButtonArgs( okButton, Strings.OK, Color.green, buttonLocation, new Next() ) ) );
            }

            if ( state.getMode() == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ) {
                addChild( buttonFactory.f( new ButtonArgs( okButton, Strings.OK, Color.green, buttonLocation, new Next() ) ) );
            }
        }
    }
}