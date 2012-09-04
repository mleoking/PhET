// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.LineFactory.SlopeInterceptLineFactory;
import edu.colorado.phet.linegraphing.common.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.PointDragHandler;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.common.view.SlopeDragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel.PlayState;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationFactory;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * View components for a challenge in which the user is given an equation and must graph the line.
 * The equation and line manipulators correspond to slope-intercept form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GraphSlopeInterceptLineNode extends PhetPNode {

    //TODO this class has too much access to model, narrow the interface
    public GraphSlopeInterceptLineNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {

        final SlopeInterceptLineFactory lineFactory = new SlopeInterceptLineFactory();

        PNode titleNode = new PhetPText( "Graph the Line", GameConstants.TITLE_FONT, GameConstants.TITLE_COLOR ); //TODO i18n

        final EquationNode equationNode = new SlopeInterceptEquationFactory().createNode( lineFactory.withColor( model.challenge.get().answer, GameConstants.GIVEN_COLOR ),
                                                                                          GameConstants.EQUATION_FONT );

        final GameGraphNode graphNode = new GameGraphNode( model.graph, model.challenge.get().guess, model.challenge.get().answer, model.challenge.get().mvt, lineFactory );

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
            // equation centered in left half of challenge space
            equationNode.setOffset( ( 0.25 * challengeSize.getWidth() ) - ( equationNode.getFullBoundsReference().getWidth() / 2 ),
                                    ( challengeSize.getHeight() / 2 ) - ( equationNode.getFullBoundsReference().getHeight() / 2 ) );
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
                graphNode.setPickable( state == PlayState.FIRST_CHECK || state == PlayState.SECOND_CHECK );
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
                    model.challenge.get().guess.set( lineFactory.withColor( model.challenge.get().guess.get(), GameConstants.CORRECT_ANSWER_COLOR ) );
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

    //TODO generalize and promote to top level
    //TODO could SlopeInterceptGraphNode be generalized to handle this?
    private static class GameGraphNode extends GraphNode {

        private final SlopeInterceptLineNode answerNode;
        private final LineManipulatorNode slopeManipulatorNode, interceptManipulatorNode;

        public GameGraphNode( final Graph graph,
                              Property<SlopeInterceptLine> guessLine,
                              SlopeInterceptLine answerLine,
                              final ModelViewTransform mvt,
                              SlopeInterceptLineFactory lineFactory ) {
            super( graph, mvt );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = new SlopeInterceptLineNode( new SlopeInterceptLineFactory().withColor( answerLine, GameConstants.CORRECT_ANSWER_COLOR ), graph, mvt );
            answerNode.setEquationVisible( false );
            addChild( answerNode );
            answerNode.setVisible( false );

            // Manipulators for the interactive line
            final double manipulatorDiameter = mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );

            // ranges
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( new DoubleRange( graph.yRange.getMin(), graph.yRange.getMax() ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( new DoubleRange( graph.xRange.getMin(), graph.xRange.getMax() ) );
            final Property<DoubleRange> yInterceptRange = new Property<DoubleRange>( new DoubleRange( graph.yRange.getMin(), graph.yRange.getMax() ) );

            // interactivity for slope manipulator
            slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new CursorHandler() );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler<SlopeInterceptLine>( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                                                  slopeManipulatorNode, mvt, guessLine, riseRange, runRange,
                                                                                                  lineFactory ) );
            // interactivity for point (intercept) manipulator
            interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
            interceptManipulatorNode.addInputEventListener( new CursorHandler() );
            interceptManipulatorNode.addInputEventListener( new PointDragHandler<SlopeInterceptLine>( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                                                      interceptManipulatorNode, mvt, guessLine,
                                                                                                      new Property<DoubleRange>( new DoubleRange( 0, 0 ) ), yInterceptRange,
                                                                                                      lineFactory ) );

            // Rendering order
            addChild( guessNodeParent );
            addChild( interceptManipulatorNode );
            addChild( slopeManipulatorNode ); // add slope after intercept, so that slope can be changed when x=0

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
                public void apply( SlopeInterceptLine line ) {

                    // draw the line
                    {
                        guessNodeParent.removeAllChildren();
                        SlopeInterceptLineNode guessNode = new SlopeInterceptLineNode( line, graph, mvt );
                        guessNode.setEquationVisible( false );
                        guessNodeParent.addChild( guessNode );
                    }

                    // move the manipulators
                    {
                        final double y = line.rise + line.y1;
                        double x;
                        if ( line.run == 0 ) {
                            x = 0;
                        }
                        else if ( line.rise == 0 ) {
                            x = line.run;
                        }
                        else {
                            x = line.solveX( y );
                        }
                        slopeManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( x, y ) ) );
                        interceptManipulatorNode.setOffset( mvt.modelToView( new Point2D.Double( 0, line.y1 ) ) );
                    }

                    //TODO this was copied from LineFormsModel constructor
                    // adjust the ranges
                    {
                        // rise
                        final double minRise = graph.yRange.getMin() - line.y1;
                        final double maxRise = graph.yRange.getMax() - line.y1;
                        riseRange.set( new DoubleRange( minRise, maxRise ) );

                        // y yIntercept
                        final double minIntercept = ( line.rise >= 0 ) ? graph.yRange.getMin() : graph.yRange.getMin() - line.rise;
                        final double maxIntercept = ( line.rise <= 0 ) ? graph.yRange.getMax() : graph.yRange.getMax() - line.rise;
                        yInterceptRange.set( new DoubleRange( minIntercept, maxIntercept ) );
                    }
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
            slopeManipulatorNode.setVisible( false );
            interceptManipulatorNode.setVisible( false );
        }
    }
}
