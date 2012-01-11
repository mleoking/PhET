// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Cursor;
import java.awt.Image;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A momentary button is "on" while pressed.
 * Origin is at the center of the button.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MomentaryButtonNode extends PNode {

    private final PImage imageNode;
    private final DynamicCursorHandler cursorHandler;

    // Constructor that uses default images (round red buttons with 3D look)
    public MomentaryButtonNode( IUserComponent userComponent, Property<Boolean> onProperty, Property<Boolean> enabledProperty ) {
        this( userComponent, onProperty, enabledProperty,
              PiccoloPhetResources.getImage( "button_pressed.png" ),
              PiccoloPhetResources.getImage( "button_unpressed.png" ),
              PiccoloPhetResources.getImage( "button_disabled.png" ) );
    }

    public MomentaryButtonNode( final IUserComponent userComponent, final Property<Boolean> onProperty, final Property<Boolean> enabledProperty, final Image onImage, final Image offImage, final Image disabledImage ) {
        assert ( onImage != offImage ); // different images are required

        cursorHandler = new DynamicCursorHandler();

        imageNode = new PImage();
        addChild( imageNode );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                if ( enabledProperty.get() ) {
                    SimSharingManager.sendUserMessage( userComponent, UserActions.pressed );
                    onProperty.set( true );
                }
            }

            @Override public void mouseReleased( PInputEvent event ) {
                if ( enabledProperty.get() ) {
                    SimSharingManager.sendUserMessage( userComponent, UserActions.released );
                    onProperty.set( false );
                }
            }
        } );

        onProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( enabledProperty.get() ) {
                    setImage( onProperty.get() ? onImage : offImage );
                }
                else {
                    setImage( disabledImage );
                }
            }
        } );

        enabledProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( enabledProperty.get() ) {
                    setImage( offImage );
                    cursorHandler.setCursor( Cursor.HAND_CURSOR );
                }
                else {
                    onProperty.set( false );
                    setImage( disabledImage );
                    cursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
                }
            }
        } );

        addInputEventListener( cursorHandler );
    }

    private void setImage( Image image ) {
        imageNode.setImage( image );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() / 2 );
    }
}