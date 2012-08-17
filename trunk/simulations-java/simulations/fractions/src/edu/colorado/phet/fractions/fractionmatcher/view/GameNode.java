// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.Effect;
import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.CompositeBooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.Min;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.common.view.FNode;
import edu.colorado.phet.fractions.common.view.LevelSelectionScreenButton;
import edu.colorado.phet.fractions.fractionmatcher.model.Cell;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.model.Mode;
import edu.colorado.phet.fractions.fractionmatcher.model.MovableFraction;
import edu.colorado.phet.fractions.fractionmatcher.view.Controller.GameOver;
import edu.colorado.phet.fractions.fractionmatcher.view.Controller.Resample;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;

import static java.awt.Color.lightGray;

/**
 * Things to show during the game (i.e. not when settings dialog showing.
 *
 * @author Sam Reid
 */
class GameNode extends PNode {

    private final int FADE_DURATION = 400;

    public GameNode( final boolean dev, final MatchingGameModel model, final PNode emptyBarGraphNode, final PNode rootNode ) {

        //Show the reward node behind the main node so it won't interfere with the results the user collected.
        addChild( new RewardNode( model ) );

        //Fade in and out on refresh
        model.addRefreshListener( new VoidFunction0() {
            public void apply() {
                animateToTransparency( 0, FADE_DURATION ).setDelegate( new PActivityDelegate() {
                    public void activityStarted( final PActivity activity ) {
                    }

                    public void activityStepped( final PActivity activity ) {
                    }

                    public void activityFinished( final PActivity activity ) {
                        model.finishRefresh();
                        animateToTransparency( 1, FADE_DURATION );
                    }
                } );
            }
        } );

        //Animation when selected
        final CompositeBooleanProperty notChoosingSettings = new CompositeBooleanProperty( new Function0<Boolean>() {
            public Boolean apply() {
                return model.state.get().info.mode != Mode.CHOOSING_SETTINGS;
            }
        }, model.state );
        notChoosingSettings.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean showGameNode ) {
                if ( showGameNode ) {
                    setVisible( true );
                    setOffset( AbstractFractionsCanvas.STAGE_SIZE.getWidth(), 0 );
                    animateToPositionScaleRotation( 0, 0, 1, 0, 400 );
                }
                else {
                    animateToPositionScaleRotation( AbstractFractionsCanvas.STAGE_SIZE.getWidth(), 0, 1, 0, 400 ).
                            setDelegate( new PActivityDelegate() {
                                public void activityStarted( final PActivity activity ) {
                                }

                                public void activityStepped( final PActivity activity ) {
                                }

                                public void activityFinished( final PActivity activity ) {
                                    setVisible( false );
                                }
                            } );
                }
            }
        } );
        setVisible( false );

        //Cells at the top of the board
        final PNode scalesNode = new RichPNode( model.state.get().leftScale.toNode(), model.state.get().rightScale.toNode() );
        addChild( scalesNode );

        //Time it takes to max out the bar graph bars.
        final double MAX_BAR_GRAPH_ANIMATION_TIME = 0.375;
        Min min = new Min( model.barGraphAnimationTime, new Property<Double>( MAX_BAR_GRAPH_ANIMATION_TIME ) );
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode parent ) {
                final double scale = Math.min( model.barGraphAnimationTime.get() / MAX_BAR_GRAPH_ANIMATION_TIME, 1.0 );
                final Option<MovableFraction> leftScaleFraction = model.state.get().getScaleFraction( model.state.get().leftScale );
                final Option<MovableFraction> rightScaleFraction = model.state.get().getScaleFraction( model.state.get().rightScale );
                if ( leftScaleFraction.isSome() && rightScaleFraction.isSome() ) {
                    parent.addChild( new ZeroOffsetNode( new PNode() {{
                        addChild( emptyBarGraphNode );
                        if ( model.revealClues.get() ) {
                            addChild( new BarGraphNodeBars( model.leftScaleValue.get() * scale, leftScaleFraction.some().color,
                                                            model.rightScaleValue.get() * scale, rightScaleFraction.some().color
                            ) );
                        }
                    }} ) {{
                        setOffset( scalesNode.getFullBounds().getCenterX() - 100, scalesNode.getFullBounds().getCenterY() - 15 - 400 );
                        setOffset( scalesNode.getFullBounds().getCenterX() - getFullWidth() / 2, scalesNode.getFullBounds().getCenterY() - getFullHeight() - 15 );
                    }} );
                }
            }
        }, model.leftScaleValue, model.rightScaleValue, model.revealClues, min ) );

        final FNode scoreCellsLayer = new FNode( model.state.get().scoreCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.toRoundedRectangle(), Color.lightGray );
            }
        } ) );
        addChild( scoreCellsLayer );

        final PhetPText myMatchesText = new PhetPText( Strings.MY_MATCHES, new PhetFont( 18, true ) ) {{

            //Show "my matches" under the top left cell
            final double offsetX = scoreCellsLayer.getChild( 0 ).getFullBounds().getX();
            setOffset( offsetX, scoreCellsLayer.getMaxY() );
        }};
        addChild( myMatchesText );

        addChild( new FNode( model.state.get().startCells.map( new F<Cell, PNode>() {
            @Override public PNode f( Cell c ) {
                return new PhetPPath( c.rectangle.toRectangle2D(), new BasicStroke( 1 ), lightGray );
            }
        } ) ) );

        //Show the draggable Fraction nodes.  If fraction ids changes or reveal clues changes, just update the lot of them
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode parent ) {
                //For each unique ID, show a graphic for that one.
                for ( int i : model.fractionIDs.get() ) {
                    final int finalI = i;
                    parent.addChild( new PNode() {{
                        CompositeProperty<MovableFraction> m = new CompositeProperty<MovableFraction>( new Function0<MovableFraction>() {
                            public MovableFraction apply() {
                                return model.state.get().fractions.find( new F<MovableFraction, Boolean>() {
                                    @Override public Boolean f( final MovableFraction movableFraction ) {
                                        return movableFraction.id.value == finalI;
                                    }
                                } ).orSome( (MovableFraction) null );
                            }
                        }, model.state );
                        m.addObserver( new VoidFunction1<MovableFraction>() {
                            public void apply( final MovableFraction f ) {
                                removeAllChildren();
                                if ( f != null ) {
                                    addChild( new MovableFractionNode( model.state, f, f.getNodeWithCorrectScale(), rootNode, !model.revealClues.get() ) );
                                }
                            }
                        } );
                    }} );
                }
            }
        }, model.fractionIDs, model.revealClues ) );

        final LevelSelectionScreenButton levelSelectionScreenButton = new LevelSelectionScreenButton( new VoidFunction0() {
            public void apply() {
                model.state.set( model.state.get().withMode( Mode.CHOOSING_SETTINGS ) );
            }
        }, Images.FRACTIONS_BUTTON_MATCHING ) {{
            setOffset( AbstractFractionsCanvas.INSET, myMatchesText.getMaxY() + AbstractFractionsCanvas.INSET * 2 );
        }};

        //Update the scoreboard when level, score, timerVisible, or time (in seconds) changes
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode parent ) {
                parent.addChild( new ScoreboardNode( model, levelSelectionScreenButton ) {{

                    //Update the location, but get it approximately right so it doesn't expand the dirty region
                    setOffset( AbstractFractionsCanvas.STAGE_SIZE.width - AbstractFractionsCanvas.INSET * 2, scoreCellsLayer.getMaxY() + AbstractFractionsCanvas.INSET );
                    setOffset( AbstractFractionsCanvas.STAGE_SIZE.width - getFullBounds().getWidth() - AbstractFractionsCanvas.INSET * 2, scoreCellsLayer.getMaxY() + AbstractFractionsCanvas.INSET );
                }} );
            }
        }, model.level, model.score, model.timerVisible, model.timeInSec ) );

        //Function for creating game buttons from a ButtonArgs
        final F<ButtonArgs, Button> buttonFactory = new F<ButtonArgs, Button>() {
            @Override public Button f( final ButtonArgs buttonArgs ) {
                return new Button( buttonArgs.component, buttonArgs.text, buttonArgs.color, buttonArgs.location, new ActionListener() {
                    public void actionPerformed( final ActionEvent e ) {
                        model.state.set( buttonArgs.listener.f( model.state.get() ) );
                    }
                } );
            }
        };

        final ObservableProperty<Vector2D> buttonLocation = new CompositeProperty<Vector2D>( new Function0<Vector2D>() {
            public Vector2D apply() {

                //Show the "check answer" button always on the left so it doesn't interfere with the scoreboard readout on the right
                return new Vector2D( scalesNode.getFullBounds().getX() - 80,
                                     scalesNode.getFullBounds().getCenterY() );
            }
        }, model.leftScaleValue, model.rightScaleValue );

        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode parent ) {
                parent.addChild( new GameButtonsNode( model.state.get(), buttonFactory, buttonLocation.get() ) );
            }
        }, model.leftScaleValue, model.rightScaleValue, model.mode, model.checks, buttonLocation ) );

        //Show the sign node, but only if revealClues is true
        addChild( new UpdateNode( new Effect<PNode>() {
            @Override public void e( final PNode parent ) {
                parent.addChild( model.revealClues.get() ? new SignNode( model.state.get(), scalesNode ) : new PNode() );
            }
        }, model.leftScaleValue, model.rightScaleValue, model.mode, model.revealClues ) );

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
        }, model.scored ) );

        if ( dev ) {
            addChild( new HBox( buttonFactory.f( new ButtonArgs( null, "Resample", Color.red, new Vector2D( 100, 6 ), new Resample( model.levelFactory ) ) ),
                                buttonFactory.f( new ButtonArgs( null, "Test game over", Color.green, new Vector2D( 100, 6 ), new GameOver() ) ) ) );
        }

        //Show the game over dialog, if the game has ended.  Also, it has a stateful piccolo button so must not be cleared when the model changes, so it is stored in a field
        //and only regenerated when new games end.
        addChild( GameOverDialog.createGameOverDialog( model ) );
    }
}