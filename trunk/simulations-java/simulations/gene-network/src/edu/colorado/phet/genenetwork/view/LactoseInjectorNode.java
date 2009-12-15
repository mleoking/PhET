/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

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
	private static final double INJECTOR_HEIGHT = 200;
	
	// Angle of rotation of this node.
	private static final double ROTATION_ANGLE = Math.PI/3;
	
	// Offset of button within this node.  This was determined by trial and
	// error and will need to be tweaked if the images change.
	private static final Point2D BUTTON_OFFSET = new Point2D.Double(45, 45);
	
	// Velocity at which lactose is injected in to the model.
	private static final Vector2D NOMINAL_LACTOSE_INJECTION_VELOCITY = 
		(new Vector2D.Double(0, -20)).rotate(-ROTATION_ANGLE);
	
	// Random number generator.
	private static final Random RAND = new Random();

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private PNode bodyImage;
	private PNode unpressedButtonImage;
	private PNode pressedButtonImage;
	private IGeneNetworkModelControl model;
	private ModelViewTransform2D mvt;
	private Dimension2D injectionPointOffset = new PDimension();
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public LactoseInjectorNode(final IGeneNetworkModelControl model, ModelViewTransform2D mvt) {
		
		this.model = model;
		this.mvt = mvt;
		
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
        
        // Set up the injection point offset.  This is currently fixed, which
        // assumes that the node is rotated so that it is pointing down and to
        // the left.  This may need to be generalized some day.
        injectionPointOffset.setSize(-getFullBoundsReference().width * 0.35, getFullBoundsReference().height * 0.25);
        
        // Set up the button handling.
        unpressedButtonImage.setPickable(true);
        pressedButtonImage.setPickable(false);
        bodyImage.setPickable(false);
        unpressedButtonImage.addInputEventListener(new CursorHandler());
        unpressedButtonImage.addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed( PInputEvent event ) {
                unpressedButtonImage.setVisible(false);
                injectLactose();
            }
        	
        	@Override
            public void mouseReleased( PInputEvent event ) {
                unpressedButtonImage.setVisible(true);
            }
        });
	}
	
	private void injectLactose(){
        // Calculate the injection point.
		double injectionPointX = mvt.viewToModelX(getFullBoundsReference().getCenter2D().getX() + injectionPointOffset.getWidth());
		double injectionPointY = mvt.viewToModelY(getFullBoundsReference().getCenter2D().getY() + injectionPointOffset.getHeight());
		Vector2D initialVelocity = new Vector2D.Double(NOMINAL_LACTOSE_INJECTION_VELOCITY);
		initialVelocity.rotate((RAND.nextDouble() - 0.5) * Math.PI * 0.15);
        model.createAndAddLactose(new Point2D.Double(injectionPointX, injectionPointY), initialVelocity); 
	}
}
