// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A button for use in spinner controls, fires continuously if pressed and held.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class SpinnerButtonNode extends PNode {

    private final PImage imageNode;
    private final DynamicCursorHandler listener = new DynamicCursorHandler();

    private final ObservableProperty<Boolean> enabled;
    private final BooleanProperty pressed = new BooleanProperty( false );
    private final BooleanProperty entered = new BooleanProperty( true );

    private final Timer timer;
    private boolean spinContinuously = false;

    // Spinner button that is always enabled.
    public SpinnerButtonNode( IUserComponent userComponent, Function0<ParameterSet> parameterSet,
                              final Image unpressedImage, final Image pressedImage, final Image disabledImage,
                              final VoidFunction0 callback ) {
        this( userComponent, parameterSet, unpressedImage, pressedImage, disabledImage, callback, new BooleanProperty( true ) );
    }

    /**
     * Constructor
     * @param userComponent component identifier for user data-collection message
     * @param parameterSet parameter set for user data-collection message
     * @param unpressedImage
     * @param pressedImage
     * @param disabledImage
     * @param callback called when the button fires
     * @param enabled property that controls whether the button is enabled
     */
    public SpinnerButtonNode( final IUserComponent userComponent, final Function0<ParameterSet> parameterSet,
                              final Image unpressedImage, final Image pressedImage, final Image disabledImage,
                              final VoidFunction0 callback, ObservableProperty<Boolean> enabled ) {

        this.enabled = enabled;

        final VoidFunction0 sendUserMessage = new VoidFunction0() {
            public void apply() {
                SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.button, UserActions.pressed,
                                                   parameterSet.apply().with( new ParameterKey( "spinContinuously" ), spinContinuously ) );
            }
        };

        // If holding down the button, then spin continuously.
        this.timer = new Timer( 200, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( SpinnerButtonNode.this.enabled.get() ) {
                    spinContinuously = true;
                    sendUserMessage.apply();
                    callback.apply();
                }
            }
        } ) {{
            setInitialDelay( 500 );
        }};

        imageNode = new PImage();
        addChild( imageNode );

        new RichSimpleObserver() {
            @Override public void update() {

                //Show a cursor hand but only if enabled
                listener.setCursor( SpinnerButtonNode.this.enabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );

                imageNode.setImage( new Function0<Image>() {
                    public Image apply() {
                        if ( SpinnerButtonNode.this.enabled.get() && !pressed.get() ) { return unpressedImage; }
                        else if ( SpinnerButtonNode.this.enabled.get() && pressed.get() ) { return pressedImage; }
                        else { return disabledImage; }
                    }
                }.apply() );
            }
        }.observe( this.enabled, pressed );


        imageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                if ( SpinnerButtonNode.this.enabled.get() && entered.get() && pressed.get() ) {
                    //Only fire the release event if the user didn't hold it down long enough to start autospin
                    if ( !spinContinuously ) {
                        sendUserMessage.apply();
                        callback.apply();
                    }
                    pressed.set( false );
                    spinContinuously = false;
                }
                timer.stop();
            }

            @Override public void mouseEntered( PInputEvent event ) {
                entered.set( true );
            }

            @Override public void mouseExited( PInputEvent event ) {
                entered.set( false );
            }

            @Override public void mousePressed( PInputEvent event ) {
                if ( SpinnerButtonNode.this.enabled.get() && entered.get() ) {
                    pressed.set( true );
                    timer.start();
                }
            }
        } );

        addInputEventListener( listener );
    }
}