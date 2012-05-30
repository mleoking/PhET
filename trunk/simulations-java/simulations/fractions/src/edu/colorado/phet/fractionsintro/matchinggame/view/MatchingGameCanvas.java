// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.Effect;
import fj.F;
import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.newLevel;
import static java.awt.Color.lightGray;
import static java.awt.Color.yellow;

/**
 * This class shows the graphics and provides user interaction for the matching game.
 * This class was written in response to #3314, because the previous version of MatchingGameCanvas redrew the screen every time step and had poor performance on
 * an old PhET laptop.  This class uses a more traditional piccolo approach, creating nodes once and maintaining them and only updating them when necessary.
 *
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    public static final double GAME_UI_SCALE = 1.5;

    public MatchingGameCanvas( final boolean dev, final MatchingGameModel model ) {

        //Game settings
        final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, 6, 1 ), false, false );
        final VoidFunction0 startGame = new VoidFunction0() {
            @Override public void apply() {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        final MatchingGameState m = newLevel( gameSettings.level.get() ).
                                withMode( Mode.WAITING_FOR_USER_TO_CHECK_ANSWER ).
                                withAudio( gameSettings.soundEnabled.get() ).withTimerVisible( gameSettings.timerEnabled.get() );
                        System.out.println( "starting game, info = " + m.info );
                        model.state.set( m );
                    }
                } );
            }
        };
        final PSwing settingsDialog = new PSwing( new GameSettingsPanel( gameSettings, startGame ) ) {{
            scale( GAME_UI_SCALE );
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.height / 2 - getFullBounds().getHeight() / 2 );

            new CompositeBooleanProperty( new Function0<Boolean>() {
                @Override public Boolean apply() {
                    return model.state.get().info.mode == Mode.CHOOSING_SETTINGS;
                }
            }, model.state ).addObserver( setNodeVisible( this ) );
        }};
        addChild( settingsDialog );
        final ObservableProperty<Mode> mode = new CompositeProperty<Mode>( new Function0<Mode>() {
            @Override public Mode apply() {
                return model.state.get().getMode();
            }
        }, model.state );

        //Things to show during the game (i.e. not when settings dialog showing.)
        final PNode gameNode = new PNode() {{
            final CompositeBooleanProperty notChoosingSettings = new CompositeBooleanProperty( new Function0<Boolean>() {
                @Override public Boolean apply() {
                    return model.state.get().info.mode != Mode.CHOOSING_SETTINGS;
                }
            }, model.state );
            notChoosingSettings.addObserver( setNodeVisible( this ) );

            //Cells at the top of the board

            final PNode scalesNode = new RichPNode( model.state.get().leftScale.toNode(), model.state.get().rightScale.toNode() );
            addChild( scalesNode );

            final ObservableProperty<Boolean> revealClues = new CompositeBooleanProperty( new Function0<Boolean>() {
                @Override public Boolean apply() {
                    return model.state.get().getMode() == Mode.SHOWING_WHY_ANSWER_WRONG ||
                           model.state.get().getMode() == Mode.USER_CHECKED_CORRECT_ANSWER ||
                           model.state.get().getMode() == Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS;
                }
            }, model.state );
            final ObservableProperty<Double> leftScaleValue = new CompositeDoubleProperty( new Function0<Double>() {
                @Override public Double apply() {
                    return model.state.get().getLeftScaleValue();
                }
            }, model.state );
            final ObservableProperty<Double> rightScaleValue = new CompositeDoubleProperty( new Function0<Double>() {
                @Override public Double apply() {
                    return model.state.get().getRightScaleValue();
                }
            }, model.state );
            final ObservableProperty<Integer> checks = new CompositeProperty<Integer>( new Function0<Integer>() {
                @Override public Integer apply() {
                    return model.state.get().getChecks();
                }
            }, model.state );
            final ObservableProperty<Integer> scored = new CompositeProperty<Integer>( new Function0<Integer>() {
                @Override public Integer apply() {
                    return model.state.get().scored;
                }
            }, model.state );
            final ObservableProperty<Integer> level = new CompositeProperty<Integer>( new Function0<Integer>() {
                @Override public Integer apply() {
                    return model.state.get().info.level;
                }
            }, model.state );
            final ObservableProperty<Integer> score = new CompositeProperty<Integer>( new Function0<Integer>() {
                @Override public Integer apply() {
                    return model.state.get().info.score;
                }
            }, model.state );
            final ObservableProperty<Boolean> timerVisible = new CompositeProperty<Boolean>( new Function0<Boolean>() {
                @Override public Boolean apply() {
                    return model.state.get().info.timerVisible;
                }
            }, model.state );
            final ObservableProperty<Long> timeInSec = new CompositeProperty<Long>( new Function0<Long>() {
                @Override public Long apply() {
                    return model.state.get().info.time / 1000L;
                }
            }, model.state );
            final ObservableProperty<Double> barGraphAnimationTime = new CompositeProperty<Double>( new Function0<Double>() {
                @Override public Double apply() {
                    return model.state.get().barGraphAnimationTime;
                }
            }, model.state );
            final ObservableProperty<List<Integer>> fractionIDs = new CompositeProperty<List<Integer>>( new Function0<List<Integer>>() {
                @Override public List<Integer> apply() {
                    return model.state.get().fractions.map( new F<MovableFraction, Integer>() {
                        @Override public Integer f( final MovableFraction m ) {
                            return m.id;
                        }
                    } );
                }
            }, model.state );

            //Time it takes to max out the bar graph bars.
            final double MAX_BAR_GRAPH_ANIMATION_TIME = 0.3;
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    final double scale = Math.min( barGraphAnimationTime.get() / MAX_BAR_GRAPH_ANIMATION_TIME, 1.0 );
                    final Option<MovableFraction> leftScaleFraction = model.state.get().getScaleFraction( model.state.get().leftScale );
                    final Option<MovableFraction> rightScaleFraction = model.state.get().getScaleFraction( model.state.get().rightScale );
                    if ( leftScaleFraction.isSome() && rightScaleFraction.isSome() ) {
                        parent.addChild( new ZeroOffsetNode( new BarGraphNode( leftScaleValue.get() * scale, leftScaleFraction.some().color,
                                                                               rightScaleValue.get() * scale, rightScaleFraction.some().color,
                                                                               revealClues.get() ) ) {{
                            setOffset( scalesNode.getFullBounds().getCenterX() - getFullWidth() / 2, scalesNode.getFullBounds().getCenterY() - getFullHeight() - 15 );
                        }} );
                    }
                }
            }, leftScaleValue, rightScaleValue, revealClues, barGraphAnimationTime ) );

            final FNode scoreCellsLayer = new FNode( model.state.get().scoreCells.map( new F<Cell, PNode>() {
                @Override public PNode f( Cell c ) {
                    return new PhetPPath( c.toRoundedRectangle(), Color.lightGray );
                }
            } ) );
            addChild( scoreCellsLayer );

            addChild( new PhetPText( "My Matches", new PhetFont( 18, true ) ) {{

                //Center "my matches" under the top left cell
                setOffset( scoreCellsLayer.getChild( 0 ).getFullBounds().getCenterX() - getFullWidth() / 2, scoreCellsLayer.getMaxY() );
            }} );

            addChild( new FNode( model.state.get().startCells.map( new F<Cell, PNode>() {
                @Override public PNode f( Cell c ) {
                    return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
                }
            } ) ) );

            //Show the draggable Fraction nodes.  if fraction ids changes or revealclues changes, just update the lot of them
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    //For each unique ID, show a graphic for that one.
                    for ( int i : fractionIDs.get() ) {
                        final int finalI = i;
                        parent.addChild( new PNode() {{
                            CompositeProperty<MovableFraction> m = new CompositeProperty<MovableFraction>( new Function0<MovableFraction>() {
                                @Override public MovableFraction apply() {
                                    return model.state.get().fractions.find( new F<MovableFraction, Boolean>() {
                                        @Override public Boolean f( final MovableFraction movableFraction ) {
                                            return movableFraction.id == finalI;
                                        }
                                    } ).orSome( (MovableFraction) null );
                                }
                            }, model.state );
                            m.addObserver( new VoidFunction1<MovableFraction>() {
                                @Override public void apply( final MovableFraction f ) {
                                    removeAllChildren();
                                    if ( f != null ) {
                                        addChild( new MovableFractionNode( model.state, f, f.toNode(), rootNode, !revealClues.get() ) );
                                    }
                                }
                            } );
                        }} );
                    }
                }
            }, fractionIDs, revealClues ) );

            //Update when level, score, timerVisible, time in seconds changes
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    parent.addChild( new ScoreboardNode( model.state ) {{

                        //Update the location, but get it approximately right so it doesn't expand the dirty region
                        setOffset( STAGE_SIZE.width - INSET, scoreCellsLayer.getMaxY() + INSET );
                        setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, scoreCellsLayer.getMaxY() + INSET );
                    }} );
                }
            }, level, score, timerVisible, timeInSec ) );

            //Way of creating game buttons, cache is vestigial and could be removed.
            final F<ButtonArgs, Button> buttonFactory = Cache.cache( new F<ButtonArgs, Button>() {
                @Override public Button f( final ButtonArgs buttonArgs ) {
                    return new Button( buttonArgs.component, buttonArgs.text, buttonArgs.color, buttonArgs.location, new ActionListener() {
                        @Override public void actionPerformed( final ActionEvent e ) {
                            model.state.set( buttonArgs.listener.f( model.state.get() ) );
                        }
                    } );
                }
            } );

            final ObservableProperty<Vector2D> buttonLocation = new CompositeProperty<Vector2D>( new Function0<Vector2D>() {
                @Override public Vector2D apply() {
                    return new Vector2D( model.state.get().getLastDroppedScaleRight() ? scalesNode.getFullBounds().getMaxX() + 80 : scalesNode.getFullBounds().getX() - 80,
                                         scalesNode.getFullBounds().getCenterY() );
                }
            }, leftScaleValue, rightScaleValue );

            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    parent.addChild( new GameButtonsNode( model.state.get(), buttonFactory, buttonLocation.get() ) );
                }
            }, leftScaleValue, rightScaleValue, mode, checks, buttonLocation ) );

            //Show the sign node, but only if revealClues is true
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    parent.addChild( revealClues.get() ? getSignNode( model.state.get(), scalesNode ) : new PNode() );
                }
            },
                                      leftScaleValue, rightScaleValue, mode, revealClues ) );

            //Show equals signs in the scoreboard.
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    parent.addChild( new FNode( model.state.get().scoreCells.take( model.state.get().scored ).map( new F<Cell, PNode>() {
                        @Override public PNode f( final Cell cell ) {
                            return new PhetPText( "=", new PhetFont( 22 ) ) {{
                                centerFullBoundsOnPoint( cell.rectangle.getCenter() );
                            }};
                        }
                    } ) ) );
                }
            }, scored ) );

            if ( dev ) {
                addChild( buttonFactory.f( new ButtonArgs( null, "Resample", Color.red, new Vector2D( 100, 6 ), new Resample() ) ) );
            }
        }};

        addChild( gameNode );

        //Show the game over dialog, if the game has ended.  Also, it has a stateful piccolo button so must not be cleared when the model changes, so it is stored in a field
        //and only regenerated when new games end.
        addChild( new UpdateNode(
                new Effect<PNode>() {
                    @Override public void e( final PNode parent ) {
                        if ( mode.get() == Mode.SHOWING_GAME_OVER_SCREEN ) {
                            MatchingGameState state = model.state.get();
                            addChild( new GameOverNode( state.info.level, state.info.score, 12, new DecimalFormat( "0" ), state.info.time, state.info.bestTime, state.info.time >= state.info.bestTime, state.info.timerVisible ) {{
                                scale( GAME_UI_SCALE );
                                centerFullBoundsOnPoint( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() / 2 );
                                addGameOverListener( new GameOverListener() {
                                    @Override public void newGamePressed() {
                                        model.state.set( model.state.get().newGame() );
                                    }
                                } );
                            }} );
                        }
                    }
                }
        ) );
    }

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    public static PhetPPath createSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }

    private static PNode getSignNode( final MatchingGameState state, final PNode scales ) {
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
        return sign;
    }

    public static VoidFunction1<Boolean> setNodeVisible( final PNode node ) {
        return new VoidFunction1<Boolean>() {
            @Override public void apply( final Boolean visible ) {
                node.setVisible( visible );
            }
        };
    }
}