// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.view;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Button with nice up or down images provided by NP.  This library uses phetcommon's function library instead of
 * functionaljava in case we don't have to put functionaljava as a required dependency for phetcommon.
 *
 * @author Sam Reid
 */
public class SpinnerButtonNode extends PNode {
    private final VoidFunction1<Boolean> callback;
    private final ObservableProperty<Boolean> enabled;
    private final BooleanProperty pressed = new BooleanProperty( false );
    private final BooleanProperty entered = new BooleanProperty( true );
    private final PImage imageNode;
    private final DynamicCursorHandler listener = new DynamicCursorHandler();
    private boolean autoSpinning = false;

    //If holding down the button, then automatically spin
    private Timer timer = new Timer( 200, new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            if ( enabled.get() ) {
                autoSpinning = true;
                callback.apply( autoSpinning );
            }
        }
    } ) {{
        setInitialDelay( 500 );
    }};

    public SpinnerButtonNode( final BufferedImage unpressedImage, final BufferedImage pressedImage, final BufferedImage disabledImage, final VoidFunction1<Boolean> callback ) {
        this( unpressedImage, pressedImage, disabledImage, callback, new BooleanProperty( true ) );
    }

    public SpinnerButtonNode( final BufferedImage unpressedImage, final BufferedImage pressedImage, final BufferedImage disabledImage, final VoidFunction1<Boolean> callback, final ObservableProperty<Boolean> enabled ) {
        this.callback = callback;
        this.enabled = enabled;
        imageNode = new PImage();
        addChild( imageNode );

        new RichSimpleObserver() {
            @Override public void update() {

                //Show a cursor hand but only if enabled
                listener.setCursor( enabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );

                imageNode.setImage( enabled.get() && !pressed.get() ? unpressedImage :
                                    enabled.get() && pressed.get() ? pressedImage :
                                    disabledImage );
            }
        }.observe( this.enabled, pressed );

        enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( final Boolean enabled ) {
                if ( !enabled && pressed.get() ) {
                    pressed.set( false );
                }
            }
        } );

        imageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                timer.stop();
                if ( enabled.get() && entered.get() && pressed.get() ) {

                    //Only fire the release event if the user didn't hold it down long enough to start autospin
                    if ( !autoSpinning ) {
                        callback.apply( autoSpinning );
                    }
                    pressed.set( false );
                }
                autoSpinning = false;
            }

            @Override public void mouseEntered( PInputEvent event ) {
                entered.set( true );
            }

            @Override public void mouseExited( PInputEvent event ) {
                entered.set( false );
            }

            @Override public void mousePressed( PInputEvent event ) {
                if ( enabled.get() && entered.get() ) {
                    pressed.set( true );
                    timer.start();
                }
            }
        } );

        addInputEventListener( listener );
    }
}