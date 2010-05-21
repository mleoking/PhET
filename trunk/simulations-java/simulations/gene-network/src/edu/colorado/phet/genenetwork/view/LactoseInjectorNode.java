/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkResources;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Node that represents the thing with which the user interacts in order to
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
	private static final double INJECTOR_HEIGHT = 130;
	
	// Size of the panel that allows the user to select either manual or
	// automatic injection mode.
	private static final double SELECTOR_WIDTH = INJECTOR_HEIGHT * 0.9;
	private static final double SELECTOR_HEIGHT = INJECTOR_HEIGHT * 0.7;
	
	// Offset of button within this node.  This was determined by trial and
	// error and will need to be tweaked if the images change.
	private static final Point2D BUTTON_OFFSET = new Point2D.Double(-100, -65);
	
	// Velocity at which lactose is injected in to the model.
	private static double NOMINAL_LACTOSE_INJECTION_VELOCITY = 20; 
	
	// Random number generator.
	private static final Random RAND = new Random();
	
    // Time values used for fading in and out.
	private static final int FADE_IN_DELAY_TIME = 40; // In milliseconds.
    private static final float FADE_INCREMENT = 0.05f;
    
    // Debug controls.
    private static final boolean DEBUG_INJECTION_POINT = false;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    double rotationAngle;
	private PNode injectorBodyImageNode;
	private PNode unpressedButtonImageNode;
	private PNode pressedButtonImageNode;
	private PNode autoInjectionControl;
	private PNode injectorNode;
	private IGeneNetworkModelControl model;
	private ModelViewTransform2D mvt;
	private Point2D injectionPoint = new Point2D.Double();
	private float transparency;  // For fading.  0 is transparent, 1 is opaque.
	private Point2D injectionPointInModelCoords = new Point2D.Double();
	private Vector2D nominalInjectionVelocityVector = new Vector2D.Double(NOMINAL_LACTOSE_INJECTION_VELOCITY, 0);
    private final Timer fadeInTimer = new Timer( FADE_IN_DELAY_TIME, null );
    
    // For debug.
    private PNode injectPtNode;

	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Constructs a lactose injection node.
     * 
     * @param model - The model into which the lactose will be injected.
     * @param mvt - Model-view transform for relating view space to model space.
     * @param rotationAngle - Angle of rotation for the injection bulb.
     * @param labelText - Text of label that will go above the injector, null
     * indicates that no label should be shown.
     */
	public LactoseInjectorNode(final IGeneNetworkModelControl model, ModelViewTransform2D mvt, double rotationAngle, 
			String labelText) {
		
		this.model = model;
		this.mvt = mvt;
		this.rotationAngle = rotationAngle;
		
		nominalInjectionVelocityVector.rotate(rotationAngle);

		injectorNode = new PNode();
		
		// Load the graphic images for this device.  These are offset in order
		// to make the center of rotation be the center of the bulb.
		BufferedImage injectorBodyImage = GeneNetworkResources.getImage( "squeezer_background.png" );
        injectorBodyImageNode = new PImage( injectorBodyImage );
        Rectangle2D originalBodyBounds = injectorBodyImageNode.getFullBounds();
        injectorBodyImageNode.setOffset(-originalBodyBounds.getWidth() / 2, -originalBodyBounds.getHeight() / 2);
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
        injectorNode.rotate(-rotationAngle);
        injectorNode.scale(scale);
        
        // Add the node that allows control of automatic injection.
        autoInjectionControl = new AutomaticInjectionSelector(model, SELECTOR_WIDTH, SELECTOR_HEIGHT);
        autoInjectionControl.setOffset(
        	injectorNode.getFullBoundsReference().getMinX() - autoInjectionControl.getFullBoundsReference().width + 5,
        	injectorNode.getFullBoundsReference().getCenterY() - autoInjectionControl.getFullBoundsReference().height / 2);
        addChild(autoInjectionControl);
        
        // Add the injector image node.  Note that the position has to be
        // tweaked in order to account for the rotation of the node image,
        // since the rotation of the square image enlarges the bounds.
        injectorNode.setOffset(-Math.abs(Math.sin(rotationAngle * 2)) * 30, 0);
        addChild(injectorNode);
        
        if (DEBUG_INJECTION_POINT){
        	injectPtNode = new PhetPPath(new Ellipse2D.Double(-6, -6, 12, 12), Color.PINK);
        	addChild(injectPtNode);
        }
        
        // Set the point for automatic injection to be at the tip of the
        // injector.
        updateInjectionPoint();
        model.setAutomaticLactoseInjectionParams( injectionPointInModelCoords, nominalInjectionVelocityVector );
        
        // If specified, add the label.  To better support internationalization,
        // a dimension is defined into which the label must fit, and the label
        // is scaled as needed to fit within this dimension.
        if (labelText != null){
        	Dimension2D labelMaxSize = new PDimension(getFullBoundsReference().width * 0.6,
        			getFullBoundsReference().getHeight() * 0.2);
        	PText label = new PText("Lactose Injector");
        	label.setFont(new PhetFont());
        	double labelScale = Math.min(labelMaxSize.getWidth() / label.getWidth(),
        			labelMaxSize.getHeight() / label.getHeight());
        	label.setScale(labelScale);
        	// Position the label.  This must be adjusted based on the rotational
        	// angle of the image, since rotation messes up the bounds.
        	label.setOffset( getFullBoundsReference().getCenterX() - getFullBoundsReference().getWidth() * 0.1 - label.getFullBoundsReference().width / 2,
        			getFullBoundsReference().getMinY() - label.getFullBoundsReference().height + Math.sin(Math.abs(rotationAngle * 2)) * label.getFullBoundsReference().height * 0.9);
        	addChild(label);
        }
        
        // Set up the button handling.
        unpressedButtonImageNode.setPickable(true);
        pressedButtonImageNode.setPickable(false);
        injectorBodyImageNode.setPickable(false);
        unpressedButtonImageNode.addInputEventListener(new CursorHandler());
        pressedButtonImageNode.addInputEventListener(new CursorHandler());
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
        		updateInjectButtonVisibility();
        	}
        	public void automaticLactoseInjectionEnabledStateChange() { 
        		updateInjectButtonVisibility();
        	}
        });
        
        // Set up listener to the timer used for fading in.
		fadeInTimer.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
        		transparency = Math.min(1, transparency + FADE_INCREMENT);
        		setTransparency(transparency);
        		if (transparency >= 1){
        			fadeInTimer.stop();
        		}
            }
        } );
        
        updateInjectorNodeVisibility();
        updateInjectButtonVisibility();
	}
	
	/**
	 * Constructor that assumes no label.
	 * 
	 * @param model
	 * @param mvt
	 * @param rotationAngle
	 */
	public LactoseInjectorNode(final IGeneNetworkModelControl model, ModelViewTransform2D mvt, double rotationAngle){
		this(model, mvt, rotationAngle, null);
	}

	/**
	 * If we get moved, we need to update the model location where lactose is
	 * injected.
	 */
	@Override
	public void setOffset(double x, double y) {
		super.setOffset(x, y);
		updateInjectionPoint();
		model.setAutomaticLactoseInjectionParams(injectionPointInModelCoords, nominalInjectionVelocityVector);
	}

	private void injectLactose(){
		Vector2D initialVelocity = new Vector2D.Double(nominalInjectionVelocityVector);
		initialVelocity.rotate((RAND.nextDouble() - 0.5) * Math.PI * 0.15);
        model.createAndAddLactose(injectionPointInModelCoords, initialVelocity); 
	}
	
	private void updateInjectorNodeVisibility(){
		if (model.isLactoseInjectionAllowed()){
			fadeInTimer.start();
		}
		else{
			transparency = 0;
			setTransparency(transparency);
		}
	}
	
	private void updateInjectButtonVisibility(){
		// Buttons that allow manual injection should only be visible when
		// in the manual injection mode.
		boolean injectionEnabled = model.isLactoseInjectionAllowed();
		boolean autoInjectionEnabled = model.isAutomaticLactoseInjectionEnabled();
		unpressedButtonImageNode.setVisible(injectionEnabled && !autoInjectionEnabled);
		unpressedButtonImageNode.setPickable(injectionEnabled && !autoInjectionEnabled);
		pressedButtonImageNode.setVisible(injectionEnabled && !autoInjectionEnabled);
	}
	
	private void updateInjectionPoint(){

		// Set up the injection point. This makes some assumptions about the
        // nature of the image, and will need to be updated if the image is
        // changed.
        double distanceCenterToTip = 0.7 * INJECTOR_HEIGHT;
        Point2D centerOfInjector = injectorNode.getFullBoundsReference().getCenter2D();
        injectionPoint.setLocation(distanceCenterToTip * Math.cos(rotationAngle) + centerOfInjector.getX(),
        		distanceCenterToTip * Math.sin(-rotationAngle) + centerOfInjector.getY());
		Point2D injectionPointInGlobalCoords =
			localToGlobal(new Point2D.Double(injectionPoint.getX(), injectionPoint.getY()));
		injectionPointInModelCoords.setLocation(mvt.viewToModel(injectionPointInGlobalCoords));
		if (DEBUG_INJECTION_POINT){
			injectPtNode.setOffset(injectionPoint);
		}
	}
	
	private static class AutomaticInjectionSelector extends PNode {
		
		private static final Font AUTO_INJECT_CTRL_LABEL_FONT = new PhetFont(14);
		private static final Color BACKGROUND_COLOR = new Color(248, 236, 84);
		private static final float BORDER_STROKE_WIDTH = 2;
		private static final Stroke BORDER_STROKE = new BasicStroke(BORDER_STROKE_WIDTH);
		private static final Stroke CONNECTOR_STROKE = new BasicStroke(8f);
		private static final double CONNECTOR_LENGTH = 20;  // In screen coords, which is roughly pixels.
		
		private IGeneNetworkModelControl model;
		private JRadioButton manualButton;
		private JRadioButton autoButton;
		
		/**
		 * Constructor.  The height and width are specified, and the buttons
		 * and labels are scaled to fit the specification.  This is done so
		 * that translations don't end up changing the overall size of this
		 * node.  The down side is that long names for these labels will end
		 * up looking small.
		 * 
		 * @param model
		 * @param height
		 */
		public AutomaticInjectionSelector(final IGeneNetworkModelControl model, double width, double height){
		
			this.model = model;
			
			// Create the radio buttons that will allow the user to enable
			// or disable automatic lactose injections.
			manualButton = new JRadioButton(GeneNetworkStrings.MANUAL_LACTOSE_INJECTION_LABEL);
			manualButton.setBackground(BACKGROUND_COLOR);
			manualButton.setFont(AUTO_INJECT_CTRL_LABEL_FONT);
			manualButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.setAutomaticLactoseInjectionEnabled(false);
				}
			});
			autoButton = new JRadioButton(GeneNetworkStrings.AUTO_LACTOSE_INJECTION_LABEL);
			autoButton.setBackground(BACKGROUND_COLOR);
			autoButton.setFont(AUTO_INJECT_CTRL_LABEL_FONT);
			autoButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					model.setAutomaticLactoseInjectionEnabled(true);
				}
			});
			
			// Create a button group.
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(manualButton);
			buttonGroup.add(autoButton);
			
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
			
			// Create a panel for placing the buttons.
			JPanel buttonPanel = new VerticalLayoutPanel();
			buttonPanel.setBackground(BACKGROUND_COLOR);
			buttonPanel.add(manualButton);
			buttonPanel.add(autoButton);
			
			// Wrap the panel in a PSwing so that it can be added to this
			// PNode.
			PSwing buttonPanelPSwing = new PSwing(buttonPanel);
			
			// Scale the button panel so that it will fit.
			PBounds buttonPanelBounds = buttonPanelPSwing.getFullBoundsReference();
			double buttonPanelScaleFactor = Math.min((width - 4 * BORDER_STROKE_WIDTH) / buttonPanelBounds.getWidth(),
					(height - 4 * BORDER_STROKE_WIDTH) / buttonPanelBounds.getHeight());
			buttonPanelPSwing.setScale(buttonPanelScaleFactor);
			
			// Create the body of the node.
			PhetPPath body = new PhetPPath(new RoundRectangle2D.Double(0, 0, width, height, 7, 7), 
					BACKGROUND_COLOR, BORDER_STROKE, Color.black);
			
			// Add the button panel to the body such that the are centered.
			body.addChild(buttonPanelPSwing);
			buttonPanelPSwing.setOffset(width / 2 - buttonPanelPSwing.getFullBoundsReference().width / 2,
					height / 2 - buttonPanelPSwing.getFullBoundsReference().height / 2);
			
			// Create the line that will visually connect this node to the
			// main injector node.
			PhetPPath connector = new PhetPPath(new Line2D.Double(0, 0, CONNECTOR_LENGTH, 0), CONNECTOR_STROKE, 
					Color.BLACK);
			
			// Add the body and the line that will connect it to the injector node.
			addChild(connector);
			addChild(body);
			connector.setOffset(body.getFullBoundsReference().width, body.getFullBoundsReference().height / 2);
		}

		private void updateButtonState() {
			manualButton.setSelected(!model.isAutomaticLactoseInjectionEnabled());
			autoButton.setSelected(model.isAutomaticLactoseInjectionEnabled());
		}
	}
}
