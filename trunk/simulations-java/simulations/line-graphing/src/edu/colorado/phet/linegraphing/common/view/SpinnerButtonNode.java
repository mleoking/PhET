// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A button for use in spinner controls, fires continuously if pressed and held.
 * (Adapted from fractions sim.)
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class SpinnerButtonNode<T> extends PNode {

    private final BooleanProperty pressed = new BooleanProperty( false );
    private boolean entered = false;
    private boolean spinContinuously = false;

    // Spinner button that is always enabled.
    public SpinnerButtonNode( IUserComponent userComponent,
                              Image unpressedImage, Image pressedImage, Image disabledImage,
                              Property<T> value, Function0<T> newValueFunction ) {
        this( userComponent, unpressedImage, pressedImage, disabledImage, value, newValueFunction, new BooleanProperty( true ) );
    }

    /**
     * Constructor
     * @param userComponent component identifier for user data-collection message
     * @param unpressedImage
     * @param pressedImage
     * @param disabledImage
     * @param newValueFunction function that computes the new value when the button fires
     * @param enabled property that controls whether the button is enabled
     */
    public SpinnerButtonNode( final IUserComponent userComponent,
                              final Image unpressedImage, final Image pressedImage, final Image disabledImage,
                              final Property<T> value, final Function0<T> newValueFunction,
                              final ObservableProperty<Boolean> enabled ) {

        final DynamicCursorHandler listener = new DynamicCursorHandler();
        final PImage imageNode = new PImage();
        addChild( imageNode );

        // Called whenever the button fires
        final VoidFunction0 buttonFired = new VoidFunction0() {
            public void apply() {
                T newValue = newValueFunction.apply();
                SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.button, UserActions.pressed,
                                                   ParameterSet.parameterSet( ParameterKeys.value, newValue.toString() ).with( new ParameterKey( "spinContinuously" ), spinContinuously ) );
                value.set( newValue );
            }
        };

        // If holding down the button, then spin continuously.
        final Timer timer = new Timer( 200, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( enabled.get() ) {
                    spinContinuously = true;
                    buttonFired.apply();
                }
            }
        } ) {{
            setInitialDelay( 500 );
        }};

        // Manage the cursor and button state
        new RichSimpleObserver() {
            @Override public void update() {

                //Show a cursor hand if enabled
                listener.setCursor( enabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );

                // Enabled/disable the button
                imageNode.setImage( new Function0<Image>() {
                    public Image apply() {
                        if ( enabled.get() && !pressed.get() ) { return unpressedImage; }
                        else if ( enabled.get() && pressed.get() ) { return pressedImage; }
                        else { return disabledImage; }
                    }
                }.apply() );
            }
        }.observe( enabled, pressed );

        // Handle mouse events
        imageNode.addInputEventListener( new PBasicInputEventHandler() {

            @Override public void mousePressed( PInputEvent event ) {
                if ( enabled.get() && entered ) {
                    pressed.set( true );
                    timer.start();
                }
            }

            @Override public void mouseReleased( PInputEvent event ) {
                if ( enabled.get() && entered && pressed.get() ) {
                    // Only fire if the user didn't hold down long enough to start spinning continuously.
                    if ( !spinContinuously ) {
                        buttonFired.apply();
                    }
                    pressed.set( false );
                    spinContinuously = false;
                }
                timer.stop();
            }

            @Override public void mouseEntered( PInputEvent event ) {
                entered = true;
            }

            @Override public void mouseExited( PInputEvent event ) {
                entered = false;
            }
        } );

        addInputEventListener( listener );
    }
}