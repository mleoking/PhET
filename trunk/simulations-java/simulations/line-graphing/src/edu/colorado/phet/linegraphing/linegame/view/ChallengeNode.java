// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
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
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class for the view portion of challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ChallengeNode extends PhetPNode {

    public ChallengeNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize, boolean equationOnLeft, String title ) {

        PNode titleNode = new PhetPText( title, GameConstants.TITLE_FONT, GameConstants.TITLE_COLOR );

        final EquationNode equationNode = createEquationNode( model.challenge.get().answer, GameConstants.ANSWER_COLOR, GameConstants.EQUATION_FONT );

        final ChallengeGraphNode graphNode = createChallengeGraphNode( model.graph, model.challenge.get().guess, model.challenge.get().answer,
                                                                       model.challenge.get().mvt );

        final FaceNode faceNode = new FaceNode( GameConstants.FACE_DIAMETER, GameConstants.FACE_COLOR );

        final PText pointsNode = new PhetPText( "", GameConstants.POINTS_FONT, GameConstants.POINTS_COLOR );

        // Buttons
        final Font buttonFont = GameConstants.BUTTON_FONT;
        final Color buttonForeground = LGColors.GAME_INSTRUCTION_COLORS;
        final TextButtonNode checkButton = new TextButtonNode( Strings.CHECK, buttonFont, buttonForeground );
        final TextButtonNode tryAgainButton = new TextButtonNode( Strings.TRY_AGAIN, buttonFont, buttonForeground );
        final TextButtonNode showAnswerButton = new TextButtonNode( Strings.SHOW_ANSWER, buttonFont, buttonForeground );
        final TextButtonNode nextButton = new TextButtonNode( Strings.NEXT, buttonFont, buttonForeground );

        // Point tools
        Rectangle2D pointToolDragBounds = new Rectangle2D.Double( 0, 0, challengeSize.getWidth(), challengeSize.getHeight() );
        PointToolNode pointToolNode1 = new PointToolNode( model.pointTool1, model.challenge.get().mvt, model.graph, pointToolDragBounds, new BooleanProperty( true ) );
        PointToolNode pointToolNode2 = new PointToolNode( model.pointTool2, model.challenge.get().mvt, model.graph, pointToolDragBounds, new BooleanProperty( true ) );

        // non-interactive nodes
        {
            titleNode.setPickable( false );
            titleNode.setChildrenPickable( false );
            equationNode.setPickable( false );
            equationNode.setChildrenPickable( false );
            faceNode.setPickable( false );
            faceNode.setChildrenPickable( false );
            pointsNode.setPickable( false );
            pointsNode.setChildrenPickable( false );
        }

        // rendering order
        {
            addChild( titleNode );
            addChild( equationNode );
            addChild( graphNode );
            addChild( checkButton );
            addChild( tryAgainButton );
            addChild( showAnswerButton );
            addChild( nextButton );
            addChild( pointToolNode1 );
            addChild( pointToolNode2 );
            addChild( faceNode );
            addChild( pointsNode );
        }

        // layout
        {
            // title centered at top
            titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 10 );
            if ( equationOnLeft ) {
                // equation centered in left half of challenge space
                equationNode.setOffset( ( 0.25 * challengeSize.getWidth() ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                        ( challengeSize.getHeight() / 2 ) - ( equationNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                // equation centered in right half of challenge space
                equationNode.setOffset( ( 0.75 * challengeSize.getWidth() ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                        ( challengeSize.getHeight() / 2 ) - ( equationNode.getFullBoundsReference().getHeight() / 2 ) );
            }
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
                                     ( state == PlayState.NEXT && model.challenge.get().isCorrect() ) );

                // visibility of points
                pointsNode.setVisible( faceNode.getVisible() && model.challenge.get().isCorrect() );

                // visibility of buttons
                checkButton.setVisible( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK );
                tryAgainButton.setVisible( state == PlayState.TRY_AGAIN );
                showAnswerButton.setVisible( state == PlayState.SHOW_ANSWER );
                nextButton.setVisible( state == PlayState.NEXT );

                // states in which the graph is interactive
                graphNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK || state == PlayState.NEXT );
                graphNode.setChildrenPickable( graphNode.getPickable() );
            }
        } );

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( model.challenge.get().isCorrect() ) {
                    faceNode.smile();
                    audioPlayer.correctAnswer();
                    equationNode.setPaintDeep( GameConstants.CORRECT_ANSWER_COLOR );
                    model.challenge.get().guess.set( model.challenge.get().guess.get().withColor( GameConstants.CORRECT_ANSWER_COLOR ) );
                    final int points = model.computePoints( model.state.get() == PlayState.FIRST_CHECK ? 1 : 2 );  //TODO handle this better
                    model.results.score.set( model.results.score.get() + points );
                    pointsNode.setText( MessageFormat.format( Strings.POINTS_AWARDED, String.valueOf( points ) ) );
                    // center points below face
                    pointsNode.setOffset( faceNode.getFullBoundsReference().getCenterX() - ( pointsNode.getFullBoundsReference().getWidth() / 2 ),
                                          faceNode.getFullBoundsReference().getMaxY() + 10 );
                    model.state.set( PlayState.NEXT );
                }
                else {
                    faceNode.frown();
                    audioPlayer.wrongAnswer();
                    pointsNode.setText( "" );
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
                graphNode.setAnswerVisible( true );
                model.state.set( PlayState.NEXT );
                equationNode.setPaintDeep( GameConstants.CORRECT_ANSWER_COLOR );
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
    public abstract EquationNode createEquationNode( Line line, Color color, PhetFont font );

    // Creates the graph portion of the view.
    public abstract ChallengeGraphNode createChallengeGraphNode( final Graph graph, Property<Line> guessLine, Line answerLine, final ModelViewTransform mvt );

    // Base class for "Graph the Line" challenges.
    public static abstract class GraphTheLineChallengeNode extends ChallengeNode {

        public GraphTheLineChallengeNode( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            super( model, audioPlayer, challengeSize, true, Strings.GRAPH_THE_LINE );
        }
    }

    // Base class for "Make the Equation" challenges.
    public static abstract class MakeTheEquationChallengeNode extends ChallengeNode {

        public MakeTheEquationChallengeNode( LineGameModel model, GameAudioPlayer audioPlayer, PDimension challengeSize ) {
            super( model, audioPlayer, challengeSize, false, Strings.MAKE_THE_EQUATION );
        }
    }

    // Base class for the graph node in all challenges.
    public static abstract class ChallengeGraphNode extends GraphNode {

        public ChallengeGraphNode( Graph graph, ModelViewTransform mvt ) {
            super( graph, mvt );
        }

        // Creates the node that corresponds to the "answer" line.
        public abstract LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt );

        // Creates the node that corresponds to the "guess" line.
        public abstract LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt );

        // Changes the visibility of the "answer" line.
        public abstract void setAnswerVisible( boolean visible );
    }
}
