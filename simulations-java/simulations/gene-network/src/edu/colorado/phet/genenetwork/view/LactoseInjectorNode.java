/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JRadioButton;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

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
	private static final double INJECTOR_HEIGHT = 175;
	
	// Angle of rotation of this node.
	private static final double ROTATION_ANGLE = -Math.PI/3;
	
	// Offset of button within this node.  This was determined by trial and
	// error and will need to be tweaked if the images change.
	private static final Point2D BUTTON_OFFSET = new Point2D.Double(45, 45);
	
	// Velocity at which lactose is injected in to the model.
	private static final Vector2D NOMINAL_LACTOSE_INJECTION_VELOCITY = 
		(new Vector2D.Double(0, -20)).rotate(-ROTATION_ANGLE);
	
	// Random number generator.
	private static final Random RAND = new Random();
	
    // Timer used for fading in and out.
	private static final int BUTTON_DELAY_TIME = 40; // In milliseconds.
    private static final Timer FADE_IN_TIMER = new Timer( BUTTON_DELAY_TIME, null );
    private static final Timer FADE_OUT_TIMER = new Timer( BUTTON_DELAY_TIME, null );
    private static final float FADE_INCREMENT = 0.05f;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private PNode injectorBodyImageNode;
	private PNode unpressedButtonImageNode;
	private PNode pressedButtonImageNode;
	private PNode autoInjectionControl;
	private IGeneNetworkModelControl model;
	private ModelViewTransform2D mvt;
	private Dimension2D injectionPointOffset = new PDimension();
	private float transparency;  // For fading.  0 is transparent, 1 is opaque.
	private Point2D injectionPointInModelCoords = new Point2D.Double();
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public LactoseInjectorNode(final IGeneNetworkModelControl model, ModelViewTransform2D mvt) {
		
		this.model = model;
		this.mvt = mvt;

		PNode injectorNode = new PNode();
		
		// Load the graphic images for this device.
		BufferedImage injectorBodyImage = GeneNetworkResources.getImage( "squeezer_background.png" );
        injectorBodyImageNode = new PImage( injectorBodyImage );
        injectorNode.addChild(injectorBodyImageNode);
        BufferedImage pressedButtonImage = GeneNetworkResources.getImage( "button_pressed.png" );
        pressedButtonImageNode = new PImage(pressedButtonImage);
        pressedButtonImageNode.setOffset(BUTTON_OFFSET);
        injectorNode.addChild(pressedButtonImageNode);
        BufferedImage unpressedButtonImage = GeneNetworkResources.getImage( "button_unpressed.png" );
        unpressedButtonImageNode = new PImage(unpressedButtonImage);
        unpressedButtonImageNode.setOffset(BUTTON_OFFSET);
        injectorNode.addChild(unpressedButtonImageNode);
        
        // Rotate and scale the image node as a whole.
        double scale = INJECTOR_HEIGHT / injectorBodyImageNode.getFullBoundsReference().height;
        injectorNode.rotateAboutPoint( ROTATION_ANGLE, new Point2D.Double( injectorBodyImageNode.getFullBoundsReference().width / 2, injectorBodyImageNode.getFullBoundsReference().height / 2));
        injectorNode.scale(scale);
        System.out.println("1. Full bounds = " + getFullBoundsReference());
        
        // Add the node that allows control of automatic injection.
        autoInjectionControl = new AutomaticInjectionSelector(model, injectorNode.getFullBoundsReference().height * 0.4);
        autoInjectionControl.setOffset(0,
        	injectorNode.getFullBoundsReference().getCenterY() - autoInjectionControl.getFullBoundsReference().width / 2);
        addChild(autoInjectionControl);
        System.out.println("2. Full bounds = " + getFullBoundsReference());
        
        // Add the injector node.  Note that the position has to be tweaked
        // in order to account for the rotation of the node image, since the
        // rotation of the square image enlarges the bounds.
        addChild(injectorNode);
        System.out.println("3. Full bounds = " + getFullBoundsReference());
        injectorNode.setOffset(autoInjectionControl.getFullBoundsReference().getMaxX() - 26,
        		getFullBoundsReference().getCenterY());
        
        // Set up the injection point offset.  This is currently fixed, which
        // assumes that the node is rotated so that it is pointing down and to
        // the right.  This may need to be generalized some day.
        injectionPointOffset.setSize(getFullBoundsReference().width * 0.46, getFullBoundsReference().height * 0.23);
        
        // Set the point for automatic injection to be at the tip of the
        // injector.
        updateInjectionPoint();
        model.setAutomaticLactoseInjectionParams( injectionPointInModelCoords, NOMINAL_LACTOSE_INJECTION_VELOCITY );
        
        
        // Set up the button handling.
        unpressedButtonImageNode.setPickable(true);
        pressedButtonImageNode.setPickable(false);
        injectorBodyImageNode.setPickable(false);
        unpressedButtonImageNode.addInputEventListener(new CursorHandler());
        unpressedButtonImageNode.addInputEventListener(new PBasicInputEventHandler(){
        	@Override
            public void mousePressed( PInputEvent event ) {
                unpressedButtonImageNode.setVisible(false);
                injectLactose();
            }
        	
        	@Override
            public void mouseReleased( PInputEvent event ) {
                unpressedButtonImageNode.setVisible(true);
            }
        });
        
        // Listen for changes that affect this node's visibility.
        model.addListener(new GeneNetworkModelAdapter(){
        	public void lactoseInjectionAllowedStateChange(){
        		updateInjectorNodeVisibility();
        	}
        	public void automaticLactoseInjectionEnabledStateChange() { 
        		updateInjectButtonVisibility();
        	}
        });
        
        // Set up listener to the timer used for fading in.
		FADE_IN_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
        		transparency = Math.min(1, transparency + FADE_INCREMENT);
        		setTransparency(transparency);
        		if (transparency >= 1){
        			FADE_IN_TIMER.stop();
        		}
            }
        } );
        
        // Set up listener to the timer used for fading out.
		FADE_OUT_TIMER.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
        		transparency = Math.max(0, transparency - FADE_INCREMENT);
        		setTransparency(transparency);
        		if (transparency <= 0){
        			FADE_OUT_TIMER.stop();
        		}
            }
        } );
        
        updateInjectorNodeVisibility();
        updateInjectButtonVisibility();
	}

	/**
	 * If we get moved, we need to update the model location where lactose is
	 * injected.
	 */
	@Override
	public void setOffset(double x, double y) {
		super.setOffset(x, y);
		updateInjectionPoint();
		model.setAutomaticLactoseInjectionParams(injectionPointInModelCoords, NOMINAL_LACTOSE_INJECTION_VELOCITY);
	}

	private void injectLactose(){
		Vector2D initialVelocity = new Vector2D.Double(NOMINAL_LACTOSE_INJECTION_VELOCITY);
		initialVelocity.rotate((RAND.nextDouble() - 0.5) * Math.PI * 0.15);
        model.createAndAddLactose(injectionPointInModelCoords, initialVelocity); 
	}
	
	private void updateInjectorNodeVisibility(){
		if (model.isLactoseInjectionAllowed()){
			FADE_OUT_TIMER.stop();
			FADE_IN_TIMER.start();
			unpressedButtonImageNode.setPickable(true);
		}
		else{
			transparency = 0;
			setTransparency(transparency);
			unpressedButtonImageNode.setPickable(false);
		}
	}
	
	private void updateInjectButtonVisibility(){
		// Buttons that allow manual injection should only be visible when
		// in the manual injection mode.
		boolean autoInjectionEnabled = model.isAutomaticLactoseInjectionEnabled();
		unpressedButtonImageNode.setVisible(!autoInjectionEnabled);
		unpressedButtonImageNode.setPickable(!autoInjectionEnabled);
		pressedButtonImageNode.setVisible(!autoInjectionEnabled);
	}
	
	private void updateInjectionPoint(){
		double injectionPointX = mvt.viewToModelX(getFullBoundsReference().getCenter2D().getX() + injectionPointOffset.getWidth());
		double injectionPointY = mvt.viewToModelY(getFullBoundsReference().getCenter2D().getY() + injectionPointOffset.getHeight());
		injectionPointInModelCoords.setLocation(injectionPointX, injectionPointY);
	}
	
	private static class AutomaticInjectionSelector extends PNode {
		
		private static final Font LABEL_FONT = new PhetFont(14);
		private static final Color BACKGROUND_COLOR = new Color(248, 236, 84);
		private static final Stroke BORDER_STROKE = new BasicStroke(2f);
		private static final Stroke CONNECTOR_STROKE = new BasicStroke(8f);
		private static final double CONNECTOR_LENGTH = 10;  // In screen coords, which is roughly pixels.
		
		private IGeneNetworkModelControl model;
		private JRadioButton manualButton;
		private JRadioButton autoButton;
		
		/**
		 * Constructor.  The height is specified, and the width is then
		 * determined as a function of the height and the string lengths.
		 * 
		 * @param model
		 * @param height
		 */
		public AutomaticInjectionSelector(final IGeneNetworkModelControl model, double height){
		
			this.model = model;
			
			// Create the radio buttons that will allow the user to enable
			// or disable automatic lactose injections.
			manualButton = new JRadioButton("Manual");
			manualButton.setFont(LABEL_FONT);
			manualButton.setBackground(BACKGROUND_COLOR);
			manualButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.setAutomaticLactoseInjectionEnabled(false);
				}
			});
			autoButton = new JRadioButton("Auto");
			autoButton.setFont(LABEL_FONT);
			autoButton.setBackground(BACKGROUND_COLOR);
			autoButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.setAutomaticLactoseInjectionEnabled(true);
				}
			});
			
			// Set the initial state of the radio buttons.
			updateButtonState();
			
			// Listen to the model for notifications of change to the
			// auto/manual setting.
			model.addListener(new GeneNetworkModelAdapter(){
				@Override
				public void automaticLactoseInjectionEnabledStateChange() { 
					updateButtonState();
				}
			});
			
			// Wrap the buttons in PSwings so that they can be added to this
			// pnode.
			PSwing autoButtonPSwing = new PSwing(autoButton);
			PSwing manualButtonPSwing = new PSwing(manualButton);
			
			// Create the body of the node based on the button size.
			double bodyWidth = Math.max(autoButtonPSwing.getFullBoundsReference().width,
					manualButtonPSwing.getFullBoundsReference().width);
			bodyWidth *= 1.1;
			double bodyHeight = autoButtonPSwing.getHeight() * 2.5;
			PhetPPath body = new PhetPPath(new RoundRectangle2D.Double(0, 0, bodyWidth, bodyHeight, 7, 7), 
					BACKGROUND_COLOR, BORDER_STROKE, Color.black);
			
			// Add the buttons to the body.
			body.addChild(manualButtonPSwing);
			manualButtonPSwing.setOffset(3, 3);
			body.addChild(autoButtonPSwing);
			autoButtonPSwing.setOffset(3, manualButtonPSwing.getFullBoundsReference().getMaxY());
			
			// Scale the body to match the specified height.
			body.scale(height / body.getFullBoundsReference().height);
			
			// Create the line that will visually connect this node to the
			// main injector node.
			PhetPPath connector = new PhetPPath(new Line2D.Double(0, 0, CONNECTOR_LENGTH, 0), CONNECTOR_STROKE, 
					Color.BLACK);
			
			// Add the body and the line that will connect it to the injector node.
			addChild(body);
			addChild(connector);
			connector.setOffset(body.getFullBoundsReference().width, body.getFullBoundsReference().height / 2);
		}

		private void updateButtonState() {
			manualButton.setSelected(!model.isAutomaticLactoseInjectionEnabled());
			autoButton.setSelected(model.isAutomaticLactoseInjectionEnabled());
		}
	}
}
