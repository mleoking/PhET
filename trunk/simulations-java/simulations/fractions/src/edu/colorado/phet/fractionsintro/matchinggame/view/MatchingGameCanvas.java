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
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameConstants;
import edu.colorado.phet.common.games.GameOverNode;
import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.Min;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.Cell;
import edu.colorado.phet.fractionsintro.matchinggame.model.GameResult;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.Controller.Resample;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.newLevel;
import static java.awt.Color.lightGray;
import static java.awt.Color.yellow;

/**
 * This class shows the graphics and provides user interaction for the matching game.
 * Even though using an immutable model, this class uses a more traditional piccolo approach for graphics,
 * creating nodes once and maintaining them and only updating them when necessary.
 *
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {

    public MatchingGameCanvas( final boolean dev, final MatchingGameModel model, final boolean standaloneSim ) {

        //Bar graph node that shows now bars, shown when the user has put something on both scales but hasn't checked the answer
        final PNode emptyBarGraphNode = new EmptyBarGraphNode();

        //Game settings
        final GameSettings gameSettings = new GameSettings( new IntegerRange( 1, 8, 1 ), false, false );

        //Function invoked when the user pushes a level button to start the game.
        final VoidFunction0 startGame = new VoidFunction0() {
            public void apply() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {

                        final MatchingGameState m = newLevel( gameSettings.level.get(), model.state.get().gameResults ).
                                withMode( Mode.WAITING_FOR_USER_TO_CHECK_ANSWER ).
                                withAudio( gameSettings.soundEnabled.get() ).
                                withTimerVisible( gameSettings.timerEnabled.get() );
                        model.state.set( m );
                    }
                } );
            }
        };

        //Property for the list of game results
        final Property<List<GameResult>> gameResults = new Property<List<GameResult>>( model.state.get().gameResults );
        model.state.addObserver( new VoidFunction1<MatchingGameState>() {
            public void apply( final MatchingGameState matchingGameState ) {
                gameResults.set( model.state.get().gameResults );
            }
        } );

        //Dialog for selecting and starting a level
        final PNode levelSelectionDialog = new ZeroOffsetNode( new LevelSelectionNode( startGame, gameSettings, gameResults, standaloneSim ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );

            new CompositeBooleanProperty( new Function0<Boolean>() {
                public Boolean apply() {
                    return model.state.get().info.mode == Mode.CHOOSING_SETTINGS;
                }
            }, model.state ).addObserver( setNodeVisible( this ) );
        }};

        //Title text, only shown when the user is choosing a level
        final PNode titleText = new PNode() {{
            addChild( new PhetPText( Strings.FRACTION_MATCHER, new PhetFont( 38, true ) ) );
            new CompositeBooleanProperty( new Function0<Boolean>() {
                public Boolean apply() {
                    return model.state.get().info.mode == Mode.CHOOSING_SETTINGS;
                }
            }, model.state ).addObserver( setNodeVisible( this ) );

            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, levelSelectionDialog.getFullBounds().getMinY() / 3 - getFullBounds().getHeight() / 2 );
        }};
        addChild( levelSelectionDialog );
        addChild( titleText );

        final int iconWidth = 40;
        final BufferedImage stopwatchIcon = BufferedImageUtils.multiScaleToWidth( GameConstants.STOPWATCH_ICON, iconWidth );
        final BufferedImage soundIcon = BufferedImageUtils.multiScaleToWidth( GameConstants.SOUND_ICON, iconWidth );
        final BufferedImage soundOffIcon = BufferedImageUtils.multiScaleToWidth( GameConstants.SOUND_OFF_ICON, iconWidth );
        final int maxIconWidth = Math.max( stopwatchIcon.getWidth(), soundIcon.getWidth() ) + 10;
        final int maxIconHeight = Math.max( stopwatchIcon.getHeight(), soundIcon.getHeight() ) + 10;
        final ToggleButtonNode stopwatchButton = new ToggleButtonNode( new PaddedIcon( maxIconWidth, maxIconHeight, new PImage( stopwatchIcon ) ), gameSettings.timerEnabled, new VoidFunction0() {
            public void apply() {
                gameSettings.timerEnabled.toggle();
            }
        }, ToggleButtonNode.FAINT_GREEN, false );

        class SoundIconNode extends PNode {
            SoundIconNode() {
                gameSettings.soundEnabled.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( final Boolean enabled ) {
                        removeAllChildren();
                        addChild( new PaddedIcon( maxIconWidth, maxIconHeight, new PImage( enabled ? soundIcon : soundOffIcon ) ) );
                    }
                } );
            }
        }

        final ToggleButtonNode soundButton = new ToggleButtonNode( new SoundIconNode(), gameSettings.soundEnabled, new VoidFunction0() {
            public void apply() {
                gameSettings.soundEnabled.toggle();
            }
        }, ToggleButtonNode.FAINT_GREEN, false );
        addChild( new HBox( stopwatchButton, soundButton ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, STAGE_SIZE.height - getFullBounds().getHeight() - INSET );
            new CompositeBooleanProperty( new Function0<Boolean>() {
                public Boolean apply() {
                    return model.state.get().info.mode == Mode.CHOOSING_SETTINGS;
                }
            }, model.state ).addObserver( setNodeVisible( this ) );
        }} );

        final ObservableProperty<Mode> mode = new CompositeProperty<Mode>( new Function0<Mode>() {
            public Mode apply() {
                return model.state.get().getMode();
            }
        }, model.state );

        //Things to show during the game (i.e. not when settings dialog showing.)
        final PNode gameNode = new PNode() {{
            final CompositeBooleanProperty notChoosingSettings = new CompositeBooleanProperty( new Function0<Boolean>() {
                public Boolean apply() {
                    return model.state.get().info.mode != Mode.CHOOSING_SETTINGS;
                }
            }, model.state );
            notChoosingSettings.addObserver( setNodeVisible( this ) );

            //Cells at the top of the board

            final PNode scalesNode = new RichPNode( model.state.get().leftScale.toNode(), model.state.get().rightScale.toNode() );
            addChild( scalesNode );


            //Model properties that need to be observed.
            final ObservableProperty<Boolean> revealClues = new CompositeBooleanProperty( new Function0<Boolean>() {
                public Boolean apply() {
                    return model.state.get().getMode() == Mode.SHOWING_WHY_ANSWER_WRONG ||
                           model.state.get().getMode() == Mode.USER_CHECKED_CORRECT_ANSWER ||
                           model.state.get().getMode() == Mode.SHOWING_CORRECT_ANSWER_AFTER_INCORRECT_GUESS;
                }
            }, model.state );
            final ObservableProperty<Double> leftScaleValue = new CompositeDoubleProperty( new Function0<Double>() {
                public Double apply() {
                    return model.state.get().getLeftScaleValue();
                }
            }, model.state );
            final ObservableProperty<Double> rightScaleValue = new CompositeDoubleProperty( new Function0<Double>() {
                public Double apply() {
                    return model.state.get().getRightScaleValue();
                }
            }, model.state );
            final ObservableProperty<Integer> checks = new CompositeProperty<Integer>( new Function0<Integer>() {
                public Integer apply() {
                    return model.state.get().getChecks();
                }
            }, model.state );
            final ObservableProperty<Integer> scored = new CompositeProperty<Integer>( new Function0<Integer>() {
                public Integer apply() {
                    return model.state.get().scored;
                }
            }, model.state );
            final ObservableProperty<Integer> level = new CompositeProperty<Integer>( new Function0<Integer>() {
                public Integer apply() {
                    return model.state.get().info.level;
                }
            }, model.state );
            final ObservableProperty<Integer> score = new CompositeProperty<Integer>( new Function0<Integer>() {
                public Integer apply() {
                    return model.state.get().info.score;
                }
            }, model.state );
            final ObservableProperty<Boolean> timerVisible = new CompositeProperty<Boolean>( new Function0<Boolean>() {
                public Boolean apply() {
                    return model.state.get().info.timerVisible;
                }
            }, model.state );
            final ObservableProperty<Long> timeInSec = new CompositeProperty<Long>( new Function0<Long>() {
                public Long apply() {
                    return model.state.get().info.time / 1000L;
                }
            }, model.state );
            final ObservableProperty<Double> barGraphAnimationTime = new CompositeProperty<Double>( new Function0<Double>() {
                public Double apply() {
                    return model.state.get().barGraphAnimationTime;
                }
            }, model.state );
            final ObservableProperty<List<Integer>> fractionIDs = new CompositeProperty<List<Integer>>( new Function0<List<Integer>>() {
                public List<Integer> apply() {
                    return model.state.get().fractions.map( new F<MovableFraction, Integer>() {
                        @Override public Integer f( final MovableFraction m ) {
                            return m.id;
                        }
                    } );
                }
            }, model.state );

            //Time it takes to max out the bar graph bars.
            final double MAX_BAR_GRAPH_ANIMATION_TIME = 0.375;
            Min min = new Min( barGraphAnimationTime, new Property<Double>( MAX_BAR_GRAPH_ANIMATION_TIME ) );
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    final double scale = Math.min( barGraphAnimationTime.get() / MAX_BAR_GRAPH_ANIMATION_TIME, 1.0 );
                    final Option<MovableFraction> leftScaleFraction = model.state.get().getScaleFraction( model.state.get().leftScale );
                    final Option<MovableFraction> rightScaleFraction = model.state.get().getScaleFraction( model.state.get().rightScale );
                    if ( leftScaleFraction.isSome() && rightScaleFraction.isSome() ) {
                        parent.addChild( new ZeroOffsetNode( new PNode() {{
                            addChild( emptyBarGraphNode );
                            if ( revealClues.get() ) {
                                addChild( new BarGraphNodeBars( leftScaleValue.get() * scale, leftScaleFraction.some().color,
                                                                rightScaleValue.get() * scale, rightScaleFraction.some().color
                                ) );
                            }
                        }} ) {{
                            setOffset( scalesNode.getFullBounds().getCenterX() - 100, scalesNode.getFullBounds().getCenterY() - 15 - 400 );
                            setOffset( scalesNode.getFullBounds().getCenterX() - getFullWidth() / 2, scalesNode.getFullBounds().getCenterY() - getFullHeight() - 15 );
                        }} );
                    }
                }
            }, leftScaleValue, rightScaleValue, revealClues, min ) );

            final FNode scoreCellsLayer = new FNode( model.state.get().scoreCells.map( new F<Cell, PNode>() {
                @Override public PNode f( Cell c ) {
                    return new PhetPPath( c.toRoundedRectangle(), Color.lightGray );
                }
            } ) );
            addChild( scoreCellsLayer );

            addChild( new PhetPText( Strings.MY_MATCHES, new PhetFont( 18, true ) ) {{

                //Center "my matches" under the top left cell
                setOffset( scoreCellsLayer.getChild( 0 ).getFullBounds().getCenterX() - getFullWidth() / 2, scoreCellsLayer.getMaxY() );
            }} );

            addChild( new FNode( model.state.get().startCells.map( new F<Cell, PNode>() {
                @Override public PNode f( Cell c ) {
                    return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
                }
            } ) ) );

            //Show the draggable Fraction nodes.  if fraction ids changes or reveal clues changes, just update the lot of them
            addChild( new UpdateNode( new Effect<PNode>() {
                @Override public void e( final PNode parent ) {
                    //For each unique ID, show a graphic for that one.
                    for ( int i : fractionIDs.get() ) {
                        final int finalI = i;
                        parent.addChild( new PNode() {{
                            CompositeProperty<MovableFraction> m = new CompositeProperty<MovableFraction>( new Function0<MovableFraction>() {
                                public MovableFraction apply() {
                                    return model.state.get().fractions.find( new F<MovableFraction, Boolean>() {
                                        @Override public Boolean f( final MovableFraction movableFraction ) {
                                            return movableFraction.id == finalI;
                                        }
                                    } ).orSome( (MovableFraction) null );
                                }
                            }, model.state );
                            m.addObserver( new VoidFunction1<MovableFraction>() {
                                public void apply( final MovableFraction f ) {
                                    removeAllChildren();
                                    if ( f != null ) {
                                        addChild( new MovableFractionNode( model.state, f, f.getNodeWithCorrectScale(), rootNode, !revealClues.get() ) );
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
                        public void actionPerformed( final ActionEvent e ) {
                            model.state.set( buttonArgs.listener.f( model.state.get() ) );
                        }
                    } );
                }
            } );

            final ObservableProperty<Vector2D> buttonLocation = new CompositeProperty<Vector2D>( new Function0<Vector2D>() {
                public Vector2D apply() {

                    //Where should the "check answer" button should be shown?  Originally it was on the same side as the last dropped value, but in interviews one student said this was confusing.
//                    final boolean showButtonsOnRight = model.state.get().getLastDroppedScaleRight();
                    final boolean showButtonsOnRight = false;
                    return new Vector2D( showButtonsOnRight ? scalesNode.getFullBounds().getMaxX() + 80 : scalesNode.getFullBounds().getX() - 80,
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
                        parent.removeAllChildren();
                        if ( mode.get() == Mode.SHOWING_GAME_OVER_SCREEN ) {
                            final MatchingGameState state = model.state.get();
                            final int maxPoints = 12;
                            parent.addChild( new GameOverNode( state.info.level, state.info.score, maxPoints, new DecimalFormat( "0" ), state.info.time, state.info.bestTime, state.info.time >= state.info.bestTime, state.info.timerVisible ) {{
                                scale( 1.5 );
                                centerFullBoundsOnPoint( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() / 2 );
                                addGameOverListener( new GameOverListener() {
                                    public void newGamePressed() {
                                        model.state.set( model.state.get().newGame( state.info.level, state.info.score, maxPoints ) );
                                    }
                                } );
                            }} );
                        }
                    }
                }, mode ) );
    }

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    private static PhetPPath getSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }

    private static PNode getSignNode( final MatchingGameState state, final PNode scales ) {
        class TextSign extends PNode {
            TextSign( String text ) {
                final PhetFont textFont = new PhetFont( 100, true );
                addChild( getSignNode( textFont.createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), text ).getOutline() ) );
            }
        }

        final PNode sign = state.getLeftScaleValue() < state.getRightScaleValue() ? new TextSign( "<" ) :
                           state.getLeftScaleValue() > state.getRightScaleValue() ? new TextSign( ">" ) :
                           new EqualsSignNode();
        sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() + 10 );
        return sign;
    }

    private static VoidFunction1<Boolean> setNodeVisible( final PNode node ) {
        return new VoidFunction1<Boolean>() {
            public void apply( final Boolean visible ) {
                node.setVisible( visible );
            }
        };
    }
}