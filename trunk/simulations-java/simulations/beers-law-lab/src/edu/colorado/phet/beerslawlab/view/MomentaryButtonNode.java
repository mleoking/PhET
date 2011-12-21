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
    public MomentaryButtonNode( Property<Boolean> onProperty ) {
        this( onProperty, PiccoloPhetResources.getImage( "button_pressed.png" ), PiccoloPhetResources.getImage( "button_unpressed.png" ) );
    }

    public MomentaryButtonNode( final Property<Boolean> onProperty, Image onImage, Image offImage ) {
        assert ( onImage != offImage ); // different images are required

        final PImage onNode = new PImage( onImage );
        addChild( onNode );

        final PImage offNode = new PImage( offImage );
        addChild( offNode );

        // Both images must have the same size.
        assert ( onNode.getFullBoundsReference().equals( offNode.getFullBoundsReference() ) );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                SimSharingManager.sendEvent( simSharingObject, Actions.PRESSED );
                onProperty.set( true );
            }

            @Override public void mouseReleased( PInputEvent event ) {
                SimSharingManager.sendEvent( simSharingObject, Actions.RELEASED );
                onProperty.set( false );
            }
        } );

        onProperty.addObserver( new SimpleObserver() {
            public void update() {
                onNode.setVisible( onProperty.get() );
                offNode.setVisible( !onProperty.get() );
            }
        } );
    }

    public void setSimSharingObject( String simSharingObject ) {
        this.simSharingObject = simSharingObject;
    }
}