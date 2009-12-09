/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Node that represents the thing that the user interacts with in order to
 * inject lactose into the system.
 * 
 * @author John Blanco
 */
public class LactoseInjectorNode extends PNode {

	//------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Height of the injector prior to rotation.  This is in world size, which
	// is close to pixels (but not quite exactly due to all that transform
	// craziness).
	private static final double INJECTOR_HEIGHT = 120;
	
	// Angle of rotation of this node.
	private static final double ROTATION_ANGLE = Math.PI/4;
//	private static final double ROTATION_ANGLE = 0;
	
	// Offset of button within this node.  This was determined by trial and
	// error and will need to be tweaked if the images change.
	private static final Point2D BUTTON_OFFSET = new Point2D.Double(45, 45);

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	PNode bodyImage;
	PNode unpressedButtonImage;
	PNode pressedButtonImage;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public LactoseInjectorNode() {
        // Load the graphic images for this device.  IMPORTANT: This code
		// assumes that the place where the lactose comes out is at the
		// bottom center of this image.
        BufferedImage bufferedImage = GeneNetworkResources.getImage( "squeezer_background.png" );
        bodyImage = new PImage( bufferedImage );
        addChild(bodyImage);
        bufferedImage = GeneNetworkResources.getImage( "button_pressed.png" );
        pressedButtonImage = new PImage (bufferedImage);
        pressedButtonImage.setOffset(BUTTON_OFFSET);
        addChild(pressedButtonImage);
        bufferedImage = GeneNetworkResources.getImage( "button_unpressed.png" );
        unpressedButtonImage = new PImage (bufferedImage);
        unpressedButtonImage.setOffset(BUTTON_OFFSET);
        addChild(unpressedButtonImage);
        
        // Rotate and scale the node as a whole.
        double scale = INJECTOR_HEIGHT / bodyImage.getFullBoundsReference().height;
        rotateAboutPoint( ROTATION_ANGLE, new Point2D.Double( bodyImage.getFullBoundsReference().width / 2, bodyImage.getFullBoundsReference().height / 2));
        scale(scale);
        
        // Set up the button handling.
        unpressedButtonImage.setPickable(true);
        pressedButtonImage.setPickable(false);
        bodyImage.setPickable(false);
        unpressedButtonImage.addInputEventListener(new CursorHandler());
        unpressedButtonImage.addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed( PInputEvent event ) {
                unpressedButtonImage.setVisible(false);
            }
        	
        	@Override
            public void mouseReleased( PInputEvent event ) {
                unpressedButtonImage.setVisible(true);
            }
        });
	}
}
