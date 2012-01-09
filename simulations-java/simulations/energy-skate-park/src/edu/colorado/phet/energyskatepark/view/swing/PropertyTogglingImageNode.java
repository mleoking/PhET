// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.componentType;
import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentTypes.icon;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;

/**
 * This class represents a PNode that is an image and that, when clicked,
 * toggles the value of a boolean property.  This is most often used in
 * conjunction with a check box.
 *
 * @author John Blanco
 */

public class PropertyTogglingImageNode extends PNode {

    public PropertyTogglingImageNode( final UserComponent userComponent, Image image, final BooleanProperty property ) {

        // Create and add the image node.
        PNode imageNode = new PImage( image );
        addChild( imageNode );

        // Hook up the image node to toggle the property.
        imageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                SimSharingManager.sendUserMessage( userComponent, pressed, componentType( icon ), param( ParameterKeys.isSelected, !property.get() ) );
                property.set( !property.get() );
            }
        } );
    }
}
