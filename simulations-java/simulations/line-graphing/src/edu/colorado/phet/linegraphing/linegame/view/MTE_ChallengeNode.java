// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_ChallengeNode extends ChallengeNode {

    public MTE_ChallengeNode( final LineGameModel model, final MTE_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );

        final double boxWidth = 0.4 * challengeSize.getWidth();

        // The equation for the user's guess.
        final PNode guessBoxNode = new EquationBoxNode( Strings.YOUR_EQUATION, challenge.guess.get().color, new PDimension( boxWidth, 0.3 * challengeSize.getHeight() ),
                                                        createGuessEquationNode( challenge.lineForm, challenge.manipulationMode, challenge.guess, challenge.graph,
                                                                                 LineGameConstants.INTERACTIVE_EQUATION_FONT, LineGameConstants.STATIC_EQUATION_FONT,
                                                                                 challenge.guess.get().color ) );

        // The equation for the correct answer.
        final PNode answerBoxNode = new EquationBoxNode( Strings.CORRECT_EQUATION, challenge.answer.color, new PDimension( boxWidth, 0.2 * challengeSize.getHeight() ),
                                                         createAnswerEquationNode( challenge.lineForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );

        // icons for indicating correct vs incorrect
        final PNode answerCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessIncorrectNode = new PImage( Images.X_MARK );

        final MTE_GraphNode graphNode = new MTE_GraphNode( challenge );

        // rendering order
        {
            subclassParent.addChild( graphNode );
            subclassParent.addChild( answerBoxNode );
            subclassParent.addChild( guessBoxNode );
            subclassParent.addChild( answerCorrectNode );
            subclassParent.addChild( guessCorrectNode );
            subclassParent.addChild( guessIncorrectNode );
        }

        // layout
        final int iconXMargin = 10;
        final int iconYMargin = 5;
        {
            // graphNode is positioned automatically based on mvt's origin offset.

            // guess equation in right half of challenge space
            guessBoxNode.setOffset( ( 0.75 * challengeSize.getWidth() ) - ( guessBoxNode.getFullBoundsReference().getWidth() / 2 ) + 10,
                                    graphNode.getFullBoundsReference().getMinY() + 50 );

            // answer below guess
            answerBoxNode.setOffset( guessBoxNode.getXOffset(), guessBoxNode.getFullBoundsReference().getMaxY() + 20 );

            // correct/incorrect icons are in upper-right corners of equation boxes
            answerCorrectNode.setOffset( answerBoxNode.getFullBoundsReference().getMaxX() - answerCorrectNode.getFullBoundsReference().getWidth() - iconXMargin,
                                         answerBoxNode.getFullBoundsReference().getMinY() + iconYMargin );
            guessCorrectNode.setOffset( guessBoxNode.getFullBoundsReference().getMaxX() - guessCorrectNode.getFullBoundsReference().getWidth() - iconXMargin,
                                        guessBoxNode.getFullBoundsReference().getMinY() + iconYMargin );
            guessIncorrectNode.setOffset( guessBoxNode.getFullBoundsReference().getMaxX() - guessIncorrectNode.getFullBoundsReference().getWidth() - iconXMargin,
                                          guessBoxNode.getFullBoundsReference().getMinY() + iconYMargin );

            // face centered below equation boxes
            faceNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                checkButton.getFullBoundsReference().getMaxY() - faceNode.getFullBoundsReference().getHeight() );
        }

        // To reduce brain damage during development, show the answer equation in translucent gray.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            PNode devAnswerNode = createAnswerEquationNode( challenge.lineForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, new Color( 0, 0, 0, 25 ) );
            devAnswerNode.setOffset( answerBoxNode.getFullBoundsReference().getMinX() + 30,
                                     answerBoxNode.getFullBoundsReference().getCenterY() - ( devAnswerNode.getFullBoundsReference().getHeight() / 2 ) );
            addChild( devAnswerNode );
            devAnswerNode.moveToBack();
        }

        // Update visibility of the correct/incorrect icons.
        final VoidFunction0 updateIcons = new VoidFunction0() {
            public void apply() {
                answerCorrectNode.setVisible( model.state.get() == PlayState.NEXT );
                guessCorrectNode.setVisible( answerCorrectNode.getVisible() && challenge.isCorrect() );
                guessIncorrectNode.setVisible( answerCorrectNode.getVisible() && !challenge.isCorrect() );
            }
        };

        // when the guess changes...
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                updateIcons.apply();
            }
        } );

        // state changes
        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {

                // states in which the equation is interactive
                guessBoxNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || ( state == PlayState.NEXT && !challenge.isCorrect() ) );
                guessBoxNode.setChildrenPickable( guessBoxNode.getPickable() );

                // Show all equations and lines at the end of the challenge.
                answerBoxNode.setVisible( state == PlayState.NEXT );
                graphNode.setGuessVisible( state == PlayState.NEXT );

                // visibility of correct/incorrect icons
                updateIcons.apply();

                // slope tool visible when user got it wrong
                graphNode.setSlopeToolVisible( state == PlayState.NEXT && !challenge.isCorrect() );
            }
        } );
    }

    // Creates the "answer" equation portion of the view.
    private EquationNode createAnswerEquationNode( LineForm lineForm, Line line, PhetFont font, Color color ) {
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            return new SlopeInterceptEquationNode( line, font, color );
        }
        else if ( lineForm == LineForm.POINT_SLOPE ) {
            return new PointSlopeEquationNode( line, font, color );
        }
        else {
            throw new IllegalArgumentException( "unsupported line form: " + lineForm );
        }
    }

    // Creates the "guess" equation portion of the view.
    private EquationNode createGuessEquationNode( LineForm lineForm, ManipulationMode manipulationMode,
                                                  Property<Line> line, Graph graph, PhetFont interactiveFont, PhetFont staticFont, Color staticColor ) {
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            boolean interactiveSlope = ( manipulationMode == ManipulationMode.SLOPE ) || ( manipulationMode == ManipulationMode.SLOPE_INTERCEPT );
            boolean interactiveIntercept = ( manipulationMode == ManipulationMode.INTERCEPT ) || ( manipulationMode == ManipulationMode.SLOPE_INTERCEPT );
            return new SlopeInterceptEquationNode( line,
                                                   new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                                   new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                                   new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                                   interactiveSlope, interactiveIntercept,
                                                   interactiveFont, staticFont, staticColor );
        }
        else if ( lineForm == LineForm.POINT_SLOPE ) {
            boolean interactivePoint = ( manipulationMode == ManipulationMode.POINT ) || ( manipulationMode == ManipulationMode.POINT_SLOPE );
            boolean interactiveSlope = ( manipulationMode == ManipulationMode.SLOPE ) || ( manipulationMode == ManipulationMode.POINT_SLOPE );
            return new PointSlopeEquationNode( line,
                                               new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                               new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                               new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                               new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                               interactivePoint, interactivePoint, interactiveSlope,
                                               interactiveFont, staticFont, staticColor );
        }
        else {
            throw new IllegalArgumentException( "unsupported line form: " + lineForm );
        }
    }
}
