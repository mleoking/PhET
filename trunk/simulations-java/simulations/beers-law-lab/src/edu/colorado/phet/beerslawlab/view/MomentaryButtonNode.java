// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * A momentary button is "on" while pressed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MomentaryButtonNode extends PNode {

    private String simSharingObject = "MomentaryButtonNode";

    // Constructor that uses default images (round red buttons with 3D look)
    public MomentaryButtonNode( Property<Boolean> onProperty, Property<Boolean> enabledProperty ) {
        this( onProperty, enabledProperty,
              PiccoloPhetResources.getImage( "button_pressed.png" ),
              PiccoloPhetResources.getImage( "button_unpressed.png" ),
              PiccoloPhetResources.getImage( "button_disabled.png" ) );
    }

    public MomentaryButtonNode( final Property<Boolean> onProperty, final Property<Boolean> enabledProperty, final Image onImage, final Image offImage, final Image disabledImage ) {
        assert ( onImage != offImage ); // different images are required

        final PImage imageNode = new PImage();
        addChild( imageNode );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                if ( enabledProperty.get() ) {
                    SimSharingManager.sendEvent( simSharingObject, Actions.PRESSED );
                    onProperty.set( true );
                }
            }

            @Override public void mouseReleased( PInputEvent event ) {
                if ( enabledProperty.get() ) {
                    SimSharingManager.sendEvent( simSharingObject, Actions.RELEASED );
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
                    imageNode.setImage( disabledImage );
                }
            }
        } );

        enabledProperty.addObserver( new SimpleObserver() {
            public void update() {
                if ( enabledProperty.get() ) {
                    imageNode.setImage( offImage );
                }
                else {
                    onProperty.set( false );
                    imageNode.setImage( disabledImage );
                }
            }
        } );
    }

    public void setSimSharingObject( String simSharingObject ) {
        this.simSharingObject = simSharingObject;
    }
}