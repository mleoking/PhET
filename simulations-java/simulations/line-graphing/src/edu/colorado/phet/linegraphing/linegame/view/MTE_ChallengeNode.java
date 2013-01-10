// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.EquationForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Make the Equation" (MTE) challenges.
 * User manipulates an equation on the right, graph is displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_ChallengeNode extends ChallengeNode {

    /**
     * Constructor
     * @parma challenge the challenge
     * @param model the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer the audio player, for providing audio feedback during game play
     */
    public MTE_ChallengeNode( final MTE_Challenge challenge, final LineGameModel model, PDimension challengeSize, final GameAudioPlayer audioPlayer ) {
        super( challenge, model, challengeSize, audioPlayer );

        final double boxWidth = 0.4 * challengeSize.getWidth();

        // Answer
        final EquationBoxNode answerBoxNode =
                new EquationBoxNode( Strings.CORRECT_EQUATION, challenge.answer.color, new PDimension( boxWidth, 0.2 * challengeSize.getHeight() ),
                                     createEquationNode( challenge.equationForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );

        // Guess
        final EquationBoxNode guessBoxNode =
                new EquationBoxNode( Strings.YOUR_EQUATION, challenge.guess.get().color, new PDimension( boxWidth, 0.3 * challengeSize.getHeight() ),
                                     createInteractiveEquationNode( challenge.equationForm, challenge.manipulationMode, challenge.guess, challenge.graph,
                                                                    LineGameConstants.INTERACTIVE_EQUATION_FONT, LineGameConstants.STATIC_EQUATION_FONT,
                                                                    challenge.guess.get().color ) );

        // Graph
        final ChallengeGraphNode graphNode = new AnswerGraphNode( challenge );

        // rendering order
        subclassParent.addChild( graphNode );
        subclassParent.addChild( answerBoxNode );
        subclassParent.addChild( guessBoxNode );

        // layout
        {
            // graphNode is positioned automatically based on mvt's origin offset.

            // guess equation in right half of challenge space
            guessBoxNode.setOffset( ( 0.75 * challengeSize.getWidth() ) - ( guessBoxNode.getFullBoundsReference().getWidth() / 2 ) + 10,
                                    graphNode.getFullBoundsReference().getMinY() + 50 );

            // answer below guess
            answerBoxNode.setOffset( guessBoxNode.getXOffset(), guessBoxNode.getFullBoundsReference().getMaxY() + 20 );

            // face centered below equation boxes
            faceNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                checkButton.getFullBoundsReference().getMaxY() - faceNode.getFullBoundsReference().getHeight() );
        }

        // To reduce brain damage during development, show the answer equation in translucent gray.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            PNode devAnswerNode = createEquationNode( challenge.equationForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, new Color( 0, 0, 0, 25 ) );
            devAnswerNode.setOffset( answerBoxNode.getFullBoundsReference().getMinX() + 30,
                                     answerBoxNode.getFullBoundsReference().getCenterY() - ( devAnswerNode.getFullBoundsReference().getHeight() / 2 ) );
            addChild( devAnswerNode );
            devAnswerNode.moveToBack();
        }

        // Update visibility of the correct/incorrect icons.
        final VoidFunction0 updateIcons = new VoidFunction0() {
            public void apply() {
                answerBoxNode.setCorrectIconVisible( model.state.get() == PlayState.NEXT );
                guessBoxNode.setCorrectIconVisible( model.state.get() == PlayState.NEXT && challenge.isCorrect() );
                guessBoxNode.setIncorrectIconVisible( model.state.get() == PlayState.NEXT && !challenge.isCorrect() );
            }
        };

        // sync with guess
        challenge.guess.addObserver( new SimpleObserver() {
            public void update() {
                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );

        // sync with game state
        model.state.addObserver( new VoidFunction1<PlayState>() {
            public void apply( PlayState state ) {

                // states in which the equation is interactive
                guessBoxNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || ( state == PlayState.NEXT && !challenge.isCorrect() ) );
                guessBoxNode.setChildrenPickable( guessBoxNode.getPickable() );

                // Show all equations and lines at the end of the challenge.
                answerBoxNode.setVisible( state == PlayState.NEXT );
                graphNode.setGuessVisible( state == PlayState.NEXT );

                // slope tool visible when user got it wrong
                graphNode.setSlopeToolVisible( state == PlayState.NEXT && !challenge.isCorrect() );

                // visibility of correct/incorrect icons
                updateIcons.apply();
            }
        } );
    }

    // Creates an interactive equation.
    private EquationNode createInteractiveEquationNode( EquationForm equationForm, ManipulationMode manipulationMode,
                                                        Property<Line> line, Graph graph, PhetFont interactiveFont, PhetFont staticFont, Color staticColor ) {
        if ( equationForm == EquationForm.SLOPE_INTERCEPT ) {
            boolean interactiveSlope = ( manipulationMode == ManipulationMode.SLOPE ) || ( manipulationMode == ManipulationMode.SLOPE_INTERCEPT );
            boolean interactiveIntercept = ( manipulationMode == ManipulationMode.INTERCEPT ) || ( manipulationMode == ManipulationMode.SLOPE_INTERCEPT );
            return new SlopeInterceptEquationNode( line,
                                                   new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                                   new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                                   new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                                   interactiveSlope, interactiveIntercept,
                                                   interactiveFont, staticFont, staticColor );
        }
        else if ( equationForm == EquationForm.POINT_SLOPE ) {
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
            throw new IllegalArgumentException( "unsupported equation form: " + equationForm );
        }
    }
}
