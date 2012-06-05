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
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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
 *
 * @author Chris Malley
 */
public class SpinnerButtonNode2<T> extends PNode {

    private boolean spinContinuously = false;

    /**
     * Constructor
     * @param userComponent component identifier for user data-collection message
     * @param inactiveImage
     * @param highlightedImage
     * @param pressedImage
     * @param disabledImage
     * @param pressed
     * @param inside
     * @param enabled property that controls whether the button is enabled
     * @param newValueFunction function that computes the new value when the button fires
     */
    public SpinnerButtonNode2( final IUserComponent userComponent,
                               final Image inactiveImage, final Image highlightedImage, final Image pressedImage, final Image disabledImage,
                               final Property<Boolean> pressed, final Property<Boolean> inside, final ObservableProperty<Boolean> enabled,
                               final Property<T> value, final Function0<T> newValueFunction ) {

        final PImage imageNode = new PImage();
        addChild( imageNode );

        // manage the cursor
        final DynamicCursorHandler cursorHandler = new DynamicCursorHandler();
        addInputEventListener( cursorHandler );
        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                cursorHandler.setCursor( enabled ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );
            }
        } );

        // button state
        new RichSimpleObserver() {
            @Override public void update() {
                imageNode.setImage( new Function0<Image>() {
                    public Image apply() {
                        if ( !enabled.get() ) {
                            return disabledImage;
                        }
                        else if ( pressed.get() ) {
                            return pressedImage;
                        }
                        else if ( inside.get() ) {
                            return highlightedImage;
                        }
                        else {
                            return inactiveImage;
                        }
                    }
                }.apply() );
            }
        }.observe( enabled, pressed, inside );

        // button action
        {
            final VoidFunction0 buttonAction = new VoidFunction0() {
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
                        buttonAction.apply();
                    }
                }
            } ) {{
                setInitialDelay( 500 );
            }};

            pressed.addObserver( new SimpleObserver() {
                public void update() {
                    if ( pressed.get() ) {
                        if ( enabled.get() ) {
                            timer.start();
                        }
                    }
                    else {
                        timer.stop();
                        if ( enabled.get() && inside.get() ) {
                            // Perform the action only if the user didn't hold down long enough to start spinning continuously.
                            if ( !spinContinuously ) {
                                buttonAction.apply();
                            }
                        }
                        spinContinuously = false;
                    }
                }
            } );
        }

        // handle mouse events
        imageNode.addInputEventListener( new PBasicInputEventHandler() {

            @Override public void mousePressed( PInputEvent event ) {
                pressed.set( true );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                pressed.set( false );
            }

            @Override public void mouseEntered( PInputEvent event ) {
                inside.set( true );
            }

            @Override public void mouseExited( PInputEvent event ) {
                inside.set( false );
            }
        } );
    }
}