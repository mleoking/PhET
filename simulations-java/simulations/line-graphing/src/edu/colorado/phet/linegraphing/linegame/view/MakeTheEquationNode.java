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
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.EquationForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.MakeTheEquation;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Make the Equation" challenges.
 * User manipulates an equation on the right, graph is displayed on the left.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MakeTheEquationNode extends ChallengeNode {

    /**
     * Constructor
     * @param challenge the challenge
     * @param model the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer the audio player, for providing audio feedback during game play
     */
    public MakeTheEquationNode( final MakeTheEquation challenge, final LineGameModel model, PDimension challengeSize, final GameAudioPlayer audioPlayer ) {
        super( challenge, model, challengeSize, audioPlayer );

        // title, possibly scaled for i18n
        PNode titleNode = new PhetPText( challenge.title, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );
        final double maxTitleWidth = 0.45 * challengeSize.getWidth();
        if ( titleNode.getFullBoundsReference().getWidth() > maxTitleWidth ) {
            titleNode.scale( maxTitleWidth / titleNode.getFullBoundsReference().getWidth() );
        }

        final PDimension boxSize = new PDimension( 0.4 * challengeSize.getWidth(), 0.3 * challengeSize.getHeight() );

        // Answer
        final EquationBoxNode answerBoxNode =
                new EquationBoxNode( Strings.A_CORRECT_EQUATION, challenge.answer.color, boxSize,
                                     createEquationNode( challenge.equationForm, challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color ) );
        answerBoxNode.setVisible( false );

        // Guess
        final EquationBoxNode guessBoxNode =
                new EquationBoxNode( Strings.YOUR_EQUATION, challenge.guess.get().color, boxSize,
                                     createInteractiveEquationNode( challenge.equationForm, challenge.manipulationMode, challenge.guess, challenge.graph,
                                                                    LineGameConstants.INTERACTIVE_EQUATION_FONT, LineGameConstants.STATIC_EQUATION_FONT,
                                                                    challenge.guess.get().color ) );

        // Graph
        final ChallengeGraphNode graphNode = new AnswerGraphNode( challenge );

        // rendering order
        subclassParent.addChild( titleNode );
        subclassParent.addChild( graphNode );
        subclassParent.addChild( answerBoxNode );
        subclassParent.addChild( guessBoxNode );

        // layout
        {
            // graphNode is positioned automatically based on mvt's origin offset.

            // guess equation in left half of challenge space
            guessBoxNode.setOffset( ( 0.5 * challengeSize.getWidth() ) - ( guessBoxNode.getFullBoundsReference().getWidth() ) - 50,
                                    challenge.mvt.modelToViewY( 0 ) - guessBoxNode.getFullBoundsReference().getHeight() - 10 );

            // answer below guess
            answerBoxNode.setOffset( guessBoxNode.getXOffset(),
                                     challenge.mvt.modelToViewY( 0 ) + 10 );

            // face centered below equation boxes
            faceNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                checkButton.getFullBoundsReference().getMaxY() - faceNode.getFullBoundsReference().getHeight() );

            // title above guess equation, left justified
            titleNode.setOffset( guessBoxNode.getFullBoundsReference().getMinX(),
                                 guessBoxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - 20 );

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

                // Graph the guess line at the end of the challenge.
                graphNode.setGuessVisible( state == PlayState.NEXT );

                // show stuff when the user got the challenge wrong
                if ( state == PlayState.NEXT && !challenge.isCorrect() ) {
                    answerBoxNode.setVisible( true );
                    graphNode.setAnswerPointVisible( true );
                    graphNode.setGuessPointVisible( true );
                    graphNode.setSlopeToolVisible( true );
                }

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
