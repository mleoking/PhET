// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.colorado.phet.linegraphing.linegame.model.MTE_Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * View for "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MTE_ChallengeNode extends ChallengeNode {

    public MTE_ChallengeNode( final LineGameModel model, final MTE_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {

        PNode titleNode = new PhetPText( challenge.title, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );

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

        final FaceNode faceNode = new FaceNode( LineGameConstants.FACE_DIAMETER, LineGameConstants.FACE_COLOR,
                                                new BasicStroke( 1f ), LineGameConstants.FACE_COLOR.darker(), Color.BLACK, Color.BLACK );

        final PText pointsAwardedNode = new PhetPText( "", LineGameConstants.POINTS_AWARDED_FONT, LineGameConstants.POINTS_AWARDED_COLOR );

        // Buttons
        final Font buttonFont = LineGameConstants.BUTTON_FONT;
        final Color buttonForeground = LineGameConstants.BUTTON_COLOR;
        final TextButtonNode checkButton = new TextButtonNode( Strings.CHECK, buttonFont, buttonForeground );
        final TextButtonNode tryAgainButton = new TextButtonNode( Strings.TRY_AGAIN, buttonFont, buttonForeground );
        final TextButtonNode showAnswerButton = new TextButtonNode( Strings.SHOW_ANSWER, buttonFont, buttonForeground );
        final TextButtonNode nextButton = new TextButtonNode( Strings.NEXT, buttonFont, buttonForeground );

        // Point tools
        Rectangle2D pointToolDragBounds = new Rectangle2D.Double( 0, 0, challengeSize.getWidth(), challengeSize.getHeight() );
        PointToolNode pointToolNode1 = new PointToolNode( challenge.pointTool1, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        PointToolNode pointToolNode2 = new PointToolNode( challenge.pointTool2, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        pointToolNode1.scale( LineGameConstants.POINT_TOOL_SCALE );
        pointToolNode2.scale( LineGameConstants.POINT_TOOL_SCALE );

        // Point tools moveToFront when dragged, so we give them a common parent to preserve rendering order of the reset of the scenegraph.
        PNode pointToolParent = new PNode();
        pointToolParent.addChild( pointToolNode1 );
        pointToolParent.addChild( pointToolNode2 );

        // rendering order
        {
            addChild( titleNode );
            addChild( answerBoxNode );
            addChild( guessBoxNode );
            addChild( graphNode );
            addChild( checkButton );
            addChild( tryAgainButton );
            addChild( showAnswerButton );
            addChild( nextButton );
            addChild( answerCorrectNode );
            addChild( guessCorrectNode );
            addChild( guessIncorrectNode );
            addChild( pointToolParent );
            addChild( faceNode );
            addChild( pointsAwardedNode );
        }

        // layout
        final int iconXMargin = 10;
        final int iconYMargin = 5;
        {
            // title centered at top
            titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ), 10 );

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

            // graphNode is positioned automatically based on mvt's origin offset.
            // buttons centered at bottom of challenge space
            final double ySpacing = 15;
            final double buttonCenterX = ( challengeSize.getWidth() / 2 );
            final double buttonCenterY = graphNode.getFullBoundsReference().getMaxY() + ySpacing;
            checkButton.setOffset( buttonCenterX - ( checkButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            tryAgainButton.setOffset( buttonCenterX - ( tryAgainButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            showAnswerButton.setOffset( buttonCenterX - ( showAnswerButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            nextButton.setOffset( buttonCenterX - ( nextButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );

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

                // visibility of face
                faceNode.setVisible( state == PlayState.TRY_AGAIN ||
                                     state == PlayState.SHOW_ANSWER ||
                                     ( state == PlayState.NEXT && challenge.isCorrect() ) );

                // visibility of points
                pointsAwardedNode.setVisible( faceNode.getVisible() && challenge.isCorrect() );

                // visibility of buttons
                checkButton.setVisible( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK );
                tryAgainButton.setVisible( state == PlayState.TRY_AGAIN );
                showAnswerButton.setVisible( state == PlayState.SHOW_ANSWER );
                nextButton.setVisible( state == PlayState.NEXT );

                // states in which the equation is interactive
                guessBoxNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || ( state == PlayState.NEXT && !challenge.isCorrect() ) );
                guessBoxNode.setChildrenPickable( guessBoxNode.getPickable() );

                // Show the equation for the answer at the end of the challenge.
                answerBoxNode.setVisible( state == PlayState.NEXT );

                // visibility of correct/incorrect icons
                updateIcons.apply();

                // slope tool visible when user got it wrong
                graphNode.setSlopeToolVisible( state == PlayState.NEXT && !challenge.isCorrect() );
            }
        } );

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( challenge.isCorrect() ) {
                    faceNode.smile();
                    graphNode.setGuessVisible( true );
                    audioPlayer.correctAnswer();
                    final int points = model.computePoints( model.state.get() == PlayState.FIRST_CHECK ? 1 : 2 );  //TODO handle this better
                    model.results.score.set( model.results.score.get() + points );
                    pointsAwardedNode.setText( MessageFormat.format( Strings.POINTS_AWARDED, String.valueOf( points ) ) );
                    // points to right of face
                    pointsAwardedNode.setOffset( faceNode.getFullBoundsReference().getMaxX() + 10,
                                                 faceNode.getFullBoundsReference().getCenterY() - ( pointsAwardedNode.getFullBoundsReference().getHeight() / 2 ) );
                    model.state.set( PlayState.NEXT );
                }
                else {
                    faceNode.frown();
                    audioPlayer.wrongAnswer();
                    pointsAwardedNode.setText( "" );
                    if ( model.state.get() == PlayState.FIRST_CHECK ) {
                        model.state.set( PlayState.TRY_AGAIN );
                    }
                    else {
                        model.state.set( PlayState.SHOW_ANSWER );
                    }
                }
            }
        } );

        // "Try Again" button
        tryAgainButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.state.set( PlayState.SECOND_CHECK );
            }
        } );

        // "Show Answer" button
        showAnswerButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                graphNode.setGuessVisible( true );
                model.state.set( PlayState.NEXT );
            }
        } );

        // "Next" button
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.state.set( PlayState.FIRST_CHECK );
            }
        } );
    }

    // Creates the "answer" equation portion of the view.
    private EquationNode createAnswerEquationNode( LineForm lineForm, Line line, PhetFont font, Color color ) {
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            return new SlopeInterceptEquationNode( line, font, color );
        }
        else {
            return new PointSlopeEquationNode( line, font, color );
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
        else {
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
    }
}
