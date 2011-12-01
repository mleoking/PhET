// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * This class represents a PNode that is an image and that, when clicked,
 * toggles the value of a boolean property.  This is most often used in
 * conjunction with a check box.
 *
 * @author John Blanco
 */

public class PropertyTogglingImageNode extends PNode {

    public PropertyTogglingImageNode( Image image, final BooleanProperty property ) {

        // Create and add the image node.
        PNode imageNode = new PImage( image );
        addChild( imageNode );

        // Hook up the image node to toggle the property.
        imageNode.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                property.set( !property.get() );
            }
        } );
    }
}
