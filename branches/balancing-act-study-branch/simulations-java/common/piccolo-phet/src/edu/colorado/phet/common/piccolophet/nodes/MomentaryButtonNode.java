// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.colorado.phet.common.piccolophet.event.DynamicCursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A momentary button is "on" while pressed, "off" when released.
 * Origin is at the upper-left of the bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MomentaryButtonNode extends PNode {

    private final PImage imageNode;
    private final DynamicCursorHandler cursorHandler;

    // Button that will always be enabled.
    public MomentaryButtonNode( IUserComponent userComponent, Property<Boolean> onProperty, final Image onImage, final Image offImage ) {
        this( userComponent, onProperty, onImage, offImage, new Property<Boolean>( true ), onImage, offImage );
    }

    public MomentaryButtonNode( final IUserComponent userComponent,
                                final Property<Boolean> onProperty, final Image onImage, final Image offImage,
                                final Property<Boolean> enabledProperty, final Image onDisabledImage, final Image offDisabledImage ) {

        cursorHandler = new DynamicCursorHandler();

        imageNode = new PImage();
        addChild( imageNode );

        addInputEventListener( new PBasicInputEventHandler() {

            private final IUserComponentType userComponentType = UserComponentTypes.button;

            @Override public void mousePressed( PInputEvent event ) {
                if ( enabledProperty.get() ) {
                    SimSharingManager.sendUserMessage( userComponent, userComponentType, UserActions.pressed );
                    onProperty.set( true );
                }
            }

            @Override public void mouseReleased( PInputEvent event ) {
                if ( enabledProperty.get() ) {
                    SimSharingManager.sendUserMessage( userComponent, userComponentType, UserActions.released );
                    onProperty.set( false );
                }
            }
        } );

        onProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( enabledProperty.get() ) {
                    imageNode.setImage( onProperty.get() ? onImage : offImage );
                }
                else {
                    imageNode.setImage( onProperty.get() ? onDisabledImage : offDisabledImage );
                }
            }
        } );

        enabledProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( enabledProperty.get() ) {
                    imageNode.setImage( onProperty.get() ? onImage : offImage );
                    cursorHandler.setCursor( Cursor.HAND_CURSOR );
                }
                else {
                    imageNode.setImage( onProperty.get() ? onDisabledImage : offDisabledImage );
                    cursorHandler.setCursor( Cursor.DEFAULT_CURSOR );
                }
            }
        } );

        addInputEventListener( cursorHandler );
    }

    // test
    public static void main( String[] args ) {

        Property<Boolean> buttonOn = new Property<Boolean>( false ) {{
            addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean buttonOn ) {
                    System.out.println( "buttonOn = " + buttonOn );
                }
            } );
        }};

        Property<Boolean> buttonEnabled = new Property<Boolean>( true ){{
            addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean buttonEnabled ) {
                    System.out.println( "buttonEnabled = " + buttonEnabled );
                }
            } );
        }};

        PNode button = new MomentaryButtonNode( new UserComponent( "testButton"),
                                                buttonOn, PiccoloPhetResources.getImage( "button_pressed.png" ), PiccoloPhetResources.getImage( "button_unpressed.png" ),
                                                buttonEnabled, PiccoloPhetResources.getImage( "button_pressed_disabled.png" ), PiccoloPhetResources.getImage( "button_unpressed_disabled.png" ));
        PCanvas canvas = new PCanvas();
        canvas.setPreferredSize( new Dimension( 500, 500 ) );
        canvas.getLayer().addChild( button );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}