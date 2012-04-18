// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.Equal;
import fj.F;
import fj.Hash;
import fj.Unit;
import fj.data.HashSet;
import fj.data.List;
import fj.data.Option;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;
import edu.colorado.phet.fractionsintro.matchinggame.model.MovableFraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.MatchingGameNode.ButtonArgs;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState.newLevel;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Mode.CHOOSING_SETTINGS;

/**
 * Canvas for the matching game. Uses the immutable model so reconstructs the scene graph any time the model changes.
 *
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {

    public static final double GAME_UI_SCALE = 1.5;

    public MatchingGameCanvas( final boolean showDeveloperControls, final MatchingGameModel model ) {

        //Have to cache the buttons to re-use them between frames because they are animated piccolo components and do not have their model subsumed by this model.
        final F<ButtonArgs, Button> buttonFactory = Cache.cache( new F<ButtonArgs, Button>() {
            @Override public Button f( final ButtonArgs buttonArgs ) {
                return new Button( buttonArgs.component, buttonArgs.text, buttonArgs.color, buttonArgs.location, new ActionListener() {
                    @Override public void actionPerformed( final ActionEvent e ) {
                        model.state.set( buttonArgs.listener.f( model.state.get() ) );
                    }
                } );
            }
        } );

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
        }};

        addChild( settingsDialog );

        addChild( new PNode() {{
            model.state.addObserver( new SimpleObserver() {
                @Override public void update() {
                    removeAllChildren();

                    //Show the settings dialog
                    if ( model.state.get().getMode() == CHOOSING_SETTINGS ) {
                        addChild( settingsDialog );
                    }
                    else {
                        addChild( new MatchingGameNode( showDeveloperControls, model.state, rootNode, buttonFactory ) );
                    }
//                    paintImmediately( 0, 0, MatchingGameCanvas.this.getWidth(), MatchingGameCanvas.this.getHeight() );
                }
            } );
        }} );

        //Feasibility test for performance improvements in rendering just the changed parts of the screen
//        addRepaintListener( model );
    }

    private void addRepaintListener( final MatchingGameModel model ) {//Testing getting optimal performance, not necessarily the best way
        //        PDebug.debugRegionManagement = true;
        model.state.addObserver( new ChangeObserver<MatchingGameState>() {
            @Override public void update( final MatchingGameState newValue, final MatchingGameState oldValue ) {

                final F<MovableFraction, Integer> getID = new F<MovableFraction, Integer>() {
                    @Override public Integer f( final MovableFraction m ) {
                        return m.id;
                    }
                };
                List<Integer> ids = newValue.fractions.map( getID ).append( oldValue.fractions.map( getID ) );
                final HashSet<Integer> set = new HashSet<Integer>( Equal.<Integer>anyEqual(), Hash.<Integer>anyHash() );
                ids.foreach( new F<Integer, Unit>() {
                    @Override public Unit f( final Integer integer ) {
                        set.set( integer );
                        return Unit.unit();
                    }
                } );
                System.out.println( "set = " + set.toCollection() );
                for ( final Integer id : set ) {
                    Option<MovableFraction> oldOne = oldValue.fractions.find( new F<MovableFraction, Boolean>() {
                        @Override public Boolean f( final MovableFraction m ) {
                            return m.id == id;
                        }
                    } );
                    final Option<MovableFraction> newOne = newValue.fractions.find( new F<MovableFraction, Boolean>() {
                        @Override public Boolean f( final MovableFraction m ) {
                            return m.id == id;
                        }
                    } );

                    if ( oldOne.isSome() && newOne.isSome() && !oldOne.some().position.equals( newOne.some().position ) ) {
                        final Rectangle newBounds = MatchingGameCanvas.this.getBounds( newOne, model.state );
                        paintImmediately( newBounds );
                        final Rectangle oldBounds = MatchingGameCanvas.this.getBounds( oldOne, model.state );
                        System.out.println( "newBounds = " + newBounds + ", oldBounds = " + oldBounds );
                        paintImmediately( oldBounds );
                    }
                }

//                    Option<MovableFraction> newDrag = newValue.fractions.find( new F<MovableFraction, Boolean>() {
//                        @Override public Boolean f( final MovableFraction m ) {
//                            return m.dragging;
//                        }
//                    } );
//                    Option<MovableFraction> oldDrag = oldValue.fractions.find( new F<MovableFraction, Boolean>() {
//                        @Override public Boolean f( final MovableFraction m ) {
//                            return m.dragging;
//                        }
//                    } );
//
//                    count++;
                if ( ( oldValue.getMode() != newValue.getMode() ) || newValue.info.mode == Mode.CHOOSING_SETTINGS || true ) {
                    paintImmediately( new Rectangle( 0, 0, MatchingGameCanvas.this.getWidth(), MatchingGameCanvas.this.getHeight() ) );
                    System.out.println( "immediate, w = " + getWidth() + "" );
                }
//                    else {
//                        if ( newDrag.isSome() ) { paintImmediately( newDrag.some().toNode().getGlobalFullBounds().getBounds() ); }
//                        if ( oldDrag.isSome() ) { paintImmediately( oldDrag.some().toNode().getGlobalFullBounds().getBounds() ); }
//                    }
            }
        } );
    }

    private Rectangle getBounds( final Option<MovableFraction> value, final SettableProperty<MatchingGameState> model ) {
        return new MovableFractionNode( model, value.some(), value.some().toNode(), rootNode, false ).getGlobalFullBounds().getBounds();
    }
}