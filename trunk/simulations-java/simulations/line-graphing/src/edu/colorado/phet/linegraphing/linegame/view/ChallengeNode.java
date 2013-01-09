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
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.PlayState;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Base class view for all challenges.
 * Provides the view components that are common to all challenges.
 * <p/>
 * Subclasses are responsible for:
 * <li>
 * <ul>providing the nodes for graph and equations</ul>
 * <ul>positioning faceNode</ul>
 * </li>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ChallengeNode extends PhetPNode {

    protected final FaceNode faceNode;
    protected final PText pointsAwardedNode;
    protected final TextButtonNode checkButton;
    protected final PNode subclassParent; // subclasses should add children to this node, to preserve rendering order

    /**
     * Constructor
     * @parma challenge the challenge
     * @param model the game model
     * @param challengeSize dimensions of the view rectangle that is available for rendering the challenge
     * @param audioPlayer the audio player, for providing audio feedback during game play
     */
    public ChallengeNode( final Challenge challenge, final LineGameModel model, PDimension challengeSize, final GameAudioPlayer audioPlayer ) {

        // title
        PNode titleNode = new PhetPText( challenge.title, LineGameConstants.TITLE_FONT, LineGameConstants.TITLE_COLOR );
        titleNode.setOffset( ( challengeSize.getWidth() / 2 ) - ( titleNode.getFullBoundsReference().getWidth() / 2 ), 10 ); // top center

        // description (dev)
        PNode descriptionNode = new PhetPText( challenge.description, new PhetFont(12), Color.BLACK );
        descriptionNode.setOffset( titleNode.getFullBoundsReference().getCenterX() - ( descriptionNode.getFullBoundsReference().getWidth() / 2 ),
                                   titleNode.getFullBoundsReference().getMaxY() + 2 );

        // smiley/frowny face
        faceNode = new FaceNode( LineGameConstants.FACE_DIAMETER, LineGameConstants.FACE_COLOR,
                                 new BasicStroke( 1f ), LineGameConstants.FACE_COLOR.darker(), Color.BLACK, Color.BLACK );

        // points awarded
        pointsAwardedNode = new PhetPText( "", LineGameConstants.POINTS_AWARDED_FONT, LineGameConstants.POINTS_AWARDED_COLOR );

        // buttons
        final Font buttonFont = LineGameConstants.BUTTON_FONT;
        final Color buttonBackground = LineGameConstants.BUTTON_COLOR;
        checkButton = new TextButtonNode( Strings.CHECK, buttonFont, buttonBackground );
        final TextButtonNode tryAgainButton = new TextButtonNode( Strings.TRY_AGAIN, buttonFont, buttonBackground );
        final TextButtonNode showAnswerButton = new TextButtonNode( Strings.SHOW_ANSWER, buttonFont, buttonBackground );
        final TextButtonNode nextButton = new TextButtonNode( Strings.NEXT, buttonFont, buttonBackground );
        final TextButtonNode skipButton = new TextButtonNode( "dev: Skip", new PhetFont( Font.BOLD, 12 ), Color.WHITE ); // developer control, no i18n
        final TextButtonNode replayButton = new TextButtonNode( "dev: Replay", new PhetFont( Font.BOLD, 12 ), Color.WHITE ); // developer control, no i18n

        // point tools
        Rectangle2D pointToolDragBounds = new Rectangle2D.Double( 0, 0, challengeSize.getWidth(), challengeSize.getHeight() );
        PointToolNode pointToolNode1 = new PointToolNode( challenge.pointTool1, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        PointToolNode pointToolNode2 = new PointToolNode( challenge.pointTool2, challenge.mvt, challenge.graph, pointToolDragBounds, new BooleanProperty( true ) );
        pointToolNode1.scale( LineGameConstants.POINT_TOOL_SCALE );
        pointToolNode2.scale( LineGameConstants.POINT_TOOL_SCALE );

        // Point tools moveToFront when dragged, so we give them a common parent to preserve rendering order of the reset of the scenegraph.
        PNode pointToolParent = new PNode();
        pointToolParent.addChild( pointToolNode1 );
        pointToolParent.addChild( pointToolNode2 );

        // Parent for subclass-specific nodes, to maintain rendering order.
        subclassParent = new PNode();

        // Rendering order
        {
            addChild( subclassParent );
            addChild( titleNode );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
               addChild( descriptionNode );
            }
            addChild( checkButton );
            addChild( tryAgainButton );
            addChild( showAnswerButton );
            addChild( nextButton );
            if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addChild( skipButton ); // This button lets you skip the current challenge.
                addChild( replayButton ); // This button lets you repeat the current challenge.
            }
            addChild( pointToolParent );
            addChild( faceNode );
            addChild( pointsAwardedNode );
        }

        // layout
        {
            // buttons at bottom center
            final double buttonCenterX = ( challengeSize.getWidth() / 2 );
            final double buttonCenterY = challengeSize.getHeight() - checkButton.getFullBoundsReference().getHeight() - 30;
            checkButton.setOffset( buttonCenterX - ( checkButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            tryAgainButton.setOffset( buttonCenterX - ( tryAgainButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            showAnswerButton.setOffset( buttonCenterX - ( showAnswerButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            nextButton.setOffset( buttonCenterX - ( nextButton.getFullBoundsReference().getWidth() / 2 ), buttonCenterY );
            skipButton.setOffset( nextButton.getFullBoundsReference().getCenterX() - ( skipButton.getFullBoundsReference().getWidth() / 2 ),
                                  nextButton.getFullBoundsReference().getMaxY() + 2 );
            replayButton.setOffset( nextButton.getFullBoundsReference().getCenterX() - ( replayButton.getFullBoundsReference().getWidth() / 2 ),
                                    nextButton.getFullBoundsReference().getMaxY() + 2 );
        }

        // "Check" button
        checkButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( challenge.isCorrect() ) {
                    faceNode.smile();
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
                model.state.set( PlayState.NEXT );
            }
        } );

        // "Next" button
        nextButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.state.set( PlayState.FIRST_CHECK );
            }
        } );

        // "Skip" button
        skipButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.skipCurrentChallenge();
            }
        } );

        // "Repeat" button
        replayButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.replayCurrentChallenge();
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
                skipButton.setVisible( !nextButton.getVisible() );
                replayButton.setVisible( nextButton.getVisible() );
            }
        } );
    }

    // Creates a static (non-interactive) equation.
    protected static EquationNode createEquationNode( LineForm lineForm, Line line, PhetFont font, Color color ) {
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
}
