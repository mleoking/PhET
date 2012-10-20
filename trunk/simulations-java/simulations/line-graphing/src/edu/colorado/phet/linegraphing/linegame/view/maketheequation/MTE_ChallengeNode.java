// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

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
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.colorado.phet.linegraphing.linegame.model.maketheequation.MTE_Challenge;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.EquationBoxNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class view for "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_ChallengeNode extends ChallengeNode {

    private static final int ICON_X_SPACING = 10;
    private static final int BOX_Y_SPACING = 20;

    public MTE_ChallengeNode( final LineGameModel model, final MTE_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {

        PNode titleNode = new PhetPText( Strings.MAKE_THE_EQUATION, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );

        final PDimension boxSize = new PDimension( 0.35 * challengeSize.getWidth(), 0.35 * challengeSize.getHeight() );

        // The equation for the user's guess.
        EquationNode guessEquationNode = createGuessEquationNode( challenge.guess, challenge.graph, LineGameConstants.INTERACTIVE_EQUATION_FONT, LineGameConstants.STATIC_EQUATION_FONT, Color.BLACK );
        final PNode guessBoxNode = new EquationBoxNode( guessEquationNode, Strings.YOUR_LINE, boxSize );

        // The equation for the correct answer
        EquationNode answerEquationNode = createAnswerEquationNode( challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, challenge.answer.color );
        final PNode answerBoxNode = new EquationBoxNode( answerEquationNode, Strings.GIVEN_LINE, boxSize );

        // icons for indicating correct vs incorrect
        final PNode answerCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessCorrectNode = new PImage( Images.CHECK_MARK );
        final PNode guessIncorrectNode = new PImage( Images.X_MARK );

        final MTE_GraphNode graphNode = createGraphNode( challenge );

        final FaceNode faceNode = new FaceNode( LineGameConstants.FACE_DIAMETER, LineGameConstants.FACE_COLOR );

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

        // non-interactive nodes
        {
            titleNode.setPickable( false );
            titleNode.setChildrenPickable( false );
            answerBoxNode.setPickable( false );
            answerBoxNode.setChildrenPickable( false );
            answerCorrectNode.setPickable( false );
            guessCorrectNode.setPickable( false );
            guessIncorrectNode.setPickable( false );
            graphNode.setPickable( false );
            graphNode.setChildrenPickable( false );
            faceNode.setPickable( false );
            faceNode.setChildrenPickable( false );
            pointsAwardedNode.setPickable( false );
            pointsAwardedNode.setChildrenPickable( false );
        }

        // rendering order
        {
            addChild( titleNode );
            addChild( answerBoxNode );
            addChild( answerCorrectNode );
            addChild( guessBoxNode );
            addChild( guessCorrectNode );
            addChild( guessIncorrectNode );
            addChild( graphNode );
            addChild( checkButton );
            addChild( tryAgainButton );
            addChild( showAnswerButton );
            addChild( nextButton );
            addChild( pointToolParent );
            addChild( faceNode );
            addChild( pointsAwardedNode );
        }

        // layout
        {
            // title centered at top
            titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ), 10 );

            // guess equation centered in right half of challenge space
            guessBoxNode.setOffset( ( 0.75 * challengeSize.getWidth() ) - ( guessBoxNode.getFullBoundsReference().getWidth() / 2 ),
                                    ( challengeSize.getHeight() / 2 ) - guessBoxNode.getFullBoundsReference().getHeight() - ( BOX_Y_SPACING / 2 ) );

            // answer below guess
            answerBoxNode.setOffset( guessBoxNode.getXOffset(), ( challengeSize.getHeight() / 2 ) + ( BOX_Y_SPACING / 2 ) );

            // correct/incorrect icons are to left of equations
            answerCorrectNode.setOffset( answerBoxNode.getFullBoundsReference().getMinX() - answerCorrectNode.getFullBoundsReference().getWidth() - ICON_X_SPACING,
                                         answerBoxNode.getFullBoundsReference().getCenterY() - ( answerCorrectNode.getFullBoundsReference().getHeight() / 2 ) );
            guessCorrectNode.setOffset( guessBoxNode.getFullBoundsReference().getMinX() - guessCorrectNode.getFullBoundsReference().getWidth() - ICON_X_SPACING,
                                        guessBoxNode.getFullBoundsReference().getCenterY() - ( guessCorrectNode.getFullBoundsReference().getHeight() / 2 ) );
            guessIncorrectNode.setOffset( guessBoxNode.getFullBoundsReference().getMinX() - guessIncorrectNode.getFullBoundsReference().getWidth() - ICON_X_SPACING,
                                          guessBoxNode.getFullBoundsReference().getCenterY() - ( guessIncorrectNode.getFullBoundsReference().getHeight() / 2 ) );

            // graphNode is positioned automatically based on mvt's origin offset.
            // buttons centered at bottom of challenge space
            final double ySpacing = 15;
            final double buttonCenterX = ( challengeSize.getWidth() / 2 );
            final double buttonCenterY = graphNode.getFullBoundsReference().getMaxY() + ySpacing;
            checkButton.setOffset( buttonCenterX - ( checkButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            tryAgainButton.setOffset( buttonCenterX - ( tryAgainButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            showAnswerButton.setOffset( buttonCenterX - ( showAnswerButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            nextButton.setOffset( buttonCenterX - ( nextButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );

            // face centered in the challenge space
            faceNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( faceNode.getFullBoundsReference().getWidth() / 2 ),
                                ( challengeSize.getHeight() / 2 ) - ( faceNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // To reduce brain damage during development, show the answer equation in translucent gray.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            PNode devAnswerNode = createAnswerEquationNode( challenge.answer, LineGameConstants.STATIC_EQUATION_FONT, new Color( 0, 0, 0, 25 ) );
            devAnswerNode.setOffset( answerBoxNode.getFullBoundsReference().getCenterX() - ( devAnswerNode.getFullBoundsReference().getWidth() / 2 ),
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
            }
        } );

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( challenge.isCorrect() ) {
                    faceNode.smile();
                    audioPlayer.correctAnswer();
                    challenge.guess.set( challenge.guess.get().withColor( LineGameConstants.ANSWER_COLOR ) );
                    final int points = model.computePoints( model.state.get() == PlayState.FIRST_CHECK ? 1 : 2 );  //TODO handle this better
                    model.results.score.set( model.results.score.get() + points );
                    pointsAwardedNode.setText( MessageFormat.format( Strings.POINTS_AWARDED, String.valueOf( points ) ) );
                    // center points below face
                    pointsAwardedNode.setOffset( faceNode.getFullBoundsReference().getCenterX() - ( pointsAwardedNode.getFullBoundsReference().getWidth() / 2 ),
                                                 faceNode.getFullBoundsReference().getMaxY() + 10 );
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

    // Creates the "guess" equation portion of the view.
    protected abstract EquationNode createGuessEquationNode( Property<Line> line, Graph graph, PhetFont interactiveFont, PhetFont staticFont, Color staticColor );

    // Creates the "answer" equation portion of the view.
    protected abstract EquationNode createAnswerEquationNode( Line line, PhetFont font, Color color );

    // Creates the graph portion of the view.
    protected abstract MTE_GraphNode createGraphNode( MTE_Challenge challenge );
}
