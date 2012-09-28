// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.maketheequation;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.InteractiveEquationNode;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.colorado.phet.linegraphing.linegame.model.maketheequation.MTE_Challenge;
import edu.colorado.phet.linegraphing.linegame.view.ChallengeNode;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class view for "Make the Equation" (MTE) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MTE_ChallengeNode extends ChallengeNode {

    public MTE_ChallengeNode( final LineGameModel model, final MTE_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {

        PNode titleNode = new PhetPText( Strings.MAKE_THE_EQUATION, GameConstants.TITLE_FONT, GameConstants.TITLE_COLOR );

        // The equation for the user's guess.
        final InteractiveEquationNode guessEquationNode = createEquationNode( challenge.guess, challenge.graph, GameConstants.EQUATION_FONT );

        final MTE_GraphNode graphNode = createGraphNode( challenge );

        final FaceNode faceNode = new FaceNode( GameConstants.FACE_DIAMETER, GameConstants.FACE_COLOR );

        final PText pointsAwardedNode = new PhetPText( "", GameConstants.POINTS_AWARDED_FONT, GameConstants.POINTS_AWARDED_COLOR );

        // Buttons
        final Font buttonFont = GameConstants.BUTTON_FONT;
        final Color buttonForeground = LGColors.GAME_INSTRUCTION_COLORS;
        final TextButtonNode checkButton = new TextButtonNode( Strings.CHECK, buttonFont, buttonForeground );
        final TextButtonNode tryAgainButton = new TextButtonNode( Strings.TRY_AGAIN, buttonFont, buttonForeground );
        final TextButtonNode showAnswerButton = new TextButtonNode( Strings.SHOW_ANSWER, buttonFont, buttonForeground );
        final TextButtonNode nextButton = new TextButtonNode( Strings.NEXT, buttonFont, buttonForeground );

        // Point tools
        Rectangle2D pointToolDragBounds = new Rectangle2D.Double( 0, 0, challengeSize.getWidth(), challengeSize.getHeight() );
        PointToolNode pointToolNode1 = new PointToolNode( challenge.pointTool1, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        PointToolNode pointToolNode2 = new PointToolNode( challenge.pointTool2, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );

        // Point tools moveToFront when dragged, so we give them a common parent to preserve rendering order of the reset of the scenegraph.
        PNode pointToolParent = new PNode();
        pointToolParent.addChild( pointToolNode1 );
        pointToolParent.addChild( pointToolNode2 );

        // non-interactive nodes
        {
            titleNode.setPickable( false );
            titleNode.setChildrenPickable( false );
            faceNode.setPickable( false );
            faceNode.setChildrenPickable( false );
            pointsAwardedNode.setPickable( false );
            pointsAwardedNode.setChildrenPickable( false );
        }

        // rendering order
        {
            addChild( titleNode );
            addChild( guessEquationNode );
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
            titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 10 );
            // equation centered in right half of challenge space
            guessEquationNode.setOffset( ( 0.75 * challengeSize.getWidth() ) - ( guessEquationNode.getFullBoundsReference().getWidth() / 2 ),
                                         ( challengeSize.getHeight() / 2 ) - ( guessEquationNode.getFullBoundsReference().getHeight() / 2 ) );
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
                guessEquationNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || state == PlayState.NEXT );
                guessEquationNode.setChildrenPickable( guessEquationNode.getPickable() );
            }
        } );

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( challenge.isCorrect() ) {
                    faceNode.smile();
                    audioPlayer.correctAnswer();
                    challenge.guess.set( challenge.guess.get().withColor( GameConstants.ANSWER_COLOR ) );
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

    // Creates the equation portion of the view.
    protected abstract InteractiveEquationNode createEquationNode( Property<Line> line, Graph graph, PhetFont font );

    // Creates the graph portion of the view.
    protected abstract MTE_GraphNode createGraphNode( MTE_Challenge challenge );
}
