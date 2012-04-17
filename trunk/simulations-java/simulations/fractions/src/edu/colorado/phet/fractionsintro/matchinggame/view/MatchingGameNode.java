// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.Scale;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.Next;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.ShowAnswer;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.TryAgain;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components.*;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.INSET;
import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.STAGE_SIZE;
import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.initialState;
import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.newLevel;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.*;
import static java.awt.Color.lightGray;
import static java.awt.Color.yellow;

/**
 * Displays an immutable matching game state
 *
 * @author Sam Reid
 */
public class MatchingGameNode extends FNode {

    private final SettableProperty<MatchingGameState> model;

    public MatchingGameNode( final boolean showDeveloperControls, final SettableProperty<MatchingGameState> model, final PNode rootNode ) {
        this.model = model;
        final MatchingGameState state = model.get();

        //Show the settings dialog
        if ( state.getMode() == CHOOSING_SETTINGS ) {
            final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, 6, 1 ), false, false );
            final VoidFunction0 startGame = new VoidFunction0() {
                @Override public void apply() {
                    model.set( newLevel( gameSettings.level.get() ).
                            withMode( Mode.WAITING_FOR_USER_TO_CHECK_ANSWER ).
                            withAudio( gameSettings.soundEnabled.get() ) );
                }
            };
            final PSwing settingsDialog = new PSwing( new GameSettingsPanel( gameSettings, startGame ) ) {{
                scale( MatchingGameCanvas.GAME_UI_SCALE );
                setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.height / 2 - getFullBounds().getHeight() / 2 );
            }};

            addChild( settingsDialog );
            return;
        }

        final PNode scales = new RichPNode( state.leftScale.toNode(), state.rightScale.toNode() );
        addChild( scales );

        final boolean revealClues = state.getMode() == SHOWING_WHY_ANSWER_WRONG ||
                                    state.getMode() == USER_CHECKED_CORRECT_ANSWER ||
                                    state.getMode() == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS;
        addChild( new ZeroOffsetNode( new BarGraphNode( state.getLeftScaleValue(), state.leftScaleDropTime, state.getRightScaleValue(), state.rightScaleDropTime, revealClues ) ) {{
            setOffset( scales.getFullBounds().getCenterX() - getFullWidth() / 2, scales.getFullBounds().getCenterY() - getFullHeight() - 15 );
        }} );

        final FNode scalesNode = new FNode( state.getScales().map( new F<Scale, PNode>() {
            @Override public PNode f( final Scale s ) {
                return s.toNode();
            }
        } ) );
        addChild( scalesNode );

        final FNode scoreCellsLayer = new FNode( state.scoreCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.toRoundedRectangle(), lightGray );
            }
        } ) );
        addChild( scoreCellsLayer );

        addChild( new PhetPText( "My Matches", new PhetFont( 18, true ) ) {{

            //Center "my matches" under the top left cell
            setOffset( scoreCellsLayer.getChild( 0 ).getFullBounds().getCenterX() - getFullWidth() / 2, scoreCellsLayer.getMaxY() );
        }} );

        final ImmutableVector2D buttonLocation = new ImmutableVector2D( state.getLastDroppedScaleRight() ? scalesNode.getFullBounds().getMaxX() + 80 : scalesNode.getFullBounds().getX() - 80,
                                                                        scalesNode.getFullBounds().getCenterY() );

        //Show the sign
        if ( revealClues ) {
            addSignNode( state, scales );
        }

        if ( state.getLeftScaleValue() > 0 && state.getRightScaleValue() > 0 ) {
            System.out.println( "state = " + state.getMode() + ", state.checks = " + state.getChecks() );

            if ( state.getMode() == SHOWING_WHY_ANSWER_WRONG ) {
                if ( state.getChecks() < 2 ) {
                    addChild( new Button( tryAgainButton, "Try again", Color.red, buttonLocation, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            updateWith( new TryAgain() );
                        }
                    } ) );
                }
                else {
                    addChild( new Button( showAnswerButton, "Show answer", Color.red, buttonLocation, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            updateWith( new ShowAnswer() );
                        }
                    } ) );
                }
            }

            //TODO: This shows a flicker of "check answer" after user presses next, needs to be fixed
            else if ( state.getChecks() < 2 && state.getMode() == WAITING_FOR_USER_TO_CHECK_ANSWER ) {
                addChild( new Button( checkAnswerButton, "Check answer", Color.orange, buttonLocation, new ActionListener() {
                    @Override public void actionPerformed( final ActionEvent e ) {
                        updateWith( new CheckAnswer() );
                    }
                } ) );
            }

            //If they match, show a "Keep" button. This allows the student to look at the right answer as long as they want before moving it to the scoreboard.
            if ( state.getMode() == USER_CHECKED_CORRECT_ANSWER ) {
                addSignNode( state, scales );

                addChild( new VBox( new FaceNode( 200 ), new PhetPText( state.getChecks() == 1 ? "+2" : "+1", new PhetFont( 18, true ) ) ) {{
                    final ImmutableVector2D pt = buttonLocation.plus( 0, -150 );
                    centerFullBoundsOnPoint( pt.getX(), pt.getY() );
                }} );

                addChild( new Button( Components.keepMatchButton, "Next", Color.green, buttonLocation, new ActionListener() {
                    @Override public void actionPerformed( ActionEvent e ) {
                        updateWith( new Next() );
                    }
                } ) );
            }

            if ( state.getMode() == SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS ) {
                addChild( new Button( Components.keepMatchButton, "Next", Color.green, buttonLocation, new ActionListener() {
                    @Override public void actionPerformed( ActionEvent e ) {
                        updateWith( new Next() );
                    }
                } ) );
            }
        }
        state.scoreCells.take( state.scored ).map( new F<Cell, PNode>() {
            @Override public PNode f( final Cell cell ) {
                return new PhetPText( "=", new PhetFont( 22 ) ) {{centerFullBoundsOnPoint( cell.rectangle.getCenter() );}};
            }
        } ).foreach( addChild );

        //Render the cells, costs about 50% of a CPU--could be optimized
        state.startCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ).foreach( addChild );
        state.fractions.map( new F<MovableFraction, PNode>() {
            @Override public PNode f( final MovableFraction f ) {
                return new MovableFractionNode( model, f, f.toNode(), rootNode, !revealClues );
            }
        } ).foreach( addChild );

        final int newLevel = state.info.level + 1;
        final ActionListener nextLevel = new ActionListener() {
            @Override public void actionPerformed( ActionEvent e ) {
                model.set( newLevel( newLevel ).withAudio( state.info.audio ) );
            }
        };

        if ( state.scored == state.scoreCells.length() ) {
            GameOverNode gameOverNode = new GameOverNode( state.info.level, state.info.score, 12, new DecimalFormat( "0" ), state.info.time, state.info.bestTime, state.info.time >= state.info.bestTime, state.info.timerVisible ) {{
                scale( MatchingGameCanvas.GAME_UI_SCALE );
                centerFullBoundsOnPoint( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() / 2 );
                addGameOverListener( new GameOverListener() {
                    @Override public void newGamePressed() {
                        model.set( model.get().newGame() );
                    }
                } );
            }};
            addChild( gameOverNode );
        }

        addChild( new ScoreboardNode( model ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, scoreCellsLayer.getMaxY() + INSET );
        }} );

        if ( showDeveloperControls && false ) {
            showDeveloperControls( model, newLevel, nextLevel );
        }
    }

    private void showDeveloperControls( final SettableProperty<MatchingGameState> model, final int newLevel, final ActionListener nextLevel ) {
        addChild( new VBox(
                new Button( null, "Reset", Color.yellow, ImmutableVector2D.ZERO, new ActionListener() {
                    @Override public void actionPerformed( final ActionEvent e ) {
                        model.set( initialState() );
                    }
                } ),
                new Button( null, "Resample", Color.yellow, ImmutableVector2D.ZERO, new ActionListener() {
                    @Override public void actionPerformed( final ActionEvent e ) {
                        model.set( newLevel( model.get().info.level ) );
                    }
                } ),
                new Button( null, "Skip to level " + newLevel, Color.yellow, ImmutableVector2D.ZERO, nextLevel )
        ) {{
            setOffset( 0, 200 );
        }} );
    }

    //Apply the specified function to the model
    public void updateWith( final F<MatchingGameState, MatchingGameState> f ) { model.set( f.f( model.get() ) ); }

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    public static PhetPPath createSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }

    private void addSignNode( final MatchingGameState state, final PNode scales ) {
        class TextSign extends PNode {
            TextSign( String text ) {
                final PhetFont textFont = new PhetFont( 100, true );
                final PNode sign = createSignNode( textFont.createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), text ).getOutline() );
                addChild( sign );
            }
        }

        final PNode sign = state.getLeftScaleValue() < state.getRightScaleValue() ? new TextSign( "<" ) :
                           state.getLeftScaleValue() > state.getRightScaleValue() ? new TextSign( ">" ) :
                           new EqualsSignNode();
        sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() + 10 );
        addChild( sign );
    }
}
