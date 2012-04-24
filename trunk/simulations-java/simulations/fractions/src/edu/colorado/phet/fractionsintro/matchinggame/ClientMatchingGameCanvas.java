// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
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
import edu.colorado.phet.fractionsintro.matchinggame.view.BarGraphNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.Button;
import edu.colorado.phet.fractionsintro.matchinggame.view.ButtonArgs;
import edu.colorado.phet.fractionsintro.matchinggame.view.EqualsSignNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.view.MovableFractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.ScoreboardNode;
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
public class ClientMatchingGameCanvas extends AbstractFractionsCanvas {
    public ClientMatchingGameCanvas( final boolean dev, final MatchingGameModel model ) {

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
            scale( MatchingGameCanvas.GAME_UI_SCALE );
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.height / 2 - getFullBounds().getHeight() / 2 );

            new CompositeBooleanProperty( new Function0<Boolean>() {
                @Override public Boolean apply() {
                    return model.state.get().info.mode == Mode.CHOOSING_SETTINGS;
                }
            }, model.state ).addObserver( setNodeVisible( this ) );
        }};
        addChild( settingsDialog );

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
            final ObservableProperty<Mode> mode = new CompositeProperty<Mode>( new Function0<Mode>() {
                @Override public Mode apply() {
                    return model.state.get().getMode();
                }
            }, model.state );
            final ObservableProperty<Integer> checks = new CompositeProperty<Integer>( new Function0<Integer>() {
                @Override public Integer apply() {
                    return model.state.get().getChecks();
                }
            }, model.state );

            addChild( new PNode() {{
                new RichSimpleObserver() {
                    @Override public void update() {
                        removeAllChildren();
                        addChild( new ZeroOffsetNode( new BarGraphNode( leftScaleValue.get(), rightScaleValue.get(), revealClues.get() ) ) {{
                            setOffset( scalesNode.getFullBounds().getCenterX() - getFullWidth() / 2, scalesNode.getFullBounds().getCenterY() - getFullHeight() - 15 );
                        }} );
                    }
                }.observe( leftScaleValue, rightScaleValue, revealClues );
            }} );

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

            //For each unique ID, show a graphic for that one.
            //TODO: handle new indices and throw away no longer used indices
            for ( int i = 0; i < 24; i++ ) {
                final int finalI = i;
                addChild( new PNode() {{
                    CompositeProperty<MovableFraction> m = new CompositeProperty<MovableFraction>( new Function0<MovableFraction>() {
                        @Override public MovableFraction apply() {

                            List<Integer> result = model.state.get().fractions.map( new F<MovableFraction, Integer>() {
                                @Override public Integer f( final MovableFraction movableFraction ) {
                                    return movableFraction.id;
                                }
                            } );
//                            System.out.println( "result = " + result );

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

            addChild( new ScoreboardNode( model.state ) {{
                setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - INSET, scoreCellsLayer.getMaxY() + INSET );
            }} );

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

            addChild( new PNode() {{
                //leftScaleValue, rightScaleValue, mode, checks, buttonLocation,
                new RichSimpleObserver() {
                    @Override public void update() {
                        removeAllChildren();
                        addChild( new GameButtonsNode( model.state.get(), buttonFactory, buttonLocation.get() ) );
                    }
                }.observe( leftScaleValue, rightScaleValue, mode, checks, buttonLocation );
            }} );
//
//            if ( showDeveloperControls ) {
//                addChild( buttonFactory.f( new ButtonArgs( null, "Resample", Color.red, new Vector2D( 100, 6 ), new Resample() ) ) );
//            }

            //Show the sign node, but only if revealClues is true
            addChild( new PNode() {{
                new RichSimpleObserver() {
                    @Override public void update() {
                        removeAllChildren();
                        if ( revealClues.get() ) {
                            addChild( getSignNode( model.state.get(), scalesNode ) );
                        }
                    }
                }.observe( leftScaleValue, rightScaleValue, mode, revealClues );
            }} );

        }};

        addChild( gameNode );
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