// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.common.view;

import java.awt.Cursor;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class SpinnerButtonNode extends PNode {
    public final ObservableProperty<Boolean> enabled;
    public final BooleanProperty pressed = new BooleanProperty( false );
    public final BooleanProperty entered = new BooleanProperty( true );
    private final PImage imageNode;
    private final CursorHandler listener = new CursorHandler();

    public SpinnerButtonNode( final BufferedImage unpressedImage, final BufferedImage pressedImage, final BufferedImage disabledImage, final VoidFunction0 callback ) {
        this( unpressedImage, pressedImage, disabledImage, callback, new BooleanProperty( true ) );
    }

    public SpinnerButtonNode( final BufferedImage unpressedImage, final BufferedImage pressedImage, final BufferedImage disabledImage, final VoidFunction0 callback, ObservableProperty<Boolean> enabled ) {
        this.enabled = enabled;
        imageNode = new PImage();
        addChild( imageNode );

        new RichSimpleObserver() {
            @Override public void update() {

                //Show a cursor hand but only if enabled
                listener.setCursor( SpinnerButtonNode.this.enabled.get() ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR );

                imageNode.setImage( new Function0<BufferedImage>() {
                    public BufferedImage apply() {
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
                    callback.apply();
                    pressed.set( false );
                }
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
                }
            }
        } );

        addInputEventListener( listener );
    }
}