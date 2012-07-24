// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationFactory;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * TODO class doc
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FindLineNode extends PhetPNode {

    //TODO use these constants in the view for all challenges
    private static final PhetFont TITLE_FONT = new PhetFont( Font.BOLD, 40 );
    private static final Color TITLE_COLOR = Color.BLACK;
    private static final Font BUTTON_FONT = new PhetFont( Font.BOLD, 22 );
    private static final PhetFont EQUATION_FONT = new PhetFont( Font.BOLD, 40 );
    private static final double FACE_DIAMETER = 240;
    private static final Color GUESS_COLOR = PhetColorScheme.RED_COLORBLIND;
    private static final Color CORRECT_ANSWER_COLOR = Color.GREEN;
    private static final Color FACE_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow

    //TODO this class has too much access to model, narrow the interface
    public FindLineNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {

        PNode titleNode = new PhetPText( "Make the Line", TITLE_FONT, TITLE_COLOR );  //TODO i18n

        PNode equationNode = new SlopeInterceptEquationFactory().createNode( model.challenge.get().answer.withColor( GUESS_COLOR ), EQUATION_FONT );

        final GameGraphNode graphNode = new GameGraphNode( model.graph, model.challenge.get().guess, model.challenge.get().answer, model.mvt );

        final FaceNode faceNode = new FaceNode( FACE_DIAMETER, FACE_COLOR );

        // Buttons
        final Color buttonForeground = LGColors.GAME_INSTRUCTION_COLORS;
        final TextButtonNode checkButton = new TextButtonNode( Strings.CHECK, BUTTON_FONT, buttonForeground );
        final TextButtonNode tryAgainButton = new TextButtonNode( Strings.TRY_AGAIN, BUTTON_FONT, buttonForeground );
        final TextButtonNode showAnswerButton = new TextButtonNode( Strings.SHOW_ANSWER, BUTTON_FONT, buttonForeground );
        final TextButtonNode nextButton = new TextButtonNode( Strings.NEXT, BUTTON_FONT, buttonForeground );

        // rendering order
        {
            addChild( titleNode );
            addChild( equationNode );
            addChild( graphNode );
            addChild( checkButton );
            addChild( tryAgainButton );
            addChild( showAnswerButton );
            addChild( nextButton );
            addChild( faceNode );
        }

        // layout
        {
            // title centered at top
            titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ),
                                 10 );
            // equation centered in left half of challenge space
            equationNode.setOffset( ( 0.25 * challengeSize.getWidth() ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                    ( challengeSize.getHeight() / 2 ) - ( equationNode.getFullBoundsReference().getHeight() / 2 ) );
            // graph centered in right half of challenge space
            graphNode.setOffset( ( challengeSize.getWidth() / 2 ),
                                 titleNode.getFullBoundsReference().getMaxY() );
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

                // visibility of buttons
                checkButton.setVisible( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK );
                tryAgainButton.setVisible( state == PlayState.TRY_AGAIN );
                showAnswerButton.setVisible( state == PlayState.SHOW_ANSWER );
                nextButton.setVisible( state == PlayState.NEXT );
            }
        } );

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( model.challenge.get().isCorrect() ) {
                    faceNode.smile();
                    audioPlayer.correctAnswer();
                    model.score.set( model.score.get() + model.computePoints( model.state.get() == PlayState.FIRST_CHECK ? 1 : 2 ) ); //TODO handle this better
                    model.state.set( PlayState.NEXT );
                }
                else {
                    faceNode.frown();
                    audioPlayer.wrongAnswer();
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
            }
        } );

        // "Next" button
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //TODO tell model to advance to the next challenge
            }
        } );
    }

    //TODO generalize and promote to top level
    private static class GameGraphNode extends GraphNode {

        private final SlopeInterceptLineNode answerNode;
        private SlopeInterceptLineNode guessNode;

        public GameGraphNode( final Graph graph, Property<StraightLine> guessLine, StraightLine answerLine, final ModelViewTransform mvt ) {
            super( graph, mvt );

            // the correct answer, initially hidden
            answerNode = new SlopeInterceptLineNode( answerLine.withColor( CORRECT_ANSWER_COLOR ), graph, mvt );
            answerNode.setEquationVisible( false );
            addChild( answerNode );
            answerNode.setVisible( false );

            //TODO add manipulators

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<StraightLine>() {
                public void apply( StraightLine line ) {
                    if ( guessNode != null ) {
                        removeChild( guessNode );
                    }
                    guessNode = new SlopeInterceptLineNode( line.withColor( GUESS_COLOR ), graph, mvt );
                    guessNode.setEquationVisible( false );
                    addChild( guessNode );
                }
            } );
        }

        // Sets the visibility of the correct answer.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
        }
    }
}
